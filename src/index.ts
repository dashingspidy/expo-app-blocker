// Reexport the native module. On web, it will be resolved to ExpoAppBlockerModule.web.ts
// and on native platforms to ExpoAppBlockerModule.ts
export { default } from './ExpoAppBlockerModule';
export * from './ExpoAppBlocker.types';
