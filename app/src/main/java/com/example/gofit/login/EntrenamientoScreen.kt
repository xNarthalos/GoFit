package com.example.gofit.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
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
    var isPaused by remember { mutableStateOf(false) }
    var buttonColor by remember { mutableStateOf(Color(0xFF5DCF14)) }
    var buttonOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var isButtonEnabled by remember { mutableStateOf(true) }

    val pasosCronometro by stepCountViewModel.pasosCronometro.observeAsState(0)
    val distanciaCronometro by stepCountViewModel.distanciaCronometro.observeAsState(0f)
    val caloriasCronometro by stepCountViewModel.caloriasCronometro.observeAsState(0)
    val time by stepCountViewModel.tiempoCronometro.observeAsState(0L)

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        StatCard(
            title = "Pasos dados",
            value = pasosCronometro.toString(),
            unit = "pasos",
            backgroundColor = Color(0xFF6BF711)
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Calorías quemadas",
            value = caloriasCronometro.toString(),
            unit = "kcal",
            backgroundColor = Color(0xFF6BF711)
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Tiempo activo",
            value = "${time / 60} minutos : ${time % 60} segundos",
            unit = "",
            backgroundColor = Color(0xFF6BF711)
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Distancia recorrida",
            value = String.format("%.2f", distanciaCronometro),
            unit = "km",
            backgroundColor = Color(0xFF6BF711)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (isRunning) {
                if (isPaused) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.Green, shape = CircleShape)
                            .clickable(enabled = isButtonEnabled) {
                                isPaused = false
                                buttonColor = Color.Red
                                buttonOffset = Offset(100f, 0f)
                                stepCountViewModel.resumeCronometro()
                                scope.launch {
                                    while (isRunning && !isPaused) {
                                        delay(1000)
                                        stepCountViewModel.incrementTime()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Continue",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.Red, shape = CircleShape)
                            .clickable(enabled = isButtonEnabled) {
                                scope.launch {
                                    delay(3000)
                                    isRunning = false
                                    isPaused = false
                                    buttonColor = Color(0xFF6BF711)
                                    buttonOffset = Offset(0f, 0f)
                                    isButtonEnabled = false
                                    delay(1000)
                                    isButtonEnabled = true
                                    stepCountViewModel.resetCronometro()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stop,
                            contentDescription = "Stop",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.Red, shape = CircleShape)
                            .clickable(enabled = isButtonEnabled) {
                                isPaused = true
                                buttonColor = Color.Green
                                buttonOffset = Offset(100f, 0f)
                                stepCountViewModel.pauseCronometro()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(buttonColor, shape = CircleShape)
                        .clickable(enabled = isButtonEnabled) {
                            isRunning = true
                            buttonColor = Color.Red
                            buttonOffset = Offset(100f, 0f)
                            stepCountViewModel.startCronometro()
                            scope.launch {
                                while (isRunning && !isPaused) {
                                    delay(1000)
                                    stepCountViewModel.incrementTime()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
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
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
