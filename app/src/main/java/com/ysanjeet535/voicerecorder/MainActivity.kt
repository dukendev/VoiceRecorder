package com.ysanjeet535.voicerecorder

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.ysanjeet535.voicerecorder.services.ACTION_START_FOREGROUND_SERVICE
import com.ysanjeet535.voicerecorder.services.AudioService
import com.ysanjeet535.voicerecorder.ui.composables.EliteButtons
import com.ysanjeet535.voicerecorder.ui.theme.VoiceRecorderTheme
import com.ysanjeet535.voicerecorder.ui.theme.blueDark
import com.ysanjeet535.voicerecorder.ui.theme.lightWhite
import com.ysanjeet535.voicerecorder.utils.convertSecondsToHMmSs
import com.ysanjeet535.voicerecorder.viewModels.TimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.nikhilchaudhari.library.NeuInsets
import me.nikhilchaudhari.library.neumorphic
import me.nikhilchaudhari.library.shapes.CornerType
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
                    color = lightWhite
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
        val intent = Intent(this, AudioService::class.java)
        intent.action = ACTION_START_FOREGROUND_SERVICE
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        } else {
            startService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)

        }

    }

}

@Composable
fun CircularTimerView(viewModel: TimerViewModel) {

    val time by viewModel.timerValue.observeAsState()

    val angle = animateFloatAsState(
        targetValue = (time?.times(6)?.toFloat())?.rem(360) ?: 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .height(80.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .clip(RoundedCornerShape(64.dp))
            .background(lightWhite)
    ) {
        Column(
            modifier = Modifier
                .padding(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .neumorphic(
                    neuShape =
                    Punched.Rounded(radius = 32.dp)
                )
                .background(lightWhite),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(32.dp)
                    .clip(shape = RoundedCornerShape(96.dp))
                    .neumorphic(
                        neuShape = Pot(cornerType = CornerType.Rounded(radius = 320.dp)),
                        neuInsets = NeuInsets(8.dp, 8.dp),
                        elevation = 12.dp
                    )
                    .background(lightWhite)
            ) {

                Canvas(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp)
                        .align(Alignment.Center)
                ) {

                    drawArc(
                        color = blueDark,
                        startAngle = -90f,
                        sweepAngle = angle.value,
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )

                }

                Text(
                    text = "${time?.convertSecondsToHMmSs()}",
                    color = Color.Black,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

            }

            Row(
                modifier = Modifier.padding(32.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    viewModel.stopTimer()
                }, modifier = Modifier.width(40.dp)) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }

                Button(onClick = {
                    viewModel.startTimer()
                }, modifier = Modifier.width(40.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_record),
                        contentDescription = null,
                        tint = Color.Red
                    )
                }

                Button(
                    onClick = {
                        viewModel.pauseTimer()
                    },
                    modifier = Modifier.width(40.dp)
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null)
                }
            }

            EliteButtons(iconId = R.drawable.ic_record) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {
        EliteButtons {}
    }
}