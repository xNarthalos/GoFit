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
    private val auth: FirebaseAuth = Firebase.auth

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _passwordVisibility = MutableLiveData<Boolean>()
    val passwordVisibility: LiveData<Boolean> = _passwordVisibility

    private val _LoginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _LoginEnable

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _hasFocus = MutableLiveData<Boolean>()
    val hasFocus: LiveData<Boolean> = _hasFocus


    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _LoginEnable.value = isValidEmail(email) && isValidPassword(password)


    }

    fun isValidPassword(password: String): Boolean = password.length >= 6

    fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun onLoginSelected() {

    }


    fun togglePasswordVisibility(passwordVisibility: Boolean) {
        _passwordVisibility.value = !passwordVisibility

    }

    fun signInWithEmailAndPassword(email: String, password: String, navController: NavController, onLoginSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onLoginSuccess()
                    navController.navigate("Menu") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                } else {
                    _errorMessage.value =
                        "Por favor, comprueba que el email y la contraseña son los correctos."
                }
            }
    }

    fun signInWithGoogle(credential: AuthCredential, home: () -> Unit, onLoginSuccess: () -> Unit
    ) = viewModelScope.launch  {
        try {
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
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


    private fun guardarDatosUsuario(uid: String, email: String?, displayName: String?) {
        val db = FirebaseFirestore.getInstance()
        val datosUsuario = hashMapOf(
            "email" to email,
            "usuario" to displayName,
        )
        db.collection("usuarios").document(uid).update(datosUsuario as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("GoFit", "Datos del usuario guardados correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("GoFit", "Error al guardar los datos del usuario", e)
            }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun setFocus(hasFocus: Boolean) {
        _hasFocus.value = hasFocus
    }

}





