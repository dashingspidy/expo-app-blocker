package expo.modules.appblocker

import android.content.Context

object AppBlockerStore {
  private const val NAME = "expo_app_blocker"
  private const val BLOCKED_PACKAGES = "blocked_packages"
  private const val BLOCKING_ENABLED = "blocking_enabled"

  fun setBlockedPackages(context: Context, packages: Set<String>) {
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
      .edit()
      .putStringSet(BLOCKED_PACKAGES, packages)
      .apply()
  }

  fun getBlockedPackages(context: Context): Set<String> {
    return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
      .getStringSet(BLOCKED_PACKAGES, emptySet())
      ?: emptySet()
  }

  fun setBlockingEnabled(context: Context, enabled: Boolean) {
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
      .edit()
      .putBoolean(BLOCKING_ENABLED, enabled)
      .apply()
  }

  fun isBlockingEnabled(context: Context): Boolean {
    return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
      .getBoolean(BLOCKING_ENABLED, false)
  }
}
