package br.com.mauker.blogparser.main.datasources.preferences

interface BlogTextLocalCache {
    suspend fun getLatestEntryTime(): Long
    suspend fun saveBlogText(text: String, time: Long)
    suspend fun getBlogText(): String
}