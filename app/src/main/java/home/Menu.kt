package home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gofit.R
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(
    navigationController: NavHostController,
    menuViewModel: MenuViewModel ) {
    val auth = FirebaseAuth.getInstance()
    val innerNavController = rememberNavController()
    val userName by menuViewModel.userName.observeAsState()



    Scaffold(
        topBar = {
            val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
            val title = when (currentBackStackEntry?.destination?.route ?: "GoFit") {
                "Ruta" -> "GoFit - Ruta"
                "Perfil" -> "GoFit - Perfil"
                "home" -> "GoFit - Inicio"
                else -> "GoFit"
            }
            MyTopAppBar(
                title = title,
                userName = userName,
                onSignOut = { signOutUser(auth, navigationController,menuViewModel) }
            )
        },
        bottomBar = {
            MyBottomNavigation(innerNavController,menuViewModel)
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "home",
            Modifier.padding(innerPadding)
        ) {
            composable("home") { Inicio(menuViewModel) }
            composable("Ruta") { Entrenamiento(menuViewModel) }
            composable("Perfil") { Perfil(menuViewModel) }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    userName: String?,
    onSignOut: () -> Unit
) {
    val menuExpanded = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row {
                Text(
                    text = title,
                   fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = userName ?: "Usuario",
                     fontSize = 16.sp,
                    color = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor =  colorResource(id = R.color.verdeOscuro),
            titleContentColor = Color.White,
        ),
        actions = {
            IconButton(onClick = { menuExpanded.value = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Opciones",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false },
                modifier = Modifier.background(colorResource(id = R.color.verdeOscuro)),
            ) {
                DropdownMenuItem(onClick = {
                    onSignOut()
                    menuExpanded.value = false
                }, text = { Text("Cerrar Sesión") })
            }
        }
    )
}


@Composable
fun MyBottomNavigation(navigationController: NavHostController,menuViewModel: MenuViewModel) {
    var index by remember { mutableStateOf(0) }
    NavigationBar(containerColor = colorResource(id = R.color.verdeOscuro)) {
        NavigationBarItem(
            selected = index == 0,
            onClick = {

                index = 0
                navigationController.navigate("home") {
                    popUpTo(navigationController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio", tint = Color.White)
            },
            label = { Text(text = "Inicio") }
        )
        NavigationBarItem(
            selected = index == 1,
            onClick = {

                index = 1
                navigationController.navigate("Ruta") {
                    popUpTo(navigationController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(painter = painterResource(id = R.drawable.entrenamiento), contentDescription = "Entrenamiento", tint = Color.White)
            },
            label = { Text(text = "Ruta") }
        )
        NavigationBarItem(
            selected = index == 2,
            onClick = {

                index = 2
                navigationController.navigate("Perfil") {
                    popUpTo(navigationController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
            },
            label = { Text(text = "Perfil") }
        )
    }
}

fun signOutUser(
    auth: FirebaseAuth,
    navigationController: NavHostController,
    menuViewModel: MenuViewModel
) {
    auth.signOut()
    menuViewModel.stopSensor()

    if (auth.currentUser == null) {
        navigationController.navigate("LoginScreen") {
            popUpTo(navigationController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    menuViewModel.saveDataToFirestore()
    menuViewModel.clearData()
    menuViewModel.updateUserId()
}
