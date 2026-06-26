package expo.modules.appblocker

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class BlockActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME).orEmpty()
    val layout = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      gravity = Gravity.CENTER
      setPadding(48, 48, 48, 48)
    }
    val title = TextView(this).apply {
      text = "App blocked"
      textSize = 28f
      gravity = Gravity.CENTER
    }
    val message = TextView(this).apply {
      text = if (packageName.isBlank()) "This app is blocked." else "$packageName is blocked."
      textSize = 16f
      gravity = Gravity.CENTER
      setPadding(0, 24, 0, 24)
    }
    val button = Button(this).apply {
      text = "Close"
      setOnClickListener {
        moveTaskToBack(true)
      }
    }

    layout.addView(title)
    layout.addView(message)
    layout.addView(button)
    setContentView(layout)
  }

  companion object {
    const val EXTRA_PACKAGE_NAME = "packageName"
  }
}
