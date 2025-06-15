import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package '@lmapp/react-native-cloudpayments' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const CloudpaymentsSdk = NativeModules.CloudpaymentsSdk
  ? NativeModules.CloudpaymentsSdk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return CloudpaymentsSdk.multiply(a, b);
}

// Экспорт сервисов
export { PaymentService, CardService } from './services';

// Экспорт типов
export type * from './types';

// Экспорт событий
export { eventEmitter } from './events';

// Экспорт хуков
export * from './hooks';

export const init = (publicId: string): Promise<boolean> => {
  return CloudpaymentsSdk.initialize(publicId);
};
