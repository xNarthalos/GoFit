package registro

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroViewModel : ViewModel() {
    // Obtenemos la instancia de firebaseauth para manejar la autenticación desde firebase
    private val auth: FirebaseAuth = Firebase.auth
    // Obtenemos la instancia de firebase para controlar la base de datos de firestore
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // MutableLiveData para guardar el email ingresado por el usuario
    private val _email = MutableLiveData<String>()
    // LiveData para llevar el email a la vista
    val email: LiveData<String> = _email
    // MutableLiveData para guardar la contraseña ingresada por el usuario
    private val _password = MutableLiveData<String>()
    // LiveData para llevar el valor de la contraseña a la vista
    val password: LiveData<String> = _password
    // MutableLiveData para guardar la  repeticion de contraseña ingresada por el usuario
    private val _repetirPassword = MutableLiveData<String>()
    // LiveData para llevar el valor de la repeticion de la contraseña a las vista
    val repetirPassword: LiveData<String> = _repetirPassword
    // MutableLiveData para guardar el nombre de usuario
    private val _userName = MutableLiveData<String>()
    // LiveData para llevar el valor del username a la vista
    val userName: LiveData<String> = _userName
    // MutableLiveData para guardar la fecha de nacimiento del usuario
    private val _fechaDeNacimiento = MutableLiveData<Calendar>()
    // LiveData para llevar la fecha de nacimiento a la vista
    val fechaDeNacimiento: LiveData<Calendar> = _fechaDeNacimiento
    // MutableLiveData para habilitar o deshabilitar el botón de registro
    private val _registroEnable = MutableLiveData<Boolean>()
    val registroEnable: LiveData<Boolean> = _registroEnable
    // MutableLiveData para habilitar o deshabilitar el botón de registro
    private val _passwordVisibility = MutableLiveData<Boolean>()
    val passwordVisibility: LiveData<Boolean> = _passwordVisibility
    // MutableLiveData para controlar la visibilidad de la repetición de la contraseña
    private val _repeatPasswordVisibility = MutableLiveData<Boolean>()
    val repeatPasswordVisibility: LiveData<Boolean> = _repeatPasswordVisibility
    // MutableLiveData para almacenar errores de email
    private val _emailError = MutableLiveData<String?>()

    // MutableLiveData para controlar el enfoque de los campos de entrada
    private val _hasFocus = MutableLiveData<Boolean>()

    // Al inciar  establecemos una fecha de nacimiento predeterminada hace 18 años para el calendario
    init {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18)
        _fechaDeNacimiento.value = calendar
    }

    // Función que actualiza el valor de los campos del registro que introduce el usuario
    fun onRegistroChanged(
        email: String,
        password: String,
        userName: String,
        repetirPassword: String
    ) {
        // Actualizamos los valores de los LiveData
        _email.value = email
        _password.value = password
        _repetirPassword.value = repetirPassword
        _userName.value = userName

     //Comprobamos que el email es validp
        val validEmail = isValidEmail(email)
        //si no es valido configurarmos el mensaje de error
        if (!validEmail) {
            _emailError.value = "Email no válido"
        } else {
            // Reiniciamos el mensaje de error si el email es válido
            _emailError.value = null
        }

        // Comprobamos si todos los campos tienen valores no nulos
        val allFieldsFilled =
            email.isNotBlank() && password.isNotBlank() && repetirPassword.isNotBlank() && userName.isNotBlank()

        // Comprobamos si la contraseña tiene al menos 6 caracteres
        val validPassword = isValidPassword(password)

        // Comprobamos si las dos contraseñas coinciden
        val passwordsMatch = password == repetirPassword

        // Habilitamos el botón de registro solo si todas las condiciones se cumplen
        _registroEnable.value = allFieldsFilled && validEmail && validPassword && passwordsMatch
    }

    // Función para establecer la fecha de nacimiento
    fun setFechaDeNacimiento(fecha: Calendar) {
        _fechaDeNacimiento.value = fecha
    }
    // Función para verificar si el email es válido
    fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    // Función para verificar si la contraseña es válida, es decir, que sea mayor o igual a 6
    fun isValidPassword(password: String): Boolean = password.length >= 6
    // Función para alternar la visibilidad de la contraseña
    fun togglePasswordVisibility(passwordVisibility: Boolean) {
        _passwordVisibility.value = !passwordVisibility

    }
    // Función para alternar la visibilidad de la repetición de la contraseña
    fun toggleRepeatPasswordVisibility(passwordVisibility: Boolean) {

        _repeatPasswordVisibility.value = !passwordVisibility

    }
    // Función para establecer el enfoque de los campos de entrada
    fun setFocus(hasFocus: Boolean) {
        _hasFocus.value = hasFocus
    }
    // Función que sirve para  registrar un nuevo usuario con el email y la contraseña
    fun registro(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    //guardamos sus datos en firebase
                    guardarDatosUsuario(it.uid)
                }
            }
        }
    }
    // Función que guarda los datos del usuario en Firebase
    fun guardarDatosUsuario(uid: String) {
        val email = _email.value
        val usuario = _userName.value
        val fechaDeNacimiento = _fechaDeNacimiento.value?.let {
            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it.time)
        }
        // Se crea un mapa con los datos del usuario
        val datosUsuario = hashMapOf(
            "email" to email,
            "usuario" to usuario,
            "fechaDeNacimiento" to fechaDeNacimiento
        )
        // Se guardan los datos del usuario en Firestore
        db.collection("usuarios").document(uid).set(datosUsuario)

    }
}

