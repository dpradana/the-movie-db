package id.dpradana.themoviedb.feature.reviews.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.ReviewPage
import id.dpradana.themoviedb.feature.reviews.domain.repository.ReviewRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMovieReviewsUseCaseTest {

    private lateinit var useCase: GetMovieReviewsUseCase
    private val repository: ReviewRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetMovieReviewsUseCase(repository)
    }

    @Test
    fun `invoke calls repository`() = runBlocking {
        val expected = AppResult.Success(ReviewPage(emptyList(), 1, 1))
        coEvery { repository.getReviews(1, 1) } returns expected

        val result = useCase(1, 1)

        assertEquals(expected, result)
    }
}
