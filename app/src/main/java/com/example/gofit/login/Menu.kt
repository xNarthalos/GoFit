import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Menu(navigationController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    Column(Modifier.fillMaxSize()) {
        MyTopAppBar(onClickIcon = {}, onClickDrawer = {}, onSignOut = { signOutUser(auth = auth, navigationController = navigationController) })
        Box(
            Modifier
                .weight(1f)
                .padding(8.dp)
                .background(Color.White)
        ) {
            Text(text = "Menu", Modifier.fillMaxSize())
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




fun signOutUser(auth: FirebaseAuth, navigationController: NavHostController) {
    auth.signOut()

    if (auth.currentUser == null) {

        navigationController.popBackStack(route = "LoginScreen", inclusive = true)
        navigationController.navigate("LoginScreen")
    }
}