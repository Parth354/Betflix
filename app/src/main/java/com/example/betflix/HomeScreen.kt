package com.example.betflix

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: MovieViewModel = getViewModel()) {
    val movies by viewModel.movies.collectAsState()
    val tvShows by viewModel.tvShows.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var showMovies by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { showMovies = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showMovies) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text("Movies", color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { showMovies = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!showMovies) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text("TV Shows", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            ShimmerEffect()
        } else {
            LazyColumn {
                val list = if (showMovies) movies else tvShows

                if (list.isEmpty()) {
                    item {
                        Text(
                            text = "No ${if (showMovies) "movies" else "TV shows"} found.",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                }

                items(list) { movie ->
                    MovieItem(
                        movie = movie,
                        onClick = { navController.navigate("details/${movie.id}") }
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                if (showMovies) {
                                    viewModel.loadMoreMovies()
                                } else {
                                    viewModel.loadMoreTVShows()
                                }
                            }
                        ) {
                            Text("Load More")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            val imageUrl = movie.poster.orEmpty()
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = movie.title.orEmpty(),
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(100.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                },
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title ?: "Unknown Title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Year: ${movie.year ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
