package id.dpradana.themoviedb.feature.genre.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenreDaoTest {

    private lateinit var database: GenreDatabase
    private lateinit var dao: GenreDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GenreDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.genreDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndObserveGenres() = runTest {
        val genres = listOf(
            GenreEntity(1, "Action"),
            GenreEntity(2, "Comedy")
        )
        dao.insertGenres(genres)

        val observed = dao.observeGenres().first()
        assertEquals(genres.sortedBy { it.name }, observed)
    }

    @Test
    fun getGenres() = runTest {
        val genres = listOf(
            GenreEntity(2, "Comedy"),
            GenreEntity(1, "Action")
        )
        dao.insertGenres(genres)

        val result = dao.getGenres()
        assertEquals(listOf(GenreEntity(1, "Action"), GenreEntity(2, "Comedy")), result)
    }

    @Test
    fun clearGenres() = runTest {
        val genres = listOf(GenreEntity(1, "Action"))
        dao.insertGenres(genres)
        dao.clearGenres()

        val result = dao.getGenres()
        assertTrue(result.isEmpty())
    }

    @Test
    fun replaceGenres() = runTest {
        val oldGenres = listOf(GenreEntity(1, "Action"))
        dao.insertGenres(oldGenres)

        val newGenres = listOf(GenreEntity(2, "Comedy"))
        dao.replaceGenres(newGenres)

        val result = dao.getGenres()
        assertEquals(newGenres, result)
    }

    @Test
    fun emptyDatabaseReturnsEmptyList() = runTest {
        val result = dao.getGenres()
        assertTrue(result.isEmpty())
    }
}
