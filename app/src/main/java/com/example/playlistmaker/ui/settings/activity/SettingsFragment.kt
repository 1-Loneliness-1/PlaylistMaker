package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.settings.model.IntentType
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val viewModel: SettingsViewModel by viewModel()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shareAppButton = binding.tvShareApp
        val writeToSupportButton = binding.tvSupport
        val userAgreementButton = binding.tvUserAgreement
        val themeSwitcher = binding.sNightTheme

        viewModel.getDarkThemeStateLiveData().observe(viewLifecycleOwner) { darkThemeState ->
            themeSwitcher.isChecked =
                darkThemeState.isDarkThemeOn
                    ?: (requireActivity().applicationContext as App).getDarkModeState()
        }

        viewModel.checkDarkThemeState()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (requireActivity().applicationContext as App).switchTheme(checked)
        }

        shareAppButton.setOnClickListener {
            viewModel.createIntent(IntentType.ShareAppType)
        }

        writeToSupportButton.setOnClickListener {
            viewModel.createIntent(IntentType.WriteToSupportType)
        }

        userAgreementButton.setOnClickListener {
            viewModel.createIntent(IntentType.ReadUserAgreementType)
        }
    }

}