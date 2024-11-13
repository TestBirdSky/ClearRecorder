package com.refill.waiter

import android.content.ContentValues
import android.net.Uri
import com.waitress.greeting.BaseProvider

/**
 * Date：2024/11/12
 * Describe:
 */
class OrderProvider : BaseProvider() {
    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}