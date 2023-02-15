package br.com.mauker.blogparser.main.datasources

interface BlogRemoteDataSource {
    suspend fun getBlogText(): String
}