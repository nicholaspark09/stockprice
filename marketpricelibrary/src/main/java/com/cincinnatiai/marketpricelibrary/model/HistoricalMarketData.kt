package com.cincinnatiai.marketpricelibrary.model

data class HistoricalMarketData(
    val name: String = "",
    val history: Map<String, SingleDayHistory> = mutableMapOf()
)