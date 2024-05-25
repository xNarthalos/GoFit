package com.example.gofit.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Perfil() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6BF711)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Objetivo de Actividad",
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider(color = Color.White, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        ObjetivoDropdown()
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6BF711)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tus Datos",
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider(color = Color.White, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        UserDataFields()
                    }
                }
            }
        }
    }
}

@Composable
fun ObjetivoDropdown() {
    var dailyStepsGoal by remember { mutableStateOf(5000f) }
    var goalSliderDialogOpen by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Objetivo de pasos diario",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { goalSliderDialogOpen = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = "${dailyStepsGoal.toInt()} pasos", color = Color.White)
            }
        }
        if (goalSliderDialogOpen) {
            AlertDialog(
                onDismissRequest = { goalSliderDialogOpen = false },
                title = { Text(text = "Selecciona tu objetivo de pasos diario") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    if (dailyStepsGoal > 500) dailyStepsGoal -= 500
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("-", fontSize = 24.sp, color = Color.White)
                            }
                            Slider(
                                value = dailyStepsGoal,
                                onValueChange = { dailyStepsGoal = it },
                                valueRange = 500f..90000f,
                                steps = ((90000f - 500f) / 500f).toInt() - 1,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (dailyStepsGoal < 90000) dailyStepsGoal += 500
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("+", fontSize = 24.sp, color = Color.White)
                            }
                        }
                        Text(
                            text = "${(dailyStepsGoal / 500).toInt() * 500} pasos",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { goalSliderDialogOpen = false }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { goalSliderDialogOpen = false }) {
                        Text("Cancelar")
                    }
                },

            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataFields() {
    var expanded by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("Hombre") }
    val genderOptions = listOf("Hombre", "Mujer")
    var height by remember { mutableStateOf(160f) }
    var weight by remember { mutableStateOf(60f) }
    var birthDate by remember { mutableStateOf(Calendar.getInstance()) }
    var heightSliderDialogOpen by remember { mutableStateOf(false) }
    var weightSliderDialogOpen by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Género",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = gender, color = Color.White)
            }
            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.background(Color(0xFF6BF711)),
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            gender = option
                            expanded = false
                        },
                        text = {
                            Text(
                                text = option,
                                color = Color.White
                            )
                        }
                    )
                }
            }
        }
        Text(
            text = "Altura",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)) {
            OutlinedButton(
                onClick = { heightSliderDialogOpen = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = "${height.toInt()} cm", color = Color.White)
            }
        }
        if (heightSliderDialogOpen) {
            AlertDialog(
                onDismissRequest = { heightSliderDialogOpen = false },
                title = { Text(text = "Selecciona tu altura") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    if (height > 100) height -= 1
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("-", fontSize = 24.sp, color = Color.White)
                            }
                            Slider(
                                value = height,
                                onValueChange = { height = it },
                                valueRange = 100f..250f,
                                steps = 150,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (height < 250) height += 1
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("+", fontSize = 24.sp, color = Color.White)
                            }
                        }
                        Text(
                            text = "${height.toInt()} cm",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { heightSliderDialogOpen = false }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { heightSliderDialogOpen = false }) {
                        Text("Cancelar")
                    }
                },

                )
        }
        Text(
            text = "Peso",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)) {
            OutlinedButton(
                onClick = { weightSliderDialogOpen = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = "${weight.toInt()} kg", color = Color.White)
            }
        }
        if (weightSliderDialogOpen) {
            AlertDialog(
                onDismissRequest = { weightSliderDialogOpen = false },
                title = { Text(text = "Selecciona tu peso") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    if (weight > 30) weight -= 1
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("-", fontSize = 24.sp, color = Color.White)
                            }
                            Slider(
                                value = weight,
                                onValueChange = { weight = it },
                                valueRange = 30f..200f,
                                steps = 170,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (weight < 200) weight += 1
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("+", fontSize = 24.sp, color = Color.White)
                            }
                        }
                        Text(
                            text = "${weight.toInt()} kg",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { weightSliderDialogOpen = false }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { weightSliderDialogOpen = false }) {
                        Text("Cancelar")
                    }
                },

                )
        }
        Text(
            text = "Fecha de Nacimiento",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(modifier = Modifier

            .fillMaxWidth()
            .padding(bottom = 8.dp)) {
            OutlinedButton(

                onClick = { showDatePickerDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(birthDate.time), color = Color.White)
            }
        }
        if (showDatePickerDialog) {
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                initialDate = birthDate,
                onDateSelected = { nuevaFecha ->
                    birthDate = nuevaFecha
                    showDatePickerDialog = false
                }
            )
        }
    }
}