package id.dpradana.themoviedb.feature.genre.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.core.network.api.model.GenreDto
import id.dpradana.themoviedb.core.network.api.model.GenreResponseDto
import id.dpradana.themoviedb.feature.genre.data.local.GenreEntity
import id.dpradana.themoviedb.feature.genre.data.local.GenreLocalDataSource
import id.dpradana.themoviedb.feature.genre.data.remote.GenreRemoteDataSource
import id.dpradana.themoviedb.feature.genre.domain.model.Genre
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GenreRepositoryImplTest {

    private lateinit var repository: GenreRepositoryImpl
    private val remoteDataSource: GenreRemoteDataSource = mockk()
    private val localDataSource: GenreLocalDataSource = mockk()

    @Before
    fun setUp() {
        repository = GenreRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getGenres should save to local and return genres on remote success`() = runTest {
        val dto = GenreDto(1, "Action")
        val responseDto = GenreResponseDto(listOf(dto))
        coEvery { remoteDataSource.getGenres() } returns responseDto
        coEvery { localDataSource.replaceGenres(any()) } returns Unit

        val result = repository.getGenres()

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(Genre(1, "Action")), (result as AppResult.Success).data)
        coVerify { localDataSource.replaceGenres(listOf(GenreEntity(1, "Action"))) }
    }

    @Test
    fun `getGenres should return cached genres on remote failure and cache not empty`() = runTest {
        val exception = Exception("API error")
        coEvery { remoteDataSource.getGenres() } throws exception
        coEvery { localDataSource.getGenres() } returns listOf(GenreEntity(1, "Action"))

        val result = repository.getGenres()

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(Genre(1, "Action")), (result as AppResult.Success).data)
    }

    @Test
    fun `getGenres should return error on remote failure and cache empty`() = runTest {
        val exception = Exception("API error")
        coEvery { remoteDataSource.getGenres() } throws exception
        coEvery { localDataSource.getGenres() } returns emptyList()

        val result = repository.getGenres()

        assertTrue(result is AppResult.Error)
        assertEquals(exception, (result as AppResult.Error).exception)
    }
}
