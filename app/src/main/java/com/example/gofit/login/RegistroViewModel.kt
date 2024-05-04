package com.example.gofit.login

import android.util.Patterns
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

    private val _registroEnable=MutableLiveData<Boolean>()
    val registroEnable: LiveData<Boolean> = _registroEnable
    private val _passwordVisibility=MutableLiveData<Boolean>()
    val passwordVisibility: LiveData<Boolean> = _passwordVisibility
    private val _repeatPasswordVisibility=MutableLiveData<Boolean>()
    val repeatPasswordVisibility: LiveData<Boolean> = _repeatPasswordVisibility



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



        // Comprobar si todos los campos tienen valores no nulos
        val allFieldsFilled = email.isNotBlank() && password.isNotBlank() && repetirPassword.isNotBlank() && userName.isNotBlank()

        // Comprobar si el correo electrónico es válido
        val validEmail = isValidEmail(email)

        // Comprobar si la contraseña tiene al menos 6 caracteres
        val validPassword = isValidPassword(password)

        // Comprobar si las dos contraseñas coinciden
        val passwordsMatch = password == repetirPassword



        // Habilitar el botón de registro solo si todas las condiciones se cumplen
        _registroEnable.value = allFieldsFilled && validEmail && validPassword && passwordsMatch
    }

    fun setFechaDeNacimiento(fecha: Calendar) {
        _fechaDeNacimiento.value = fecha
    }

    private fun isValidEmail(email: String): Boolean= Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPassword(password: String): Boolean =password.length >= 6

    fun togglePasswordVisibility(passwordVisibility: Boolean) {
        _passwordVisibility.value=!passwordVisibility

    }
    fun toggleRepeatPasswordVisibility(passwordVisibility: Boolean) {

        _repeatPasswordVisibility.value=!passwordVisibility

    }
}

