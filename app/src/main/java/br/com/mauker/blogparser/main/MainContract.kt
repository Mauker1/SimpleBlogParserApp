package br.com.mauker.blogparser.main

import br.com.mauker.blogparser.mvp.BasePresenter
import br.com.mauker.blogparser.mvp.BaseView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

interface MainContract {

    interface View: BaseView<Presenter> {
        fun setupInitialTexts()
        fun showNoConnectionTexts()

        fun enableSyncButton()
        fun disableSyncButton()

        fun setRequest10thCharText(text: String)
        fun setRequestEvery10thCharText(text: String)
        fun setRequestCountWordText(text: String)

        fun showRequest10thCharLoading()
        fun hideRequest10thCharLoading()
        fun showRequestEvery10thCharLoading()
        fun hideRequestEvery10thCharLoading()
        fun showRequestWordCountLoading()
        fun hideRequestWordCountLoading()
    }

    interface Presenter: BasePresenter {
        fun onSyncClicked()

        fun get10thCharTaskAsync(scope: CoroutineScope, textSource: suspend () -> String): Deferred<Unit>
        fun getEvery10thCharTaskAsync(scope: CoroutineScope, textSource: suspend () -> String): Deferred<Unit>
        fun getCountWordsTaskAsync(scope: CoroutineScope, textSource: suspend () -> String): Deferred<Unit>
    }
}