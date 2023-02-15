package br.com.mauker.blogparser.main.repository

interface MainRepository {
    suspend fun getBlogText(): String
    suspend fun getCachedBlogText(): String
}