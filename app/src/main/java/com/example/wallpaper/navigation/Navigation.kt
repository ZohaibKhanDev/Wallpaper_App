package com.example.wallpaper.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallpaper.About_Us
import com.example.wallpaper.BottomFav
import com.example.wallpaper.Contact_Us
import com.example.wallpaper.HomeScreen
import com.example.wallpaper.My_Wishlist
import com.example.wallpaper.NewScreen
import com.example.wallpaper.Privacy_Policy

import com.example.wallpaper.SearchScreen
import com.example.wallpaper.SettingScreen

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
        composable(Screen.My_Wishlist.route){
            My_Wishlist(navController)
        }
        composable(Screen.About_Us.route){
            About_Us(navController)
        }
        composable(Screen.Privacy.route){
            Privacy_Policy(navController)
        }
        composable(Screen.Contact_Us.route){
            Contact_Us(navController)
        }
        composable(
            Screen.Detail.route + "/{src}",
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

    object Detail : Screen(
        "Detail",
        "Detail",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    )

    object My_Wishlist : Screen(
        "My_Wishlist",
        "My_Wishlist",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    )
    object About_Us : Screen(
        "About_Us",
        "About_Us",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    )
    object Privacy : Screen(
        "Privacy",
        "Privacy",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    )
    object Contact_Us : Screen(
        "Contact_Us",
        "Contact_Us",
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
            NavigationBarItem(
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    selectedIndicatorColor = Color(0XFFDF1F5A),
                    unselectedIconColor = Color.White,
                    disabledIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    disabledTextColor = Color.White
                ),
                selected = current == it.route,
                onClick = {
                    navController.navigate(it.route) {
                        navController.graph?.let {
                            it.route?.let { it1 -> popUpTo(it1) }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {

                    Icon(
                        imageVector = if (current == it.route) it.selectedIcon else it.unSelectedIcon,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .width(32.dp)
                            .height(28.dp)
                    )
                },

                )
        }
    }

}