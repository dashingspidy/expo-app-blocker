const {
  AndroidConfig,
  createRunOncePlugin,
  withAndroidManifest,
  withEntitlementsPlist,
  withInfoPlist,
  withStringsXml,
} = require('@expo/config-plugins');

const pkg = require('./package.json');

const withExpoAppBlocker = (config, props = {}) => {
  config = withInfoPlist(config, (mod) => {
    mod.modResults.NSFamilyControlsUsageDescription =
      props.iosFamilyControlsUsageDescription ||
      'Allow app blocking and screen-time schedules.';
    return mod;
  });

  config = withEntitlementsPlist(config, (mod) => {
    mod.modResults['com.apple.developer.family-controls'] = true;
    return mod;
  });

  config = withStringsXml(config, (mod) => {
    AndroidConfig.Strings.setStringItem(
      [
        {
          $: { name: 'expo_app_blocker_accessibility_description' },
          _: props.androidAccessibilityServiceLabel || 'Detects selected apps so they can be blocked.',
        },
      ],
      mod.modResults
    );
    return mod;
  });

  config = withAndroidManifest(config, (mod) => {
    const manifest = mod.modResults.manifest;
    manifest['uses-permission'] = manifest['uses-permission'] || [];

    addPermission(manifest, 'android.permission.FOREGROUND_SERVICE');
    addPermission(manifest, 'android.permission.PACKAGE_USAGE_STATS');
    addPermission(manifest, 'android.permission.QUERY_ALL_PACKAGES');

    return mod;
  });

  return config;
};

function addPermission(manifest, name) {
  const permissions = manifest['uses-permission'];
  if (!permissions.some((permission) => permission.$['android:name'] === name)) {
    permissions.push({ $: { 'android:name': name } });
  }
}

module.exports = createRunOncePlugin(withExpoAppBlocker, pkg.name, pkg.version);
