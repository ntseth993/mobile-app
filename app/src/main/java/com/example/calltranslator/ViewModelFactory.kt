package com.example.calltranslator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

class LinguaPhoneViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LinguaPhoneViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LinguaPhoneViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
