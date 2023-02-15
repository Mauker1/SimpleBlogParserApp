package br.com.mauker.blogparser.utils.di

import br.com.mauker.blogparser.utils.NetworkUtils
import br.com.mauker.blogparser.utils.NetworkUtilsImpl
import org.koin.dsl.module

val utilsModule = module {
    factory<NetworkUtils> { NetworkUtilsImpl(context = get()) }
}