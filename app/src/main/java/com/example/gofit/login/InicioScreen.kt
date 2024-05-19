package com.example.gofit.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Inicio(stepCountViewModel: StepCountViewModel = viewModel()) {


    val pasos by stepCountViewModel.pasos.observeAsState(0)
    val calorias by stepCountViewModel.calorias.observeAsState(0)
    val distancia by stepCountViewModel.distancia.observeAsState(0f)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        MyCard(
            title = "Pasos dados hoy",
            value = pasos.toString(),
            unit = "pasos"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyCard(
            title = "Calorías quemadas hoy",
            value = calorias.toString(),
            unit = "kcal"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyCard(
            title = "Distancia recorrida hoy",
            value = String.format("%.2f", distancia),
            unit = "km"
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyCard(
            title = "Tiempo activo hoy",
            value = "0 segundos",
            unit = ""
        )
    }
}

@Composable
fun MyCard(title: String, value: String, unit: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5DCF14))
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
