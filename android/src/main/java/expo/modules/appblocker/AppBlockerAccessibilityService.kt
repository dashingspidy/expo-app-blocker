package expo.modules.appblocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppBlockerAccessibilityService : AccessibilityService() {
  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
      return
    }

    val packageName = event.packageName?.toString() ?: return
    if (!AppBlockerStore.isBlockingEnabled(this)) {
      return
    }
    if (!AppBlockerStore.getBlockedPackages(this).contains(packageName)) {
      return
    }

    startActivity(
      Intent(this, BlockActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(BlockActivity.EXTRA_PACKAGE_NAME, packageName)
    )
  }

  override fun onInterrupt() {}
}
