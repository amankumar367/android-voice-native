package com.voice.android_native

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.voice.android_native.extention.showToast
import com.voice.android_native.speechHelper.SpeechCallback
import com.voice.android_native.speechHelper.SpeechRecoginizerHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mSpeechRecoginizerHelper : SpeechRecoginizerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initVoiceRecognizationWithoutGoogleDialog()
        onClick()

    }

    private fun onClick() {
        floating_action_btn.setOnClickListener {
            if(hasAudioRecordingPermission()){
//                startVoiceRecoginizationWithGoogleDialog()
                mSpeechRecoginizerHelper.startSpeech()
            } else {
                requestAudioRecordingPermission()
            }
        }

    }

    private fun initVoiceRecognizationWithoutGoogleDialog() {
        mSpeechRecoginizerHelper = SpeechRecoginizerHelper(this, object : SpeechCallback {
                override fun onSpeechStart() {
                    floating_action_btn.setImageResource(R.drawable.ic_mic)
                    tv_stt.text = getString(R.string.listening)
                }

                override fun onSpeechStop() {
                    floating_action_btn.setImageResource(R.drawable.ic_mic_none)
                }

                override fun onSpeechResult(result: ArrayList<String>?) {
                    Log.d(TAG, "Speech Result : ${result.toString()}")
                    floating_action_btn.setImageResource(R.drawable.ic_mic_none)
                    tv_stt.text = result!![0]
                }

                override fun onSpeechError(message: String) {
                    Log.d(TAG, "Speech Error : $message")
                    floating_action_btn.setImageResource(R.drawable.ic_mic_off)
                    tv_stt.text = message

                }
            })
    }

    private fun startVoiceRecoginizationWithGoogleDialog() {
        floating_action_btn.setImageResource(R.drawable.ic_mic)
        showToast("Speech Recoginization Started")

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, DEFAULT_SPEECH_PROMPT)
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e : ActivityNotFoundException) {
            showToast("Sorry! Your device doesn't support speech input")
        }

    }

    private fun hasAudioRecordingPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioRecordingPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_CODE_AUDIO_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        if(requestCode == REQUEST_CODE_AUDIO_PERMISSION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startVoiceRecoginizationWithGoogleDialog()
                mSpeechRecoginizerHelper.startSpeech()
            } else{
                floating_action_btn.setImageResource(R.drawable.ic_mic_off)

                if(isPermanentlyDenied()){
                    showPermissionDialog()
                } else {
                    requestAudioRecordingPermission()
                }
            }
        }
    }

    private fun isPermanentlyDenied(): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(TITLE)
            .setMessage(MESSAGE)
            .setPositiveButton(POSITIVE_BUTTON_TEXT) { _, _ ->
                goToAppSetting()
            }
            .setNegativeButton(NEGATIVE_BUTTON_TEXT) { _, _ ->
                showToast("Cancelled")
                finish()
            }
            .create()
            .show()
    }

    private fun goToAppSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_CODE_SPEECH_INPUT -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val recognizedSTT =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    tv_stt.text = recognizedSTT!![0]
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        mSpeechRecoginizerHelper.stopSpeech()
    }

    override fun onRestart() {
        super.onRestart()
        if(hasAudioRecordingPermission()){
//            startVoiceRecoginizationWithGoogleDialog()
            mSpeechRecoginizerHelper.startSpeech()
        }
    }

    companion object{
        private var TAG = MainActivity::class.java.simpleName
        const val REQUEST_CODE_AUDIO_PERMISSION = 100
        const val REQUEST_CODE_SPEECH_INPUT = 101
        const val TITLE = "Required Permission"
        const val MESSAGE = "Please grant Microphone permission to record audio"
        const val POSITIVE_BUTTON_TEXT = "Go To Setting"
        const val NEGATIVE_BUTTON_TEXT = "Cancel"
        const val DEFAULT_SPEECH_PROMPT = "Say something"
    }
}
