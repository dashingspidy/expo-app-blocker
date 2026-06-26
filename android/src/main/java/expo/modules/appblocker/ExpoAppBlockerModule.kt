package expo.modules.appblocker

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Process
import android.provider.Settings
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoAppBlockerModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoAppBlocker")

    AsyncFunction("requestPermissions") {
      mapOf("status" to if (hasUsageAccess()) "granted" else "denied")
    }

    AsyncFunction("openUsageAccessSettings") {
      val context = appContext.reactContext ?: return@AsyncFunction
      context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    AsyncFunction("openAccessibilitySettings") {
      val context = appContext.reactContext ?: return@AsyncFunction
      context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    AsyncFunction("selectApps") {
      Unit
    }

    AsyncFunction("getInstalledApps") {
      val packageManager = appContext.reactContext?.packageManager
        ?: return@AsyncFunction emptyList<Map<String, String>>()

      packageManager.getInstalledApplications(0)
        .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        .map {
          mapOf(
            "packageName" to it.packageName,
            "appName" to packageManager.getApplicationLabel(it).toString()
          )
        }
        .sortedBy { it["appName"]?.lowercase() }
    }

    AsyncFunction("setBlockedApps") { packageNames: List<String> ->
      val context = appContext.reactContext ?: return@AsyncFunction
      AppBlockerStore.setBlockedPackages(context, packageNames.toSet())
    }

    AsyncFunction("startBlocking") {
      val context = appContext.reactContext ?: return@AsyncFunction
      AppBlockerStore.setBlockingEnabled(context, true)
      val intent = Intent(context, AppBlockerForegroundService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    AsyncFunction("stopBlocking") {
      val context = appContext.reactContext ?: return@AsyncFunction
      AppBlockerStore.setBlockingEnabled(context, false)
      context.stopService(Intent(context, AppBlockerForegroundService::class.java))
    }
  }

  private fun hasUsageAccess(): Boolean {
    val context = appContext.reactContext ?: return false
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
      AppOpsManager.OPSTR_GET_USAGE_STATS,
      Process.myUid(),
      context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
  }
}
