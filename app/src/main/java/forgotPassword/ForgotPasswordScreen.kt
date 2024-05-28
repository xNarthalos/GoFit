package forgotPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gofit.login.ImageLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(viewModel: ForgotPasswordViewModel, navigationController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GoFit - Recuperar Contraseña",
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
            Spacer(modifier = Modifier.height(16.dp))
            ImageLogo(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(16.dp))
            BodyForgot(Modifier.align(Alignment.CenterHorizontally), viewModel, navigationController)
        }
    }
}

@Composable
fun ButtonForgot(loginButtonEnable: Boolean, email: String, viewModel: ForgotPasswordViewModel, navigationController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Correo Enviado") },
            text = { Text(text = "Revise su correo electrónico para cambiar su contraseña.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navigationController.navigate("LoginScreen") {
                            popUpTo(navigationController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Button(
        onClick = {
            viewModel.resetPassword(email)
            showDialog = true
        },
        enabled = loginButtonEnable,
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
        Text(text = "Recibir Email")
    }
}

@Composable
fun BodyForgot(modifier: Modifier, viewModel: ForgotPasswordViewModel, navigationController: NavHostController) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val loginButtonEnable: Boolean by viewModel.forgotButtonEnable.observeAsState(initial = false)

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.size(42.dp))
        EmailForgot(email) { viewModel.onForgotPasswordChanged(it) }
        Spacer(modifier = Modifier.size(16.dp))
        ButtonForgot(loginButtonEnable, email, viewModel, navigationController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailForgot(email: String, onTextChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Introduce un Email") },
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
