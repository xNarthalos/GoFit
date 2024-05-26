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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gofit.data.UserData
import com.example.gofit.data.WeeklyDayData
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Inicio(stepCountViewModel: StepCountViewModel = viewModel()) {

    val pasos by stepCountViewModel.pasos.observeAsState(0)
    val calorias by stepCountViewModel.calorias.observeAsState(0)
    val distancia by stepCountViewModel.distancia.observeAsState(0f)
    val weeklyData by stepCountViewModel.weeklyData.observeAsState(emptyList())

    val todayDayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()).take(1).uppercase()

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
                weeklyDayData = generateWeeklySummary(weeklyData, "steps", pasos, todayDayName),
                todayValue = pasos.toString(),
                todayDayName = todayDayName
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyCard(
                title = "Calorías Semanales Quemadas",
                weeklyDayData = generateWeeklySummary(weeklyData, "calories", calorias, todayDayName),
                todayValue = calorias.toString(),
                todayDayName = todayDayName
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyCard(
                title = "Distancia Semanal",
                weeklyDayData = generateWeeklySummary(weeklyData, "distance", String.format("%.2f", distancia), todayDayName),
                todayValue = String.format("%.2f", distancia),
                todayDayName = todayDayName
            )
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
                modifier = Modifier.padding(bottom = 8.dp),
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