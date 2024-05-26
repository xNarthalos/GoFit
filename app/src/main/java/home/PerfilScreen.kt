package home

import registro.DatePickerDialog
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Perfil(viewModel: PerfilViewModel) {
    DisposableEffect(Unit) {
        onDispose {
            viewModel.guardarDatosUsuario()
        }
    }

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
                        DaylyGoal()
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
                        UserDataFields(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DaylyGoal() {
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

@Composable
fun UserDataFields(viewModel: PerfilViewModel) {
    val gender by viewModel.gender.observeAsState()
    val height by viewModel.height.observeAsState()
    val weight by viewModel.weight.observeAsState()
    val birthDate by viewModel.birthDate.observeAsState()
    val heightSliderDialogOpen by viewModel.heightSliderDialogOpen.observeAsState(false)
    val weightSliderDialogOpen by viewModel.weightSliderDialogOpen.observeAsState(false)
    val showDatePickerDialog by viewModel.showDatePickerDialog.observeAsState(false)

    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Hombre", "Mujer")

    Column {
        // Gender
        Text(
            text = "Género",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = gender ?: "---", color = Color.White)
            }
            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.background(Color(0xFF6BF711)),
                onDismissRequest = { expanded = false }) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        viewModel.setGender(option)
                        expanded = false
                    }, text = { Text(text = option, color = Color.White) })
                }
            }
        }

        // Height
        Text(
            text = "Altura",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.toggleHeightSliderDialog(true) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = height?.let { "${it.toInt()} cm" } ?: "---", color = Color.White)
            }
        }
        if (heightSliderDialogOpen) {
            var tempHeight by remember { mutableStateOf(height ?: 160f) }
            AlertDialog(
                onDismissRequest = { viewModel.toggleHeightSliderDialog(false) },
                title = { Text(text = "Selecciona tu altura") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { if (tempHeight > 100) tempHeight -= 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("-", fontSize = 24.sp, color = Color.White)
                            }
                            Slider(
                                value = tempHeight,
                                onValueChange = { tempHeight = it },
                                valueRange = 100f..250f,
                                steps = 150,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { if (tempHeight < 250) tempHeight += 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("+", fontSize = 24.sp, color = Color.White)
                            }
                        }
                        Text(
                            text = "${tempHeight.toInt()} cm",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.setHeight(tempHeight)
                        viewModel.toggleHeightSliderDialog(false)
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.toggleHeightSliderDialog(false) }) { Text("Cancelar") }
                })
        }

        // Weight
        Text(
            text = "Peso",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.toggleWeightSliderDialog(true) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = weight?.let { "${it.toInt()} kg" } ?: "---", color = Color.White)
            }
        }
        if (weightSliderDialogOpen) {
            var tempWeight by remember { mutableStateOf(weight ?: 60f) }
            AlertDialog(
                onDismissRequest = { viewModel.toggleWeightSliderDialog(false) },
                title = { Text(text = "Selecciona tu peso") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { if (tempWeight > 30) tempWeight -= 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("-", fontSize = 24.sp, color = Color.White)
                            }
                            Slider(
                                value = tempWeight,
                                onValueChange = { tempWeight = it },
                                valueRange = 40f..170f,
                                steps = 170,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { if (tempWeight < 200) tempWeight += 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("+", fontSize = 24.sp, color = Color.White)
                            }
                        }
                        Text(
                            text = "${tempWeight.toInt()} kg",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.setWeight(tempWeight)
                        viewModel.toggleWeightSliderDialog(false)
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.toggleWeightSliderDialog(false) }) { Text("Cancelar") }
                })
        }

        // Birth Date
        Text(
            text = "Fecha de Nacimiento",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.toggleDatePickerDialog(true) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = birthDate?.let {
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(it.time)
                } ?: "---", color = Color.White)
            }
        }
        if (showDatePickerDialog) {
            DatePickerDialog(
                onDismissRequest = { viewModel.toggleDatePickerDialog(false) },
                initialDate = birthDate ?: Calendar.getInstance(),
                onDateSelected = { nuevaFecha ->
                    viewModel.setBirthDate(nuevaFecha)
                    viewModel.toggleDatePickerDialog(false)
                })
        }
    }
}