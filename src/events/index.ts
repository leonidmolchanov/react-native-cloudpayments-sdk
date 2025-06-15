import { NativeEventEmitter, NativeModules } from 'react-native';

export const eventEmitter = new NativeEventEmitter(
  NativeModules.CloudpaymentsSdk
);
