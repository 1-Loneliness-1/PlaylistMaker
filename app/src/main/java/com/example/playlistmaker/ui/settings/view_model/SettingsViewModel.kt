package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.DarkThemeState
import com.example.playlistmaker.domain.settings.model.IntentType

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private val darkThemeStateLiveData = MutableLiveData<DarkThemeState>()

    fun getDarkThemeStateLiveData() : LiveData<DarkThemeState> = darkThemeStateLiveData

    fun checkDarkThemeState() {
        darkThemeStateLiveData.postValue(DarkThemeState(settingsInteractor.getDarkThemeStateFromSharPref()))
    }

    fun createIntent(intentType: IntentType) {
        when (intentType) {
            is IntentType.ShareAppType -> {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setType("text/plain")
                    .putExtra(
                        Intent.EXTRA_TEXT,
                        application.getString(R.string.yp_android_course_link)
                    )
                application.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        application.getString(R.string.share_apk_text)
                    )
                )
            }

            is IntentType.WriteToSupportType -> {
                val writeToSupportIntent = Intent(Intent.ACTION_SEND)
                writeToSupportIntent.setType("message/rfc822")
                    .putExtra(Intent.EXTRA_EMAIL, arrayOf(application.getString(R.string.my_email)))
                    .putExtra(
                        Intent.EXTRA_SUBJECT,
                        application.getString(R.string.message_for_android_developers)
                    )
                    .putExtra(
                        Intent.EXTRA_TEXT,
                        application.getString(R.string.acknowledge_for_android_dev)
                    )
                application.startActivity(
                    Intent.createChooser(
                        writeToSupportIntent,
                        application.getString(R.string.write_to_support)
                    )
                )
            }

            is IntentType.ReadUserAgreementType -> {
                val userAgreementIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(application.getString(R.string.yp_offertory_link))
                )
                application.startActivity(userAgreementIntent)
            }
        }
    }
}