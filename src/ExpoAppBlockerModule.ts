import { NativeModule, requireNativeModule } from 'expo';

import {
  AppBlockerPermissionResult,
  ExpoAppBlockerModuleEvents,
  InstalledApp,
  StartBlockingOptions,
} from './ExpoAppBlocker.types';

declare class ExpoAppBlockerModule extends NativeModule<ExpoAppBlockerModuleEvents> {
  requestPermissions(): Promise<AppBlockerPermissionResult>;
  openUsageAccessSettings(): Promise<void>;
  openAccessibilitySettings(): Promise<void>;
  selectApps(): Promise<void>;
  getInstalledApps(): Promise<InstalledApp[]>;
  setBlockedApps(packageNames: string[]): Promise<void>;
  startBlocking(options: StartBlockingOptions): Promise<void>;
  stopBlocking(): Promise<void>;
}

export default requireNativeModule<ExpoAppBlockerModule>('ExpoAppBlocker');
