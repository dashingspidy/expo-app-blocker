package expo.modules.appblocker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class AppBlockerForegroundService : Service() {
  private val handler = Handler(Looper.getMainLooper())
  private val pollRunnable = object : Runnable {
    override fun run() {
      blockForegroundPackage()
      handler.postDelayed(this, 1500)
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_ID, "App Blocker", NotificationManager.IMPORTANCE_LOW)
      getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Notification.Builder(this, CHANNEL_ID)
    } else {
      @Suppress("DEPRECATION")
      Notification.Builder(this)
    }
      .setContentTitle("App blocker active")
      .setContentText("Blocking selected apps")
      .setSmallIcon(android.R.drawable.ic_lock_lock)
      .build()

    startForeground(1, notification)
    handler.removeCallbacks(pollRunnable)
    handler.post(pollRunnable)
    return START_STICKY
  }

  override fun onDestroy() {
    handler.removeCallbacks(pollRunnable)
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun blockForegroundPackage() {
    if (!AppBlockerStore.isBlockingEnabled(this)) {
      return
    }

    val packageName = getForegroundPackage() ?: return
    if (!AppBlockerStore.getBlockedPackages(this).contains(packageName)) {
      return
    }

    startActivity(
      Intent(this, BlockActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(BlockActivity.EXTRA_PACKAGE_NAME, packageName)
    )
  }

  private fun getForegroundPackage(): String? {
    val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val end = System.currentTimeMillis()
    val begin = end - 10_000
    val events = usageStatsManager.queryEvents(begin, end)
    val event = UsageEvents.Event()
    var packageName: String? = null

    while (events.hasNextEvent()) {
      events.getNextEvent(event)
      if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
        packageName = event.packageName
      }
    }

    return packageName
  }

  companion object {
    private const val CHANNEL_ID = "expo_app_blocker"
  }
}
