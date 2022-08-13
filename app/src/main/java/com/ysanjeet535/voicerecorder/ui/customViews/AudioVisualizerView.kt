package com.ysanjeet535.voicerecorder.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ysanjeet535.voicerecorder.R
import com.ysanjeet535.voicerecorder.databinding.AudioVisualizerViewBinding

class AudioVisualizerView : View {

    private lateinit var binding: AudioVisualizerViewBinding

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AudioVisualizerViewBinding.inflate(inflater, parent as ViewGroup?, true)
    }

    fun setColor() {
        binding.visualizer.setColor(ContextCompat.getColor(context, R.color.blueLight));
    }

    fun setDensityValue(value: Float = 72f) {
        binding.visualizer.setDensity(value)
    }

    fun setPlayerId(id: Int?) {
        if (id != null) {
            binding.visualizer.setPlayer(id)
        }
    }
}