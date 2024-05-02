package com.example.gofit.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ForgotPasswordScreen(viewModel: ForgotPasswordViewModel, navigationController: NavHostController){

    Box(
        Modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(Color.White)


    ) {
        BodyForgot(Modifier.align(Alignment.Center),viewModel)

    }
}

@Composable
fun ButtonForgot() {
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
        Text(text = "Recibir Email")
    }
}

@Composable
fun BodyForgot(modifier: Modifier,viewModel: ForgotPasswordViewModel) {
    val email: String by viewModel.email.observeAsState(initial = "")

    Column(modifier=modifier) {
        EmailForgot(email){ viewModel.onForgotPasswordChanged(it) }

        Spacer(modifier = Modifier.size(16.dp))
        ButtonForgot()
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailForgot(email: String, onTextChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = {  onTextChanged(it)},
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





