@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gofit.login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gofit.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun LoginScreen(viewModel: LoginViewModel, navigationController: NavHostController, onLoginSuccess: () -> Unit) {


    Box(
        Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(Color.White)


    ) {
        Header(Modifier.align(Alignment.TopEnd))
        Body(Modifier.align(Alignment.Center), viewModel, navigationController, onLoginSuccess)
        Footer(Modifier.align(Alignment.BottomCenter), navigationController)
    }

}

@Composable
fun Footer(modifier: Modifier, navigationController: NavHostController) {
    Column(modifier = modifier.fillMaxWidth()) {
        Divider(
            Modifier
                .background(Color(0xFFF9F9F9))
                .height(1.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(24.dp))
        SingUp(navigationController)
        Spacer(modifier = Modifier.size(24.dp))

    }
}

@Composable
fun SingUp(navigationController: NavHostController) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = "¿No tienes una cuenta?", fontSize = 12.sp, color = Color(0xFFB5B5B5))
        Text(
            text = "Registrese aqui.",
            Modifier
                .padding(horizontal = 8.dp)
                .clickable { navigationController.navigate("RegistroScreen") },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4EA8E9)

        )
    }
}

@Composable
fun Body(modifier: Modifier, viewModel: LoginViewModel, navigationController: NavHostController, onLoginSuccess: () -> Unit) {

    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val passwordVisibility: Boolean by viewModel.passwordVisibility.observeAsState(initial = false)


    Column(modifier = modifier) {
        ImageLogo(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.size(16.dp))
        Email(email, {
            viewModel.onLoginChanged(it, password)
        }, viewModel)
        Spacer(modifier = Modifier.size(4.dp))
        Password(
            password,
            passwordVisibility,
            onTextChanged = { viewModel.onLoginChanged(email, it) },
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.size(8.dp))
        ForgotPassword(Modifier.align(Alignment.End), navigationController)
        Spacer(modifier = Modifier.size(16.dp))
        LoginButton(
            isLoginEnabled = loginEnable,
            navigationController = navigationController,
            viewModel = viewModel,
            email = email,
            password = password,
            onLoginSelected = viewModel::onLoginSelected,
            onLoginSuccess = onLoginSuccess

        )

        Spacer(modifier = Modifier.size(16.dp))
        LoginDivider()
        Spacer(modifier = Modifier.size(32.dp))
        SocialLogin(viewModel, navigationController, onLoginSuccess)

    }
}

@SuppressLint("NewApi")
@Composable
fun SocialLogin(viewModel: LoginViewModel, navigationController: NavHostController, onLoginSuccess: () -> Unit) {

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.signInWithGoogle(credential,{  navigationController.navigate("Menu") {
                    popUpTo("LoginScreen") { inclusive = true }
                } }, onLoginSuccess)
            } catch (ex: Exception) {
                Log.d("google", "excepcion al iniciar con google " + ex.localizedMessage)
            }

        }
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                val opciones = GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("175197824911-25bfjevv9f8gjnmkvj1rugkjjtm3pgj1.apps.googleusercontent.com")
                    .requestEmail()
                    .build()

                val googleSignInCliente= GoogleSignIn.getClient(context,opciones)
                launcher.launch(googleSignInCliente.signInIntent)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google),
            contentDescription = "Logo Fb",
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "Inicia sesion con Google",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            color = Color(0xFF4EA8E9)
        )
    }
}

@Composable
fun LoginDivider() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Divider(
            Modifier
                .background(Color(0xFFF9F9F9))
                .height(1.dp)
                .weight(1F)
        )
        Text(
            text = "OR",
            modifier = Modifier.padding(horizontal = 18.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB5B5B5)
        )
        Divider(
            Modifier
                .background(Color(0xFFF9F9F9))
                .height(1.dp)
                .weight(1F)
        )
    }
}

@Composable
fun LoginButton(
    isLoginEnabled: Boolean,
    navigationController: NavHostController,
    onLoginSelected: () -> Unit,
    viewModel: LoginViewModel,
    email: String,
    password: String,
    onLoginSuccess: () -> Unit
) {
    val errorMessage: String? by viewModel.errorMessage.observeAsState()

    Button(
        onClick = {
            onLoginSelected()
            if (isLoginEnabled) {
                viewModel.signInWithEmailAndPassword(email, password, navigationController){
                    onLoginSuccess()
                }
            }
        },
        enabled = isLoginEnabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.verdeClaro),
            disabledContainerColor = colorResource(id = R.color.verdeClaro),
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "Entrar")
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearErrorMessage() },
            title = { Text(text = "Fallo al iniciar sesion") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearErrorMessage() }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}


@Composable
fun ForgotPassword(modifier: Modifier, navigationController: NavHostController) {
    Text(
        text = "¿Olvido su contraseña?",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4EA8E9),
        modifier = modifier.clickable { navigationController.navigate("forgotPassword") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Password(
    password: String, passwordVisibility: Boolean, onTextChanged: (String) -> Unit,
    viewModel: LoginViewModel
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor =
        if (!viewModel.isValidPassword(password) && password.isNotEmpty() && !isFocused) Color.Red else Color(
            0xFFFAFAFA
        )

    TextField(
        value = password,
        onValueChange = { onTextChanged(it) },
        placeholder = { Text(text = "Contraseña") },
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
        },
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


@Composable
fun Email(email: String, onTextChanged: (String) -> Unit, viewModel: LoginViewModel) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor =
        if (!viewModel.isValidEmail(email) && email.isNotEmpty() && !isFocused) Color.Red else Color(
            0xFFFAFAFA
        )
    TextField(
        value = email,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
                viewModel.setFocus(isFocused)
            },
        placeholder = { Text(text = "Email") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFFB2B2B2),
            containerColor = Color(0xFFFAFAFA),
            focusedIndicatorColor = borderColor,
            unfocusedIndicatorColor = borderColor
        )
    )
    if (!isFocused && !viewModel.isValidEmail(email) && email.isNotEmpty()) {
        Text(
            text = "El formato del email introducido no es correcto",
            style = TextStyle(color = Color.Red),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
fun ImageLogo(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "logo GoFIt",
        modifier = modifier.size(200.dp)
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun Header(modifier: Modifier) {
    val activity = LocalContext.current as Activity
    Icon(imageVector = Icons.Default.Close,
        contentDescription = " Close App",
        modifier = modifier.clickable { activity.finish() })
}
