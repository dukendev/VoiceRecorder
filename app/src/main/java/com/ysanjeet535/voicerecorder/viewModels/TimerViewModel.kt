package com.ysanjeet535.voicerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {

    private val _timerValue = MutableLiveData(0L)
    val timerValue: LiveData<Long> get() = _timerValue

    private var startTime = 0L

    var job: Job? = null

    fun startTimer() {
        if (job?.isActive == true) {
            return
        }
        job = viewModelScope.launch {
            while (true) {
                delay(1000)
                startTime += 1
                _timerValue.postValue(startTime)
            }
        }
        job!!.start()
    }

    fun stopTimer() {
        job?.cancel()
        startTime = 0
        _timerValue.postValue(0)
        if (job?.isCancelled == true) {
            job = null
        }
    }

    fun toggleTimer(isPaused: Boolean) {
        if (isPaused) {
            job?.cancel()
            _timerValue.postValue(startTime)
        } else {
            job?.start()
            startTimer()
            _timerValue.postValue(startTime)
        }
    }
}