package com.cincinnatiai.marketpricelibrary.model

import com.google.gson.annotations.SerializedName

data class StockResult(
    @SerializedName("symbols_requested")
    val symbols_requested: Int = 0,
    @SerializedName("symbols_returned")
    val symbols_returned: Int = 0,
    val data: List<Stock> = emptyList()
) {
    fun getFirstStock(): Stock? = data.firstOrNull()

    fun getStockWithName(name: String): Stock? = data.firstOrNull { it.name == name }
}