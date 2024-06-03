package home

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning

    private val _isPaused = MutableLiveData<Boolean>()
    val isPaused: LiveData<Boolean> = _isPaused

    // LiveData para almacenar las calorías quemadas con el cronómetro
    private val _caloriasCronometro = MutableLiveData<Int>()
    val caloriasCronometro: LiveData<Int> = _caloriasCronometro

    private val _mostRecentEntrenamiento = MutableLiveData<Map<String, Any>>()
    val mostRecentEntrenamiento: LiveData<Map<String, Any>> = _mostRecentEntrenamiento

    // Variables para almacenar los pasos iniciales y los pasos del cronómetro iniciales
    private var pasosIniciales: Int? = null
    private var pasosCronometroIniciales: Int = 0

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

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _birthDate = MutableLiveData<Calendar?>()
    val birthDate: LiveData<Calendar?> = _birthDate

    private val _heightSliderDialogOpen = MutableLiveData(false)
    val heightSliderDialogOpen: LiveData<Boolean> = _heightSliderDialogOpen

    private val _weightSliderDialogOpen = MutableLiveData(false)
    val weightSliderDialogOpen: LiveData<Boolean> = _weightSliderDialogOpen

    private val _showDatePickerDialog = MutableLiveData(false)
    val showDatePickerDialog: LiveData<Boolean> = _showDatePickerDialog

    // LiveData para almacenar la puntuación
    private val _puntuacion = MutableLiveData<Int>()
    val puntuacion: LiveData<Int> = _puntuacion

    private val _puntuacionTotal = MutableLiveData<Int>()
    val puntuacionTotal: LiveData<Int> = _puntuacionTotal

    private val objetivoDiario = 10000

    // Bloque de inicialización, inicia el sensor y carga los datos del usuario
    init {
        startSensor()

        updateUserId()
        loadUserData()
        _isRunning.value = false
        _isPaused.value = false
        _puntuacion.value = 0
    }


    // Actualiza el ID del usuario y recarga los datos
    fun updateUserId() {

        userId = FirebaseAuth.getInstance().currentUser?.uid
        _uid.value = userId
        if (userId != null) {
            loadWeeklyData()
            loadMostRecentEntrenamiento()
            loadUserData()
            loadTotalScore()
            loadUserName()
        }
    }


    fun loadUserName() {
        userId?.let { uid ->
            val docRef = firestore.collection("usuarios").document(uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.contains("usuario")) {
                    _userName.value = document.getString("usuario")
                } else {
                    _userName.value = "Usuario"
                }
            }.addOnFailureListener {
                _userName.value = "Usuario"
            }
        }
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
        _isRunning.value = false
        _isPaused.value = false
        _gender.value = null
        _height.value = null
        _weight.value = null
        _birthDate.value = null
    }
    // Carga los datos del usuario desde la base de datos
    private suspend fun loadData(): UserData? {
        return userId?.let { uid ->
            userDataDao.getUserDataByDate(uid, currentDate)?.also { userData ->
                withContext(Dispatchers.Main) {
                    pasosManuales = userData.steps
                    _pasos.value = userData.steps
                    _distancia.value = userData.distance
                    _calorias.value = userData.calories
                    actualizarPuntuacion(userData.steps)
                }
            }
        }
    }
    fun saveData(pasos: Int, distancia: Float, calorias: Int) {
        userId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                userDataDao.insert(
                    UserData(
                        userId = uid,
                        date = currentDate,
                        steps = pasos,
                        distance = distancia,
                        calories = calorias,
                        score = _puntuacion.value ?: 0
                    )
                )
            }
        }
    }
    fun loadWeeklyData() {
        val (startDate, endDate) = getCurrentWeekDates()
        getWeeklyData(startDate, endDate)
    }

    fun getWeeklyData(startDate: String, endDate: String) {
        userId?.let { uid ->
            viewModelScope.launch {
                val weeklyData = userDataDao.getUserDataForWeek(uid, startDate, endDate)
                _weeklyData.postValue(weeklyData)
            }
        }
    }

    fun getCurrentWeekDates(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
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

    private var pasosManuales = 0

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            viewModelScope.launch(Dispatchers.IO) {
                // Carga los datos más recientes desde la base de datos
                val userData = loadData()

                withContext(Dispatchers.Main) {
                    val pasosTotales = event.values[0].toInt()

                    // Inicializa pasosIniciales si es la primera vez que se ejecuta
                    if (pasosIniciales == null) {
                        userData?.steps?.let {
                            pasosIniciales = pasosTotales - it
                        }
                    }

                    // Calcula los pasos actuales
                    val pasosActuales = pasosTotales - (pasosIniciales ?: pasosTotales)
                    _pasos.value = pasosActuales

                    // Calcula la distancia recorrida (en kilómetros)
                    val distanciaRecorrida = pasosActuales * 0.762f / 1000
                    _distancia.value = distanciaRecorrida

                    // Calcula las calorías quemadas
                    val caloriasQuemadas = (pasosActuales * 0.05f).toInt()
                    _calorias.value = caloriasQuemadas

                    actualizarPuntuacion(pasosActuales)

                    // Guarda los datos actualizados en Room
                    saveData(pasosActuales, distanciaRecorrida, caloriasQuemadas)

                    // Actualiza los datos del cronómetro si está corriendo y no está en pausa
                    if (_isRunning.value == true && _isPaused.value == false) {
                        val pasosCronometroActuales = pasosActuales - pasosCronometroIniciales
                        _pasosCronometro.value = pasosCronometroActuales

                        val distanciaCronometro = pasosCronometroActuales * 0.762f / 1000
                        _distanciaCronometro.value = distanciaCronometro

                        val caloriasCronometro = (pasosCronometroActuales * 0.05f).toInt()
                        _caloriasCronometro.value = caloriasCronometro
                    }
                }
            }
        }
    }

    // Inicia el cronómetro
    fun startCronometro() {
        _isRunning.value = true
        _isPaused.value = false
        pasosCronometroIniciales = _pasos.value ?: 0
        _pasosCronometro.value = 0
        _tiempoCronometro.value = 0L
        viewModelScope.launch {
            while (_isRunning.value == true && _isPaused.value == false) {
                delay(1000)
                incrementTime()
            }
        }
    }
    // Pausa el cronómetro
    fun pauseCronometro() {
        _isPaused.value = true
    }
    // Reanuda el cronómetro
    fun resumeCronometro() {
        _isPaused.value = false
        viewModelScope.launch {
            while (_isRunning.value == true && _isPaused.value == false) {
                delay(1000)
                incrementTime()
            }
        }
    }
    // Resetea el cronómetro
    fun resetCronometro() {
        saveCronometroDataToFirestore()
        pasosCronometroIniciales = 0
        _pasosCronometro.value = 0
        _distanciaCronometro.value = 0f
        _caloriasCronometro.value = 0
        _tiempoCronometro.value = 0L
        _isRunning.value = false
        _isPaused.value = false
    }

    // Incrementa el tiempo del cronómetro en un segundo
    fun incrementTime() {
        _tiempoCronometro.postValue((_tiempoCronometro.value ?: 0L) + 1L)
    }
    // Maneja los cambios en la precisión del sensor(no se usa)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Paramos el listener del sensor cuando el ViewModel se destruye
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
                "calories" to (_calorias.value ?: 0),
                "score" to (_puntuacionTotal.value ?: 0)
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

    private fun actualizarPuntuacion(pasos: Int) {
        // Calcular puntos por pasos 200 pasos equivalen a 1 punto
        val puntosPorPasos = (pasos / 200)

        // Calcular puntos por alcanzar el objetivo
        val puntosPorObjetivo = if (pasos >= objetivoDiario) {
            100 + ((pasos - objetivoDiario) / 200)
        } else {
            0
        }

        // Sumar los puntos totales
        val totalPuntos = puntosPorPasos + puntosPorObjetivo
        _puntuacion.postValue(totalPuntos)


        Log.d("MenuViewModel", "Pasos: $pasos, Puntos por pasos: $puntosPorPasos, Puntos por objetivo: $puntosPorObjetivo, Total puntos: $totalPuntos")
    }

    fun loadTotalScore() {
        userId?.let { uid ->
            viewModelScope.launch {
                val totalScore = userDataDao.getTotalScore(uid) ?: 0
                _puntuacionTotal.postValue(totalScore)
            }
        }
    }



}
