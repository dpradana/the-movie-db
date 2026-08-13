package id.dpradana.themoviedb.feature.movie.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.core.network.api.model.MovieDto
import id.dpradana.themoviedb.core.network.api.model.MovieResponseDto
import id.dpradana.themoviedb.feature.movie.data.source.MovieRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieRepositoryImplTest {

    private val remoteDataSource: MovieRemoteDataSource = mockk()
    private val repository = MovieRepositoryImpl(remoteDataSource)

    @Test
    fun `discoverMovies success should return Success with domain models`() = runTest {
        val dto = MovieDto(1, "Title", "Overview", "/path", "/backdrop", "2026", 8.0, 100)
        val response = MovieResponseDto(1, listOf(dto), 2, 20)
        coEvery { remoteDataSource.discoverMovies(28, 1) } returns response

        val result = repository.discoverMovies(28, 1)

        assertTrue(result is AppResult.Success)
        val domain = (result as AppResult.Success).data
        assertEquals(1, domain.movies.size)
        assertEquals("Title", domain.movies[0].title)
        assertEquals(1, domain.page)
        assertEquals(2, domain.totalPages)
    }

    @Test
    fun `discoverMovies error should return Error`() = runTest {
        coEvery { remoteDataSource.discoverMovies(28, 1) } throws Exception("Network Error")

        val result = repository.discoverMovies(28, 1)

        assertTrue(result is AppResult.Error)
        assertEquals("Network Error", (result as AppResult.Error).exception.message)
    }
}
