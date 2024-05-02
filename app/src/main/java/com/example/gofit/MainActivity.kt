package com.example.gofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gofit.login.ForgotPassword
import com.example.gofit.login.ForgotPasswordScreen
import com.example.gofit.login.ForgotPasswordViewModel
import com.example.gofit.login.LoginScreen
import com.example.gofit.login.LoginViewModel
import com.example.gofit.login.Menu
import com.example.gofit.login.RegistroScreen
import com.example.gofit.login.RegistroViewModel
import com.example.gofit.ui.theme.GoFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoFitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationController= rememberNavController()
                   NavHost(navController = navigationController, startDestination = "LoginScreen"){
                       composable("LoginScreen"){ LoginScreen(viewModel =LoginViewModel(),navigationController )
                       }

                       composable("RegistroScreen"){  RegistroScreen(viewModel = RegistroViewModel(),navigationController )
                       }
                       composable("Menu"){  Menu(navigationController )
                       }
                       composable("ForgotPassword"){  ForgotPasswordScreen(viewModel= ForgotPasswordViewModel(),navigationController)
                       }
                   }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    GoFitTheme {
        Greeting("Android")
    }
}