package com.mendix.developerapp.history

import java.util.Date

data class History(
    val id: Int? = 0,
    var url: String = "",
    val lastConnection: Date,
    var isFavorite: Boolean = false
)
