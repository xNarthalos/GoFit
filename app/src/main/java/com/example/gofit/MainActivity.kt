package com.example.gofit
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import android.provider.Settings
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

    private val menuViewModel: MenuViewModel by viewModels()


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            menuViewModel.startSensor()
        } else {
            showPermissionWarning()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permiso ya concedido
                    menuViewModel.startSensor()
                }
                else -> {
                    // Solicitar permiso
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
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
                        composable("Menu") { Menu(navigationController, menuViewModel) }
                        composable("forgotPassword") { ForgotPasswordScreen(viewModel = viewModel(), navigationController) }
                    }
                }
            }
        }
    }

    private fun showPermissionWarning() {
        AlertDialog.Builder(this)
            .setTitle("Permiso necesario")
            .setMessage("La aplicación necesita permiso para reconocer tu actividad física para funcionar correctamente. Por favor, habilita el permiso en los ajustes.")
            .setPositiveButton("Abrir ajustes") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()

            }
            .show()
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