package com.voice.android_native

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log

class LanguageDetailsChecker: BroadcastReceiver() {

    private var supportedLanguages: List<String>? = null

    private var languagePreference: String? = null

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d(TAG, " >> onReceive Called")
        val results = getResultExtras(true)

        if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)) {
            languagePreference = results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)
            Log.d(TAG, " >> Language Preferance : $languagePreference")
        }

        if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)) {
            supportedLanguages = results.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)
            Log.d(TAG, " >> Supported Language : $supportedLanguages")
        }
    }

    companion object {
        val TAG = LanguageDetailsChecker::class.java.simpleName
    }
}