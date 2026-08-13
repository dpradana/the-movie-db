package id.dpradana.themoviedb.feature.movie.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.movie.domain.model.MoviePage
import id.dpradana.themoviedb.feature.movie.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DiscoverMoviesUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = DiscoverMoviesUseCase(repository)

    @Test
    fun `invoke should call repository`() = runTest {
        val expected = AppResult.Success(MoviePage(emptyList(), 1, 1))
        coEvery { repository.discoverMovies(28, 1) } returns expected

        val result = useCase(28, 1)

        assertEquals(expected, result)
    }
}
