package br.com.mauker.blogparser.main.datasources

import br.com.mauker.blogparser.coVerifyOnce
import br.com.mauker.blogparser.main.service.BlogService
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test

class BlogRemoteDataSourceTest {
    private lateinit var remoteDataSource: BlogRemoteDataSource
    private val service: BlogService = mockk(relaxed = true)

    @Before
    fun setup() {
        clearAllMocks()

        remoteDataSource = BlogRemoteDataSourceImpl(blogService = service)
        coEvery { service.getBlogText() } returns VALID_BLOG_TEXT
    }

    @Test
    fun `when request is made, should call service`() {
        runBlocking {
            remoteDataSource.getBlogText()

            coVerifyOnce { service.getBlogText() }
            confirmVerified(service)
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