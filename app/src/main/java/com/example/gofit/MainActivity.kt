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
    // Declaramos el ViewModel para gestionar el estado de la aplicación
    val menuViewModel: MenuViewModel by viewModels()

    // Controlamos el resultado de la solicitud del permiso
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Si el permiso no es concedido, muestra una advertencia
        if (!isGranted) {
            showPermissionWarning()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Comprobamos si el permiso de reconocimiento de actividad está concedido
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) -> {
                    // Si el permiso está concedido, iniciamos el sensor
                    menuViewModel.startSensor()
                }
                else -> {
                    // Si el permiso no está concedido, solicitamos el permiso
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        }
        // Establecemos el contenido de la actividad
        setContent {
            GoFitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Creamos un controlador de navegación
                    val navigationController = rememberNavController()
                    // Obtenemos la instancia de FirebaseAuth y el usuario actual
                    val auth = FirebaseAuth.getInstance()
                    val currentUser = auth.currentUser
                    // Establecemos la pantalla de inicio dependiendo de si el usuario está autenticado
                    val startDestination = if (currentUser != null) "Menu" else "LoginScreen"
                    // Configuramos el controlador de navegación
                    NavHost(navController = navigationController, startDestination = startDestination) {
                        composable("LoginScreen") {
                            LoginScreen(viewModel = viewModel(), navigationController) {
                                // Actualizamos el ID del usuario y reinicia el sensor
                                menuViewModel.updateUserId()
                                menuViewModel.startSensor()

                            }
                        }
                        //Definimos rutas de navegacion
                        composable("RegistroScreen") { RegistroScreen(viewModel = viewModel(), navigationController) }
                        composable("Menu") { Menu(navigationController, menuViewModel) }
                        composable("forgotPassword") { ForgotPasswordScreen(viewModel = viewModel(), navigationController) }
                    }
                }
            }
        }
    }

    //Metodo para mostrar un mensaje de advertencia si no tiene el permiso, y poder llevar al usuario a ajustes para activar el permiso
    private fun showPermissionWarning() {
        AlertDialog.Builder(this)
            .setTitle("Permiso necesario")
            .setMessage("La aplicación necesita permiso para reconocer tu actividad física para funcionar correctamente. Por favor, habilita el permiso en los ajustes.")
            .setPositiveButton("Abrir ajustes") { dialog, _ ->
                // Abre los ajustes de la aplicación para que el usuario conceda el permiso
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

        menuViewModel.saveDataToFirestore()
    }

    override fun onStop() {
        super.onStop()

        menuViewModel.saveDataToFirestore()
    }

    override fun onDestroy() {
        super.onDestroy()
        menuViewModel.stopSensor()
    }
}