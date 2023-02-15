package br.com.mauker.blogparser.main.datasources

import br.com.mauker.blogparser.EMPTY_STRING
import br.com.mauker.blogparser.NO_CACHE_ENTRY
import br.com.mauker.blogparser.main.datasources.preferences.BlogTextLocalCache
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class BlogLocalDataSourceTest {
    private lateinit var localDataSource: BlogLocalDataSource
    private val blogCache: BlogTextLocalCache = mockk(relaxed = true)

    @Before
    fun setup() {
        clearAllMocks()
        localDataSource = BlogLocalDataSourceImpl(cache = blogCache)
    }

    @Test
    fun `when data is valid, should return valid cache`() {
        val before = Calendar.getInstance().apply {
            add(Calendar.SECOND, -1)
        }
        coEvery { blogCache.getLatestEntryTime() } returns before.timeInMillis
        coEvery { blogCache.getBlogText() } returns VALID_BLOG_TEXT

        runBlocking {
            Assert.assertTrue(localDataSource.isValidCache())
        }
    }

    @Test
    fun `when blog text is empty, cache should return invalid cache`() {
        val before = Calendar.getInstance().apply {
            add(Calendar.SECOND, -1)
        }
        coEvery { blogCache.getLatestEntryTime() } returns before.timeInMillis
        coEvery { blogCache.getBlogText() } returns BLANK_BLOG_TEXT

        runBlocking {
            Assert.assertFalse(localDataSource.isValidCache())
        }
    }

    @Test
    fun `when latest time entry text is empty, cache should return invalid cache`() {
        coEvery { blogCache.getLatestEntryTime() } returns NO_CACHE_ENTRY
        coEvery { blogCache.getBlogText() } returns VALID_BLOG_TEXT

        runBlocking {
            Assert.assertFalse(localDataSource.isValidCache())
        }
    }

    companion object {
        private const val VALID_BLOG_TEXT = "<html> Hello World! </html>"
        private const val BLANK_BLOG_TEXT = EMPTY_STRING

        @JvmStatic
        @AfterClass
        fun tearDown() {
            unmockkAll()
        }
    }
}