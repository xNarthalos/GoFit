package com.example.gofit

import Menu
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gofit.login.ForgotPasswordScreen
import com.example.gofit.login.ForgotPasswordViewModel
import com.example.gofit.login.LoginScreen
import com.example.gofit.login.LoginViewModel
import com.example.gofit.login.RegistroScreen
import com.example.gofit.login.RegistroViewModel
import com.example.gofit.login.StepCountViewModel

import com.example.gofit.ui.theme.GoFitTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    private val stepCountViewModel: StepCountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    ACTIVITY_RECOGNITION_REQUEST_CODE
                )
            }
        }

        setContent {
            GoFitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationController = rememberNavController()
                    val auth = FirebaseAuth.getInstance()
                    val currentUser = auth.currentUser

                    val startDestination = if (currentUser != null) "Menu" else "LoginScreen"
                    NavHost(navController = navigationController, startDestination = startDestination) {
                        composable("LoginScreen") { LoginScreen(viewModel = LoginViewModel(), navigationController) }
                        composable("RegistroScreen") { RegistroScreen(viewModel = RegistroViewModel(), navigationController) }
                        composable("Menu") { Menu(navigationController, stepCountViewModel) }
                        composable("ForgotPassword") { ForgotPasswordScreen(viewModel = ForgotPasswordViewModel(), navigationController) }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                val stepCountViewModel = StepCountViewModel(application)
                stepCountViewModel.startSensor()
            }
        }
    }
}