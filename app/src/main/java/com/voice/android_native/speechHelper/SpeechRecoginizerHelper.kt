package com.voice.android_native.speechHelper

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*

class SpeechRecoginizerHelper(
    context: Context,
    speechCallback: SpeechCallback
) : RecognitionListener {

    private var mSpeechRecognizer: SpeechRecognizer? = null

    private lateinit var mSpeechRecognizerIntent : Intent

    private var speechCallback : SpeechCallback? = null

    init {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        this.speechCallback = speechCallback
        createSpeechRecognizerIntent()
        setSpeechRecognizerListner()
    }

    private fun createSpeechRecognizerIntent() {
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, PREFER_OFFLINE)
        }
    }

    private fun setSpeechRecognizerListner() {
        mSpeechRecognizer?.setRecognitionListener(this)
    }

    fun startSpeech() {
        mSpeechRecognizer?.startListening(mSpeechRecognizerIntent)
    }

    fun stopSpeech() {
        mSpeechRecognizer?.stopListening()
        speechCallback!!.onSpeechStop()
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.d(TAG, "onReadyForSpeech ---> Bundle : $p0")
    }

    override fun onRmsChanged(p0: Float) {
        Log.d(TAG, "onRmsChanged ---> Float : $p0")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.d(TAG, "onBufferReceived ---> ByteArray : $p0")
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.d(TAG, "onPartialResults ---> Bundle : $p0")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.d(TAG, "onEvent ---> Type of the occurred event : $p0 || Bundle : $p1")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech ---> No parameter")
        speechCallback!!.onSpeechStart()
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech ---> No parameter")
    }

    override fun onError(p0: Int) {
        Log.d(TAG, "onError ---> Error Code : $p0")
        speechCallback!!.onSpeechError("Got Some error while listning\nwith Error Code : $p0")
    }

    override fun onResults(p0: Bundle?) {
        Log.d(TAG, "onResults --> $p0")
        speechCallback!!.onSpeechResult(p0!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION))
    }

    companion object{
        private var TAG = SpeechRecoginizerHelper::class.java.simpleName
        private const val PREFER_OFFLINE = false
    }

}