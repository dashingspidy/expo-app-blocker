# expo-app-blocker

Native app blocking for Expo and React Native apps.

`expo-app-blocker` provides a shared TypeScript API with native iOS Screen Time support and Android Usage Access / Accessibility support.

## Features

- iOS app selection with Apple's native `FamilyActivityPicker`
- iOS app/category blocking with `ManagedSettings`
- Android installed app listing with `PackageManager`
- Android foreground app detection with `AccessibilityService` and `UsageStatsManager`
- Android native block screen
- Expo config plugin for required native setup

## Install

```bash
npm install expo-app-blocker
npx expo prebuild
```

## Expo config

```json
{
  "expo": {
    "plugins": [
      [
        "expo-app-blocker",
        {
          "androidAccessibilityServiceLabel": "App Blocker",
          "iosFamilyControlsUsageDescription": "Allow app blocking and screen-time controls."
        }
      ]
    ]
  }
}
```

Then rebuild the native app:

```bash
npx expo run:ios
npx expo run:android
```

This package does not work in Expo Go because it contains native code.

## iOS usage

iOS uses Apple's Screen Time APIs. Apps must be selected through Apple's native picker; iOS does not allow listing all installed apps directly.

```ts
import ExpoAppBlocker from "expo-app-blocker";

const permission = await ExpoAppBlocker.requestPermissions();

if (permission.status === "granted") {
  await ExpoAppBlocker.selectApps();
  await ExpoAppBlocker.startBlocking();
}
```

Stop blocking:

```ts
await ExpoAppBlocker.stopBlocking();
```

### iOS requirements

- Real iOS device
- Family Controls entitlement enabled for your Apple developer account/app
- Development build or production build
- iOS 16.4+

## Android usage

Android can list installed apps. Your app should render this list in your own UI and let the user choose which apps to block.

```ts
import ExpoAppBlocker from "expo-app-blocker";

const apps = await ExpoAppBlocker.getInstalledApps();
```

Example app item:

```ts
type InstalledApp = {
  packageName: string;
  appName: string;
};
```

After the user selects apps:

```ts
await ExpoAppBlocker.setBlockedApps([
  "com.instagram.android",
  "com.zhiliaoapp.musically"
]);

await ExpoAppBlocker.startBlocking();
```

Stop blocking:

```ts
await ExpoAppBlocker.stopBlocking();
```

### Android permissions flow

Android does not allow apps to silently grant Usage Access or Accessibility permissions. You must send the user to system settings and the user must enable both.

```ts
await ExpoAppBlocker.openUsageAccessSettings();
await ExpoAppBlocker.openAccessibilitySettings();
```

Required user actions:

- Enable Usage Access for your app
- Enable the App Blocker accessibility service

These permissions are required for foreground app detection and blocking.

## API

### `requestPermissions()`

```ts
const result = await ExpoAppBlocker.requestPermissions();
```

Returns:

```ts
type AppBlockerPermissionResult = {
  status: "granted" | "denied" | "unavailable";
};
```

On iOS, this requests Family Controls authorization.

On Android, this currently checks Usage Access.

### `selectApps()`

```ts
await ExpoAppBlocker.selectApps();
```

Opens Apple's native app/category picker on iOS.

No-op on Android. Use `getInstalledApps()` and your own UI on Android.

### `getInstalledApps()`

```ts
const apps = await ExpoAppBlocker.getInstalledApps();
```

Returns installed user apps on Android.

Returns an empty array on iOS because iOS does not expose installed apps.

### `setBlockedApps(packageNames)`

```ts
await ExpoAppBlocker.setBlockedApps(["com.instagram.android"]);
```

Sets blocked Android package names.

No-op on iOS because iOS stores selected app tokens from `selectApps()`.

### `startBlocking()`

```ts
await ExpoAppBlocker.startBlocking();
```

Starts blocking selected apps.

### `stopBlocking()`

```ts
await ExpoAppBlocker.stopBlocking();
```

Stops blocking selected apps.

### `openUsageAccessSettings()`

```ts
await ExpoAppBlocker.openUsageAccessSettings();
```

Opens Android Usage Access settings.

### `openAccessibilitySettings()`

```ts
await ExpoAppBlocker.openAccessibilitySettings();
```

Opens Android Accessibility settings.

## Platform notes

### iOS

Uses:

- `FamilyControls`
- `ManagedSettings`
- `FamilyActivityPicker`

iOS blocking works through Screen Time shields. Apple controls the selection UI and permission flow.

### Android

Uses:

- `PackageManager`
- `UsageStatsManager`
- `AccessibilityService`
- `ForegroundService`
- Native block activity

Android reliability can vary by device manufacturer, battery optimization settings, and Accessibility restrictions.

## Development

```bash
npm install
npm run build
npm run lint
```

Android native builds require JDK 17+.

## License

MIT
