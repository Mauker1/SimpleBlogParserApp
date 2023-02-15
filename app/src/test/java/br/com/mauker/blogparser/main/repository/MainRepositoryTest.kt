package br.com.mauker.blogparser.main.repository

import br.com.mauker.blogparser.coVerifyNever
import br.com.mauker.blogparser.coVerifyOnce
import br.com.mauker.blogparser.main.datasources.BlogLocalDataSource
import br.com.mauker.blogparser.main.datasources.BlogRemoteDataSource
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test

class MainRepositoryTest {
    private lateinit var repository: MainRepository
    private val localDataSource: BlogLocalDataSource = mockk(relaxed = true)
    private val remoteDataSource: BlogRemoteDataSource = mockk(relaxed = true)

    @Before
    fun setup() {
        clearAllMocks()

        repository = MainRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `when get cache is called, should invoke local data source`() {
        coEvery { localDataSource.getCachedBlogText() } returns VALID_BLOG_TEXT

        runBlocking {
            repository.getCachedBlogText()

            coVerifyOnce { localDataSource.getCachedBlogText() }
            confirmVerified(localDataSource)
        }
    }

    @Test
    fun `when cache is valid, should return cached text`() {
        coEvery { remoteDataSource.getBlogText() } returns VALID_BLOG_TEXT
        coEvery { localDataSource.getCachedBlogText() } returns VALID_BLOG_TEXT
        coEvery { localDataSource.isValidCache() } returns true

        runBlocking {
            repository.getBlogText()

            coVerifyOnce { localDataSource.isValidCache() }
            coVerifyOnce { localDataSource.getCachedBlogText() }
            coVerifyNever { remoteDataSource.getBlogText() }
            confirmVerified(localDataSource)
            confirmVerified(remoteDataSource)
        }
    }

    @Test
    fun `when cache is invalid, should return network text`() {
        coEvery { remoteDataSource.getBlogText() } returns VALID_BLOG_TEXT
        coEvery { localDataSource.getCachedBlogText() } returns VALID_BLOG_TEXT
        coEvery { localDataSource.isValidCache() } returns false

        runBlocking {
            repository.getBlogText()

            coVerifyOnce { localDataSource.isValidCache() }
            coVerifyNever { localDataSource.getCachedBlogText() }
            coVerifyOnce { remoteDataSource.getBlogText() }
            coVerifyOnce { localDataSource.saveBlogText(any()) }
            confirmVerified(localDataSource)
            confirmVerified(remoteDataSource)
        }
    }

    companion object {
        private const val VALID_BLOG_TEXT = "<html> Hello World! </html>"

        @JvmStatic
        @AfterClass
        fun tearDown() {
            unmockkAll()
        }
    }
}