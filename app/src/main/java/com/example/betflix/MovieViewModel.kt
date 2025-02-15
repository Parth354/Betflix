package com.example.betflix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _tvShows = MutableStateFlow<List<Movie>>(emptyList())
    val tvShows: StateFlow<List<Movie>> = _tvShows

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val disposables = CompositeDisposable()

    private var moviePage = 1
    private var tvPage = 1

    init {
        fetchData(API_KEY)
    }

    fun loadMoreMovies() {
        viewModelScope.launch {
            val nextPage = moviePage + 1
            repository.getMovies(nextPage).collect { newMovies ->
                if (newMovies.isNotEmpty()) {
                    newMovies.forEach { fetchMovieDetails(it) }
                    _movies.value = _movies.value + newMovies
                    moviePage = nextPage
                }
            }
        }
    }

    fun loadMoreTVShows() {
        viewModelScope.launch {
            val nextPage = tvPage + 1
            repository.getTVShows(nextPage).collect { newShows ->
                if (newShows.isNotEmpty()) {
                    newShows.forEach { fetchMovieDetails(it) }
                    _tvShows.value = _tvShows.value + newShows
                    tvPage = nextPage
                }
            }
        }
    }

    fun fetchData(apiKey: String) {
        _loading.value = true
        _error.value = null

        val disposable = repository.fetchMoviesAndShows(apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (moviesList, tvShowsList) ->
                _movies.value = moviesList
                _tvShows.value = tvShowsList
                _loading.value = false

                moviesList.forEach { fetchMovieDetails(it) }
                tvShowsList.forEach { fetchMovieDetails(it) }
            }, { error ->
                _loading.value = false
                _error.value = error.message ?: "Unknown error occurred"
            })

        disposables.add(disposable)
    }

    fun fetchMovieDetails(item: Movie) {
        // If there is no title to search, skip
        if (item.title.isNullOrEmpty()) return

        // Use search type 3 for movies, 4 for TV shows
        val searchType = if (item.type == "movie") 3 else 4

        val disposable = repository.searchTitle(API_KEY, item.title, searchType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ searchResult ->
                searchResult?.let {
                    // Merge the search details into our Movie
                    val updatedMovie = item.mergeWith(it)
                    updateMovieInLists(updatedMovie)
                }
            }, { /* Optionally handle errors here */ })

        disposables.add(disposable)
    }

    private fun updateMovieInLists(updatedMovie: Movie) {
        // Update movies list if it contains the movie; otherwise update TV shows
        _movies.value = _movies.value.map { if (it.id == updatedMovie.id) updatedMovie else it }
        _tvShows.value = _tvShows.value.map { if (it.id == updatedMovie.id) updatedMovie else it }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    companion object {
        private const val API_KEY = "4kDOyhmJz8TBGrPPT2iaOgjC82OjAdyBkqLd4Tv7"
    }
}
