package id.dpradana.themoviedb.feature.genre.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.genre.domain.model.Genre
import id.dpradana.themoviedb.feature.genre.domain.repository.GenreRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetGenresUseCaseTest {

    private lateinit var useCase: GetGenresUseCase
    private val repository: GenreRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetGenresUseCase(repository)
    }

    @Test
    fun `invoke should return success from repository`() = runTest {
        val genres = listOf(Genre(1, "Action"))
        coEvery { repository.getGenres() } returns AppResult.Success(genres)

        val result = useCase()

        assertEquals(AppResult.Success(genres), result)
    }

    @Test
    fun `invoke should return error from repository`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getGenres() } returns AppResult.Error(exception)

        val result = useCase()

        assertEquals(AppResult.Error(exception), result)
    }
}
