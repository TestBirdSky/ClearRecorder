package com.waitress.greeting

import android.content.ContentProvider
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

/**
 * Date：2024/11/12
 * Describe:
 */
abstract class BaseProvider : ContentProvider() {

    private val providerStr = arrayOf(
        "accountName", "accountType", "displayName", "typeResourceId",
        "exportSupport", "shortcutSupport", "photoSupport")

    private fun getCursor(uri: Uri?, name: String): Cursor? {
        if (uri == null || !uri.toString().endsWith("/directories")) {
            return null
        }
        val matrixCursor = MatrixCursor(providerStr)
        matrixCursor.addRow(arrayOf<Any>(name, name, name, "0", "1".toInt(), 1, 1))
        return matrixCursor
    }


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return getCursor(uri, context?.packageName ?: "")
    }

}