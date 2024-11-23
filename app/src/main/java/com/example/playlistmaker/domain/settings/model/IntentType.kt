package com.example.playlistmaker.domain.settings.model

sealed class IntentType {

    data object ShareAppType : IntentType()

    data object WriteToSupportType : IntentType()

    data object ReadUserAgreementType : IntentType()

}