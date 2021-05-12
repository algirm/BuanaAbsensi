package com.papb.buanaabsensi.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    protected val _errorEvent = Channel<String>()
    val errorEvent = _errorEvent.receiveAsFlow()

    fun handleError(message: String?) = viewModelScope.launch {
        _errorEvent.send(message ?: "Unexpected Error")
    }

}