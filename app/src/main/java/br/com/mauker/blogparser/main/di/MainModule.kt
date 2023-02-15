package br.com.mauker.blogparser.main.di

import br.com.mauker.blogparser.BASE_URL
import br.com.mauker.blogparser.main.MainContract
import br.com.mauker.blogparser.main.MainPresenter
import br.com.mauker.blogparser.main.datasources.BlogLocalDataSource
import br.com.mauker.blogparser.main.datasources.BlogLocalDataSourceImpl
import br.com.mauker.blogparser.main.datasources.BlogRemoteDataSource
import br.com.mauker.blogparser.main.datasources.BlogRemoteDataSourceImpl
import br.com.mauker.blogparser.main.datasources.preferences.BlogTextLocalCache
import br.com.mauker.blogparser.main.datasources.preferences.BlogTextPrefs
import br.com.mauker.blogparser.main.repository.MainRepository
import br.com.mauker.blogparser.main.repository.MainRepositoryImpl
import br.com.mauker.blogparser.main.service.BlogService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

val mainModule = module {

    //region Networking
    single<Interceptor> {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor = get())
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(MoshiConverterFactory.create()) // Not using it for now.
            .build()
    }

    single<BlogService> { get<Retrofit>().create(BlogService::class.java) }
    //endregion

    single<BlogTextLocalCache> { BlogTextPrefs(context = get()) }

    single<BlogLocalDataSource> { BlogLocalDataSourceImpl(cache = get()) }

    single<BlogRemoteDataSource> { BlogRemoteDataSourceImpl(blogService = get()) }

    single<MainRepository> { MainRepositoryImpl(remoteDataSource = get(), localDataSource = get()) }

    factory<MainContract.Presenter> { (view: MainContract.View) ->
        MainPresenter(view = view, repository = get(), networkUtils = get())
    }
}