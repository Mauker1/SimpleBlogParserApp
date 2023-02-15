package br.com.mauker.blogparser.main.presenter

import br.com.mauker.blogparser.EMPTY_STRING
import br.com.mauker.blogparser.extensionFunctions.toFormattedString
import br.com.mauker.blogparser.main.MainContract
import br.com.mauker.blogparser.main.MainPresenter
import br.com.mauker.blogparser.main.datasources.BlogLocalDataSource
import br.com.mauker.blogparser.main.datasources.BlogRemoteDataSource
import br.com.mauker.blogparser.main.repository.MainRepository
import br.com.mauker.blogparser.main.repository.MainRepositoryImpl
import br.com.mauker.blogparser.utils.NetworkUtils
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainPresenterTest {
    private lateinit var presenter: MainContract.Presenter
    private val view: MainContract.View = mockk(relaxed = true)
    private val networkUtils: NetworkUtils = mockk(relaxed = true)
    private lateinit var repository: MainRepository
    private val localDataSource: BlogLocalDataSource = mockk(relaxed = true)
    private val remoteDataSource: BlogRemoteDataSource = mockk(relaxed = true)

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        clearAllMocks()
        Dispatchers.setMain(mainThreadSurrogate)

        repository = MainRepositoryImpl(remoteDataSource, localDataSource)
        presenter = MainPresenter(
            view = view,
            repository = repository,
            networkUtils = networkUtils
        )
    }

    @After
    fun preTearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `on start, should setup view`() {
        presenter.start()

        verifyOrder {
            view.run {
                setupInitialTexts()
                hideRequest10thCharLoading()
                hideRequestEvery10thCharLoading()
                hideRequestWordCountLoading()
            }
        }
    }

    @Test
    fun `on get 10th character, should display it`() {
        every { networkUtils.isConnected() } returns true
        coEvery { localDataSource.isValidCache() } returns false
        coEvery { remoteDataSource.getBlogText() } returns VALID_BLOG_TEXT

        runBlocking {
            presenter.get10thCharTaskAsync(this, repository::getBlogText).await()

            coVerifyOrder {
                view.run {
                    setRequest10thCharText(TENTH_CHAR)
                    hideRequest10thCharLoading()
                }
            }
            confirmVerified(view)
        }
    }

    @Test
    fun `on get every 10th character, should display them`() {
        every { networkUtils.isConnected() } returns true
        coEvery { localDataSource.isValidCache() } returns false
        coEvery { remoteDataSource.getBlogText() } returns VALID_BLOG_TEXT

        runBlocking {
            presenter.getEvery10thCharTaskAsync(this, repository::getBlogText).await()

            coVerifyOrder {
                view.run {
                    setRequestEvery10thCharText(EVERY_10TH_CHAR.toString())
                    hideRequestEvery10thCharLoading()
                }
            }
            confirmVerified(view)
        }
    }

    @Test
    fun `on get word count, should display them`() {
        every { networkUtils.isConnected() } returns true
        coEvery { localDataSource.isValidCache() } returns false
        coEvery { remoteDataSource.getBlogText() } returns VALID_BLOG_TEXT

        runBlocking {
            presenter.getCountWordsTaskAsync(this, repository::getBlogText).await()

            val fs = WORD_COUNT

            coVerifyOrder {
                view.run {
                    setRequestCountWordText(fs.toFormattedString())
                    hideRequestWordCountLoading()
                }
            }
            confirmVerified(view)
        }
    }

    @Test
    fun `on disconnected with no cache, should show error message`() {
        every { networkUtils.isConnected() } returns false
        coEvery { localDataSource.isValidCache() } returns false
        coEvery { localDataSource.getCachedBlogText() } returns EMPTY_STRING

        presenter.onSyncClicked()

        runBlocking {
            coVerifyOrder {
                view.run {
                    disableSyncButton()
                    showNoConnectionTexts()
                    enableSyncButton()
                }
            }
            confirmVerified(view)
        }
    }

    @Test
    fun `on disconnected with cache, should show requests`() {
        every { networkUtils.isConnected() } returns false
        coEvery { localDataSource.isValidCache() } returns true
        coEvery { localDataSource.getCachedBlogText() } returns VALID_BLOG_TEXT

        presenter.onSyncClicked()

        runBlocking {
            coVerify {
                view.run {
                    disableSyncButton()
                    showRequest10thCharLoading()
                    showRequestEvery10thCharLoading()
                    showRequestWordCountLoading()
                }
            }
        }
    }

    @Test
    fun `on connected without cache, should show requests`() {
        every { networkUtils.isConnected() } returns true
        coEvery { localDataSource.isValidCache() } returns false
        coEvery { remoteDataSource.getBlogText() } returns VALID_BLOG_TEXT

        presenter.onSyncClicked()

        runBlocking {
            coVerify {
                view.run {
                    disableSyncButton()
                    showRequest10thCharLoading()
                    showRequestEvery10thCharLoading()
                    showRequestWordCountLoading()
                }
            }
        }
    }

    companion object {
        private const val VALID_BLOG_TEXT = "<html>\n\t<p> Hello World!! </p>\n\t<p> Hello again </p>\n</html>"
        private const val TENTH_CHAR = "p"
        private val EVERY_10TH_CHAR = listOf('p','o','>','l','/','>')
        private val WORD_COUNT = mutableMapOf(
            "<html>" to 1,
            "<p>" to 2,
            "Hello" to 2,
            "World!!" to 1,
            "</p>" to 2,
            "again" to 1,
            "</html>" to 1
        )

        @JvmStatic
        @AfterClass
        fun tearDown() {
            unmockkAll()
        }
    }
}