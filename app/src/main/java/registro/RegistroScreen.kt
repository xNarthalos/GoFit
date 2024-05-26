package registro

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.gofit.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(viewModel: RegistroViewModel, navigationController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GoFit - Registro",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF5DCF14)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {

            BodyRegistro(Modifier, viewModel, navigationController)
        }
    }
}






@Composable
fun BodyRegistro(modifier: Modifier, viewModel: RegistroViewModel, navigationController: NavHostController) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val repetirPassword: String by viewModel.repetirPassword.observeAsState(initial = "")
    val userName: String by viewModel.userName.observeAsState(initial = "")
    val registroEnable: Boolean by viewModel.registroEnable.observeAsState(initial = false)
    val passwordVisibility: Boolean by viewModel.passwordVisibility.observeAsState(initial = false)
    val repeatPasswordVisibility: Boolean by viewModel.repeatPasswordVisibility.observeAsState(initial = false)



    val fechaDeNacimiento: Calendar by viewModel.fechaDeNacimiento.observeAsState(initial = Calendar.getInstance())

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.size(60.dp))
        RegistroEmail(
            email,
            { viewModel.onRegistroChanged(it, password, userName, repetirPassword) },
            viewModel
        )
        Spacer(modifier = Modifier.size(30.dp))
        RegistroNombreUsuario(userName) {
            viewModel.onRegistroChanged(
                email,
                password,
                it,
                repetirPassword
            )
        }
        Spacer(modifier = Modifier.size(30.dp))
        FechaNacimiento(
            dateOfBirth = fechaDeNacimiento,
            onDateSelected = { nuevaFecha ->
                viewModel.setFechaDeNacimiento(nuevaFecha)
            }
        )

        Spacer(modifier = Modifier.size(30.dp))


        RegistroPassword(
            password,
            passwordVisibility,
            onTextChanged = { viewModel.onRegistroChanged(email, it, userName, repetirPassword) },
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.size(30.dp))
        RepetirPassword(
            repetirPassword,
            repeatPasswordVisibility,
            onTextChanged = { viewModel.onRegistroChanged(email, password, userName, it) },
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.size(30.dp))
        BotonRegistro(registroEnable,email,password,viewModel,navigationController)
    }
}

@Composable
fun BotonRegistro(registroEnable: Boolean, email :String, password: String, viewModel: RegistroViewModel, navigationController: NavHostController ) {
    Button(
        onClick = {viewModel.registro(email,password)
                   navigationController.navigate("Menu")},
        enabled = registroEnable,
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
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
fun RepetirPassword(
    password: String,
    repeatPasswordVisibility: Boolean,
    onTextChanged: (String) -> Unit,
    viewModel: RegistroViewModel
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor =
        if (password != viewModel.password.value && password.isNotEmpty() && !isFocused) Color.Red else Color(
            0xFFFAFAFA
        )

    TextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        label = { Text(text = "Repetir Contraseña") },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
                viewModel.setFocus(isFocused)
            },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFB2B2B2),
            focusedIndicatorColor = borderColor,
            unfocusedIndicatorColor = borderColor,
            containerColor = Color(0xFFFAFAFA)
        ),
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val icon = if (repeatPasswordVisibility) {
                painterResource(id = R.drawable.visibility_off)
            } else {
                painterResource(id = R.drawable.visibility)
            }
            IconButton(onClick = { viewModel.toggleRepeatPasswordVisibility(repeatPasswordVisibility) }) {
                Icon(painter = icon, contentDescription = "Show password")
            }
        },
        visualTransformation = if (repeatPasswordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )

    if (!isFocused && password != viewModel.password.value && password.isNotEmpty()) {
        Text(
            text = "Las contraseñas no coinciden",
            style = TextStyle(color = Color.Red),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
fun FechaNacimiento(
    dateOfBirth: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(horizontalAlignment = CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(
                onClick = { showDatePickerDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Transparent),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFFAFAFA),
                    contentColor = Color(0xFFB2B2B2)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Fecha de Nacimiento",
                        fontSize = 14.sp,
                        color = Color(0xFFB2B2B2),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dateOfBirth.time),
                        color = Color(0xFFB2B2B2)
                    )
                }
            }
        }
        if (showDatePickerDialog) {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    onDateSelected(selectedDate)
                    showDatePickerDialog = false
                },
                dateOfBirth.get(Calendar.YEAR),
                dateOfBirth.get(Calendar.MONTH),
                dateOfBirth.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
                show()
            }
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

        datePicker.maxDate = System.currentTimeMillis()


        setOnDismissListener { onDismissRequest() }


        show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroPassword(
    password: String,
    passwordVisibility: Boolean,
    onTextChanged: (String) -> Unit,
    viewModel: RegistroViewModel
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor =
        if (!viewModel.isValidPassword(password) && password.isNotEmpty() && !isFocused) Color.Red else Color(
            0xFFFAFAFA
        )
    TextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        label = { Text(text = "Contraseña") },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
                viewModel.setFocus(isFocused)
            },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFB2B2B2),
            focusedIndicatorColor = borderColor,
            unfocusedIndicatorColor = borderColor,
            containerColor = Color(0xFFFAFAFA)
        ),
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

        trailingIcon = {
            val icon = if (passwordVisibility) {
                painterResource(id = R.drawable.visibility_off)
            } else {
                painterResource(id = R.drawable.visibility)
            }
            IconButton(onClick = { viewModel.togglePasswordVisibility(passwordVisibility) }) {
                Icon(painter = icon, contentDescription = "Show password")
            }
        }
        ,
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }

    )
    if (!isFocused && password.length < 6 && password.isNotEmpty()) {
        Text(
            text = "La contraseña debe tener al menos 6 caracteres",
            style = TextStyle(color = Color.Red),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNombreUsuario(userName: String, onTextChanged: (String) -> Unit) {
    TextField(
        value = userName,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Nombre de Usuario") },
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
fun RegistroEmail(email: String, onEmailChanged: (String) -> Unit, viewModel: RegistroViewModel) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor =
        if (!viewModel.isValidEmail(email) && email.isNotEmpty() && !isFocused) Color.Red else Color(
            0xFFFAFAFA
        )

    Column {
        TextField(
            value = email,
            onValueChange = { onEmailChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocused = it.isFocused
                    viewModel.setFocus(isFocused)
                },
            label = { Text(text = "Email") },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(0xFFB2B2B2),
                focusedIndicatorColor = borderColor,
                unfocusedIndicatorColor = borderColor,
                containerColor = Color(0xFFFAFAFA)
            )
        )

        if (!viewModel.isValidEmail(email) && !isFocused && email.isNotEmpty()) {
            Text(
                text = "Email no válido",
                style = TextStyle(color = Color.Red),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}



