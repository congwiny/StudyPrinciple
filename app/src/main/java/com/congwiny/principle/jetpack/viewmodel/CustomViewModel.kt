package com.congwiny.principle.jetpack.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomViewModel : ViewModel() {
    val data = MutableLiveData<Int?>(null)
}