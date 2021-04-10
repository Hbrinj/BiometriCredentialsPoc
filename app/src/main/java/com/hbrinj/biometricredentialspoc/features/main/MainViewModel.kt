package com.hbrinj.biometricredentialspoc.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hbrinj.biometricredentialspoc.core.architecture.Event
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {
    private val _encryptDataEvent = MutableLiveData<Event>()
    val encryptDataEvent: LiveData<Event> = _encryptDataEvent

    private val _decryptDataEvent = MutableLiveData<Event>()
    val decryptDataEvent: LiveData<Event> = _decryptDataEvent

    fun encrypt() {
        _encryptDataEvent.postValue(Event())
    }

    fun decrypt() {
        _decryptDataEvent.postValue(Event())
    }
}