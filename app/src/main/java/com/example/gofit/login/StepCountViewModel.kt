package com.example.gofit.login

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
import com.example.gofit.login.data.GoFitDatabase
import com.example.gofit.login.data.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StepCountViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

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

    // Variables para almacenar los pasos iniciales y los pasos del cronómetro iniciales
    private var pasosIniciales: Int? = null
    private var pasosCronometroIniciales: Int = 0

    // Variables para controlar el estado del cronómetro
    var isCronometroRunning: Boolean = false
    var isCronometroPaused: Boolean = false

    // Inicializa el DAO para acceder a la base de datos
    private val userDataDao = GoFitDatabase.getDatabase(application).userDataDao()
    // Formatea la fecha actual
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentDate = dateFormat.format(Date())
    // Obtiene el ID del usuario actual de Firebase
    private var userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    // Bloque de inicialización, inicia el sensor y carga los datos del usuario
    init {
        startSensor()
        loadData()
    }

    // Actualiza el ID del usuario y recarga los datos
    fun updateUserId() {
        userId = FirebaseAuth.getInstance().currentUser?.uid
        loadData()
    }

    // Carga los datos del usuario desde la base de datos
    fun loadData() {
        userId?.let { uid ->
            viewModelScope.launch {
                val userData = userDataDao.getUserDataByDate(uid, currentDate)
                if (userData != null) {
                    _pasos.postValue(userData.steps) // Establece los pasos iniciales
                    _distancia.postValue(userData.distance)
                    _calorias.postValue(userData.calories)
                } else {
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
                userDataDao.insert(
                    UserData(
                        userId = uid,
                        date = currentDate,
                        steps = _pasos.value ?: 0,
                        distance = _distancia.value ?: 0f,
                        calories = _calorias.value ?: 0
                    )
                )
            }
        }
    }

    // Inicia el sensor de conteo de pasos
    fun startSensor() {
        stepCountSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
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

    // Maneja los cambios en el sensor de pasos
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val pasosTotales = event.values[0].toInt()
            if (pasosIniciales == null) {

                _pasos.value?.let {
                    // Ajusta pasosIniciales basado en los pasos almacenados
                    pasosIniciales = pasosTotales - it
                }
            }
            val pasosActuales = pasosTotales - (pasosIniciales ?: pasosTotales)
            _pasos.postValue(pasosActuales)
            // Calcula la distancia recorrida
            _distancia.postValue(pasosActuales * 0.762f / 1000)
            // Calcula las calorías quemadas
            _calorias.postValue((pasosActuales * 0.05f).toInt())



            // Actualiza los datos del cronómetro si está corriendo y no está en pausa
            if (isCronometroRunning && !isCronometroPaused) {
                val pasosCronometroActuales = pasosActuales - pasosCronometroIniciales
                _pasosCronometro.postValue(pasosCronometroActuales)
                _distanciaCronometro.postValue(pasosCronometroActuales * 0.762f / 1000)
                _caloriasCronometro.postValue((pasosCronometroActuales * 0.05f).toInt())
            }
        }
    }

    // Maneja los cambios en la precisión del sensor (no usado aquí)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Desregistrar el listener del sensor cuando el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
