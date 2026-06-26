import { registerWebModule, NativeModule } from 'expo';

// ExpoAppBlockerModule is not available on the web platform.
class ExpoAppBlockerModule extends NativeModule<{}> {}

export default registerWebModule(ExpoAppBlockerModule, 'ExpoAppBlockerModule');
