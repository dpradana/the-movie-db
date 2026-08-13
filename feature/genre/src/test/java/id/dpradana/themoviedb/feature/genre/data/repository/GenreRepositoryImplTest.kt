package id.dpradana.themoviedb.feature.genre.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.core.network.api.model.GenreDto
import id.dpradana.themoviedb.core.network.api.model.GenreResponseDto
import id.dpradana.themoviedb.feature.genre.data.remote.GenreRemoteDataSource
import id.dpradana.themoviedb.feature.genre.domain.model.Genre
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GenreRepositoryImplTest {

    private lateinit var repository: GenreRepositoryImpl
    private val remoteDataSource: GenreRemoteDataSource = mockk()

    @Before
    fun setUp() {
        repository = GenreRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getGenres should return mapped genres on success`() = runTest {
        val dto = GenreDto(1, "Action")
        val responseDto = GenreResponseDto(listOf(dto))
        coEvery { remoteDataSource.getGenres() } returns responseDto

        val result = repository.getGenres()

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(Genre(1, "Action")), (result as AppResult.Success).data)
    }

    @Test
    fun `getGenres should return error on exception`() = runTest {
        val exception = Exception("API error")
        coEvery { remoteDataSource.getGenres() } throws exception

        val result = repository.getGenres()

        assertTrue(result is AppResult.Error)
        assertEquals(exception, (result as AppResult.Error).exception)
    }
}
