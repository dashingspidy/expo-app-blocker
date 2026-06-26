package expo.modules.appblocker

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoAppBlockerModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoAppBlocker")
  }
}
