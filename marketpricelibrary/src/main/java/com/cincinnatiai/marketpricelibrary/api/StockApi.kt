package com.cincinnatiai.marketpricelibrary.api

import com.cincinnatiai.marketpricelibrary.model.HistoricalMarketData
import com.cincinnatiai.marketpricelibrary.model.StockResult
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("api/v1/stock")
    suspend fun getStockPrice(@Query("symbol") symbol: String): StockResult

    @GET("api/v1/history")
    suspend fun getHistoricalData(@Query("symbol") symbol: String,
                                  @Query("date_from") dateFrom: String,
                                  @Query("date_to") dateTo: String): HistoricalMarketData
}