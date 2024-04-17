package com.example.wallpaper

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
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
import io.ktor.http.Url
import io.ktor.http.toURI
import kotlinx.coroutines.launch

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
        context, MyDataBase::class.java, "demo.db"
    ).allowMainThreadQueries().build()
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
            viewModel.getAllWallpaper(30)
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
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0XFF14182b)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "New WallPaper", color = Color.White)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0XFF14182b))
                .weight(1f)
                .padding(top = 10.dp, bottom = 100.dp)
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
                            AsyncImage(model = it.src.portrait,
                                contentDescription = "",
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(
                                            Screen.Detail.route + "/${Uri.encode(it.src.landscape)}"
                                        )
                                    }
                                    .background(Color(0XFF14182b)))

                        }
                    }
                }
            }


        }


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


    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Fav WallPaper", color = Color.White)
        }, colors = TopAppBarDefaults.topAppBarColors(Color(0XFF14182b)))
    }) {
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
                        navController = navController, FavItem(null, fav.image, fav.des)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    var searchBar by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context, MyDataBase::class.java, "demo.db"
    ).allowMainThreadQueries().build()
    val repository = remember {
        Repository(db)
    }
    val viewModel = remember {
        MainViewModel(repository)
    }
    var searchdata by remember {
        mutableStateOf<Wallpaper?>(null)
    }
    val loading by remember {
        mutableStateOf(false)
    }
    val searchState by viewModel.allSearch.collectAsState()
    when (searchState) {
        is ResultState.Error -> {
            loading == false
            val error = (searchState as ResultState.Error).error
            Text(text = error.toString())
        }

        ResultState.Loading -> {
            loading == true
        }

        is ResultState.Success -> {
            loading == false
            val success = (searchState as ResultState.Success).response
            searchdata = success
        }
    }
    var clearState by remember {
        mutableStateOf(false)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFF14182b))
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0XFF14182b)
                )
        ) {
            SearchBar(modifier = Modifier
                .fillMaxWidth()
                .background(Color(0XFF14182b)),
                colors = SearchBarDefaults.colors(
                    containerColor = Color(0XFF14182b),
                    dividerColor = Color.White,
                    inputFieldColors = TextFieldDefaults.textFieldColors(Color.White)
                ),
                query = searchBar,
                onQueryChange = {
                    searchBar = it
                },
                onSearch = {
                    viewModel.getAllSearch(per_page = 40, searchBar)
                },
                placeholder = {
                    Text(text = "Enter WallPaper Name", color = Color.White)
                },
                trailingIcon = {

                    if (searchBar >= 0.toString()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.clickable { searchBar = "" },
                            tint = Color.White
                        )
                    }

                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = Color.White
                    )
                },
                active = true,
                onActiveChange = {

                }) {
                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                searchdata?.photos?.let { photo ->
                    SearchItem(
                        photo = photo, navController = navController
                    )
                }


            }
        }
    }
}


@Composable
fun SearchItem(photo: List<Photo>, navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0XFF14182b)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0XFF14182b))

                .padding(top = 10.dp, bottom = 50.dp)
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
                        verticalArrangement = Arrangement.Center
                    ) {

                        AsyncImage(model = it.src.landscape,
                            contentDescription = "",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(
                                        Screen.Detail.route + "/${Uri.encode(it.src.landscape)}"
                                    )
                                }
                                .background(Color(0XFF14182b)))

                    }
                }
            }
        }


    }


}

@Composable
fun SettingScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0XFF14182b))
            .padding(bottom = 60.dp)
            .verticalScroll(scrollState, enabled = true, reverseScrolling = true)
    ) {

        Card(
            modifier = Modifier.fillMaxSize(),

            colors = CardDefaults.cardColors(
                Color(0XFF14182b)
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(top = 40.dp, start = 20.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0XFFDF1F5A))
                        .width(50.dp)
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }

            Text(
                text = "Profile",
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 40.dp, top = 10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))


            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.zohaib),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .width(110.dp)
                            .height(110.dp)
                    )

                }

                Text(
                    text = "Zohaib Khan",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Wallpaper@gmail.com",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "+82469247",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(60.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(Color(0XFFDF1F5A))
            ) {
                Text(text = "Login", color = Color.White)
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(Screen.My_Wishlist.route) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp)
                            .clip(CircleShape)
                            .background(Color(0XFFDF1F5A)), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = "",
                            modifier = Modifier.clip(
                                CircleShape
                            ),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "My Wishlist",
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(180f)
                    )
                }



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(Screen.About_Us.route) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp)
                            .clip(CircleShape)
                            .background(Color(0XFFDF1F5A)), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "",
                            modifier = Modifier.clip(
                                CircleShape
                            ),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "About Us",
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(180f)
                    )
                }





                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            navController.navigate(Screen.Privacy.route)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp)
                            .clip(CircleShape)
                            .background(Color(0XFFDF1F5A)), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PrivacyTip,
                            contentDescription = "",
                            modifier = Modifier.clip(
                                CircleShape
                            ),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Privacy Policy ",
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(180f)
                    )
                }


                val context = LocalContext.current
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            shareText(context, "WallPaper.KMP")
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp)
                            .clip(CircleShape)
                            .background(Color(0XFFDF1F5A)), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "",
                            modifier = Modifier.clip(
                                CircleShape
                            ),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Share",
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(180f)
                    )
                }



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(Screen.Contact_Us.route) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .height(35.dp)
                            .clip(CircleShape)
                            .background(Color(0XFFDF1F5A)), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = "",
                            modifier = Modifier.clip(
                                CircleShape
                            ),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Contact Us",
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(180f)
                    )
                }


            }


        }
    }
}


@Composable
fun My_Wishlist(navController: NavController) {
    LazyColumn {
        item {
            Text(
                text = "Vast Collection: A diverse and extensive collection of high-quality wallpapers across various categories such as nature, landscapes, abstract, minimalistic, artistic, etc. The app should offer a wide range of options to suit different tastes and preferences.\n" +
                        "\n" +
                        "Search Functionality: An efficient search feature that allows users to easily find wallpapers by keywords, categories, colors, or tags. This helps users quickly discover wallpapers that match their interests.\n" +
                        "\n" +
                        "Customization Options: The ability to customize wallpapers according to screen size, resolution, aspect ratio, and device orientation. Users should be able to crop, resize, or adjust wallpapers to fit their device perfectly.\n" +
                        "\n" +
                        "Favorites and Collections: A feature that enables users to create their own collections or folders to organize their favorite wallpapers. This allows users to easily access and manage their preferred wallpapers for different moods or occasions.\n" +
                        "\n" +
                        "Download and Set: Seamless integration with device functionality to download wallpapers directly to the device's gallery and set them as the device's wallpaper with just a few taps.\n" +
                        "\n" +
                        "Daily/Weekly Updates: Regular updates with new wallpapers added daily or weekly to keep the content fresh and engaging for users. This ensures that users always have access to the latest and trending wallpapers.\n" +
                        "\n" +
                        "User Profile and Sync: Optional user profiles where users can save their preferences, sync their favorite wallpapers across multiple devices, and receive personalized recommendations based on their interests and past downloads.\n" +
                        "\n" +
                        "Community Interaction: Features that foster community interaction such as user ratings, reviews, and comments on wallpapers. This allows users to share their feedback, tips, or suggestions and engage with other members of the community.\n" +
                        "\n" +
                        "Ad-Free Experience: An option for users to opt for a premium, ad-free experience either through a subscription model or one-time purchase. This ensures a distraction-free browsing experience for users who are willing to pay for it.\n" +
                        "\n" +
                        "Offline Access: The ability to access downloaded wallpapers offline, especially useful for users with limited internet connectivity or those who prefer to save wallpapers for offline use."
            )
        }
    }
}

@Composable
fun About_Us(navController: NavController) {
    LazyColumn {
        item {
            Text(
                text = "About Us\n" +
                        "\n" +
                        "Welcome to our wallpaper app, your ultimate destination for stunning wallpapers to personalize your devices and enhance your digital experience. At [App Name], we are passionate about bringing beauty and creativity to your screens, and we strive to provide you with the best collection of wallpapers tailored to your tastes and preferences.\n" +
                        "\n" +
                        "Our Mission\n" +
                        "\n" +
                        "Our mission is simple: to inspire and delight our users with a curated selection of high-quality wallpapers that cater to every style, mood, and occasion. We believe that wallpapers have the power to transform the look and feel of your devices, turning them into personalized works of art that reflect your unique personality and interests.\n" +
                        "\n" +
                        "What Sets Us Apart\n" +
                        "\n" +
                        "Quality: We are committed to offering wallpapers of the highest quality, sourced from talented artists and photographers around the world. Each wallpaper undergoes careful selection and review to ensure it meets our standards of excellence.\n" +
                        "\n" +
                        "Diversity: Our extensive collection spans a wide range of categories, from breathtaking landscapes and captivating nature scenes to modern abstract designs and vibrant illustrations. Whatever your style or preference, you'll find something to suit your taste in our diverse library of wallpapers.\n" +
                        "\n" +
                        "User Experience: We prioritize user experience above all else, designing our app to be intuitive, user-friendly, and visually appealing. With seamless navigation, intuitive search functionality, and convenient customization options, finding and setting the perfect wallpaper has never been easier.\n" +
                        "\n" +
                        "Community Engagement: We value the feedback and contributions of our users and actively encourage community engagement. Whether through user ratings, reviews, or social media interactions, we love hearing from you and learning how we can improve our app to better serve your needs.\n" +
                        "\n" +
                        "Our Team\n" +
                        "\n" +
                        "Behind [App Name] is a dedicated team of developers, designers, and wallpaper enthusiasts who are passionate about creating the best possible experience for our users. We are constantly innovating and refining our app to ensure that it remains your go-to destination for stunning wallpapers that inspire and delight.\n" +
                        "\n" +
                        "Get in Touch\n" +
                        "\n" +
                        "We'd love to hear from you! Whether you have feedback, suggestions, or just want to say hello, feel free to reach out to us at [contact email or social media handles]. Thank you for choosing [App Name], and we hope you enjoy exploring our collection of wallpapers as much as we enjoy curating them for you."
            )
        }
    }
}

@Composable
fun Privacy_Policy(navController: NavController) {
    LazyColumn {
        item {
            Text(
                text = "Your privacy is important to us. It is our policy to respect your privacy regarding any information we may collect from you across our website and other sites we own and operate.\n" +
                        "\n" +
                        "1. Information We Collect\n" +
                        "\n" +
                        "1.1. Personal Information: We may ask for personal information, such as your name, email address, and phone number, when you interact with our website, subscribe to our newsletter, or fill out a form. We only collect personal information that is relevant to the purpose of your interaction with us.\n" +
                        "\n" +
                        "1.2. Usage Data: We may also collect information that your browser sends whenever you visit our website or when you access the website through any device. This usage data may include your computer's Internet Protocol (IP) address, browser type, browser version, the pages of our website that you visit, the time and date of your visit, the time spent on those pages, and other statistics.\n" +
                        "\n" +
                        "2. How We Use Your Information\n" +
                        "\n" +
                        "2.1. We may use the information we collect for various purposes, including:\n" +
                        "\n" +
                        "To provide, operate, and maintain our website.\n" +
                        "To send you newsletters, marketing or promotional materials, and other information that may be of interest to you.\n" +
                        "To respond to your comments, inquiries, and requests.\n" +
                        "To improve our website and enhance the user experience.\n" +
                        "To monitor the usage of our website and detect, prevent, and address technical issues.\n" +
                        "3. Cookies\n" +
                        "\n" +
                        "3.1. We may use cookies and similar tracking technologies to track the activity on our website and hold certain information.\n" +
                        "\n" +
                        "3.2. You have the option to disable cookies through your browser settings. However, please note that disabling cookies may affect the functionality of our website.\n" +
                        "\n" +
                        "4. Third-Party Services\n" +
                        "\n" +
                        "4.1. We may use third-party services, such as Google Analytics, to analyze the usage of our website. These third-party service providers have their own privacy policies addressing how they use such information.\n" +
                        "\n" +
                        "5. Information Sharing\n" +
                        "\n" +
                        "5.1. We do not sell, trade, or otherwise transfer your personal information to third parties without your consent. However, we may share your information with trusted third parties who assist us in operating our website, conducting our business, or servicing you.\n" +
                        "\n" +
                        "6. Data Security\n" +
                        "\n" +
                        "6.1. We take reasonable measures to protect the security of your personal information and prevent unauthorized access, disclosure, alteration, or destruction.\n" +
                        "\n" +
                        "7. Changes to This Privacy Policy\n" +
                        "\n" +
                        "7.1. We reserve the right to update or change our privacy policy at any time. Any changes will be effective immediately upon posting the updated privacy policy on our website.\n" +
                        "\n" +
                        "8. Contact Us\n" +
                        "\n" +
                        "8.1. If you have any questions or concerns about our privacy policy, please contact us at [contact@email.com]."
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contact_Us(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFF14182b)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFF14182b)),
            contentAlignment = Alignment.TopStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, top = 50.dp)
                    .padding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(45.dp)
                        .height(45.dp)
                        .clickable { navController.popBackStack() }
                        .background(Color(0XFFDF1F5A))
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Contact Us",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var name by remember {
                    mutableStateOf("")
                }
                var email by remember {
                    mutableStateOf("")
                }
                var message by remember {
                    mutableStateOf("")
                }
                Image(
                    painter = painterResource(id = R.drawable.wallaperlogo),
                    contentDescription = "",
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .width(190.dp)
                        .height(168.dp)
                )


                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Name",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier
                            .align(
                                Alignment.Start
                            )
                            .padding(start = 10.dp, top = 20.dp)
                    )
                    TextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0XFF14182b),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            focusedTrailingIconColor = Color.White,
                            unfocusedTrailingIconColor = Color.White,
                            focusedLeadingIconColor = Color.White,
                            unfocusedLeadingIconColor = Color.White,
                            focusedPlaceholderColor = Color.White,
                            unfocusedPlaceholderColor = Color.White,
                        ), placeholder = {
                            Text(text = "Enter Your Name")
                        }
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Gmail",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier
                            .align(
                                Alignment.Start
                            )
                            .padding(start = 10.dp, top = 20.dp)
                    )
                    TextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0XFF14182b),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            focusedTrailingIconColor = Color.White,
                            unfocusedTrailingIconColor = Color.White,
                            focusedLeadingIconColor = Color.White,
                            unfocusedLeadingIconColor = Color.White,
                            focusedPlaceholderColor = Color.White,
                            unfocusedPlaceholderColor = Color.White,
                        ), placeholder = {
                            Text(text = "Enter Your Gmail")
                        }
                    )
                }



                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Message",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier
                            .align(
                                Alignment.Start
                            )
                            .padding(start = 10.dp, top = 20.dp)
                    )
                    TextField(
                        value = message,
                        onValueChange = {
                            message = it
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0XFF14182b),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            focusedTrailingIconColor = Color.White,
                            unfocusedTrailingIconColor = Color.White,
                            focusedLeadingIconColor = Color.White,
                            unfocusedLeadingIconColor = Color.White,
                            focusedPlaceholderColor = Color.White,
                            unfocusedPlaceholderColor = Color.White,
                        ), placeholder = {
                            Text(text = "Enter Your Message")
                        }
                    )
                }
                val context = LocalContext.current
                Button(
                    onClick = {

                        navController.navigate(Screen.Setting.route)
                        Toast.makeText(
                            context,
                            "Your message is Submit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }, colors = ButtonDefaults.buttonColors(Color(0XFFDF1F5A)), modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Submit",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }


            }


        }

    }
}

@Composable
fun NewScreen(navController: NavController, image: String?) {
    var like by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context, MyDataBase::class.java, "demo.db"
    ).allowMainThreadQueries().build()
    val repository = remember {
        Repository(db)
    }
    val viewModel = remember {
        MainViewModel(repository)
    }

    var info by remember {
        mutableStateOf(false)
    }



    Box(modifier = Modifier.fillMaxWidth()
        .navigationBarsPadding()
        ,contentAlignment = Alignment.Center) {

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
            modifier = Modifier.padding(top = 650.dp, end = 250.dp)
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
                    Icon(imageVector = Icons.Filled.Favorite,
                        contentDescription = "",
                        tint = Color.Red,
                        modifier = Modifier.clickable {
                            like = !like
                        })
                } else {
                    Icon(imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            like = !like
                            val fav = image?.let { FavItem(null, it, "") }
                            fav?.let { viewModel.Insert(it) }
                        })
                }
            }
        }



        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 650.dp, start = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {

                Icon(imageVector = Icons.Default.CloudDownload,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        val downloadManager =
                            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val uri = Uri.parse(image)
                        val request = DownloadManager.Request(uri).setTitle(image)
                            .setDescription("This is description")
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                            .setAllowedOverRoaming(true)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setDestinationInExternalPublicDir(
                                android.os.Environment.DIRECTORY_DOWNLOADS, "zohaib.png"
                            )

                        downloadManager.enqueue(request)

                    })

            }
        }

        val scope = rememberCoroutineScope()
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 650.dp, end = 130.dp, start = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {

                Icon(imageVector = Icons.Default.Wallpaper,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        scope.launch {
                            val bitmap: Bitmap? = image?.let { fetchBitmapFromUrl(it) }
                            bitmap?.let {
                                setWallpaper(context,it)
                            }
                        }
                    })

            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 650.dp, start = 160.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(Color(0XFF3c3d45).copy(alpha = 0.70f)),
                contentAlignment = Alignment.Center,
            ) {

                Icon(imageVector = Icons.Default.Share,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        shareText(context, "$image")
                    })

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

                Icon(imageVector = Icons.Default.Info,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        info = !info
                    })

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

                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    })

            }
        }

        if (info) {
            AlertDialog(
                onDismissRequest = { info = !info },
                confirmButton = {
                    Text(text = "Ok", modifier = Modifier.clickable { info = !info })
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


