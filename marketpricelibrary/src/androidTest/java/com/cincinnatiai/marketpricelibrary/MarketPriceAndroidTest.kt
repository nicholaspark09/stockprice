package com.cincinnatiai.marketpricelibrary

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.lang.Thread.sleep

class MarketPriceAndroidTest {

    private lateinit var marketPrice: MarketPrice

    @Before
    fun setup() {
        marketPrice = MarketPrice.intialize(
            InstrumentationRegistry.getInstrumentation().targetContext,
            apiToken = BuildConfig.API_TOKEN,
            isDebug = true
        )
    }

    @Test
    fun fetchPrice_testPriceIsReturned() {
        // When
        marketPrice.fetchStockPrice("EWY") { stock, error ->
            assertNotNull(stock)
            Log.d("MarketPrice", "the result was " + stock.toString())
        }
        sleep(600)
    }

    @Test
    fun fetchHistoricalData_testDataReturnedProperly() {
        marketPrice.fetchHistoricalMarketData("TSLA", "2019-10-28", "2019-10-30") { data, error ->
            Log.d("AndroidTest", "Data is: ${data.toString()}")
            assertNotNull(data)
        }
        sleep(600)
    }

    @Test
    fun fetchPriceContinuously_testPriceIsReturned() {
        runBlocking {
            marketPrice.fetchRealTimeStockPrice("TSLA").collect { it ->
                assertNotNull(it)
            }
        }
    }

    @Test
    fun fetchPriceContinuouslyInCallback_testCallback() {
        marketPrice.fetchContinuousStockPrice(
            "MSFT",
            intervalInMilliseconds = 300
        ) { stock, error ->
            assertNotNull(stock)
        }
        sleep(600)
    }
}