package com.ysanjeet535.voicerecorder

import android.content.ComponentName
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ysanjeet535.voicerecorder.services.AudioService
import com.ysanjeet535.voicerecorder.ui.theme.VoiceRecorderTheme
import com.ysanjeet535.voicerecorder.viewModels.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var mService: AudioService
    private var mBound: Boolean = false

    //audio recorder service related
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.AudioRecorderServiceBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceRecorderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel: TimerViewModel by viewModels()
                    CircularTimerView(viewModel)
                }
            }
        }
    }

    private fun startRecorded() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(mService.getRecordedFilePath())
            prepare()
            start()
        }
    }

    private fun stopAudioService() {
        mService.stopRecorderService()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
//        val intent = Intent(this, AudioService::class.java)
//        intent.action = ACTION_START_FOREGROUND_SERVICE
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
//        } else {
//            startService(intent)
//            bindService(intent, connection, Context.BIND_AUTO_CREATE)
//
//        }

    }

}

@Composable
fun CircularTimerView(viewModel: TimerViewModel) {

    val time by viewModel.timerValue.observeAsState()



    Row {
        Button(onClick = {
            viewModel.stopTimer()
        }) {
            Icon(Icons.Default.Close, contentDescription = null)
        }

        Button(onClick = {
            viewModel.startTimer()
        }) {
            Icon(Icons.Default.Send, contentDescription = null)
        }

        Text(text = "Time $time")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {
//        CircularTimerView()
    }
}