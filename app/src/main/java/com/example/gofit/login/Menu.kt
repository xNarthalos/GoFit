import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController



@Composable
fun Menu(navigationController: NavHostController) {
    Column(Modifier.fillMaxSize()) {
        MyTopAppBar(onClickIcon = {}, onClickDrawer = {})
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
fun MyTopAppBar(onClickIcon: () -> Unit, onClickDrawer: () -> Unit) {
    TopAppBar(
        title = { Text(text = "GoFit") },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF5DCF14),
            titleContentColor = Color.White,
        ),


        actions = {
            IconButton(onClick = onClickIcon) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Options" , tint = Color.White)
            }
        }
    )
}

