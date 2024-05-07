package com.mendix.developerapp.sampelapps

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.mendix.developerapp.BaseFragment
import com.mendix.developerapp.R
import com.mendix.developerapp.databinding.ContentSampleAppsBinding
import com.mendix.developerapp.databinding.GridItemSampleAppsBinding
import com.mendix.mendixnative.react.MendixApp
import com.mendix.mendixnative.react.MxConfiguration

class SampleAppsFragment : BaseFragment() {
    private lateinit var binding: ContentSampleAppsBinding
    private val sampleAppsViewModel: SampleAppsViewModel by viewModels {
        return@viewModels object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SampleAppsViewModel(SampleAppsManager(requireContext(), requireActivity().filesDir.absolutePath)) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ContentSampleAppsBinding.inflate(layoutInflater)
        binding.initialized = sampleAppsViewModel.sampleAppsManager.initialized
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        binding.sampleAppsGrid.layoutManager = gridLayoutManager
        sampleAppsViewModel.sampleAppsManager.initialized.observe(viewLifecycleOwner) {
            if (it) {
                binding.sampleAppsGrid.adapter = SampleAppsAdapter(requireContext(), sampleAppsViewModel.sampleAppsManager, findNavController())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = MaterialColors.getColor(requireContext(), R.attr.colorPrimary, ResourcesCompat.getColor(requireActivity().resources, R.color.red1, activity?.theme))
    }
}

class SampleAppsAdapter(context: Context, private val sampleAppsManager: SampleAppsManager, private val navController: NavController) : RecyclerView.Adapter<SampleItemViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleItemViewHolder {
        val binding = GridItemSampleAppsBinding.inflate(layoutInflater, parent, false)
        return SampleItemViewHolder(binding) {
            sampleAppsManager.selectProject(it.id)
            navController.run {
                navigate(
                        SampleAppsFragmentDirections.actionNavGalleryToNavMendixSampleApp(
                                argComponentName = "App",
                                argLaunchOptions = null,
                                argMendixApp = MendixApp(it.runtimeUrl, MxConfiguration.WarningsFilter.none, showExtendedDevMenu = false, attachCustomDeveloperMenu = true),
                                argClearData = true,
                                argUseDeveloperSupport = false,
                        )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: SampleItemViewHolder, position: Int) =
            holder.bind(sampleAppsManager.getSampleAppInfo(position))

    override fun getItemCount(): Int = sampleAppsManager.appsCount

}

class SampleItemViewHolder(private val binding: GridItemSampleAppsBinding, private val onClickHandler: (app: SampleAppViewInfo) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    fun bind(app: SampleAppViewInfo) {
        binding.app = app
        Drawable.createFromPath(app.splashFilePath).let {
            binding.cardSampleAppImage.setImageDrawable(it)
        }
        binding.root.setOnClickListener {
            onClickHandler(app)
        }
        binding.executePendingBindings()
    }

}

class SampleAppsViewModel(val sampleAppsManager: SampleAppsManager) : ViewModel()
