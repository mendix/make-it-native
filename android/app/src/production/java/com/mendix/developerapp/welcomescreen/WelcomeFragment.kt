package com.mendix.developerapp.welcomescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mendix.developerapp.BaseFragment
import com.mendix.developerapp.R
import com.mendix.developerapp.databinding.FragmentWelcomeBinding
import com.mendix.mendixnative.fragment.BackButtonHandler

class WelcomeFragment : BaseFragment(), BackButtonHandler {
    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var adapter: WelcomeViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = WelcomeViewAdapter(requireActivity())
        binding.contentWelcomePager.pagerWelcome.adapter = adapter
        binding.contentWelcomePager.pagerWelcome.registerOnPageChangeCallback(PageChangeCallback())

        binding.btnWelcomeSkip.setOnClickListener {
            skip()
        }

        binding.btnWelcomeNext.setOnClickListener {
            showNext()
        }
    }

    override fun onBackPressed(): Boolean {
        return if (binding.contentWelcomePager.pagerWelcome.currentItem == 0)
            false
        else {
            showPrevious()
            true
        }
    }

    private fun showNext() {
        binding.contentWelcomePager.pagerWelcome.currentItem += if (binding.contentWelcomePager.pagerWelcome.currentItem + 1 < adapter.itemCount) 1 else 0
    }

    private fun showPrevious() {
        binding.contentWelcomePager.pagerWelcome.currentItem -= if (binding.contentWelcomePager.pagerWelcome.currentItem > 0) 1 else 0
    }

    private fun skip() {
        navigateToHome()
    }

    private fun navigateToHome() {
        findNavController().navigate(
            R.id.nav_home,
            null,
            NavOptions.Builder().setPopUpTo(R.id.nav_welcome_screen, true).build()
        )
    }

    private inner class PageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (position == 0) {
                binding.btnWelcomeSkip.text = getString(R.string.btn_skip)
                binding.btnWelcomeSkip.setOnClickListener {
                    skip()
                }
            } else {
                binding.btnWelcomeSkip.text = getString(R.string.btn_previous)
                binding.btnWelcomeSkip.setOnClickListener {
                    showPrevious()
                }
            }

            if (position == adapter.itemCount - 1) {
                binding.btnWelcomeNext.setOnClickListener {
                    navigateToHome()
                }
            } else {
                binding.btnWelcomeNext.setOnClickListener {
                    showNext()
                }
            }
        }
    }
}

class WelcomeViewAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return WelcomeContentFragment.newInstance(
            when (position) {
                1 -> R.layout.fragment_welcome_qr_code
                2 -> R.layout.fragment_welcome_input
                else -> R.layout.fragment_welcome_get_inspired
            }
        )
    }
}
