package com.mendix.developerapp.welcomescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.mendix.developerapp.BaseFragment

private const val VIEW_ID = "view_id"

class WelcomeContentFragment : BaseFragment() {
    private var viewId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewId = it.getInt(VIEW_ID)
        } ?: throw Exception("VIEW_ID needs to be provided")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(viewId, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(@LayoutRes layoutId: Int) =
                WelcomeContentFragment().apply {
                    arguments = Bundle().apply {
                        putInt(VIEW_ID, layoutId)
                    }
                }
    }
}
