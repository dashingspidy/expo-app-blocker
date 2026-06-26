import ExpoModulesCore
import DeviceActivity
import FamilyControls
import ManagedSettings
import SwiftUI

public class ExpoAppBlockerModule: Module {
  private let store = ManagedSettingsStore()
  private let defaultsKey = "ExpoAppBlockerSelection"

  public func definition() -> ModuleDefinition {
    Name("ExpoAppBlocker")

    AsyncFunction("requestPermissions") { () async -> [String: String] in
      do {
        try await AuthorizationCenter.shared.requestAuthorization(for: .individual)
        return ["status": "granted"]
      } catch {
        return ["status": "denied"]
      }
    }

    AsyncFunction("openUsageAccessSettings") {
    }

    AsyncFunction("openAccessibilitySettings") {
    }

    AsyncFunction("selectApps") {
      DispatchQueue.main.async {
        guard let controller = self.appContext?.utilities?.currentViewController() else {
          return
        }

        let model = AppSelectionModel(selection: self.loadSelection()) { selection in
          self.saveSelection(selection)
          self.applyShield(selection)
          controller.dismiss(animated: true)
        }
        let picker = AppSelectionView(model: model)
        let hostingController = UIHostingController(rootView: picker)
        controller.present(hostingController, animated: true)
      }
    }

    AsyncFunction("getInstalledApps") {
      return []
    }

    AsyncFunction("setBlockedApps") { (_: [String]) in
    }

    AsyncFunction("startBlocking") { (_: [String: Any]) in
      self.applyShield(self.loadSelection())
    }

    AsyncFunction("stopBlocking") {
      self.store.shield.applications = nil
      self.store.shield.applicationCategories = nil
    }
  }

  private func saveSelection(_ selection: FamilyActivitySelection) {
    guard let data = try? JSONEncoder().encode(selection) else {
      return
    }
    UserDefaults.standard.set(data, forKey: defaultsKey)
  }

  private func loadSelection() -> FamilyActivitySelection {
    guard
      let data = UserDefaults.standard.data(forKey: defaultsKey),
      let selection = try? JSONDecoder().decode(FamilyActivitySelection.self, from: data)
    else {
      return FamilyActivitySelection()
    }
    return selection
  }

  private func applyShield(_ selection: FamilyActivitySelection) {
    store.shield.applications = selection.applicationTokens
    store.shield.applicationCategories = ShieldSettings.ActivityCategoryPolicy.specific(selection.categoryTokens)
  }
}

private final class AppSelectionModel: ObservableObject {
  @Published var selection: FamilyActivitySelection
  let onDone: (FamilyActivitySelection) -> Void

  init(selection: FamilyActivitySelection, onDone: @escaping (FamilyActivitySelection) -> Void) {
    self.selection = selection
    self.onDone = onDone
  }
}

private struct AppSelectionView: View {
  @ObservedObject var model: AppSelectionModel

  var body: some View {
    NavigationView {
      FamilyActivityPicker(selection: $model.selection)
        .navigationTitle("Select Apps")
        .toolbar {
          ToolbarItem(placement: .confirmationAction) {
            Button("Done") {
              model.onDone(model.selection)
            }
          }
        }
    }
  }
}
