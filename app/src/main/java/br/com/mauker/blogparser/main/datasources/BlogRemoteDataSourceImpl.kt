package br.com.mauker.blogparser.main.datasources

import br.com.mauker.blogparser.main.service.BlogService

class BlogRemoteDataSourceImpl(
    private val blogService: BlogService
): BlogRemoteDataSource {
    override suspend fun getBlogText(): String {
        return blogService.getBlogText()
    }
}