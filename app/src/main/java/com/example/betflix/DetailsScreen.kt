package com.example.betflix

import androidx.compose.foundation.layout.*
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
fun DetailsScreen(
    movieId: Int?,
    navController: NavController
) {
    val viewModel: MovieViewModel = getViewModel()

    val movies by viewModel.movies.collectAsState()
    val tvShows by viewModel.tvShows.collectAsState()

    val movie = movies.find { it.id == movieId } ?: tvShows.find { it.id == movieId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (movie == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Movie Not Found", fontSize = 18.sp)
            }
        } else {
            SubcomposeAsyncImage(
                model = movie.poster.orEmpty(),
                contentDescription = movie.title.orEmpty(),
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        CircularProgressIndicator()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = movie.title ?: "Unknown Title",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Release Year: ${movie.year ?: "N/A"}",
                fontSize = 18.sp,
                color = Color.Gray
            )

            movie.type?.let {
                Text(
                    text = "Type: ${it.replaceFirstChar { c -> c.uppercase() }}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}
