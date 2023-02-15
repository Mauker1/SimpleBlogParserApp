package br.com.mauker.blogparser

import android.app.Application
import br.com.mauker.blogparser.main.di.mainModule
import br.com.mauker.blogparser.utils.di.utilsModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import timber.log.Timber

class BlogParserApp: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@BlogParserApp)
            modules(getAppModules())
        }
    }

    private fun getAppModules(): List<Module> = listOf(
        mainModule,
        utilsModule
    )
}