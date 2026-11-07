package com.example.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.db.NuviaDatabase
import com.example.data.model.HabitCompletionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val habitId = intent.getLongExtra("habit_id", -1L)
        val title = intent.getStringExtra("title") ?: "Odat vaqti keldi!"

        if (action == NotificationHelper.ACTION_COMPLETE_HABIT && habitId > 0) {
            // Dismiss notification and mark habit as completed
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(habitId.toInt())

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            CoroutineScope(Dispatchers.IO).launch {
                val db = NuviaDatabase.getDatabase(context)
                db.habitDao().insertCompletion(
                    HabitCompletionEntity(
                        habitId = habitId,
                        date = today,
                        completedAt = System.currentTimeMillis()
                    )
                )
            }
            return
        }

        if (action == NotificationHelper.ACTION_SNOOZE && habitId > 0) {
            // Cancel current notification and schedule for 10 minutes later
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(habitId.toInt())
            return
        }

        // Show reminder notification
        NotificationHelper.createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Complete action button
        val completeIntent = Intent(context, HabitReminderReceiver::class.java).apply {
            this.action = NotificationHelper.ACTION_COMPLETE_HABIT
            putExtra("habit_id", habitId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            (habitId * 100 + 1).toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action button
        val snoozeIntent = Intent(context, HabitReminderReceiver::class.java).apply {
            this.action = NotificationHelper.ACTION_SNOOZE
            putExtra("habit_id", habitId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (habitId * 100 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = "$title — rejangizni bajarishga yana bir qadam! 🌿"

        val builder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("NUVIA: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)
            .addAction(0, "✓ Bajarildi", completePendingIntent)
            .addAction(0, "Keyinroq", snoozePendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(habitId.toInt().coerceAtLeast(100), builder.build())
    }
}
