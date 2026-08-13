package id.dpradana.themoviedb.core.network.di

import id.dpradana.themoviedb.core.network.api.MovieApi

interface AppComponentProvider {
    fun movieApi(): MovieApi
}
