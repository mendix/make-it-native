package com.mendix.developerapp.helppage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mendix.developerapp.BaseFragment
import com.mendix.developerapp.databinding.FragmentHelpBinding
import com.mendix.developerapp.welcomescreen.WelcomeViewAdapter

class HelpFragment : BaseFragment() {

    private lateinit var binding: FragmentHelpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentHelpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contentWelcomePager.pagerWelcome.adapter = WelcomeViewAdapter(requireActivity())
        binding.buttonOpenHowTo.setOnClickListener {
            openWebpage("https://docs.mendix.com/howto")
        }

        binding.buttonOpenReferenceGuide.setOnClickListener {
            openWebpage("https://docs.mendix.com/refguide")
        }
    }

    private fun openWebpage(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
