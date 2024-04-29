package com.mendix.developerapp.history

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.mendix.developerapp.util.HistoryManager
import com.mendix.mendixnative.config.AppPreferences
import kotlinx.coroutines.flow.*
import kotlin.Comparator

data class HistoryState(
    val historyList: MutableList<History> = mutableStateListOf()
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableState = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = mutableState.asStateFlow()

    private var historyManager: HistoryManager
    private var preferences: AppPreferences

    lateinit var historyItemOnClick : () -> Unit

    init {
        historyManager = HistoryManager(application)
        preferences = AppPreferences(application)
        refreshHistoryList()
    }

    fun removeHistory(history: History) {
        historyManager.removeHistory(history)
        refreshHistoryList()
    }

    fun favoriteButtonOnClick(history: History) {
        historyManager.toggleFavorite(history)
        val newHistoryList = mutableState.value.historyList.map {
            if ( it == history ) history.copy(isFavorite = !history.isFavorite) else it
        }.toMutableList()
        setHistoryList(newHistoryList)
    }

    fun historyListItemOnClick(history: History) {
        preferences.appUrl = history.url
        historyItemOnClick.invoke()
    }

    private fun refreshHistoryList(comparator: Comparator<History> = compareBy({ it.isFavorite }, {it.lastConnection})) {
        val historyList = historyManager.getHistoryList().sortedWith(
            comparator
        ).reversed().toMutableList()

        setHistoryList(historyList)
    }

    private fun setHistoryList(historyList: MutableList<History>) {
        mutableState.update { currentState ->
            currentState.copy(
                historyList = historyList
            )
        }
    }
}