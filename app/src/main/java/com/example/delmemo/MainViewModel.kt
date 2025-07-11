package com.example.delmemo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _textList = MutableStateFlow<List<String>>(emptyList())
    val textList: StateFlow<List<String>> = _textList

    fun addText(text: String) {
        _textList.value = _textList.value + text
    }
}