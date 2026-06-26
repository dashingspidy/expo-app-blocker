import { NativeModule, registerWebModule } from 'expo';

import {
  AppBlockerPermissionResult,
  ExpoAppBlockerModuleEvents,
  InstalledApp,
} from './ExpoAppBlocker.types';

class ExpoAppBlockerModule extends NativeModule<ExpoAppBlockerModuleEvents> {
  async requestPermissions(): Promise<AppBlockerPermissionResult> {
    return { status: 'unavailable' };
  }

  async openUsageAccessSettings(): Promise<void> {}

  async openAccessibilitySettings(): Promise<void> {}

  async selectApps(): Promise<void> {}

  async getInstalledApps(): Promise<InstalledApp[]> {
    return [];
  }

  async setBlockedApps(_packageNames: string[]): Promise<void> {}

  async startBlocking(): Promise<void> {}

  async stopBlocking(): Promise<void> {}
}

export default registerWebModule(ExpoAppBlockerModule, 'ExpoAppBlocker');
