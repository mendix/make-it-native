package com.mendix.developerapp.home

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.budiyev.android.codescanner.CodeScanner
import com.google.android.material.color.MaterialColors
import com.mendix.developerapp.BaseFragment
import com.mendix.developerapp.R
import com.mendix.developerapp.databinding.ContentHomeInputsBinding
import com.mendix.developerapp.databinding.ContentHomeQrBinding
import com.mendix.developerapp.databinding.FragmentHomeBinding
import com.mendix.developerapp.utilities.getWarningFilterValue
import com.mendix.developerapp.utilities.supportsAccelerometer
import com.mendix.developerapp.utilities.supportsGyroscope
import com.mendix.developerapp.viewmodels.AppViewModel
import com.mendix.mendixnative.config.AppPreferences
import com.mendix.mendixnative.react.MendixApp

class HomeFragment : BaseFragment() {
    private lateinit var rootBinding: FragmentHomeBinding
    private lateinit var inputSceneBinding: ContentHomeInputsBinding
    private lateinit var qrSceneBinding: ContentHomeQrBinding
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var codeScanner: CodeScanner? = null

    private lateinit var qrScene: Scene
    private val isInQrCodeScene get() = Scene.getCurrentScene(rootBinding.homeRoot) === qrScene
    private lateinit var startScene: Scene


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    goToQrScene()
                } else {
                    showCameraRequestRational()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootBinding = FragmentHomeBinding.inflate(layoutInflater)
        return rootBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.appUrlInvalid.observe(viewLifecycleOwner) { isValid ->
            if (viewModel.shouldShowValidationError.value == true && isValid) {
                inputSceneBinding.textInputRuntimeUrl.error =
                    getString(R.string.error_input_app_url_invalid)
                qrSceneBinding.textInputRuntimeUrl.error =
                    getString(R.string.error_input_app_url_invalid)
            } else {
                inputSceneBinding.textInputRuntimeUrl.error = null
                qrSceneBinding.textInputRuntimeUrl.error = null
            }
        }

        viewModel.supportsAr.observe(viewLifecycleOwner, { arSupported: Boolean ->
            if (!arSupported && viewModel.arUnsupportedMessageShown.value == false) {
                showArDialog()
                viewModel.onARUnsupportedMessageShown()
            } else if (!arSupported && viewModel.arUnsupportedToastShown.value == false) {
                showArToast()
                viewModel.onARUnsupportedToastMessageShown()
            }
        })

        viewModel.qrCodeInvalid.observe(viewLifecycleOwner, { invalidQr ->
            if (invalidQr) {
                Toast.makeText(
                    requireContext(),
                    "The QR Code is invalid. Please verify you scanned a Mendix Native QR Code.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        initializeScenes()
    }

    override fun onPause() {
        toggleQrCode(false)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        toggleQrCode(isInQrCodeScene)
        activity?.window?.statusBarColor = MaterialColors.getColor(
            requireContext(),
            R.attr.colorPrimary,
            ResourcesCompat.getColor(requireActivity().resources, R.color.red1, activity?.theme)
        )
    }

    private fun showArDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.title_ar_unsupported))
            .setMessage(
                "${getString(R.string.message_ar_not_supported)} ${
                    (if (!supportsGyroscope(
                            requireContext()
                        )
                    ) "\n${getString(R.string.gyroscope)}" else "")
                }${(if (!supportsAccelerometer(requireContext())) "\n${getString(R.string.acclerometer)}" else "")}"
            )
            .setPositiveButton(R.string.btn_continue) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun showArToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.message_ar_not_fully_supported),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun initializeScenes() {
        inputSceneBinding =
            ContentHomeInputsBinding.inflate(layoutInflater, rootBinding.homeRoot, false)
        inputSceneBinding.viewModel = viewModel
        inputSceneBinding.lifecycleOwner = this

        qrSceneBinding = ContentHomeQrBinding.inflate(layoutInflater, rootBinding.homeRoot, false)
        qrSceneBinding.viewModel = viewModel
        qrSceneBinding.lifecycleOwner = this

        codeScanner =
            CodeScanner(requireContext(), qrSceneBinding.root.findViewById(R.id.qr_code_root))
        codeScanner?.setDecodeCallback { scanResult ->
            requireActivity().runOnUiThread {
                viewModel.setUrlFromQrScanner(scanResult.text)
                if (viewModel.qrCodeInvalid.value == true) {
                    codeScanner?.startPreview()
                    return@runOnUiThread
                }
                codeScanner?.stopPreview()
                launch()
            }
        }

        startScene = Scene(rootBinding.homeRoot, inputSceneBinding.root)
        qrScene = Scene(rootBinding.homeRoot, qrSceneBinding.root)

        TransitionManager.go(startScene)
        applyViewListeners()

        viewModel.devModeEnabled.observe(viewLifecycleOwner) {
            AppPreferences(requireActivity()).setDevMode(it)
        }
    }

    private fun applyViewListeners() {
        view?.findViewById<Button>(R.id.btn_launch_app)?.setOnClickListener {
            launch()
        }

        view?.findViewById<Button>(R.id.btn_scan_qr_code)?.setOnClickListener {
            if (!isInQrCodeScene) requestPermissionAndSwitchToQRScene() else gotToStartScene()
        }
    }

    private fun goToQrScene() {
        TransitionManager.go(qrScene, ChangeBounds())
        applyViewListeners()
        toggleQrCode(true)
    }

    private fun gotToStartScene() {
        TransitionManager.go(getNextScene(), ChangeBounds())
        applyViewListeners()
        toggleQrCode(false)
    }


    private fun requestPermissionAndSwitchToQRScene() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> goToQrScene()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            -> showCameraRequestRational()
            else -> requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    private fun showCameraRequestRational(): Boolean {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.permission_camera_qr_scanner))
            .setTitle(getString(R.string.permission_camera_qr_scanner_title))
            .setPositiveButton(getString(R.string.generic_continue)) { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }.setNegativeButton(getString(R.string.generic_deny)) { dialog, _ ->
                dialog.dismiss()
            }.show()
        return true
    }

    private fun toggleQrCode(enabled: Boolean) {
        if (enabled) {
            startCameraDelayed()
        } else {
            codeScanner?.releaseResources()
        }
    }

    private fun getNextScene(): Scene {
        return if (Scene.getCurrentScene(rootBinding.homeRoot) == startScene) qrScene else startScene
    }

    private fun startCameraDelayed() {
        Handler(Looper.getMainLooper()).postDelayed({
            codeScanner?.startPreview()
        }, 300)
    }

    private fun launch() {
        findNavController().navigate(
            HomeFragmentDirections.actionStartAppFragmentToMendixProjectLoaderFragment(
                getString(R.string.react_native_component_name),
                activity?.intent?.extras,
                MendixApp(
                    viewModel.appUrl.value!!,
                    getWarningFilterValue(requireActivity()),
                    viewModel.devModeEnabled.value ?: false,
                    true
                ),
                viewModel.clearData.value!!,
                true,
            )
        )
    }
}
