package br.com.mauker.blogparser.extensionFunctions

import br.com.mauker.blogparser.NEW_LINE
import java.lang.StringBuilder

fun MutableMap<String, Int>.incrementValue(key: String) {
    when (val count = this[key]) {
        null -> {
            this[key] = 1
        }
        else -> {
            this[key] = count + 1
        }
    }
}

fun MutableMap<String, Int>.toFormattedString(): String {
    val sb = StringBuilder()
    val map = this

    keys.forEach { key ->
        sb.run {
            append(key)
            append(" ==> ")
            append(map[key])
            append(NEW_LINE)
            append(NEW_LINE)
        }
    }

    return sb.toString()
}