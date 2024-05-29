package home

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                unit = "pasos"
            )
            Spacer(modifier = Modifier.height(16.dp))
            DailyCard(
                title = "Calorías quemadas hoy",
                todayValue = calorias.toString(),
                unit = "kcal"
            )
            Spacer(modifier = Modifier.height(16.dp))
            DailyCard(
                title = "Distancia recorrida hoy",
                todayValue = String.format("%.2f", distancia),
                unit = "km"
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
fun DailyCard(title: String, todayValue: String, unit: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6BF711))
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
            Text(
                text = "$todayValue $unit",
                color = Color.White,
                fontSize = 14.sp,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6BF711))
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6BF711))
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
    dayAbbreviations.forEachIndexed { index, day ->
        val dataForDay = weeklyDayData.getOrNull(index)
        val value = when {
            day.equals(todayDayName, ignoreCase = true) -> todayValue?.toString() ?: "0"
            dataType == "steps" -> dataForDay?.steps?.toString() ?: "0"
            dataType == "calories" -> dataForDay?.calories?.toString() ?: "0"
            dataType == "distance" -> dataForDay?.distance?.let { "%.2f".format(it) } ?: "0"
            else -> "0"
        }
        fullWeekData.add(WeeklyDayData(day, value))
    }
    return fullWeekData
}
