export type PermissionStatus = 'granted' | 'denied' | 'unavailable';

export type AppBlockerPermissionResult = {
  status: PermissionStatus;
};

export type InstalledApp = {
  packageName: string;
  appName: string;
};

export type ExpoAppBlockerModuleEvents = Record<string, never>;
