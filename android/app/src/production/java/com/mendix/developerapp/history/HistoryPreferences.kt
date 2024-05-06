package com.mendix.developerapp.history

import android.content.Context
import android.content.SharedPreferences
import com.google.android.datatransport.backend.cct.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class HistoryPreferences(context: Context) {
    private val historyPreferences: SharedPreferences = context
        .getSharedPreferences(
            BuildConfig.APPLICATION_ID,
            Context.MODE_PRIVATE
        )
    private val _editor: SharedPreferences.Editor = historyPreferences.edit()

    companion object {
        private var HISTORY_LIST_KEY = "HISTORY_LIST"
        private var HISTORY_ID_KEY = "HISTORY_ID"
    }

    fun getHistoryList(): List<History> {
        var arrayItems: List<History> = listOf()
        val serializedObject: String? = historyPreferences.getString(
            HISTORY_LIST_KEY,
            null
        )
        if (serializedObject != null) {
            val type: Type = object : TypeToken<List<History?>?>() {}.type
            arrayItems = Gson().fromJson(serializedObject, type)
        }
        return arrayItems.toMutableList()
    }

    fun setHistoryList(historyList: List<History>) {
        val historyData = Gson().toJson(historyList)
        putString(historyData)
    }

    private fun getLastHistoryId() : Int {
        return historyPreferences.getInt(HISTORY_ID_KEY , 0)
    }

    fun getHistoryId(): Int {
        var historyId = getLastHistoryId()
        _editor.putInt(HISTORY_ID_KEY, ++historyId)
        _editor.commit()
        return historyId
    }

    private fun putString(value: String, key: String = HISTORY_LIST_KEY) {
        _editor.putString(key, value).commit()
    }
}