package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.settings.model.DarkThemeState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shareAppButton = binding.tvShareApp
        val writeToSupportButton = binding.tvSupport
        val userAgreementButton = binding.tvUserAgreement
        val themeSwitcher = binding.sNightTheme

        viewModel.getDarkThemeStateLiveData().observe(viewLifecycleOwner) { darkThemeState ->
            when (darkThemeState) {
                is DarkThemeState.DarkTheme -> {
                    themeSwitcher.isChecked =
                        darkThemeState.isDarkThemeOn
                            ?: (requireActivity().applicationContext as App).getDarkModeState()
                }
            }
        }

        viewModel.checkDarkThemeState()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (requireActivity().applicationContext as App).switchTheme(checked)
        }

        shareAppButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.yp_android_course_link))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_apk_text)))
        }

        writeToSupportButton.setOnClickListener {
            val writeToSupportIntent = Intent(Intent.ACTION_SEND)
            writeToSupportIntent.setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)) )
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_for_android_developers))
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.acknowledge_for_android_dev))
            startActivity(Intent.createChooser(writeToSupportIntent, getString(R.string.write_to_support)))
        }

        userAgreementButton.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yp_offertory_link)))
            startActivity(userAgreementIntent)
        }
    }

}