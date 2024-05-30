package home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gofit.R

@Composable
fun Entrenamiento(menuViewModel: MenuViewModel = viewModel()) {
    val isRunning by menuViewModel.isRunning.observeAsState(false)
    val isPaused by menuViewModel.isPaused.observeAsState(false)

    val pasosCronometro by menuViewModel.pasosCronometro.observeAsState(0)
    val distanciaCronometro by menuViewModel.distanciaCronometro.observeAsState(0f)
    val caloriasCronometro by menuViewModel.caloriasCronometro.observeAsState(0)
    val time by menuViewModel.tiempoCronometro.observeAsState(0L)
    val verdeClaro = colorResource(id = R.color.verdeClaro)
    val buttonColor = remember { mutableStateOf(verdeClaro) }
    var isButtonEnabled by remember { mutableStateOf(true) }

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
            backgroundColor = verdeClaro,
            icon =R.drawable.footprint
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Calorías quemadas",
            value = caloriasCronometro.toString(),
            unit = "kcal",
            backgroundColor = verdeClaro,
            icon = R.drawable.local_fire_department
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Tiempo activo",
            value = "${time / 60} minutos : ${time % 60} segundos",
            unit = "",
            backgroundColor = verdeClaro,
            icon = R.drawable.timer
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Distancia recorrida",
            value = String.format("%.2f", distanciaCronometro),
            unit = "km",
            backgroundColor = verdeClaro,
            icon = R.drawable.world
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
                            .background(buttonColor.value, shape = CircleShape)
                            .clickable(enabled = isButtonEnabled) {
                                menuViewModel.resumeCronometro()
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
                            .background(colorResource(id = R.color.rojoOscuro), shape = CircleShape)
                            .clickable(enabled = isButtonEnabled) {
                                menuViewModel.resetCronometro()
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
                            .background(colorResource(id = R.color.rojoOscuro), shape = CircleShape)
                            .clickable(enabled = isButtonEnabled) {
                                menuViewModel.pauseCronometro()
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
                        .background(buttonColor.value, shape = CircleShape)
                        .clickable(enabled = isButtonEnabled) {
                            menuViewModel.startCronometro()
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
fun StatCard(
    title: String,
    value: String,
    unit: String,
    backgroundColor: Color,
    icon: Int
) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$value $unit",
                    color = Color.White,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}