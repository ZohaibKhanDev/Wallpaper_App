package com.example.wallpaper

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wallpaper.api.MainViewModel
import com.example.wallpaper.api.Photo
import com.example.wallpaper.api.Repository
import com.example.wallpaper.api.ResultState
import com.example.wallpaper.api.Wallpaper
import com.example.wallpaper.navigation.Entry
import com.example.wallpaper.navigation.Screen
import com.example.wallpaper.ui.theme.WallpaperTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WallpaperTheme {
                Entry()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    /*val context = LocalContext.current
        val db = Room.databaseBuilder(
            context,
            RoomDatabase::class.java,
            "demo.db"
        ).allowMainThreadQueries()
            .build()*/
    val repository = remember {
        Repository()
    }
    val viewModel = remember {
        MainViewModel(repository)
    }
    WallpaperTheme {
        val isWallpaper by remember {
            mutableStateOf(false)
        }
        var wallpaperData by remember {
            mutableStateOf<Wallpaper?>(null)
        }
        LaunchedEffect(key1 = isWallpaper) {
            viewModel.getAllWallpaper()
        }

        val state by viewModel.allWallpaper.collectAsState()
        when (state) {
            is ResultState.Error -> {
                val error = (state as ResultState.Error).error
                Text(text = error.toString())
            }

            ResultState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center

                ) {
                    CircularProgressIndicator()
                }
            }

            is ResultState.Success -> {
                val success = (state as ResultState.Success).response
                wallpaperData = success
            }
        }

        Scaffold(topBar = {
            TopAppBar(title = {
                Text(text = "WallPapers")
            },
                colors = TopAppBarDefaults.topAppBarColors(Color(0XFF8593ff)),
                actions = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "")
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                },

                navigationIcon = {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "")
                }
            )
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = it.calculateTopPadding()),
                color = MaterialTheme.colorScheme.background
            ) {
                wallpaperData?.photos?.let {
                    WallpaperData(
                        photo = it,
                        viewModel = viewModel,
                        navController = navController
                    )
                }


            }
        }
    }
}


@Composable
fun WallpaperData(photo: List<Photo>, viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.wrapContentWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "New WallPaper")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(top = 10.dp)
        ) {
            items(photo) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        AsyncImage(
                            model = it.src.landscape,
                            contentDescription = "",
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.Detail.route + "/${Uri.encode(it.src.landscape)}")
                            }
                        )
                    }
                }
            }


        }


        LazyRow {
            items(photo) {
                Card(
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = it.src.landscape,
                            contentDescription = "",
                            modifier = Modifier.clickable {

                            }
                        )
                    }
                }
            }
        }
    }


}


@Composable
fun New_Wallpaper_Screen(navController: NavController) {

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(text = "This is  New Wallpaper Screen")
    }
}

@Composable
fun SearchScreen(navController: NavController) {

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(text = "This is  Search Screen")
    }
}

@Composable
fun SettingScreen(navController: NavController) {

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(text = "This is  Setting Screen")
    }
}

@Composable
fun DetailScreen(navController: NavController, image: String?) {
    Box(contentAlignment = Alignment.Center) {

        AsyncImage(
            model = "$image",
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            clipToBounds = true,
            filterQuality = FilterQuality.High,
        )


        Icon(imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "",
            modifier = Modifier
                .align(
                    Alignment.TopStart
                )
                .padding(top = 7.dp, start = 6.dp)
                .clickable { navController.popBackStack() },
            colorResource(id = R.color.white)
        )

        Image(
            painter = painterResource(id = R.drawable.applelogo),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(
                CircleShape
            ).size(30.dp).align(Alignment.CenterEnd)
        )
    }


}



