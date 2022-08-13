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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    style = MaterialTheme.typography.h1,
                    color = Color.Black,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
            RecordControlButtons(viewModel)
        }
    }
}

@Composable
fun RecordControlButtons(viewModel: TimerViewModel) {

    var isPressedStop by remember {
        mutableStateOf(false)
    }

    var isPressedRecord by remember {
        mutableStateOf(false)
    }


    var isPressedTogglePause by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(
                RoundedCornerShape(corner = CornerSize(8.dp))
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        EliteButtons(iconId = R.drawable.ic_stop, label = "Stop", isPressed = isPressedStop) {
            //stop function
            viewModel.stopTimer()
            isPressedStop = !isPressedStop
            //switch other buttons
            if (isPressedRecord) {
                isPressedRecord = !isPressedRecord
            }
            if (isPressedTogglePause) {
                isPressedTogglePause = !isPressedTogglePause
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        EliteButtons(
            iconId = R.drawable.ic_record,
            iconTint = Color.Red,
            label = "Record",
            isPressed = isPressedRecord
        ) {
            if (isPressedRecord) {
                return@EliteButtons
            }
            //start record function
            viewModel.startTimer()

            isPressedRecord = !isPressedRecord
            //switch other buttons
            if (isPressedStop) {
                isPressedStop = !isPressedStop
            }
            if (isPressedTogglePause) {
                isPressedTogglePause = !isPressedTogglePause
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        EliteButtons(
            iconId = if (!isPressedTogglePause) R.drawable.ic_pause else R.drawable.ic_play,
            label = if (!isPressedTogglePause) "Pause Recording" else "Resume Recording",
            isPressed = isPressedTogglePause
        ) {
            isPressedTogglePause = !isPressedTogglePause
            //switch other buttons
            if (isPressedStop) {
                isPressedStop = !isPressedStop
            }

            if (isPressedTogglePause) {
                viewModel.toggleTimer(true)
            } else {
                viewModel.toggleTimer(false)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {
        EliteButtons(isPressed = true) {}
    }
}