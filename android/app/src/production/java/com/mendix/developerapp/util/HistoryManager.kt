package com.mendix.developerapp.util

import android.content.Context
import com.mendix.developerapp.history.History
import com.mendix.developerapp.history.HistoryPreferences

class HistoryManager(context: Context) {
    private val _historyPreferences: HistoryPreferences = HistoryPreferences(context = context)
    private val _maxItemSize: Int = 10

    fun getHistoryList(): List<History> {
        return _historyPreferences.getHistoryList()
    }

    fun addHistory(newHistory: History) {
        val historyWithId = newHistory.copy(
            id = _historyPreferences.getHistoryId()
        )

        _historyPreferences.setHistoryList(checkListSize(historyWithId))
    }

    fun updateHistory(history: History, updatedHistory: History) : Boolean {
        val historyList = _historyPreferences.getHistoryList().toMutableList()
        val currentHistoryIndex = historyList.indexOf(history)

        if (currentHistoryIndex < 0) {
            return false
        }

        historyList[currentHistoryIndex] = updatedHistory
        _historyPreferences.setHistoryList(historyList)
        return true
    }

    fun removeHistory(removeHistory: History) : MutableList<History> {
        val historyList = _historyPreferences.getHistoryList().filter { it != removeHistory } as MutableList
        _historyPreferences.setHistoryList(historyList)
        return historyList
    }

    fun toggleFavorite(favHistory: History) {
        val historyList = _historyPreferences.getHistoryList()
        historyList.find { it == favHistory }?.isFavorite = !favHistory.isFavorite
        _historyPreferences.setHistoryList(historyList)
    }

    private fun checkListSize(
        newItem: History
    ) : MutableList<History> {
        var historyList = _historyPreferences.getHistoryList().toMutableList()

        if (historyList.size >= _maxItemSize) {
            val removeHistory = historyList.filter { !it.isFavorite }
                .minByOrNull { it.lastConnection }
            if (removeHistory != null) {
                historyList = removeHistory(removeHistory)
                historyList += newItem
            }
        } else {
            historyList += newItem
        }

        return historyList
    }
}