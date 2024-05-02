package com.example.gofit.login
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class ForgotPasswordViewModel : ViewModel() {


    private val _email =MutableLiveData<String>()
    val email: LiveData<String> = _email


    fun onForgotPasswordChanged(email: String) {
        _email.value = email

    }
}