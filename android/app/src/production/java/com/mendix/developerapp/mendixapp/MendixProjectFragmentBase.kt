package com.mendix.developerapp.mendixapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import com.mendix.developerapp.MendixBaseFragment
import com.mendix.developerapp.loading.ProjectLoaderViewModel
import com.mendix.developerapp.utilities.GlobalTouchEventListener
import com.zoontek.rnbootsplash.RNBootSplash
import com.mendix.developerapp.R

open class MendixProjectFragmentBase : MendixBaseFragment(), GlobalTouchEventListener {
    protected val viewModel: ProjectLoaderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mendix_project_traditional, container, false)
        val reactNativeContainer = rootView.findViewById<FrameLayout>(R.id.react_native_container)
        val reactRootView = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.status.observe(viewLifecycleOwner) {
            if (it === ProjectLoaderViewModel.STATUS_SUCCESS) {
                RNBootSplash.init(requireActivity(), R.style.BootTheme)
            }else{
                RNBootSplash.hide(requireActivity());
            }
        }

        reactNativeContainer.addView(reactRootView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        return rootView
    }
}
