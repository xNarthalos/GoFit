package com.example.gofit.login
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordViewModel : ViewModel() {
    private  var firebaseAuth: FirebaseAuth


    private val _email =MutableLiveData<String>()
    val email: LiveData<String> = _email

    init {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private val _ForgotButtonEnable=MutableLiveData<Boolean>()
    val forgotButtonEnable: LiveData<Boolean> = _ForgotButtonEnable


    fun onForgotPasswordChanged(email: String) {
        _email.value = email
        _ForgotButtonEnable.value=isValidEmail(email)

    }
    fun resetPassword(email: String){
        firebaseAuth.sendPasswordResetEmail(email)
    }

    private fun isValidEmail(email: String): Boolean= Patterns.EMAIL_ADDRESS.matcher(email).matches()
}