import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gofit.R
import com.example.gofit.login.Entrenamiento
import com.example.gofit.login.Inicio
import com.example.gofit.login.Perfil
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(navigationController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val innerNavController = rememberNavController()

    Scaffold(
        topBar = {
            val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: "GoFit"
            val title = when (currentRoute) {
                "Ruta" -> "GoFit - Entrenamiento"
                "Perfil" -> "GoFit - Perfil"
                "Home" -> "GoFit - Inicio"
                else -> "GoFit"
            }
            MyTopAppBar(
                title = title,
                onClickIcon = {},
                onClickDrawer = {},
                onSignOut = { signOutUser(auth, navigationController) }
            )
        },
        bottomBar = {
            MyBottomNavigation(innerNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "Home",
            Modifier.padding(innerPadding)
        ) {
            composable("Home") { Inicio() }
            composable("Ruta") { Entrenamiento() }
            composable("Perfil") { Perfil() }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    onClickIcon: () -> Unit,
    onClickDrawer: () -> Unit,
    onSignOut: () -> Unit
) {
    val menuExpanded = remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF5DCF14),
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
                onDismissRequest = { menuExpanded.value = false }
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
fun MyBottomNavigation(navigationController: NavHostController) {
    var index by remember { mutableStateOf(0) }
    NavigationBar(containerColor = Color(0xFF5DCF14)) {
        NavigationBarItem(
            selected = index == 0,
            onClick = {
                index = 0
                navigationController.navigate("Home")
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
                navigationController.navigate("Ruta")
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
                navigationController.navigate("Perfil")
            },
            icon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
            },
            label = { Text(text = "Perfil") }
        )
    }
}

@Composable
fun MyCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5DCF14)),
        border = BorderStroke(5.dp, Color.Green)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Ejemplo 1", color = Color.White)
            Text(text = "Ejemplo 2", color = Color.White)
            Text(text = "Ejemplo 3", color = Color.White)
        }
    }
}

fun signOutUser(auth: FirebaseAuth, navigationController: NavHostController) {
    auth.signOut()

    if (auth.currentUser == null) {

        navigationController.popBackStack(route = "LoginScreen", inclusive = true)
        navigationController.navigate("LoginScreen")
    }
}