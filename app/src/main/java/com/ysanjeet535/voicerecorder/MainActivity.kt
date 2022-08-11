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
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ysanjeet535.voicerecorder.services.AudioService
import com.ysanjeet535.voicerecorder.ui.theme.VoiceRecorderTheme
import com.ysanjeet535.voicerecorder.ui.theme.blueDark
import com.ysanjeet535.voicerecorder.ui.theme.blueLight
import com.ysanjeet535.voicerecorder.ui.theme.greyLight
import com.ysanjeet535.voicerecorder.viewModels.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.nikhilchaudhari.library.neumorphic
import me.nikhilchaudhari.library.shapes.Pot
import me.nikhilchaudhari.library.shapes.Punched


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
                    modifier = Modifier
                        .fillMaxSize(),
                    color = greyLight
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

    Box(
        modifier = Modifier
            .height(80.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(32.dp)
            .neumorphic(
                neuShape =
                Pot.Rounded(radius = 64.dp),
                lightShadowColor = blueLight,
                darkShadowColor = blueDark
            )
    ) {
        Column(
            modifier = Modifier
                .padding(64.dp)
                .neumorphic(
                    neuShape =
                    Punched.Rounded(radius = 32.dp)
                )
        ) {

            Box(modifier = Modifier.padding(32.dp)) {
                Text(text = "Time ${time?.convertSecondsToHMmSs()}", color = Color.Black)
            }

            Row(modifier = Modifier.padding(32.dp)) {
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

                Button(onClick = {
                    viewModel.pauseTimer()
                }) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {
//        CircularTimerView()
    }
}

fun Long.convertSecondsToHMmSs(): String {
    val s = this % 60
    val m = this / 60 % 60
    val h = this / (60 * 60) % 24
    return String.format("%d:%02d:%02d", h, m, s)
}