package id.dpradana.themoviedb.feature.detail.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.core.network.api.model.MovieDetailDto
import id.dpradana.themoviedb.feature.detail.data.source.MovieDetailRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieDetailRepositoryImplTest {

    private val remoteDataSource: MovieDetailRemoteDataSource = mockk()
    private val repository = MovieDetailRepositoryImpl(remoteDataSource)

    @Test
    fun `getMovieDetail success should return Success`() = runBlocking {
        val dto = MovieDetailDto(
            id = 1,
            title = "Test",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = 8.0,
            voteCount = 100,
            runtime = 120,
            genres = null,
            tagline = null,
            status = "Released"
        )
        coEvery { remoteDataSource.getMovieDetail(1) } returns dto

        val result = repository.getMovieDetail(1)

        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.id)
    }

    @Test
    fun `getMovieDetail error should return Error`() = runBlocking {
        val exception = Exception("Network Error")
        coEvery { remoteDataSource.getMovieDetail(1) } throws exception

        val result = repository.getMovieDetail(1)

        assertTrue(result is AppResult.Error)
        assertEquals(exception, (result as AppResult.Error).exception)
    }
}
