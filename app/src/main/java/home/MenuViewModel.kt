package home

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import android.widget.Toast
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
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _mostRecentEntrenamiento = MutableLiveData<Map<String, Any>>(emptyMap())
    val mostRecentEntrenamiento: LiveData<Map<String, Any>> = _mostRecentEntrenamiento

    // Variables para almacenar los pasos iniciales y los pasos del cronómetro iniciales
    private var pasosIniciales: Int? = null
    private var pasosCronometroIniciales: Int = 0
    private var pasosManuales = 0

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
    // LiveData para almacenar el género del usuario
    private val _gender = MutableLiveData<String?>()
    val gender: LiveData<String?> = _gender
    // LiveData para almacenar la altura del usuario
    private val _height = MutableLiveData<Float?>()
    val height: LiveData<Float?> = _height
    // LiveData para almacenar el peso del usuario
    private val _weight = MutableLiveData<Float?>()
    val weight: LiveData<Float?> = _weight
    // LiveData para almacenar el nombre del usuario
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName
    // LiveData para almacenar la fecha de nacimiento del usuario
    private val _birthDate = MutableLiveData<Calendar?>()
    val birthDate: LiveData<Calendar?> = _birthDate
    // LiveData para controlar la apertura del diálogo del spinner de la altura
    private val _heightSliderDialogOpen = MutableLiveData(false)
    val heightSliderDialogOpen: LiveData<Boolean> = _heightSliderDialogOpen
    // LiveData para controlar la apertura del diálogo del spinner del peso
    private val _weightSliderDialogOpen = MutableLiveData(false)
    val weightSliderDialogOpen: LiveData<Boolean> = _weightSliderDialogOpen
    // LiveData para controlar la apertura del diálogo del selector de fecha
    private val _showDatePickerDialog = MutableLiveData(false)
    val showDatePickerDialog: LiveData<Boolean> = _showDatePickerDialog
    // LiveData para almacenar la puntuación actual del usuario
    private val _puntuacion = MutableLiveData<Int>()
    // LiveData para almacenar la puntuación total del usuario
    private val _puntuacionTotal = MutableLiveData<Int>()
    val puntuacionTotal: LiveData<Int> = _puntuacionTotal
    // StateFlow para almacenar el objetivo diario de pasos del usuario
    val _dailyStepsGoal = MutableStateFlow(5000f)
    // Variable para rastrear si el objetivo diario se ha cumplido
    private var objetivoLogrado = false
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
    fun setDailyStepsGoal(goal: Float) {
        viewModelScope.launch {
            _dailyStepsGoal.value = goal
        }
    }

    fun startSensor() {
        stepCountSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
    fun stopSensor(){
        sensorManager.unregisterListener(this)
    }
    // Bloque de inicialización, inicia el sensor y carga los datos del usuario
    init {
        startSensor()
        loadObjectiveState()
        resetDailyGoal()
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

//Metodo para obtener el nombre de usuario que se pondra en la toolbar
    fun loadUserName() {
        // Verifica si el ID del usuario no es nulo
        userId?.let { uid ->
            // Obtenemos una referencia al documento del usuario en Firestore
            val docRef = firestore.collection("usuarios").document(uid)
            docRef.get().addOnSuccessListener { document ->
                // Si el documento existe y contiene el campo "usuario", establece el valor de _userName
                if (document != null && document.contains("usuario")) {
                    _userName.value = document.getString("usuario")
                    // Si el documento no contiene el campo "usuario", establece un valor predeterminado
                } else {
                    _userName.value = ""
                }
            }.addOnFailureListener {
                // En caso de fallo al obtener el documento, establece un valor predeterminado
                _userName.value = ""
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
        _mostRecentEntrenamiento.value = emptyMap()
        _puntuacionTotal.value=0
        _userName.value = ""
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
        // Comprobamos si el ID del usuario no es nulo
        return userId?.let { uid ->
            // Se obtienen los datos del usuario para la fecha actual desde la base de datos local
            userDataDao.getUserDataByDate(uid, currentDate)?.also { userData ->
                // Cambia al contexto principal para actualizar las variables LiveData
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
        // Comprobamos si el ID del usuario no es nulo
        userId?.let { uid ->
            // Iniciamos una corrutina en el scope del ViewModel para realizar operaciones en el hilo IO
            viewModelScope.launch(Dispatchers.IO) {
                // Insertamos los datos del usuario en la base de datos local
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
        // Obtiene las fechas de inicio y fin de la semana actual
        val (startDate, endDate) = getCurrentWeekDates()
        // Carga los datos semanales para el rango de fechas obtenido
        getWeeklyData(startDate, endDate)
    }

    fun getWeeklyData(startDate: String, endDate: String) {
        // Comprueba si el ID del usuario no es nulo
        userId?.let { uid ->
            // Iniciamos una corrutina en el scope del ViewModel
            viewModelScope.launch {
                // Se obtienen los datos del usuario para esta semana desde la base de datos local
                val weeklyData = userDataDao.getUserDataForWeek(uid, startDate, endDate)
                _weeklyData.postValue(weeklyData)
            }
        }
    }

    fun getCurrentWeekDates(): Pair<String, String> {
        // Se obtiene una instancia del calendario actual
        val calendar = Calendar.getInstance()
        // Establece el primer día de la semana como lunes
        calendar.firstDayOfWeek = Calendar.MONDAY
        // Establece el día de la semana actual como lunes
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        // Formatea la fecha de inicio de la semana
        val startDate = dateFormat.format(calendar.time)
        // Avanza el calendario en 6 días para obtener el final de la semana
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        // Formatea la fecha de fin de la semana
        val endDate = dateFormat.format(calendar.time)
        // Devuelve las fechas de inicio y fin de la semana como un par
        return Pair(startDate, endDate)
    }
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
                    //Se actualizan los puntos conseguidos por el usuario
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
        // Establece el estado del cronómetro a "corriendo"
        _isRunning.value = true
        // Establece el estado de pausa del cronómetro a "no pausado"
        _isPaused.value = false
        // Guarda el número de pasos actuales como los pasos iniciales del cronómetro
        pasosCronometroIniciales = _pasos.value ?: 0
        // Resetea el contador de pasos del cronómetro a 0
        _pasosCronometro.value = 0
        // Resetea el tiempo del cronómetro a 0
        _tiempoCronometro.value = 0L
        // Se inicia una coroutine en el scope del ViewModel
        viewModelScope.launch {
            // Mientras el cronómetro esté corriendo y no esté pausado
            while (_isRunning.value == true && _isPaused.value == false) {
                // Incrementa el tiempo del cronómetro
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
        // Establece el estado de pausa del cronómetro a "no pausado"
        _isPaused.value = false
        // Se incia una coroutine en el scope del ViewModel
        viewModelScope.launch {
            // Mientras el cronómetro esté corriendo y no esté pausado
            while (_isRunning.value == true && _isPaused.value == false) {
                delay(1000)
                // Incrementa el tiempo del cronómetro
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
        // Comprueba si el ID del usuario no es nulo
        userId?.let { uid ->
            // Obtiene una referencia al documento del usuario en Firestore
            val userDocRef = firestore.collection("usuarios").document(userId!!)
            // Creamos un mapa de datos del usuario para guardar en Firestore
            val userData = hashMapOf(
                "date" to currentDate,
                "steps" to (_pasos.value ?: 0),
                "distance" to (_distancia.value ?: 0f),
                "calories" to (_calorias.value ?: 0),
                "score" to (_puntuacionTotal.value ?: 0)
            )
            // Guardamos los datos del usuario en la subcolección "datos" dentro del documento del usuario
            userDocRef.collection("datos").document(currentDate).set(userData)

        }
    }
    fun saveCronometroDataToFirestore() {
        // Comprobamos si el ID del usuario no es nulo
        userId?.let { uid ->
            // Obtenemos una referencia al documento del usuario en Firestore
            val userDocRef = firestore.collection("usuarios").document(uid)
            // Creamos un mapa de datos del cronómetro para guardar en Firestore
            val cronometroData = hashMapOf(
                "steps" to (_pasosCronometro.value ?: 0),
                "distance" to (_distanciaCronometro.value ?: 0f),
                "calories" to (_caloriasCronometro.value ?: 0),
                "time" to (_tiempoCronometro.value ?: 0L),
                "timestamp" to FieldValue.serverTimestamp()
            )
            // Añade los datos del cronómetro a la subcolección "datosEntrenamiento" dentro del documento del usuario
            userDocRef.collection("datosEntrenamiento").add(cronometroData)
        }
    }
    fun loadMostRecentEntrenamiento() {
        // Comprobamos si el ID del usuario no es nulo
        userId?.let { uid ->
            // Obtiene una referencia al documento del usuario en Firestore
            val userDocRef = firestore.collection("usuarios").document(uid)
            userDocRef.collection("datosEntrenamiento")
                // Ordena los documentos por la marca de tiempo en orden descendente
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                // Limita la consulta a 1 documento
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Obtiene el documento más reciente
                        val mostRecentDoc = documents.documents[0]
                        // Obtiene los datos del documento o un mapa vacío si los datos son nulos
                        val mostRecentData = mostRecentDoc.data ?: emptyMap()
                        // Publica los datos más recientes en el LiveData _mostRecentEntrenamiento
                        _mostRecentEntrenamiento.postValue(mostRecentData)
                    }else {
                        _mostRecentEntrenamiento.postValue(emptyMap())
                    }
                }
        }
    }


    fun guardarDatosUsuario() {
        // Comprobamos si el ID del usuario no es nulo
        val uid = _uid.value ?: return
        // Obtiene los valores de altura, peso, fecha de nacimiento, género y objetivo diario de pasos
        val altura = _height.value
        val peso = _weight.value
        val fechaDeNacimiento = _birthDate.value?.let {
            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it.time)
        }
        // Crea un mapa de datos del usuario para guardar en Firestore
        val genero = _gender.value
        val objetivoDiario = _dailyStepsGoal.value
        val datosUsuario = hashMapOf(
            "altura" to altura,
            "peso" to peso,
            "fechaDeNacimiento" to fechaDeNacimiento,
            "genero" to genero,
            "dailyStepsGoal" to objetivoDiario
        )
        // Guarda los datos del usuario en el documento en Firestore
        firestore.collection("usuarios").document(uid).set(datosUsuario, SetOptions.merge())
    }

    private fun loadUserData() {
        // Comprobamos si el ID del usuario no es nulo
        val uid = _uid.value ?: return
        // Obtiene una referencia al documento del usuario en Firestore
        val docRef = firestore.collection("usuarios").document(uid)
        // Realiza una consulta para obtener los datos del usuario
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                // Actualiza los LiveData con los datos obtenidos del documento de Firestore
                _gender.value = document.getString("genero")
                _height.value = document.getDouble("altura")?.toFloat()
                _weight.value = document.getDouble("peso")?.toFloat()
                // Obtiene la fecha de nacimiento del usuario como cadena de texto
                val fechaDeNacimientoStr = document.getString("fechaDeNacimiento")
                if (fechaDeNacimientoStr != null) {
                    // Convierte la cadena de texto a un objeto Calendar y actualiza el LiveData correspondiente
                    val calendar = Calendar.getInstance()
                    calendar.time = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).parse(fechaDeNacimientoStr)!!
                    _birthDate.value = calendar
                }
                // Obtiene el objetivo diario de pasos y lo convierte a Float
                document.getDouble("dailyStepsGoal")?.let {
                    _dailyStepsGoal.value = it.toFloat()
                }
            }
        }
    }
    // Función que  actualiza la puntuación del usuario en función de los pasos dados.
    private fun actualizarPuntuacion(pasos: Int) {
        // Obtiene el valor actual del objetivo diario de pasos
        val objetivoDiario = _dailyStepsGoal.value.toInt()
        // Calcula los puntos por pasos: 200 pasos equivalen a 1 punto
        val puntosPorPasos = (pasos / 200)
        // Calcula los puntos por alcanzar el objetivo diario
        val puntosPorObjetivo = if (pasos >= objetivoDiario && !objetivoLogrado) {
            // Si se ha alcanzado el objetivo diario y no se había alcanzado previamente
            // se otorgan 100 puntos más puntos adicionales por los pasos extra
            100 + ((pasos - objetivoDiario) / 200)
        } else {
            // Si no se ha alcanzado el objetivo diario, no se otorgan puntos adicionales
            0
        }
        // Suma los puntos totales, es decir,puntos por pasos más puntos por objetivo
        val totalPuntos = puntosPorPasos + puntosPorObjetivo
        // Actualiza el valor de la puntuación en el LiveData
        _puntuacion.postValue(totalPuntos)
        // Si se han otorgado puntos por alcanzar el objetivo diario y el objetivo no se había alcanzado previamente
        // se marca el objetivo como logrado, se guarda el estado del objetivo y se muestra un Toast
        if (puntosPorObjetivo > 0 && !objetivoLogrado) {
            objetivoLogrado = true
            saveGoalState()
            showToast()
        }
    }

    private fun showToast() {
        // Usa un Handler para asegurarse de que el Toast se muestre en el hilo principal
        android.os.Handler(Looper.getMainLooper()).post {
            // Muestra un Toast con un mensaje indicando que se ha alcanzado el objetivo diario
            Toast.makeText(getApplication(), "¡Objetivo diario alcanzado! ¡Sigue así!", Toast.LENGTH_LONG).show()
        }
    }
    // Función que guarda el estado del objetivo diario en shared preferences
    private fun saveGoalState() {
        // Obtiene una instancia de SharedPreferences
        val sharedPreferences = getApplication<Application>().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        // Crea un editor para modificar las preferencias compartidas
        val editor = sharedPreferences.edit()
        // Guarda el estado del objetivo diario en las preferencias compartidas
        editor.putBoolean("objetivoLogrado", objetivoLogrado)
        // Aplica los cambios
        editor.apply()
    }
    //Función que carga el estado del objetivo diario desde las shared preferences.
    private fun loadObjectiveState() {
        // Obtiene una instancia de SharedPreferences
        val sharedPreferences = getApplication<Application>().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        // Carga el estado del objetivo diario,si no se encuentra ningún valor guardado, se usa "false" como valor predeterminado
        objetivoLogrado = sharedPreferences.getBoolean("objetivoLogrado", false)
    }
    // Función que reinicia el estado del objetivo diario a medianoche.
    private fun resetDailyGoal() {
        // Crea un Handler para ejecutar una tarea en el hilo principal
        val handler = android.os.Handler(Looper.getMainLooper())
        // Obtiene una instancia de Calendar y la configura para la medianoche del próximo día
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        // Calcula el tiempo de espera  hasta la medianoche
        val delay = midnight.timeInMillis - System.currentTimeMillis()
        // Programa una tarea para ejecutar a medianoche
        handler.postDelayed({
            // Restablece el estado del objetivo diario a false
            objetivoLogrado = false
            // Vuelve a llamar a la función para programar el restablecimiento para la próxima medianoche
            resetDailyGoal()
        }, delay)
    }
    // Función que carga la puntuación total del usuario desde la base de datos.
    fun loadTotalScore() {
        // Comprobamos si el ID del usuario no es nulo
        userId?.let { uid ->
            // Lanza una corrutina
            viewModelScope.launch {
                // Obtiene la puntuación total del usuario desde la base de datos
                val totalScore = userDataDao.getTotalScore(uid) ?: 0
                // Actualiza el LiveData con la puntuación total
                _puntuacionTotal.postValue(totalScore)
            }
        }
    }
}
