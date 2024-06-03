package forgotPassword
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordViewModel : ViewModel() {
    // Obtenemos la intancia de firebaseauth para manejar la autenticación con firebase
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // MutableLiveData para almacenar el email ingresado por el usuario
    private val _email =MutableLiveData<String>()
    // LiveData para mostrar el email a las vistas
    val email: LiveData<String> = _email
    // MutableLiveData para habilitar o deshabilitar el botón de restablecer contraseña
    private val _ForgotButtonEnable=MutableLiveData<Boolean>()
    // LiveData para mostrar el estado del botón a las vistas
    val forgotButtonEnable: LiveData<Boolean> = _ForgotButtonEnable


    // Función que actualiza el valor del email ingresado por el usuario y comprueba que es valido
    fun onForgotPasswordChanged(email: String) {
        _email.value = email
        _ForgotButtonEnable.value=isValidEmail(email)

    }
    // Función que envia el correo de recuperar  contraseña
    fun resetPassword(email: String){
        firebaseAuth.sendPasswordResetEmail(email)
    }
    // Función que comprueba el formato del email
    private fun isValidEmail(email: String): Boolean= Patterns.EMAIL_ADDRESS.matcher(email).matches()
}