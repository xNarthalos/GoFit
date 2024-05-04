package com.example.gofit.login
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class ForgotPasswordViewModel : ViewModel() {


    private val _email =MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _ForgotButtonEnable=MutableLiveData<Boolean>()
    val forgotButtonEnable: LiveData<Boolean> = _ForgotButtonEnable


    fun onForgotPasswordChanged(email: String) {
        _email.value = email
        _ForgotButtonEnable.value=isValidEmail(email)

    }

    private fun isValidEmail(email: String): Boolean= Patterns.EMAIL_ADDRESS.matcher(email).matches()
}