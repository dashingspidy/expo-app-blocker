export type Weekday = 'mon' | 'tue' | 'wed' | 'thu' | 'fri' | 'sat' | 'sun';

export type PermissionStatus = 'granted' | 'denied' | 'unavailable';

export type AppBlockerPermissionResult = {
  status: PermissionStatus;
};

export type InstalledApp = {
  packageName: string;
  appName: string;
};

export type BlockingSchedule = {
  days: Weekday[];
  startTime: string;
  endTime: string;
};

export type StartBlockingOptions = {
  schedules: BlockingSchedule[];
};

export type ExpoAppBlockerModuleEvents = Record<string, never>;
