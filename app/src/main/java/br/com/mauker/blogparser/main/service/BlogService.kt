package br.com.mauker.blogparser.main.service

import retrofit2.http.GET

interface BlogService {

    @GET(PATH_BLOG)
    suspend fun getBlogText(): String

    companion object {
        private const val PATH_BLOG = "the-imposter-guide-to-dependency-injection"
    }
}