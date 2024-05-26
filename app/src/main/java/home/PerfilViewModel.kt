package home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PerfilViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uid = MutableLiveData<String?>()
    val uid: LiveData<String?> = _uid

    private val _gender = MutableLiveData<String?>()
    val gender: LiveData<String?> = _gender

    private val _height = MutableLiveData<Float?>()
    val height: LiveData<Float?> = _height

    private val _weight = MutableLiveData<Float?>()
    val weight: LiveData<Float?> = _weight

    private val _birthDate = MutableLiveData<Calendar?>()
    val birthDate: LiveData<Calendar?> = _birthDate

    private val _heightSliderDialogOpen = MutableLiveData(false)
    val heightSliderDialogOpen: LiveData<Boolean> = _heightSliderDialogOpen

    private val _weightSliderDialogOpen = MutableLiveData(false)
    val weightSliderDialogOpen: LiveData<Boolean> = _weightSliderDialogOpen

    private val _showDatePickerDialog = MutableLiveData(false)
    val showDatePickerDialog: LiveData<Boolean> = _showDatePickerDialog

    init {
        _uid.value = auth.currentUser?.uid
        loadUserData()
    }

    fun setGender(gender: String) {
        Log.d("PerfilViewModel", "setGender called with: $gender")
        _gender.value = gender
    }

    fun setHeight(height: Float) {
        Log.d("PerfilViewModel", "setHeight called with: $height")
        _height.value = height
    }

    fun setWeight(weight: Float) {
        Log.d("PerfilViewModel", "setWeight called with: $weight")
        _weight.value = weight
    }

    fun setBirthDate(calendar: Calendar) {
        Log.d("PerfilViewModel", "setBirthDate called with: ${SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.time)}")
        _birthDate.value = calendar
    }

    fun toggleHeightSliderDialog(open: Boolean) {
        _heightSliderDialogOpen.value = open
    }

    fun toggleWeightSliderDialog(open: Boolean) {
        _weightSliderDialogOpen.value = open
    }

    fun toggleDatePickerDialog(open: Boolean) {
        _showDatePickerDialog.value = open
    }

    fun guardarDatosUsuario() {
        val uid = _uid.value ?: return
        val altura = _height.value
        val peso = _weight.value
        val fechaDeNacimiento = _birthDate.value?.let {
            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it.time)
        }
        val genero = _gender.value

        val datosUsuario = hashMapOf(
            "altura" to altura,
            "peso" to peso,
            "fechaDeNacimiento" to fechaDeNacimiento,
            "genero" to genero
        )

        db.collection("usuarios").document(uid).set(datosUsuario, SetOptions.merge())
    }

    private fun loadUserData() {
        val uid = _uid.value ?: return
        val docRef = db.collection("usuarios").document(uid)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                _gender.value = document.getString("genero")
                _height.value = document.getDouble("altura")?.toFloat()
                _weight.value = document.getDouble("peso")?.toFloat()
                val fechaDeNacimientoStr = document.getString("fechaDeNacimiento")
                if (fechaDeNacimientoStr != null) {
                    val calendar = Calendar.getInstance()
                    calendar.time = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).parse(fechaDeNacimientoStr)!!
                    _birthDate.value = calendar
                }
            }
        }
    }
}
