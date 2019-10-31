package com.cincinnatiai.marketpricelibrary.model

import com.google.gson.annotations.SerializedName

data class Stock(
    val symbol: String = "",
    val name: String = "",
    val currency: String = "",
    val price: String = "",
    val price_open: String = "",
    val day_high: String = "",
    val day_low: String = "",
    @SerializedName("52_week_high")
    val fiftyTwoWeekHigh: String = "",
    @SerializedName("52_week_low")
    val fiftyTwoWeekLow: String = "",
    val day_change: String = "",
    val change_pct: String = "",
    val close_yesterday: String = "",
    val market_cap: String = "",
    val volume: String = "",
    val volume_avg: String = "",
    val shares: String = "",
    val stock_exchange_long: String = "",
    val stock_exchange_short: String = "",
    val timezone: String = "",
    val timezone_name: String = "",
    val gmt_offset: String = "",
    val last_trade_time: String = "",
    val pe: String = "",
    val eps: String = ""
)