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

class StepCountViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCountSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _pasos = MutableLiveData<Int>()
    val pasos: LiveData<Int> = _pasos

    private val _distancia = MutableLiveData<Float>()
    val distancia: LiveData<Float> = _distancia

    private val _calorias = MutableLiveData<Int>()
    val calorias: LiveData<Int> = _calorias
    private val _tiempoCronometro = MutableLiveData<Long>()
    val tiempoCronometro: LiveData<Long> = _tiempoCronometro

    private val _pasosCronometro = MutableLiveData<Int>()
    val pasosCronometro: LiveData<Int> = _pasosCronometro

    private val _distanciaCronometro = MutableLiveData<Float>()
    val distanciaCronometro: LiveData<Float> = _distanciaCronometro

    private val _caloriasCronometro = MutableLiveData<Int>()
    val caloriasCronometro: LiveData<Int> = _caloriasCronometro

    private var pasosIniciales: Int? = null
    private var pasosCronometroIniciales: Int = 0

    var isCronometroRunning: Boolean = false
    var isCronometroPaused: Boolean = false

    init {
        startSensor()
    }

    fun startSensor() {
        stepCountSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun startCronometro() {
        pasosCronometroIniciales = _pasos.value ?: 0
        _pasosCronometro.value = 0
        _tiempoCronometro.value = 0L
        isCronometroRunning = true
        isCronometroPaused = false
    }

    fun pauseCronometro() {
        isCronometroPaused = true
    }

    fun resumeCronometro() {
        isCronometroPaused = false
    }

    fun resetCronometro() {
        pasosCronometroIniciales = 0
        _pasosCronometro.value = 0
        _distanciaCronometro.value = 0f
        _caloriasCronometro.value = 0
        _tiempoCronometro.value = 0L
        isCronometroRunning = false
        isCronometroPaused = false
    }

    fun incrementTime() {
        _tiempoCronometro.postValue((_tiempoCronometro.value ?: 0L) + 1L)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val pasosTotales = event.values[0].toInt()
            if (pasosIniciales == null) {
                pasosIniciales = pasosTotales
            }
            val pasosActuales = pasosTotales - (pasosIniciales ?: pasosTotales)
            _pasos.postValue(pasosActuales)
            _distancia.postValue(pasosActuales * 0.762f / 1000)
            _calorias.postValue((pasosActuales * 0.05f).toInt())

            if (isCronometroRunning && !isCronometroPaused) {
                val pasosCronometroActuales = pasosActuales - pasosCronometroIniciales
                _pasosCronometro.postValue(pasosCronometroActuales)
                _distanciaCronometro.postValue(pasosCronometroActuales * 0.762f / 1000)
                _caloriasCronometro.postValue((pasosCronometroActuales * 0.05f).toInt())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
