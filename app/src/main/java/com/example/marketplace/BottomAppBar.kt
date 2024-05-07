import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.marketplace.ChatScreen
import com.example.marketplace.FavScreen
import com.example.marketplace.FavScrollContent
import com.example.marketplace.HomeScreen
import com.example.marketplace.NavBarItem
import com.example.marketplace.Routes

@Composable
fun BottomAppBar(navigationController: NavController) {
    val navController = rememberNavController()
//    var currentRoute = navController.currentBackStackEntry?.destination?.route
//    if (currentRoute == null) {
//        currentRoute = "Marketplace"
//    }

        Scaffold(
            bottomBar = {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                ) {
                    val navBackStackEntry by
                    navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    NavBarItem().NavBarItems().forEach { navItem ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    navItem.icon, contentDescription
                                    = null
                                )
                            },
                            label = { Text(navItem.label) },
                            selected = currentDestination?.hierarchy?.any
                            { it.route == navItem.route } == true,
                            onClick = {
                                navController.navigate(navItem.route)
                                {
                                    // popUpTo is used to pop up to a given  destination before navigating
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // be at most one copy of a given destination on the top of the back stack
                                    launchSingleTop = true
                                    // this navigation action should restore any state previously saved
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController,
                startDestination = Routes.Home.value,
//                Modifier.padding(paddingValues)
            ) {
                composable(Routes.Home.value) {
                    HomeScreen(navController)
                }
                composable(Routes.Chat.value) {
                    ChatScreen(navController)
                }
//            TODO: Change to add product
                composable(Routes.Chat.value) {
                    ChatScreen(navController)
                }
                composable(Routes.Favourites.value) {
                    FavScreen(navController)
                }
            }


        }
}