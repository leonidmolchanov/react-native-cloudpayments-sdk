import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

// Интерфейс для React Native Codegen (должен называться именно "Spec")
export interface Spec extends TurboModule {
  // Базовые методы
  initialize(publicId: string): Promise<boolean>;
  getPublicKey(): Promise<string>;
  // Методы валидации карт
  isCardNumberValid(cardNumber: string): Promise<boolean>;
  isExpDateValid(expDate: string): Promise<boolean>;
  isValidCvv(cvv: string, isCvvRequired: boolean): Promise<boolean>;
  cardTypeFromCardNumber(cardNumber: string): Promise<string>;
  // Методы работы с криптограммой
  makeCardCryptogramPacket(
    cardNumber: string,
    expDate: string,
    cvv: string,
    merchantPublicID: string,
    publicKey: string,
    keyVersion: number
  ): Promise<string>;
  // Методы платежей
  createIntent(paymentData: Object): Promise<Object>;
  createIntentApiPay(
    paymentData: Object,
    cardCryptogram: string,
    intentId: string
  ): Promise<Object>;
  getIntentWaitStatus(paymentData: Object, type: string): Promise<number>;
  presentPaymentForm(paymentData: Object): Promise<Object>;
  // Дополнительные методы
  getBankInfo(cardNumber: string): Promise<Object>;
}

const CloudpaymentsSdkModule =
  TurboModuleRegistry.getEnforcing<Spec>('CloudpaymentsSdk');

export default CloudpaymentsSdkModule;
