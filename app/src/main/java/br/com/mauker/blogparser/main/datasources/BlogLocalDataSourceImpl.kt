package br.com.mauker.blogparser.main.datasources

import br.com.mauker.blogparser.CACHE_VALIDITY_TIME
import br.com.mauker.blogparser.NO_CACHE_ENTRY
import br.com.mauker.blogparser.main.datasources.preferences.BlogTextLocalCache
import java.util.*

class BlogLocalDataSourceImpl(
    private val cache: BlogTextLocalCache
): BlogLocalDataSource {

    override suspend fun isValidCache(): Boolean {
        val now = Calendar.getInstance().timeInMillis
        val latestUpdate = cache.getLatestEntryTime()

        // Calculate difference in milliseconds
        val diffMillis = now - latestUpdate

        return latestUpdate != NO_CACHE_ENTRY &&
                diffMillis <= CACHE_VALIDITY_TIME &&
                cache.getBlogText().isNotBlank()
    }

    override suspend fun getCachedBlogText(): String = cache.getBlogText()

    override suspend fun saveBlogText(text: String) {
        cache.saveBlogText(
            text = text,
            time = Calendar.getInstance().timeInMillis
        )
    }
}