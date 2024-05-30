package home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gofit.R
import com.example.gofit.data.UserData
import com.example.gofit.data.WeeklyDayData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Inicio(menuViewModel: MenuViewModel) {
    val pasos by menuViewModel.pasos.observeAsState(0)
    val calorias by menuViewModel.calorias.observeAsState(0)
    val distancia by menuViewModel.distancia.observeAsState(0f)
    val weeklyData by menuViewModel.weeklyData.observeAsState(emptyList())
    val puntuacion by menuViewModel.puntuacion.observeAsState(0)
    val puntuacionTotal by menuViewModel.puntuacionTotal.observeAsState(0)

    val dayAbbreviations = mapOf(
        "lunes" to "L",
        "martes" to "M",
        "miércoles" to "X",
        "jueves" to "J",
        "viernes" to "V",
        "sábado" to "S",
        "domingo" to "D"
    )
    val todayDayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()).lowercase(Locale.getDefault())
    val todayDayAbbreviation = dayAbbreviations[todayDayName] ?: ""

    LaunchedEffect(Unit) {
        menuViewModel.loadMostRecentEntrenamiento()
        menuViewModel.loadData()
        menuViewModel.loadWeeklyData()
        menuViewModel.loadTotalScore()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            DailyCard(
                title = "Pasos hoy",
                todayValue = pasos.toString(),
                unit = "pasos",
                iconResId = R.drawable.footprint
            )
            Spacer(modifier = Modifier.height(16.dp))
            DailyCard(
                title = "Puntuación",
                todayValue = puntuacionTotal.toString(),
                unit = "puntos",
                iconResId = R.drawable.rewarded
            )
            Spacer(modifier = Modifier.height(16.dp))
            DailyCard(
                title = "Calorías quemadas hoy",
                todayValue = calorias.toString(),
                unit = "kcal",
                iconResId = R.drawable.local_fire_department
            )
            Spacer(modifier = Modifier.height(16.dp))
            DailyCard(
                title = "Distancia recorrida hoy",
                todayValue = String.format("%.2f", distancia),
                unit = "km",
                iconResId = R.drawable.world
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyCard(
                title = "Pasos Semanales",
                weeklyDayData = generateWeeklySummary(weeklyData, "steps", pasos, todayDayAbbreviation),
                todayValue = pasos.toString(),
                todayDayName = todayDayAbbreviation
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyCard(
                title = "Calorías Semanales Quemadas",
                weeklyDayData = generateWeeklySummary(weeklyData, "calories", calorias, todayDayAbbreviation),
                todayValue = calorias.toString(),
                todayDayName = todayDayAbbreviation
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyCard(
                title = "Distancia Semanal",
                weeklyDayData = generateWeeklySummary(weeklyData, "distance", String.format("%.2f", distancia), todayDayAbbreviation),
                todayValue = String.format("%.2f", distancia),
                todayDayName = todayDayAbbreviation
            )
            Spacer(modifier = Modifier.height(16.dp))
            MostRecentEntrenamientoCard(menuViewModel)
        }
    }
}

@Composable
fun DailyCard(title: String, todayValue: String, unit: String, iconResId: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.verdeClaro))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 19.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$todayValue $unit",
                    color = Color.White,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun WeeklyCard(
    title: String,
    weeklyDayData: List<WeeklyDayData>,
    todayValue: String,
    todayDayName: String
) {
    val adjustedWeeklyData = weeklyDayData.map {
        if (it.dayName.equals(todayDayName, ignoreCase = true)) {
            WeeklyDayData(it.dayName, todayValue)
        } else {
            it
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor =  colorResource(id = R.color.verdeClaro))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 19.sp,
                modifier = Modifier.padding(bottom  = 8.dp),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                adjustedWeeklyData.forEach { data ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = data.value,
                            color = Color.White,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = data.dayName,
                            color = Color.White,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MostRecentEntrenamientoCard(menuViewModel: MenuViewModel) {
    val mostRecentEntrenamiento by menuViewModel.mostRecentEntrenamiento.observeAsState()

    mostRecentEntrenamiento?.let { data ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor =  colorResource(id = R.color.verdeClaro))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Último Entrenamiento",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Pasos: ${data["steps"]}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "Distancia: ${String.format("%.2f", data["distance"] as Double)} km",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "Calorías: ${data["calories"]} kcal",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "Tiempo: ${data["time"]} segundos",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}




fun generateWeeklySummary(
    weeklyDayData: List<UserData>,
    dataType: String,
    todayValue: Any?,
    todayDayName: String
): List<WeeklyDayData> {
    val dayAbbreviations = listOf("L", "M", "X", "J", "V", "S", "D")
    val fullWeekData = mutableListOf<WeeklyDayData>()
    val tempData = mutableMapOf<String, String>()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    // Mapear los datos almacenados a sus abreviaciones correctas
    weeklyDayData.forEach { data ->
        calendar.time = dateFormat.parse(data.date)!!
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayAbbreviation = dayAbbreviations[(dayOfWeek + 5) % 7] // Ajuste para que domingo sea el último día
        tempData[dayAbbreviation] = when (dataType) {
            "steps" -> data.steps.toString()
            "calories" -> data.calories.toString()
            "distance" -> "%.2f".format(data.distance)
            else -> "0"
        }
    }

    // Asignar el valor de todayValue al día de hoy y valores almacenados a los otros días
    for (day in dayAbbreviations) {
        val value = if (day == todayDayName) {
            todayValue?.toString() ?: "0"
        } else {
            tempData[day] ?: "0"
        }
        fullWeekData.add(WeeklyDayData(day, value))

        // Añadir log para ver el valor de cada día de la semana
        Log.d("WeeklySummary", "Day: $day, Value: $value")
    }

    // Ordenar la lista final en el orden de lunes a domingo
    fullWeekData.sortBy { dayAbbreviations.indexOf(it.dayName) }

    return fullWeekData
}

