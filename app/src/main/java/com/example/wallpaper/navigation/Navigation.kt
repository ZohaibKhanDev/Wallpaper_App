package com.example.wallpaper.navigation

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallpaper.BottomFav
import com.example.wallpaper.HomeScreen
import com.example.wallpaper.NewScreen
import com.example.wallpaper.SearchScreen
import com.example.wallpaper.SettingScreen
import okhttp3.Route

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.New_Wallpaper.route) {
            BottomFav(navController)
        }

        composable(Screen.Search.route) {
            SearchScreen(navController)
        }
        composable(Screen.Setting.route) {
            SettingScreen(navController)
        }
        composable(
            DetailScreen.Detail.route + "/{src}",
            arguments = listOf(
                navArgument("src") {
                    type = NavType.StringType
                },

                )
        ) {
            val image = it.arguments?.getString("src")


            NewScreen(navController, image)
        }
    }
}
sealed class DetailScreen(
    val route: String
){
    object Detail:DetailScreen("DetailScreen")
}

sealed class Screen(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
) {
    object Home : Screen(
        "Home",
        "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    )

    object New_Wallpaper : Screen(
        "Fav Wallpaper",
        "Fav Wallpaper",
        selectedIcon = Icons.Filled.Wallpaper,
        unSelectedIcon = Icons.Outlined.Wallpaper
    )



    object Search : Screen(
        "Search",
        "Search",
        selectedIcon = Icons.Filled.Search,
        unSelectedIcon = Icons.Outlined.Search
    )


    object Setting : Screen(
        "Setting",
        "Setting",
        selectedIcon = Icons.Filled.Settings,
        unSelectedIcon = Icons.Outlined.Settings
    )


}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Entry() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNavigation(navController = navController) }) {
        Navigation(navController = navController)
    }
}


@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.New_Wallpaper,
        Screen.Search,
        Screen.Setting
    )


    NavigationBar(contentColor = Color.White, containerColor = Color(0XFF14182b)) {
        val navStack by navController.currentBackStackEntryAsState()
        val current = navStack?.destination?.route
        var icon by remember {
            mutableStateOf(false)
        }

        items.forEach {
            NavigationBarItem(selected = current == it.route, onClick = {
                navController.navigate(it.route) {
                    navController.graph?.let {
                        it.route?.let { it1 -> popUpTo(it1) }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }, icon = {

                Icon(
                    imageVector = if (current == it.route) it.selectedIcon else it.unSelectedIcon,
                    contentDescription = "",
                    tint = Color.White
                )
            },
                label = {
                    Text(text = it.route, color = Color.White)
                }
            )
        }
    }

}