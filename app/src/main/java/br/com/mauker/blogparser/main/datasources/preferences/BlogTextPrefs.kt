package br.com.mauker.blogparser.main.datasources.preferences

import android.content.Context
import br.com.mauker.blogparser.EMPTY_STRING
import br.com.mauker.blogparser.NO_CACHE_ENTRY

class BlogTextPrefs(context: Context): BlogTextLocalCache {

    private val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    private var entryTime: Long
        get() = prefs.getLong(KEY_ENTRY_TIME, NO_CACHE_ENTRY)
        set(value) = prefs.edit().putLong(KEY_ENTRY_TIME, value).apply()

    private var blogText: String
        get() = prefs.getString(KEY_TEXT, EMPTY_STRING) ?: EMPTY_STRING
        set(value) = prefs.edit().putString(KEY_TEXT, value).apply()

    override suspend fun getLatestEntryTime(): Long = entryTime

    override suspend fun saveBlogText(text: String, time: Long) {
        blogText = text
        entryTime = time
    }

    override suspend fun getBlogText(): String = blogText

    companion object {
        private const val PREF_FILE_NAME = "blog_text.prefs"

        private const val KEY_ENTRY_TIME = "ENTRY_TIME"
        private const val KEY_TEXT = "TEXT"
    }
}