package com.example.gofit.login

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun RegistroScreen(viewModel: RegistroViewModel, navigationController: NavHostController) {

    Box(
        Modifier
            .fillMaxSize()

            .background(Color.White)


    ) {
        HeaderRegistro(Modifier.align(Alignment.TopEnd))
        BodyRegistro(Modifier.align(Alignment.TopCenter),viewModel)
        FooterRegistro(Modifier.align(Alignment.BottomCenter))
    }

}

@Composable
fun FooterRegistro(modifier:Modifier) {
    Spacer(modifier = Modifier.size(60.dp))
}

@Composable
fun HeaderRegistro(align: Modifier) {

}
@Composable
fun BodyRegistro(modifier: Modifier, viewModel: RegistroViewModel) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val repetirPassword: String by viewModel.repetirPassword.observeAsState(initial = "")
    val userName: String by viewModel.userName.observeAsState(initial = "")


    val fechaDeNacimiento: Calendar by viewModel.fechaDeNacimiento.observeAsState(initial = Calendar.getInstance())

    Column(modifier=modifier) {
        Spacer(modifier = Modifier.size(180.dp))
        RegistroEmail(email) { viewModel.onRegistroChanged(it,password, userName,repetirPassword) }
        Spacer(modifier = Modifier.size(30.dp))
        RegistroNombreUsuario(userName){viewModel.onRegistroChanged(email, password, it,repetirPassword)}
        Spacer(modifier = Modifier.size(30.dp))
        FechaNacimiento(
            dateOfBirth = fechaDeNacimiento,
            onDateSelected = { nuevaFecha ->
                viewModel.setFechaDeNacimiento(nuevaFecha)
            }
        )

        Spacer(modifier = Modifier.size(30.dp))
        RegistroPassword(password){ viewModel.onRegistroChanged(email,it, userName,repetirPassword) }
        Spacer(modifier = Modifier.size(30.dp))
        RepetirPassword(repetirPassword) { viewModel.onRegistroChanged(email, password, userName, it) }
        Spacer(modifier = Modifier.size(30.dp))
        BotonRegistro()
    }
}

@Composable
fun BotonRegistro() {
    Button(
        onClick = {},
        enabled = true,
        modifier = Modifier.fillMaxWidth().padding(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5DCF14),
            disabledContainerColor = Color(0xFF5DCF14),
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "Confirmar Registro")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepetirPassword(password:String,onTextChanged: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        placeholder = { Text(text = "Repetir Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFB2B2B2),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color(0xFFFAFAFA)
        ),
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),visualTransformation = PasswordVisualTransformation()
        /* trailingIcon = {
             val icon = if (passwordVisibility) {
                 Icons.Filled.VisibilityOff
             } else {
                 Icons.Filled.Visibility
             }
             IconButton(onClick = { viewModel.togglePasswordVisibility(passwordVisibility) }) {
                 Icon(imageVector = icon, contentDescription = "Show password")
             }
         },
         visualTransformation = if (passwordVisibility) {
             VisualTransformation.None
         } else {
             PasswordVisualTransformation()
         }*/
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaNacimiento(dateOfBirth: Calendar ,onDateSelected: (Calendar) -> Unit) {
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Column {
        // Campo de texto para mostrar la fecha de nacimiento actual
        TextField(
            value = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateOfBirth.time),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Fecha de Nacimiento") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(0xFFB2B2B2),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color(0xFFFAFAFA)
            ),
            trailingIcon = {
                IconButton(onClick = { showDatePickerDialog = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            }
        )

        // Diálogo de selección de fecha
        if (showDatePickerDialog) {
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                initialDate = dateOfBirth,
                onDateSelected = { nuevaFecha ->
                    onDateSelected(nuevaFecha)
                    showDatePickerDialog = false
                }
            )
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    initialDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    val context = LocalContext.current
    val calendar by remember { mutableStateOf(initialDate) }

    // DatePickerDialog
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // Set max date
        datePicker.maxDate = System.currentTimeMillis()

        // Set onDismissListener
        setOnDismissListener { onDismissRequest() }

        // Show dialog
        show()
    }
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroPassword(password: String,onTextChanged: (String) -> Unit) {

    TextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        placeholder = { Text(text = "Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFB2B2B2),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color(0xFFFAFAFA)
        ),
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation()
       /* trailingIcon = {
            val icon = if (passwordVisibility) {
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { viewModel.togglePasswordVisibility(passwordVisibility) }) {
                Icon(imageVector = icon, contentDescription = "Show password")
            }
        },
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }*/
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNombreUsuario(userName :String, onTextChanged: (String) -> Unit)  {
    TextField(
        value = userName,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Nombre de Usuario") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFB2B2B2),
            containerColor = Color(0xFFFAFAFA),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroEmail(email: String, onTextChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = {  onTextChanged(it)},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Email") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFB2B2B2),
            containerColor = Color(0xFFFAFAFA),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )

}


