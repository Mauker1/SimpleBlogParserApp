package br.com.mauker.blogparser.main.datasources

interface BlogLocalDataSource {
    suspend fun isValidCache(): Boolean
    suspend fun getCachedBlogText(): String
    suspend fun saveBlogText(text: String)
}