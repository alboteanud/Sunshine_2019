package com.craiovadata.android.sunshine.utilities

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.text.format.DateUtils.HOUR_IN_MILLIS
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.craiovadata.android.sunshine.BuildConfig
import com.craiovadata.android.sunshine.CityData.getBackResId
import com.craiovadata.android.sunshine.R
import com.craiovadata.android.sunshine.ui.models.WeatherEntry
import com.craiovadata.android.sunshine.ui.main.MainActivity
import com.craiovadata.android.sunshine.ui.main.MainActivity.Companion.PREF_SYNC_KEY
import com.craiovadata.android.sunshine.utilities.ForegroundListener.Companion.isForeground
import java.util.*


object NotifUtils {

    private fun areNotificationsEnabled(context: Context): Boolean {
        val displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key)
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(displayNotificationsKey, true)
    }

    private fun getLastNotificationTimeInMillis(context: Context): Long {
        val lastNotificationKey = context.getString(R.string.pref_last_notification)
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getLong(lastNotificationKey, 0)
    }

    private fun getEllapsedTimeSinceLastNotification(context: Context): Long {
        val lastNotificationTimeMillis = getLastNotificationTimeInMillis(context)
        return System.currentTimeMillis() - lastNotificationTimeMillis
    }

    private fun saveLastNotificationTime(context: Context) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        val lastNotificationKey = context.getString(R.string.pref_last_notification)
        editor.putLong(lastNotificationKey, System.currentTimeMillis())
        editor.apply()
    }

    private fun notifyUserOfNewWeather(context: Context, entry: WeatherEntry) {
        val chanelId = context.getString(R.string.norif_channel_id)
        val notificationManager =
            context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                chanelId,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = buildNotif(context, entry)
        notificationManager.notify(1, notification)

        saveLastNotificationTime(context)
    }

    private fun buildNotif(context: Context, entry: WeatherEntry): Notification {
        val backgrResourceId = getBackResId(context)
        val largeIcon = BitmapFactory.decodeResource(context.resources, backgrResourceId)

        val smallIconId = SunshineWeatherUtils.getLargeArtResourceIdForIconCode(entry.iconCodeOWM)
        val chanelId = context.getString(R.string.norif_channel_id)

        val titleTxt = SunshineWeatherUtils.formatTemperature(context, entry.temperature)
        val contentTxt = SunshineWeatherUtils.getStringForWeatherCondition(context, entry.weatherId)

        val builder = NotificationCompat.Builder(context, chanelId)
            .setSmallIcon(smallIconId)
            .setLargeIcon(largeIcon)
            .setContentTitle(titleTxt)
            .setContentText(contentTxt)
//            .setColor(context.getColor(R.color.colorPrimary))
            .setAutoCancel(true)
            .setTimeoutAfter(3 * HOUR_IN_MILLIS)
            //            .setOngoing(true)
            .setContentIntent(getPendingIntentMA(context))
        //                .addAction(
        //                        android.R.drawable.ic_media_play,
        //                        getString(R.string.notif_action_play),
        //                        getPendingIntentToService(ACTION_PLAY) )

        return builder.build()
    }

    private fun getPendingIntentMA(context: Context): PendingIntent {
        val intentToMainActivity = Intent(context, MainActivity::class.java)
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addNextIntentWithParentStack(intentToMainActivity)
        return taskStackBuilder
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @JvmStatic
    fun notifyIfNeeded(context: Context, weatherEntry: WeatherEntry) {

        val timeSinceLastNotification = getEllapsedTimeSinceLastNotification(context)
        val oneDayPassedSinceLastNotification = timeSinceLastNotification > 2 * HOUR_IN_MILLIS

        val rightNow = Calendar.getInstance()
        val currentHourIn24Format = rightNow[Calendar.HOUR_OF_DAY]

        val shouldNotify = areNotificationsEnabled(context)
                && oneDayPassedSinceLastNotification
                && !isForeground()
                && currentHourIn24Format > 6
//                && currentHourIn24Format < 20

//        if (BuildConfig.DEBUG) notifyUserOfNewWeather(context, weatherEntry)

        if (shouldNotify) {
            notifyUserOfNewWeather(context, weatherEntry)

            if (BuildConfig.DEBUG) {
//                || context.getString(R.string.app_name) == "Ontario"

                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                var savedTxt = pref.getString(PREF_SYNC_KEY, "sync ")
                savedTxt += "n"
                pref.edit().putString(PREF_SYNC_KEY, savedTxt).apply()
            }
        }


    }


}