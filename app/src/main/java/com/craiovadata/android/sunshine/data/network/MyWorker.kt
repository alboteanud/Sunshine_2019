package com.craiovadata.android.sunshine.data.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.craiovadata.android.sunshine.data.network.NetworkDataSource.Companion.addTestText
import com.craiovadata.android.sunshine.utilities.InjectorUtils
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    var latch: CountDownLatch? = null
    private val MAX_WAIT_TIME_SECONDS = 10L

    override fun doWork(): Result {
        addTestText(applicationContext, "wk")
        latch = CountDownLatch(1)

        return try {
            val networkDataSource = InjectorUtils.provideNetworkDataSource(applicationContext)
//            networkDataSource.fetchWeather()
            networkDataSource.fetchWeatheMainThread {success ->
                if (success){

                } else {
                    addTestText(applicationContext, "wkRetry")
                    Result.retry()
                }
                latch?.countDown()
            }

            latch?.await(MAX_WAIT_TIME_SECONDS, TimeUnit.SECONDS)
            Result.success()
        } catch (e: Error) {
            addTestText(applicationContext, "wkEr:$e")
//            Result.failure()
            Result.retry()
        }

    }

    override fun onStopped() {
        super.onStopped()
    }
}