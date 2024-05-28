package com.example.gofit
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gofit.login.LoginScreen
import com.example.gofit.ui.theme.GoFitTheme
import com.google.firebase.auth.FirebaseAuth
import forgotPassword.ForgotPasswordScreen
import home.Menu
import home.MenuViewModel
import registro.RegistroScreen

class MainActivity : ComponentActivity() {

    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

private val menuViewModel : MenuViewModel by viewModels()

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
                        composable("LoginScreen") {
                            LoginScreen(viewModel = viewModel(), navigationController) {
                                menuViewModel.updateUserId()

                            }
                        }
                        composable("RegistroScreen") { RegistroScreen(viewModel = viewModel(), navigationController) }
                        composable("Menu") { Menu(navigationController,menuViewModel) }
                        composable("forgotPassword") { ForgotPasswordScreen(viewModel = viewModel(),navigationController) }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                menuViewModel.startSensor()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        menuViewModel.saveData()
        menuViewModel.saveDataToFirestore()
    }

    override fun onStop() {
        super.onStop()

        menuViewModel.saveData()
        menuViewModel.saveDataToFirestore()



    }

    override fun onDestroy() {
        super.onDestroy()
        menuViewModel.stopSensor()
    }
}


