/**
 * @fileoverview Типы для обратной совместимости CloudPayments SDK
 * @description Содержит устаревшие типы данных для поддержки старых версий API
 * @author CloudPayments SDK Team
 * @since 1.0.0
 * @deprecated Этот файл содержит устаревшие типы. Используйте новые типы с префиксами I/E/T
 */

import type { IPaymentData, ICreateIntentPaymentData } from './paymentData';
import type {
  IIntentResponse,
  IIntentApiPayResponse,
  IBankInfo,
  IPaymentFormResponse,
} from './responses';
import type { EPaymentMethodType } from './enums';
import type { TIntentWaitStatus } from './unionTypes';
import type { ICloudpaymentsSdkSpec } from './turboModule';

// ============================================================================
// LEGACY TYPES (для обратной совместимости)
// ============================================================================

/**
 * @deprecated Используйте IPaymentData вместо PaymentData
 * @description Устаревший тип данных платежа. В новых проектах используйте IPaymentData.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const paymentData: PaymentData = { ... };
 *
 * // Новый код (рекомендуется)
 * const paymentData: IPaymentData = { ... };
 * ```
 *
 * @since 1.0.0
 */
export type PaymentData = IPaymentData;

/**
 * @deprecated Используйте ICreateIntentPaymentData вместо СreateIntentPaymentData
 * @description Устаревший тип данных для создания Intent. В новых проектах используйте ICreateIntentPaymentData.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const intentData: СreateIntentPaymentData = { ... };
 *
 * // Новый код (рекомендуется)
 * const intentData: ICreateIntentPaymentData = { ... };
 * ```
 *
 * @since 1.0.0
 */
export type СreateIntentPaymentData = ICreateIntentPaymentData;

/**
 * @deprecated Используйте IIntentResponse вместо IntentResponse
 * @description Устаревший тип ответа Intent. В новых проектах используйте IIntentResponse.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const intent: IntentResponse = await createIntent(data);
 *
 * // Новый код (рекомендуется)
 * const intent: IIntentResponse = await createIntent(data);
 * ```
 *
 * @since 1.0.0
 */
export type IntentResponse = IIntentResponse;

/**
 * @deprecated Используйте IIntentApiPayResponse вместо IntentApiPayResponse
 * @description Устаревший тип ответа API оплаты. В новых проектах используйте IIntentApiPayResponse.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const response: IntentApiPayResponse = await apiPay(data);
 *
 * // Новый код (рекомендуется)
 * const response: IIntentApiPayResponse = await apiPay(data);
 * ```
 *
 * @since 1.0.0
 */
export type IntentApiPayResponse = IIntentApiPayResponse;

/**
 * @deprecated Используйте IBankInfo вместо BankInfo
 * @description Устаревший тип информации о банке. В новых проектах используйте IBankInfo.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const bankInfo: BankInfo = await getBankInfo(cardNumber);
 *
 * // Новый код (рекомендуется)
 * const bankInfo: IBankInfo = await getBankInfo(cardNumber);
 * ```
 *
 * @since 1.0.0
 */
export type BankInfo = IBankInfo;

/**
 * @deprecated Используйте IPaymentFormResponse вместо PaymentFormResponse
 * @description Устаревший тип ответа платежной формы. В новых проектах используйте IPaymentFormResponse.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const result: PaymentFormResponse = await presentPaymentForm(data);
 *
 * // Новый код (рекомендуется)
 * const result: IPaymentFormResponse = await presentPaymentForm(data);
 * ```
 *
 * @since 1.0.0
 */
export type PaymentFormResponse = IPaymentFormResponse;

/**
 * @deprecated Используйте EPaymentMethodType вместо PaymentMethodType
 * @description Устаревший тип способа оплаты. В новых проектах используйте EPaymentMethodType.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const method: PaymentMethodType = 'tpay';
 *
 * // Новый код (рекомендуется)
 * const method: EPaymentMethodType = EPaymentMethodType.TPAY;
 * ```
 *
 * @since 1.0.0
 */
export type PaymentMethodType = EPaymentMethodType;

/**
 * @deprecated Используйте TIntentWaitStatus вместо IntentWaitStatus
 * @description Устаревший тип статуса ожидания Intent. В новых проектах используйте TIntentWaitStatus.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const status: IntentWaitStatus = await getIntentWaitStatus(data, type);
 *
 * // Новый код (рекомендуется)
 * const status: TIntentWaitStatus = await getIntentWaitStatus(data, type);
 * ```
 *
 * @since 1.0.0
 */
export type IntentWaitStatus = TIntentWaitStatus;

/**
 * @deprecated Используйте ICloudpaymentsSdkSpec вместо Spec
 * @description Устаревший тип спецификации TurboModule. В новых проектах используйте ICloudpaymentsSdkSpec.
 *
 * @example Миграция на новый тип
 * ```typescript
 * // Старый код (deprecated)
 * const module: Spec = NativeModules.CloudpaymentsSdk;
 *
 * // Новый код (рекомендуется)
 * const module: ICloudpaymentsSdkSpec = NativeModules.CloudpaymentsSdk;
 * ```
 *
 * @since 1.0.0
 */
export type Spec = ICloudpaymentsSdkSpec;
