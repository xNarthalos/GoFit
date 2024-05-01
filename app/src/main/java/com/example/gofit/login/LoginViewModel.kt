package com.example.gofit.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel(){

  private val _email =MutableLiveData<String>()
    val email: LiveData<String> = _email

  private val _password =MutableLiveData<String>()
  val password: LiveData<String> = _password

  private val _passwordVisibility=MutableLiveData<Boolean>()
  val passwordVisibility: LiveData<Boolean> = _passwordVisibility

  private val _LoginEnable=MutableLiveData<Boolean>()
  val loginEnable: LiveData<Boolean> = _LoginEnable

  fun onLoginChanged(email :String ,password :String){
    _email.value=email
    _password.value=password
    _LoginEnable.value=isValidEmail(email)&& isValidPassword(password)



  }

  private fun isValidPassword(password: String): Boolean =password.length >= 6

  private fun isValidEmail(email: String): Boolean= Patterns.EMAIL_ADDRESS.matcher(email).matches()
  fun onLoginSelected() {

  }



  fun togglePasswordVisibility(passwordVisibility: Boolean) {
    _passwordVisibility.value=!passwordVisibility

  }


}



