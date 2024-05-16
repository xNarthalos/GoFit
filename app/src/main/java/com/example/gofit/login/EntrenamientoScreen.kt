package com.example.gofit.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Entrenamiento(stepCountViewModel: StepCountViewModel = viewModel()) {
    var isRunning by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf(0L) }
    var buttonColor by remember { mutableStateOf(Color(0xFF5DCF14)) }
    var buttonText by remember { mutableStateOf("GO") }
    var buttonOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var isButtonEnabled by remember { mutableStateOf(true) }

    val pasos by stepCountViewModel.pasos.observeAsState(0)
    val distancia by stepCountViewModel.distancia.observeAsState(0f)
    val calorias by stepCountViewModel.calorias.observeAsState(0f)

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Entrenamiento",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        StatCard(
            title = "Pasos dados",
            value = pasos.toString(),
            unit = "pasos",
            backgroundColor = Color(0xFF5DCF14),

        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Calorías quemadas",
            value =calorias.toInt().toString(),
            unit = "kcal",
            backgroundColor = Color(0xFF5DCF14)
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Tiempo activo",
            value = "${time / 60} minutos : ${time % 60} segundos",
            unit = "",
            backgroundColor = Color(0xFF5DCF14)
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Distancia recorrida",
            value = String.format("%.2f", distancia),
            unit = "km",
            backgroundColor = Color(0xFF5DCF14)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .offset(buttonOffset.x.dp, buttonOffset.y.dp)
                .size(100.dp)
                .background(buttonColor, shape = CircleShape)
                .clickable(enabled = isButtonEnabled) {
                    if (isRunning) {
                        scope.launch {
                            delay(3000)
                            isRunning = false
                            buttonColor = Color(0xFF5DCF14)
                            buttonText = "GO"
                            buttonOffset = Offset(0f, 0f)
                            isButtonEnabled = false
                            delay(1000)
                            isButtonEnabled = true
                        }
                    } else {
                        isRunning = true
                        buttonColor = Color.Red
                        buttonText = "STOP"
                        buttonOffset = Offset(100f, 0f)
                        scope.launch {
                            while (isRunning) {
                                delay(1000)
                                time++
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = buttonText, color = Color.White, fontSize = 24.sp)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, unit: String, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
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
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "$value $unit",
                color = Color.White,
                fontSize = 14.sp,
                style = MaterialTheme.typography.headlineMedium,

            )
        }
    }
}
