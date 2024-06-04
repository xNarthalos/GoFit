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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gofit.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun Perfil(menuViewModel: MenuViewModel) {

   DisposableEffect(Unit) {
        onDispose {
            menuViewModel.guardarDatosUsuario()

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
                    colors = CardDefaults.cardColors(containerColor =  colorResource(id = R.color.verdeClaro)),
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
                        DaylyGoal(menuViewModel)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor =  colorResource(id = R.color.verdeClaro)),
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
                        UserDataFields(menuViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DaylyGoal(stepsViewModel: MenuViewModel) {
    val dailyStepsGoal by stepsViewModel._dailyStepsGoal.collectAsState()
    var goalSliderDialogOpen by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
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
                                    if (dailyStepsGoal > 500) stepsViewModel.setDailyStepsGoal(dailyStepsGoal - 500)
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Text("-", fontSize = 24.sp, color = Color.White)
                            }
                            Slider(
                                value = dailyStepsGoal,
                                onValueChange = { stepsViewModel.setDailyStepsGoal(it) },
                                valueRange = 500f..35000f,
                                steps = ((35000f - 500f) / 500f).toInt() - 1,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (dailyStepsGoal < 35000) stepsViewModel.setDailyStepsGoal(dailyStepsGoal + 500)
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
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "¿Como obtengo Puntuación?",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(
                color = Color.White,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "-Obten 1 punto por cada 200 pasos dados.\n-Al cumplir un objetivo diario obtienes el equivalente en puntos de esos pasos doblados.",
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}



@Composable
fun UserDataFields(menuViewModel: MenuViewModel) {
    val gender by menuViewModel.gender.observeAsState()
    val height by menuViewModel.height.observeAsState()
    val weight by menuViewModel.weight.observeAsState()
    val birthDate by menuViewModel.birthDate.observeAsState()
    val heightSliderDialogOpen by menuViewModel.heightSliderDialogOpen.observeAsState(false)
    val weightSliderDialogOpen by menuViewModel.weightSliderDialogOpen.observeAsState(false)
    val showDatePickerDialog by menuViewModel.showDatePickerDialog.observeAsState(false)

    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Hombre", "Mujer")

    Column {

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
                modifier = Modifier.background( colorResource(id = R.color.verdeClaro)),
                onDismissRequest = { expanded = false }) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        menuViewModel.setGender(option)
                        expanded = false
                    }, text = { Text(text = option, color = Color.White) })
                }
            }
        }


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
                onClick = { menuViewModel.toggleHeightSliderDialog(true) },
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
                onDismissRequest = { menuViewModel.toggleHeightSliderDialog(false) },
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
                        menuViewModel.setHeight(tempHeight)
                        menuViewModel.toggleHeightSliderDialog(false)
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { menuViewModel.toggleHeightSliderDialog(false) }) { Text("Cancelar") }
                })
        }


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
                onClick = { menuViewModel.toggleWeightSliderDialog(true) },
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
                onDismissRequest = { menuViewModel.toggleWeightSliderDialog(false) },
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
                        menuViewModel.setWeight(tempWeight)
                        menuViewModel.toggleWeightSliderDialog(false)
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { menuViewModel.toggleWeightSliderDialog(false) }) { Text("Cancelar") }
                })
        }


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
                onClick = { menuViewModel.toggleDatePickerDialog(true) },
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
                onDismissRequest = { menuViewModel.toggleDatePickerDialog(false) },
                initialDate = birthDate ?: Calendar.getInstance(),
                onDateSelected = { nuevaFecha ->
                    menuViewModel.setBirthDate(nuevaFecha)
                    menuViewModel.toggleDatePickerDialog(false)
                })
        }
    }
}