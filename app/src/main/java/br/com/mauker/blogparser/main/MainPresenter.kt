package br.com.mauker.blogparser.main

import br.com.mauker.blogparser.*
import br.com.mauker.blogparser.extensionFunctions.incrementValue
import br.com.mauker.blogparser.extensionFunctions.toFormattedString
import br.com.mauker.blogparser.main.repository.MainRepository
import br.com.mauker.blogparser.utils.NetworkUtils
import kotlinx.coroutines.*

class MainPresenter(
    private val view: MainContract.View,
    private val repository: MainRepository,
    private val networkUtils: NetworkUtils
): MainContract.Presenter {

    private var parentJob: Job? = null

    override fun start() {
        view.run {
            setupInitialTexts()
            hideRequest10thCharLoading()
            hideRequestEvery10thCharLoading()
            hideRequestWordCountLoading()
        }
    }

    override fun stop() {
        parentJob?.cancel()
    }

    override fun onSyncClicked() {
        parentJob = CoroutineScope(Dispatchers.Main).launch {
            view.disableSyncButton()
            val textSource: (suspend () -> String) = when {
                networkUtils.isConnected() -> repository::getBlogText
                repository.getCachedBlogText().isNotBlank() -> repository::getCachedBlogText
                else -> {
                    view.run {
                        showNoConnectionTexts()
                        enableSyncButton()
                    }
                    return@launch
                }
            }

            val task10thChar = get10thCharTaskAsync(this, textSource)
            val taskEvery10thChar = getEvery10thCharTaskAsync(this, textSource)
            val taskCountWords = getCountWordsTaskAsync(this, textSource)

            view.run {
                showRequest10thCharLoading()
                showRequestEvery10thCharLoading()
                showRequestWordCountLoading()
                awaitAll(task10thChar, taskEvery10thChar, taskCountWords)
                enableSyncButton()
            }
        }
    }

    override fun get10thCharTaskAsync(scope: CoroutineScope, textSource: suspend () -> String): Deferred<Unit> {
        return scope.async(context = Dispatchers.IO) {
            val text = textSource.invoke()
            val char = if (text.length >= 10) text[9] else EMPTY_STRING
            withContext(Dispatchers.Main) {
                view.run {
                    setRequest10thCharText(char.toString())
                    hideRequest10thCharLoading()
                }
            }
        }
    }

    override fun getEvery10thCharTaskAsync(scope: CoroutineScope, textSource: suspend () -> String): Deferred<Unit> {
        return scope.async(context = Dispatchers.IO) {
            val text = textSource.invoke()
            val filteredText = mutableListOf<Char>()
            text.forEachIndexed { index, c ->
                if (index.plus(1).rem(10) == 0) {
                    filteredText.add(c)
                }
            }
            withContext(Dispatchers.Main) {
                view.run {
                    setRequestEvery10thCharText(filteredText.toString())
                    hideRequestEvery10thCharLoading()
                }
            }
        }
    }

    override fun getCountWordsTaskAsync(scope: CoroutineScope, textSource: suspend () -> String): Deferred<Unit> {
        return scope.async(context = Dispatchers.IO) {
            val text = textSource.invoke()
            val wordCountMap = mutableMapOf<String, Int>()
            val splitText = text.split(
                WHITESPACE,
                NEW_LINE_TAB,
                TAB,
                NEW_LINE
            ).filterNot { it == EMPTY_STRING }

            splitText.forEach { str ->
                wordCountMap.incrementValue(str)
            }
            withContext(Dispatchers.Main) {
                view.run {
                    setRequestCountWordText(wordCountMap.toFormattedString())
                    hideRequestWordCountLoading()
                }
            }
        }
    }
}