package br.com.mauker.blogparser.mvp

interface BaseView<out T: BasePresenter> {
    val presenter: T
}