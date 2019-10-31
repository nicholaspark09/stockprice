package com.cincinnatiai.marketpricelibrary

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.cincinnatiai.marketpricelibrary.api.StockApi
import com.cincinnatiai.marketpricelibrary.di.NetworkModule
import com.cincinnatiai.marketpricelibrary.model.HistoricalMarketData
import com.cincinnatiai.marketpricelibrary.model.Stock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect

interface MarketPriceContract {

    fun fetchStockPrice(symbol: String, callback: (stock: Stock?, error: Throwable?) -> Unit)

    fun fetchContinuousStockPrice(
        symbol: String,
        intervalInMilliseconds: Long? = null,
        callback: (stock: Stock?, error: Throwable?) -> Unit
    )

    fun clear()
}

class MarketPrice @VisibleForTesting constructor(
    @VisibleForTesting val context: Context,
    @VisibleForTesting val apiToken: String,
    @VisibleForTesting val isDebug: Boolean = false,
    @VisibleForTesting val networkModule: NetworkModule = NetworkModule.intialize(
        context.getString(R.string.market_url),
        apiToken,
        isDebug = isDebug
    ),
    @VisibleForTesting val stockApi: StockApi = networkModule.stockApi,
    @VisibleForTesting val backgroundScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    @VisibleForTesting val mainScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : MarketPriceContract {

    val jobs = mutableListOf<Job>()

    override fun fetchStockPrice(
        symbol: String,
        callback: (stock: Stock?, error: Throwable?) -> Unit
    ) {
        backgroundScope.launch {
            try {
                val result = stockApi.getStockPrice(symbol)
                mainScope.launch { callback.invoke(result.getFirstStock(), null) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun fetchContinuousStockPrice(
        symbol: String,
        intervalInMilliseconds: Long?,
        callback: (stock: Stock?, error: Throwable?) -> Unit
    ) {
        try {
            val job = backgroundScope.launch {
                val flow = fetchRealTimeStockPrice(symbol, intervalInMilliseconds)
                flow.collect { stock ->
                    mainScope.launch { callback(stock, null) }
                }
            }
            jobs.add(job)
        } catch (e: Throwable) {
            callback(null, e)
        }
    }

    @VisibleForTesting
    fun fetchRealTimeStockPrice(symbol: String, intervalInMilliseconds: Long? = null): Flow<Stock> =
        callbackFlow {
            while (true) {
                delay(intervalInMilliseconds ?: REALTIME_DELAY_ONE_MINUTE)
                try {
                    val result = stockApi.getStockPrice(symbol)
                    if (result.getFirstStock() != null) {
                        offer(result.getFirstStock()!!)
                        Log.d("MarketPrice", "Got a stock of " + result.getFirstStock().toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    close(e)
                }
            }
        }

    fun fetchHistoricalMarketData(symbol: String,
                                  dateFrom: String,
                                  dateTo: String,
                                  callback: (data: HistoricalMarketData?, error: Throwable?) -> Unit) {
        backgroundScope.launch {
            try {
                val result = stockApi.getHistoricalData(symbol, dateFrom, dateTo)
                callback.invoke(result, null)
            } catch (e: Throwable) {
                e.printStackTrace()
                callback.invoke(null, e)
            }
        }
    }

    override fun clear() {
        jobs.forEach { it.cancel() }
        jobs.clear()
    }

    companion object {

        private const val REALTIME_DELAY_ONE_MINUTE = 60 * 1000L

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: MarketPrice? = null

        @JvmStatic
        fun intialize(
            context: Context,
            apiToken: String,
            isDebug: Boolean = false
        ): MarketPrice {
            if (INSTANCE == null) {
                synchronized(MarketPrice::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MarketPrice(context, apiToken, isDebug = isDebug)
                    }
                }
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun get(): MarketPrice {
            return INSTANCE
                ?: throw IllegalStateException("You must call initialize with the url to use this network")
        }
    }

}