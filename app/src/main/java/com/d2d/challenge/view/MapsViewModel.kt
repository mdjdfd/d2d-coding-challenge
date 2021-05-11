package com.d2d.challenge.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d2d.challenge.data.entity.Payload
import com.d2d.challenge.data.repository.MapsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapsViewModel @Inject constructor(private val mapsRepository: MapsRepository) : ViewModel() {

    private val _payload = MutableLiveData<Payload>()

    val payload: LiveData<Payload>
        get() = _payload


    init {
        subscribeToSocketEvents()
    }

     private fun subscribeToSocketEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mapsRepository.getSocket().consumeEach {
                    if (it.exception == null) {
                        _payload.postValue(it.payload)
                    } else {
                        onSocketError(it.exception)
                    }
                }
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }



    private fun onSocketError(ex: Throwable) {
        Log.i(MapsViewModel::class.java.simpleName, "_log onSocketError :  ${ex.message}")
    }

    override fun onCleared() {
        super.onCleared()
        mapsRepository.disconnectSocket()
    }


}