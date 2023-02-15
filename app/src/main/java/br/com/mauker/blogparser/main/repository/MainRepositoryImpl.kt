package br.com.mauker.blogparser.main.repository

import br.com.mauker.blogparser.main.datasources.BlogLocalDataSource
import br.com.mauker.blogparser.main.datasources.BlogRemoteDataSource
import java.util.concurrent.atomic.AtomicBoolean

class MainRepositoryImpl(
    private val remoteDataSource: BlogRemoteDataSource,
    private val localDataSource: BlogLocalDataSource
): MainRepository {

    private val isWritingToCache = AtomicBoolean(false)

    override suspend fun getBlogText(): String {
        if (localDataSource.isValidCache()) {
            return localDataSource.getCachedBlogText()
        }

        val blogText = remoteDataSource.getBlogText()
        if (isWritingToCache.compareAndSet(false, true) && blogText.isNotBlank()) {
            localDataSource.saveBlogText(blogText)
            isWritingToCache.set(false)
        }

        return blogText
    }

    override suspend fun getCachedBlogText(): String = localDataSource.getCachedBlogText()
}