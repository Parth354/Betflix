package com.example.betflix

import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository(private val api: WatchmodeApi) {

    fun fetchMoviesAndShows(apiKey: String): Single<Pair<List<Movie>, List<Movie>>> {
        val moviesSingle = api.getMovies(apiKey, "movie", 10, 1)
            .map { it.titles }
            .onErrorReturn { emptyList() }

        val tvShowsSingle = api.getMovies(apiKey, "tv_series", 10, 1)
            .map { it.titles }
            .onErrorReturn { emptyList() }

        return Single.zip(moviesSingle, tvShowsSingle) { moviesList, tvShowsList ->
            Pair(moviesList, tvShowsList)
        }
    }

    fun getMovies(page: Int): Flow<List<Movie>> = flow {
        try {
            val response = api.getMovies(API_KEY, "movie", 10, page).blockingGet()
            emit(response.titles)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getTVShows(page: Int): Flow<List<Movie>> = flow {
        try {
            val response = api.getMovies(API_KEY, "tv_series", 10, page).blockingGet()
            emit(response.titles)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun searchTitle(apiKey: String, title: String, searchType: Int): Single<SearchResult> {
        return api.searchTitle(apiKey, title, searchType)
            .map { it.results.firstOrNull()!! }
    }

    companion object {
        private const val API_KEY = "4kDOyhmJz8TBGrPPT2iaOgjC82OjAdyBkqLd4Tv7"
    }
}
