package com.example.gofit.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class RegistroViewModel : ViewModel() {

    private val _email =MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password =MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _repetirPassword = MutableLiveData<String>()
    val repetirPassword: LiveData<String> = _repetirPassword


    private val _userName =MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _fechaDeNacimiento = MutableLiveData<Calendar>()
    val fechaDeNacimiento: LiveData<Calendar> = _fechaDeNacimiento

    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18)
        _fechaDeNacimiento.value = calendar
    }


    fun onRegistroChanged(email: String, password: String, userName: String, repetirPassword: String) {
        _email.value = email
        _password.value = password
        _repetirPassword.value = repetirPassword
        _userName.value = userName
    }

    fun setFechaDeNacimiento(fecha: Calendar) {
        _fechaDeNacimiento.value = fecha
    }
}