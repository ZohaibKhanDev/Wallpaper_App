package com.example.wallpaper

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import androidx.room.util.copyAndClose
import coil.compose.AsyncImage
import com.example.wallpaper.api.MainViewModel
import com.example.wallpaper.api.Photo
import com.example.wallpaper.api.Repository
import com.example.wallpaper.api.ResultState
import com.example.wallpaper.api.Wallpaper
import com.example.wallpaper.navigation.Entry
import com.example.wallpaper.navigation.Screen
import com.example.wallpaper.roomdatabase.FavItem
import com.example.wallpaper.roomdatabase.MyDataBase
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
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        MyDataBase::class.java,
        "demo.db"
    ).allowMainThreadQueries()
        .build()
    val repository = remember {
        Repository(db)
    }
    val viewModel = remember {
        MainViewModel(repository)
    }
    WallpaperTheme {
        var isWallpaper by remember {
            mutableStateOf(false)
        }
        var wallpaperData by remember {
            mutableStateOf<Wallpaper?>(null)
        }
        LaunchedEffect(key1 = Unit) {
            viewModel.getAllWallpaper()
        }


        val state by viewModel.allWallpaper.collectAsState()
        when (state) {
            is ResultState.Error -> {
                val error = (state as ResultState.Error).error
                Text(text = error.toString())
                isWallpaper = false
            }

            is ResultState.Loading -> {
                isWallpaper = true
            }

            is ResultState.Success -> {
                val success = (state as ResultState.Success).response
                wallpaperData = success
                isWallpaper = false
            }
        }
        var favData by remember {
            mutableStateOf<List<FavItem>?>(null)
        }
        val isFav by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = isFav) {
            viewModel.getAllFav()
        }

        val favState by viewModel.allFav.collectAsState()

        when (favState) {
            is ResultState.Error -> {
                val error = (favState as ResultState.Error).error
                Text(text = error.toString())
            }

            ResultState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ResultState.Success -> {
                val success = (favState as ResultState.Success).response
                favData = success
            }
        }

        Scaffold(topBar = {
            TopAppBar(title = {
                Text(text = "WallPapers", color = Color.White)
            }, colors = TopAppBarDefaults.topAppBarColors(Color(0XFF14182b)), actions = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "",
                    tint = Color.White
                )
            },

                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "",
                        tint = Color.White
                    )
                })
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = it.calculateTopPadding()),
                color = MaterialTheme.colorScheme.background
            ) {
                if (isWallpaper) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                }
                wallpaperData?.photos?.let { photo ->
                    WallpaperData(
                        photo = photo, viewModel = viewModel, navController = navController
                    )
                }


            }
        }
    }
}


@Composable
fun WallpaperData(photo: List<Photo>, viewModel: MainViewModel, navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0XFF14182b)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "New WallPaper", color = Color.White)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), modifier = Modifier
                .fillMaxWidth()
                .background(Color(0XFF14182b))
                .weight(1f)
                .padding(top = 10.dp)
        ) {
            items(photo) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0XFF14182b))
                        .padding(7.dp),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0XFF14182b)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier.background(Color(0XFF14182b))
                        ) {
                            AsyncImage(model = it.src.landscape,
                                contentDescription = "",
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(
                                            Screen.Detail.route +
                                                    "/${Uri.encode(it.src.landscape)}"
                                        )
                                    }
                                    .background(Color(0XFF14182b)))

                        }
                    }
                }
            }


        }


        /*  LazyRow (modifier = Modifier.fillMaxWidth().weight(1f)){
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
                          AsyncImage(model = it.src.landscape,
                              contentDescription = "",
                              modifier = Modifier.clickable {

                              })
                      }
                  }
              }
          }*/
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomFav(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }

    val db = Room.databaseBuilder(
        context, MyDataBase::class.java, "demo.db"
    ).allowMainThreadQueries().build()
    val repository = remember {
        Repository(db)
    }
    val viewModel = remember {
        MainViewModel(repository)
    }
    var favData by remember {
        mutableStateOf<List<FavItem>?>(null)
    }
    val isFav by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = isFav) {
        viewModel.getAllFav()
    }
    val favState by viewModel.allFav.collectAsState()
    when (favState) {
        is ResultState.Error -> {
            val error = (favState as ResultState.Error).error
            Text(text = error.toString())
        }

        ResultState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            val success = (favState as ResultState.Success).response
            favData = success
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "Fav WallPaper", color = Color.White)
            }, colors = TopAppBarDefaults.topAppBarColors(Color(0XFF14182b)))
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFF14182b))
                .padding(top = 15.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            favData?.let {
                items(it) { fav ->
                    New_Wallpaper_Screen(
                        navController = navController,
                        FavItem(null, fav.image, fav.des)
                    )
                }
            }
        }
    }
}


@Composable
fun New_Wallpaper_Screen(navController: NavController, favItem: FavItem) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0XFF14182b)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0XFF14182b))
                .padding(all = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(model = favItem.image, contentDescription = "")


        }
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
fun NewScreen(navController: NavController, image: String?) {
    var like by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        MyDataBase::class.java,
        "demo.db"
    ).allowMainThreadQueries()
        .build()
    val repository = remember {
        Repository(db)
    }
    val viewModel = remember {
        MainViewModel(repository)
    }

    var info by remember {
        mutableStateOf(false)
    }



    Box(contentAlignment = Alignment.Center) {

        AsyncImage(
            model = image,
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            clipToBounds = true,
            filterQuality = FilterQuality.High,
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 650.dp, end = 150.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {
                if (like) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "",
                        tint = Color.Red,
                        modifier = Modifier.clickable {
                            like = !like
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            like = !like
                            val fav = image?.let { FavItem(null, it, "des") }
                            fav?.let { viewModel.Insert(it) }
                        })
                }
            }
        }



        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 650.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {

                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        val downloadManager =
                            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val uri = Uri.parse(image)
                        val request = DownloadManager
                            .Request(uri)
                            .setTitle(image)
                            .setDescription("This is description")
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                            .setAllowedOverRoaming(true)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(
                                android.os.Environment.DIRECTORY_DOWNLOADS,
                                "zohaib.png"
                            )

                        downloadManager.enqueue(request)

                    }
                )

            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 650.dp, start = 150.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {

                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        shareText(context, "$image")
                    }
                )

            }
        }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(bottom = 820.dp, start = 350.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        info = !info
                    }
                )

            }
        }


        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(bottom = 820.dp, end = 350.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )

            }
        }

        if (info) {
            AlertDialog(
                onDismissRequest = { info = !info }, confirmButton = {
                    Text(text ="Ok", modifier = Modifier.clickable { info=!info})
                },
                title = {
                    Text(text = "WallPaper")
                },
                text = {
                    Text(text = "$image")


                },
                tonalElevation = 10.dp,

              /*  dismissButton = {
                    Text(text = "Cancel", modifier = Modifier.clickable { info = !info })
                }*/


            )
        }


    }


}

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}


