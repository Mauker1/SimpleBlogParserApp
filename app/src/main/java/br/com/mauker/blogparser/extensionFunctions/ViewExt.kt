package br.com.mauker.blogparser.extensionFunctions

import android.view.View

/**
 * Configures the visibility of a View to visible or gone based on the supplied [condition].
 */
fun View.visibleIf(condition: Boolean) {
    this.visibility = if (condition) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}