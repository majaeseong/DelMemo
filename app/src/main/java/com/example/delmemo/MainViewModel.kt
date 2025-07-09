package com.example.delmemo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val sharedTexts = mutableStateListOf<String>()

    fun addText(text: String) {
        sharedTexts.add(text)
    }
}