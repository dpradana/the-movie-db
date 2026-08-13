package id.dpradana.themoviedb.feature.detail.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail
import id.dpradana.themoviedb.feature.detail.domain.repository.MovieDetailRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMovieDetailUseCaseTest {

    private val repository: MovieDetailRepository = mockk()
    private val useCase = GetMovieDetailUseCase(repository)

    @Test
    fun `invoke should return result from repository`() = runBlocking {
        val movieDetail = mockk<MovieDetail>()
        val expectedResult = AppResult.Success(movieDetail)
        coEvery { repository.getMovieDetail(1) } returns expectedResult

        val result = useCase(1)

        assertEquals(expectedResult, result)
    }
}
