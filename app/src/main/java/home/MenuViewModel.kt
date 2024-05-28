package home

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gofit.data.GoFitDatabase
import com.example.gofit.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MenuViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    // Inicializa el SensorManager para gestionar sensores del dispositivo
    private val sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    // Obtiene el sensor de conteo de pasos
    private val stepCountSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    // LiveData para almacenar el número de pasos
    private val _pasos = MutableLiveData<Int>()
    val pasos: LiveData<Int> = _pasos

    // LiveData para almacenar la distancia recorrida
    private val _distancia = MutableLiveData<Float>()
    val distancia: LiveData<Float> = _distancia

    // LiveData para almacenar las calorías quemadas
    private val _calorias = MutableLiveData<Int>()
    val calorias: LiveData<Int> = _calorias

    // LiveData para almacenar el tiempo del cronómetro
    private val _tiempoCronometro = MutableLiveData<Long>()
    val tiempoCronometro: LiveData<Long> = _tiempoCronometro

    // LiveData para almacenar los pasos registrados por el cronómetro
    private val _pasosCronometro = MutableLiveData<Int>()
    val pasosCronometro: LiveData<Int> = _pasosCronometro

    // LiveData para almacenar la distancia recorrida con el cronómetro
    private val _distanciaCronometro = MutableLiveData<Float>()
    val distanciaCronometro: LiveData<Float> = _distanciaCronometro

    // LiveData para almacenar las calorías quemadas con el cronómetro
    private val _caloriasCronometro = MutableLiveData<Int>()
    val caloriasCronometro: LiveData<Int> = _caloriasCronometro

    private val _mostRecentEntrenamiento = MutableLiveData<Map<String, Any>>()
    val mostRecentEntrenamiento: LiveData<Map<String, Any>> = _mostRecentEntrenamiento

    // Variables para almacenar los pasos iniciales y los pasos del cronómetro iniciales
    private var pasosIniciales: Int? = null
    private var pasosCronometroIniciales: Int = 0

    // Variables para controlar el estado del cronómetro
    var isCronometroRunning: Boolean = false
    var isCronometroPaused: Boolean = false

    // LiveData para almacenar los datos semanales
    private val _weeklyData = MutableLiveData<List<UserData>>()
    val weeklyData: LiveData<List<UserData>> = _weeklyData

    // Inicializa el DAO para acceder a la base de datos
    private val userDataDao = GoFitDatabase.getDatabase(application).userDataDao()
    // Formatea la fecha actual
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentDate = dateFormat.format(Date())
    // Obtiene el ID del usuario actual de Firebase
    private var userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    // Instancia de Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // LiveData para los datos del perfil
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

    // Bloque de inicialización, inicia el sensor y carga los datos del usuario
    init {
        startSensor()
        updateUserId()
        loadUserData()
    }

    // Actualiza el ID del usuario y recarga los datos
    fun updateUserId() {
        userId = FirebaseAuth.getInstance().currentUser?.uid
        _uid.value = userId
        loadData()
        loadWeeklyData()
        loadMostRecentEntrenamiento()
        loadUserData()
    }
    // Limpia los datos del ViewModel
    fun clearData() {
        _pasos.value = 0
        _distancia.value = 0f
        _calorias.value = 0
        _tiempoCronometro.value = 0L
        _pasosCronometro.value = 0
        _distanciaCronometro.value = 0f
        _caloriasCronometro.value = 0
        _weeklyData.value = emptyList()
        pasosIniciales = null
        pasosCronometroIniciales = 0
        isCronometroRunning = false
        isCronometroPaused = false
        _gender.value = null
        _height.value = null
        _weight.value = null
        _birthDate.value = null
    }


    // Carga los datos del usuario desde la base de datos
    fun loadData() {
        userId?.let { uid ->
            viewModelScope.launch {
                val userData = userDataDao.getUserDataByDate(uid, currentDate)
                if (userData != null) {
                    // Establecemos los datos iniciales
                    _pasos.postValue(userData.steps)
                    _distancia.postValue(userData.distance)
                    _calorias.postValue(userData.calories)
                }
                else {
                    _pasos.postValue(0)
                    _distancia.postValue(0f)
                    _calorias.postValue(0)
                }
            }
        }
    }

    // Guarda los datos del usuario en la base de datos
    fun saveData() {
        userId?.let { uid ->
            viewModelScope.launch {
                val existingData = userDataDao.getUserDataByDate(uid, currentDate)
                val newSteps = _pasos.value ?: 0
                val newDistance = _distancia.value ?: 0f
                val newCalories = _calorias.value ?: 0

                val finalSteps = existingData?.steps?.let { if (newSteps > it) newSteps else it } ?: newSteps
                val finalDistance = existingData?.distance?.let { if (newDistance > it) newDistance else it } ?: newDistance
                val finalCalories = existingData?.calories?.let { if (newCalories > it) newCalories else it } ?: newCalories

                userDataDao.insert(
                    UserData(
                        userId = uid,
                        date = currentDate,
                        steps = finalSteps,
                        distance = finalDistance,
                        calories = finalCalories
                    )
                )
            }
        }
    }


    fun loadWeeklyData() {
        val (startDate, endDate) = getCurrentWeekDates()
        getWeeklyData(startDate, endDate)
    }

    // Ajusta la función getWeeklyData para obtener los datos semanales
    fun getWeeklyData(startDate: String, endDate: String) {
        userId?.let { uid ->
            viewModelScope.launch {
                val weeklyData = userDataDao.getUserDataForWeek(uid, startDate, endDate)
                _weeklyData.postValue(weeklyData)
            }
        }
    }

    // Calcula las fechas de lunes a domingo de la semana actual
    fun getCurrentWeekDates(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -1)
        }
        val startDate = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = dateFormat.format(calendar.time)
        return Pair(startDate, endDate)
    }




    fun startSensor() {
        stepCountSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
    fun stopSensor(){
        sensorManager.unregisterListener(this)
    }


    // Maneja los cambios en el sensor de pasos
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val pasosTotales = event.values[0].toInt()

            // Inicializa pasosIniciales si es la primera vez que se ejecuta
            if (pasosIniciales == null) {
                _pasos.value?.let {
                    pasosIniciales = pasosTotales - it
                }

            }

            // Calcula los pasos actuales
            val pasosActuales = pasosTotales - (pasosIniciales ?: pasosTotales)
            _pasos.postValue(pasosActuales)

            // Calcula la distancia recorrida (en kilómetros)
            val distanciaRecorrida = pasosActuales * 0.762f / 1000
            _distancia.postValue(distanciaRecorrida)

            // Calcula las calorías quemadas
            val caloriasQuemadas = (pasosActuales * 0.05f).toInt()
            _calorias.postValue(caloriasQuemadas)

            // Actualiza los datos del cronómetro si está corriendo y no está en pausa
            if (isCronometroRunning && !isCronometroPaused) {
                val pasosCronometroActuales = pasosActuales - pasosCronometroIniciales
                _pasosCronometro.postValue(pasosCronometroActuales)

                val distanciaCronometro = pasosCronometroActuales * 0.762f / 1000
                _distanciaCronometro.postValue(distanciaCronometro)

                val caloriasCronometro = (pasosCronometroActuales * 0.05f).toInt()
                _caloriasCronometro.postValue(caloriasCronometro)
            }
        }
    }


    // Inicia el cronómetro
    fun startCronometro() {
        pasosCronometroIniciales = _pasos.value ?: 0
        _pasosCronometro.value = 0
        _tiempoCronometro.value = 0L
        isCronometroRunning = true
        isCronometroPaused = false
    }

    // Pausa el cronómetro
    fun pauseCronometro() {
        isCronometroPaused = true
    }

    // Reanuda el cronómetro
    fun resumeCronometro() {
        isCronometroPaused = false
    }

    // Resetea el cronómetro
    fun resetCronometro() {
        saveCronometroDataToFirestore()
        pasosCronometroIniciales = 0
        _pasosCronometro.value = 0
        _distanciaCronometro.value = 0f
        _caloriasCronometro.value = 0
        _tiempoCronometro.value = 0L
        isCronometroRunning = false
        isCronometroPaused = false
    }

    // Incrementa el tiempo del cronómetro en 1 unidad (asumimos segundos)
    fun incrementTime() {
        _tiempoCronometro.postValue((_tiempoCronometro.value ?: 0L) + 1L)
    }


    // Maneja los cambios en la precisión del sensor (no usado aquí)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Desregistrar el listener del sensor cuando el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        saveDataToFirestore()
        stopSensor()
    }


    fun saveDataToFirestore() {
        userId?.let { uid ->
            val userDocRef = firestore.collection("usuarios").document(userId!!)
            val userData = hashMapOf(
                "date" to currentDate,
                "steps" to (_pasos.value ?: 0),
                "distance" to (_distancia.value ?: 0f),
                "calories" to (_calorias.value ?: 0)
            )
            userDocRef.collection("datos").document(currentDate).set(userData)

        }
    }

    fun saveCronometroDataToFirestore() {
        userId?.let { uid ->
            val userDocRef = firestore.collection("usuarios").document(uid)
            val cronometroData = hashMapOf(
                "steps" to (_pasosCronometro.value ?: 0),
                "distance" to (_distanciaCronometro.value ?: 0f),
                "calories" to (_caloriasCronometro.value ?: 0),
                "time" to (_tiempoCronometro.value ?: 0L),
                "timestamp" to FieldValue.serverTimestamp()
            )
            userDocRef.collection("datosEntrenamiento").add(cronometroData)
        }
    }

    fun loadMostRecentEntrenamiento() {
        userId?.let { uid ->
            val userDocRef = firestore.collection("usuarios").document(uid)
            userDocRef.collection("datosEntrenamiento")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val mostRecentDoc = documents.documents[0]
                        val mostRecentData = mostRecentDoc.data ?: emptyMap()
                        _mostRecentEntrenamiento.postValue(mostRecentData)
                    }
                }
        }
    }

    // Métodos del perfil de usuario

    fun setGender(gender: String) {
        _gender.value = gender
    }

    fun setHeight(height: Float) {
        _height.value = height
    }

    fun setWeight(weight: Float) {
        _weight.value = weight
    }

    fun setBirthDate(calendar: Calendar) {
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

        firestore.collection("usuarios").document(uid).set(datosUsuario, SetOptions.merge())
    }

    private fun loadUserData() {
        val uid = _uid.value ?: return
        val docRef = firestore.collection("usuarios").document(uid)
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
