package id.dpradana.themoviedb.feature.reviews.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.core.network.api.model.ReviewResponseDto
import id.dpradana.themoviedb.feature.reviews.data.source.ReviewRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReviewRepositoryImplTest {

    private lateinit var repository: ReviewRepositoryImpl
    private val remoteDataSource: ReviewRemoteDataSource = mockk()

    @Before
    fun setUp() {
        repository = ReviewRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getReviews success`() = runBlocking {
        val response = ReviewResponseDto(
            id = 1,
            page = 1,
            results = emptyList(),
            totalPages = 1,
            totalResults = 0
        )
        coEvery { remoteDataSource.getReviews(any(), any()) } returns response

        val result = repository.getReviews(1, 1)

        assertTrue(result is AppResult.Success)
    }

    @Test
    fun `getReviews error`() = runBlocking {
        coEvery { remoteDataSource.getReviews(any(), any()) } throws Exception("Error")

        val result = repository.getReviews(1, 1)

        assertTrue(result is AppResult.Error)
    }
}
