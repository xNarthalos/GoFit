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

    private val _calorias = MutableLiveData<Float>()
    val calorias: LiveData<Float> = _calorias

    private var pasosIniciales: Int? = null

    init {
        startSensor()
    }

    fun startSensor() {
        stepCountSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
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
            _calorias.postValue(pasosActuales * 0.05f)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
