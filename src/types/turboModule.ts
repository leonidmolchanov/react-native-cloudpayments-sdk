/**
 * @fileoverview React Native TurboModule спецификация для CloudPayments SDK
 * @description Содержит интерфейс TurboModule для нативного моста между JavaScript и нативным кодом
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import type { TurboModule } from 'react-native';
import type { EPaymentMethodType } from './enums';
import type {
  IIntentResponse,
  IIntentApiPayResponse,
  IBankInfo,
  IPaymentFormResponse,
} from './responses';
import type { TIntentWaitStatus } from './unionTypes';

// ============================================================================
// TURBO MODULE INTERFACE
// ============================================================================

/**
 * Спецификация TurboModule для CloudPayments SDK
 *
 * @description Интерфейс React Native TurboModule, определяющий все нативные методы
 * CloudPayments SDK. Этот интерфейс используется для генерации типобезопасного
 * моста между JavaScript и нативным кодом (iOS/Android).
 *
 * @remarks
 * Этот интерфейс предназначен для внутреннего использования SDK и автоматической
 * генерации нативного кода. Разработчики приложений должны использовать
 * высокоуровневые сервисы PaymentService и CardService вместо прямого обращения
 * к TurboModule.
 *
 * @example Использование через PaymentService (рекомендуется)
 * ```typescript
 * import { PaymentService } from '@lm/react-native-cloudpayments';
 *
 * // Вместо прямого вызова TurboModule
 * // const result = await NativeCloudpaymentsSdk.presentPaymentForm(paymentData);
 *
 * // Используйте высокоуровневый API
 * const result = await PaymentService.presentPaymentForm(paymentData);
 * ```
 *
 * @example Прямое использование TurboModule (не рекомендуется)
 * ```typescript
 * import { NativeModules } from 'react-native';
 * import type { ICloudpaymentsSdkSpec } from '@lm/react-native-cloudpayments';
 *
 * const CloudpaymentsModule = NativeModules.CloudpaymentsSdk as ICloudpaymentsSdkSpec;
 *
 * // Прямой вызов нативного метода
 * const intent = await CloudpaymentsModule.createIntent(paymentData);
 * ```
 *
 * @see {@link https://reactnative.dev/docs/the-new-architecture/pillars-turbomodules} TurboModules документация
 * @internal
 * @since 1.0.0
 */
export interface ICloudpaymentsSdkSpec extends TurboModule {
  /**
   * Создание платежного намерения (Intent)
   *
   * @description Нативный метод для создания Intent в системе CloudPayments.
   * Принимает объект с данными платежа и возвращает информацию о созданном Intent.
   *
   * @param paymentData - Объект с данными платежа (будет сериализован из ICreateIntentPaymentData)
   * @returns Promise с информацией о созданном Intent
   *
   * @internal
   * @platform ios, android
   */
  createIntent(paymentData: Object): Promise<IIntentResponse>;

  /**
   * Оплата через Intent с использованием криптограммы карты
   *
   * @description Нативный метод для выполнения оплаты через существующий Intent
   * с использованием зашифрованных данных карты (криптограммы).
   *
   * @param paymentData - Объект с данными платежа
   * @param cardCryptogram - Зашифрованные данные карты
   * @param intentId - Идентификатор существующего Intent
   * @returns Promise с результатом API оплаты
   *
   * @internal
   * @platform ios, android
   */
  createIntentApiPay(
    paymentData: Object,
    cardCryptogram: string,
    intentId: string
  ): Promise<IIntentApiPayResponse>;

  /**
   * Ожидание статуса Intent для альтернативных способов оплаты
   *
   * @description Нативный метод для запуска альтернативного способа оплаты
   * (TPay, СБП, SberPay) и ожидания завершения операции.
   *
   * @param paymentData - Объект с данными платежа
   * @param type - Тип способа оплаты
   * @returns Promise с HTTP статус кодом результата
   *
   * @internal
   * @platform ios, android
   */
  getIntentWaitStatus(
    paymentData: Object,
    type: EPaymentMethodType
  ): Promise<TIntentWaitStatus>;

  /**
   * Инициализация CloudPayments SDK
   *
   * @description Нативный метод для инициализации SDK с Public ID мерчанта.
   * Должен быть вызван перед использованием других методов.
   *
   * @param publicId - Публичный идентификатор мерчанта
   * @returns Promise с результатом инициализации
   *
   * @internal
   * @platform ios, android
   */
  initialize(publicId: string): Promise<boolean>;

  /**
   * Получение публичного ключа для шифрования
   *
   * @description Нативный метод для получения актуального публичного ключа
   * CloudPayments для шифрования данных карты.
   *
   * @returns Promise со строкой публичного ключа в формате PEM
   *
   * @internal
   * @platform ios, android
   */
  getPublicKey(): Promise<string>;

  /**
   * Валидация номера банковской карты
   *
   * @description Нативный метод для проверки корректности номера карты
   * с использованием алгоритма Луна.
   *
   * @param cardNumber - Номер банковской карты
   * @returns Promise с результатом валидации
   *
   * @internal
   * @platform ios, android
   */
  isCardNumberValid(cardNumber: string): Promise<boolean>;

  /**
   * Валидация срока действия карты
   *
   * @description Нативный метод для проверки корректности срока действия карты.
   *
   * @param expDate - Срок действия в формате MM/YY или MM/YYYY
   * @returns Promise с результатом валидации
   *
   * @internal
   * @platform ios, android
   */
  isExpDateValid(expDate: string): Promise<boolean>;

  /**
   * Валидация CVV кода карты
   *
   * @description Нативный метод для проверки корректности CVV кода.
   *
   * @param cvv - CVV код карты
   * @param isCvvRequired - Обязательность CVV для данного типа карты
   * @returns Promise с результатом валидации
   *
   * @internal
   * @platform ios, android
   */
  isValidCvv(cvv: string, isCvvRequired: boolean): Promise<boolean>;

  /**
   * Определение типа карты по номеру
   *
   * @description Нативный метод для определения платежной системы
   * (Visa, MasterCard, МИР и т.д.) по номеру карты.
   *
   * @param cardNumber - Номер банковской карты
   * @returns Promise с названием платежной системы
   *
   * @internal
   * @platform ios, android
   */
  cardTypeFromCardNumber(cardNumber: string): Promise<string>;

  /**
   * Создание криптограммы карты
   *
   * @description Нативный метод для создания зашифрованной криптограммы
   * из данных карты с использованием публичного ключа CloudPayments.
   *
   * @param cardNumber - Номер карты
   * @param expDate - Срок действия карты
   * @param cvv - CVV код карты
   * @param merchantPublicID - Public ID мерчанта
   * @param publicKey - Публичный ключ для шифрования
   * @param keyVersion - Версия публичного ключа
   * @returns Promise с криптограммой карты
   *
   * @internal
   * @platform ios, android
   */
  makeCardCryptogramPacket(
    cardNumber: string,
    expDate: string,
    cvv: string,
    merchantPublicID: string,
    publicKey: string,
    keyVersion: number
  ): Promise<string>;

  /**
   * Получение информации о банке-эмитенте
   *
   * @description Нативный метод для определения банка-эмитента карты
   * по BIN (первые 6-8 цифр номера карты).
   *
   * @param cardNumber - Номер банковской карты
   * @returns Promise с информацией о банке
   *
   * @internal
   * @platform ios, android
   */
  getBankInfo(cardNumber: string): Promise<IBankInfo>;

  /**
   * Отображение стандартной платежной формы
   *
   * @description Нативный метод для показа встроенной платежной формы
   * CloudPayments с поддержкой всех способов оплаты.
   *
   * @param paymentData - Объект с данными платежа и конфигурацией формы
   * @returns Promise с результатом платежа
   *
   * @internal
   * @platform ios, android
   */
  presentPaymentForm(paymentData: Object): Promise<IPaymentFormResponse>;
}
