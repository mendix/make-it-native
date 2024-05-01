package com.mendix.developerapp.home

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import com.mendix.developerapp.BaseFragment
import com.mendix.developerapp.R
import com.mendix.developerapp.ui.theme.MyApplicationTheme
import com.mendix.developerapp.utilities.getWarningFilterValue
import com.mendix.mendixnative.react.MendixApp

class HomeFragment : BaseFragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var composeView: ComposeView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostNotificationPermission()
        };
        composeView.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel)
            }
        }
    }

    private fun setup(){
        viewModel.qrCodeScannerButtonOnClick = this::requestPermissionAndSwitchToQRScene
        viewModel.launchApp = this::launch

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    showCameraRequestRational()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = MaterialColors.getColor(
            requireContext(),
            R.attr.colorPrimary,
            ResourcesCompat.getColor(requireActivity().resources, R.color.red1, activity?.theme)
        )
    }

    private fun requestPermissionAndSwitchToQRScene() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> viewModel.toggleQRCodeDialog()
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPostNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            -> showPostNotificationRequestRational()
            else -> requestPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPostNotificationRequestRational(): Boolean {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.permission_post_notification))
            .setTitle(getString(R.string.permission_post_notification_title))
            .setPositiveButton(getString(R.string.generic_continue)) { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }.setNegativeButton(getString(R.string.generic_deny)) { dialog, _ ->
                dialog.dismiss()
            }.show()
        return true
    }

    private fun launch() {
        findNavController().navigate(
            HomeFragmentDirections.actionStartAppFragmentToMendixProjectLoaderFragment(
                getString(R.string.react_native_component_name),
                activity?.intent?.extras,
                MendixApp(
                    viewModel.getAppUrl(),
                    getWarningFilterValue(requireActivity()),
                    viewModel.getDevModeEnabled(),
                    true
                ),
                viewModel.getClearData(),
                true,
            )
        )
    }
}
