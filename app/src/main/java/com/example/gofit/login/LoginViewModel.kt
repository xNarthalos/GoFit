package com.example.gofit.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // Obtenemos la instancia de firebaseauth para manejar la autenticación desde firebase
    private val auth: FirebaseAuth = Firebase.auth
    // MutableLiveData para almacenar el email ingresado por el usuario
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    // MutableLiveData para almacenar la contraseña ingresada por el usuario
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    // MutableLiveData para controlar la visibilidad de la contraseña
    private val _passwordVisibility = MutableLiveData<Boolean>()
    val passwordVisibility: LiveData<Boolean> = _passwordVisibility
    // MutableLiveData para habilitar o deshabilitar el botón de inicio de sesión
    private val _LoginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _LoginEnable
    // MutableLiveData para almacenar mensajes de error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    // MutableLiveData para controlar el enfoque de los campos de entrada
    private val _hasFocus = MutableLiveData<Boolean>()
    val hasFocus: LiveData<Boolean> = _hasFocus

    // Función que actualiza el valor de los campos del login que introduce el usuario
    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _LoginEnable.value = isValidEmail(email) && isValidPassword(password)


    }
    // Función para verificar si la contraseña es válida
    fun isValidPassword(password: String): Boolean = password.length >= 6
    // Función para verificar si el email es válido
    fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    // Función vacía para manejar la selección de inicio de sesión
    fun onLoginSelected() {

    }

    // Función para alternar la visibilidad de la contraseña
    fun togglePasswordVisibility(passwordVisibility: Boolean) {
        _passwordVisibility.value = !passwordVisibility

    }
    // Función para iniciar sesión con email y contraseña
    fun signInWithEmailAndPassword(email: String, password: String, navController: NavController, onLoginSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
              //Si el logeo ha sido un exito
            if (task.isSuccessful) {
                    onLoginSuccess()
                    // Navega a la pantalla de menú y elimina la pantalla de inicio de sesión de la pila de navegación
                    navController.navigate("Menu") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                //si no se llena el mensaje de error para mostrarlo
                } else {
                    _errorMessage.value =
                        "Por favor, comprueba que el email y la contraseña son los correctos."
                }
            }
    }
    // Función para iniciar sesión con Google
    fun signInWithGoogle(credential: AuthCredential, home: () -> Unit, onLoginSuccess: () -> Unit
    ) = viewModelScope.launch  {
        try {
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Guarda los datos del usuario al registrase con google
                        guardarDatosUsuario(it.uid, it.email, it.displayName)
                    }
                    onLoginSuccess()

                    Log.d("GoFit", "Logeado con google")
                    home()
                }
            }
                .addOnFailureListener {
                    Log.d("GoFit", "Fallo al logear con google")
                }
        } catch (ex: Exception) {
            Log.d("GoFit", "Excepcion al logear con Google " + ex.localizedMessage)
        }
    }

    // Función para guardar los datos del usuario en Firestore
    private fun guardarDatosUsuario(uid: String, email: String?, displayName: String?) {
        val db = FirebaseFirestore.getInstance()
        val datosUsuario = hashMapOf(
            "email" to email,
            "usuario" to displayName,
        )
        // Actualiza los datos del usuario en Firestore
        db.collection("usuarios").document(uid).update(datosUsuario as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("GoFit", "Datos del usuario guardados correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("GoFit", "Error al guardar los datos del usuario", e)
            }
    }

    // Función para limpiar el mensaje de error
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    // Función para establecer el enfoque de los campos de entrada
    fun setFocus(hasFocus: Boolean) {
        _hasFocus.value = hasFocus
    }

}





