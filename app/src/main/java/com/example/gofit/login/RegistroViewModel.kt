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

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError


    private val _hasFocus = MutableLiveData<Boolean>()
    val hasFocus: LiveData<Boolean> = _hasFocus




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


        val validEmail = isValidEmail(email)
        if (!validEmail) {
            _emailError.value = "Email no válido"
        } else {
            _emailError.value = null // Reiniciar el mensaje de error si el email es válido
        }

        // Comprobar si todos los campos tienen valores no nulos
        val allFieldsFilled = email.isNotBlank() && password.isNotBlank() && repetirPassword.isNotBlank() && userName.isNotBlank()

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

     fun isValidEmail(email: String): Boolean= Patterns.EMAIL_ADDRESS.matcher(email).matches()
     fun isValidPassword(password: String): Boolean =password.length >= 6

    fun togglePasswordVisibility(passwordVisibility: Boolean) {
        _passwordVisibility.value=!passwordVisibility

    }
    fun toggleRepeatPasswordVisibility(passwordVisibility: Boolean) {

        _repeatPasswordVisibility.value=!passwordVisibility

    }

    fun setFocus(hasFocus: Boolean) {
        _hasFocus.value = hasFocus
    }

}

