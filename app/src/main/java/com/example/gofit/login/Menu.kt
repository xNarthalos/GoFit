import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.gofit.R
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(navigationController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            MyTopAppBar(onClickIcon = {}, onClickDrawer = {}, onSignOut = { signOutUser(auth, navigationController) })
        },
        bottomBar = {
            MyBottomNavigation()
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
                .background(Color.White)
        ) {
            MyCard()
            Spacer(modifier = Modifier.height(8.dp))
            MyCard()
            Spacer(modifier = Modifier.height(8.dp))
            MyCard()
            Spacer(modifier = Modifier.height(8.dp))
            MyCard()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    onClickIcon: () -> Unit,
    onClickDrawer: () -> Unit,
    onSignOut: () -> Unit
) {
    val menuExpanded = remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = "GoFit") },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF5DCF14),
            titleContentColor = Color.White,
        ),
        actions = {
            IconButton(onClick = { menuExpanded.value = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Options",
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
                }, text = { Text("Cerra Sesion") })
            }
        }
    )
}

@Composable
fun MyBottomNavigation() {
    var index by remember { mutableStateOf(0) }
    NavigationBar(containerColor = Color(0xFF5DCF14) ) {
        NavigationBarItem(selected = index == 0, onClick = { index = 0 }, icon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Inicio",
                tint = Color.White

            )
        }, label = { Text(text = "Inicio") })
        NavigationBarItem(selected = index == 1, onClick = { index = 1 }, icon = {
            Icon(
                painter = painterResource(id = R.drawable.entrenamiento),
                contentDescription = "Entrenamiento",
                tint = Color.White
            )
        }, label = { Text(text = "Entrenamiento") })
        NavigationBarItem(selected = index == 2, onClick = { index = 2 }, icon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = Color.White
            )
        }, label = { Text(text = "Perfil") })
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