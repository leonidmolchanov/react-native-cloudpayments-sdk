/**
 * @fileoverview Union типы и mapped типы для CloudPayments SDK
 * @description Содержит сложные типы данных, объединения и маппинги для событий и ответов
 * @author Leonid Molchanov
 * @since 1.0.0
 */

import type { EPaymentFormEventName } from './enums';
import type { IBaseEventData } from './base';
import type {
  IPaymentFormUIEvent,
  IPaymentFormTransactionEvent,
  IPaymentFormEvent,
  IPaymentEvent,
  ICardEvent,
  IThreeDSEvent,
} from './events';

// ============================================================================
// UNION TYPES AND MAPPED TYPES
// ============================================================================

/**
 * Объединенный тип данных событий платежной формы
 *
 * @description Union тип, объединяющий все возможные типы событий платежной формы.
 * Используется для типизации обработчиков событий, которые могут получать
 * различные типы данных в зависимости от типа события.
 *
 * @example Обработка разных типов событий
 * ```typescript
 * import { eventEmitter } from '@lmapp/react-native-cloudpayments';
 *
 * const handlePaymentFormEvent = (eventData: TPaymentFormEventData) => {
 *   if ('action' in eventData) {
 *     // Это UI событие или событие транзакции
 *     if (eventData.action === EPaymentFormAction.TRANSACTION) {
 *       // Это событие транзакции
 *       const transactionEvent = eventData as IPaymentFormTransactionEvent;
 *       console.log('Статус транзакции:', transactionEvent.status);
 *     } else {
 *       // Это UI событие
 *       const uiEvent = eventData as IPaymentFormUIEvent;
 *       console.log('UI действие:', uiEvent.action);
 *     }
 *   }
 * };
 * ```
 *
 * @since 1.0.0
 */
export type TPaymentFormEventData =
  | IPaymentFormUIEvent
  | IPaymentFormTransactionEvent;

/**
 * Маппинг названий событий к их типам данных
 *
 * @description Внутренний тип для создания строгой типизации событий.
 * Каждому событию из EPaymentFormEventName соответствует определенный тип данных.
 * Используется для создания типобезопасного generic типа TCloudPaymentsEventData.
 *
 * @internal
 * @since 1.0.0
 */
type EventDataMap = {
  /** Общее событие платежной формы */
  [EPaymentFormEventName.PAYMENT_FORM]: IPaymentFormEvent;
  /** События платежей */
  [EPaymentFormEventName.PAYMENT]: IPaymentEvent;
  /** События карт */
  [EPaymentFormEventName.CARD]: ICardEvent;
  /** События 3DS */
  [EPaymentFormEventName.THREE_DS]: IThreeDSEvent;
};

/**
 * Типобезопасный generic тип для событий CloudPayments
 *
 * @description Элегантный generic тип, который обеспечивает строгую типизацию
 * событий SDK. Автоматически связывает тип события с соответствующими данными,
 * предотвращая ошибки типизации и улучшая developer experience.
 *
 * @template K - Тип события из перечисления EPaymentFormEventName
 *
 * @example Типизированная подписка на конкретное событие
 * ```typescript
 * import { eventEmitter, EPaymentFormEventName } from '@lmapp/react-native-cloudpayments';
 *
 * // TypeScript автоматически выведет правильный тип данных
 * eventEmitter.addListener(
 *   EPaymentFormEventName.PAYMENT_SUCCESS,
 *   (eventData: TCloudPaymentsEventData<typeof EPaymentFormEventName.PAYMENT_SUCCESS>) => {
 *     // eventData.data имеет тип IPaymentSuccessEventData
 *     console.log('ID транзакции:', eventData.data.transactionId);
 *     console.log('Сообщение:', eventData.data.message);
 *   }
 * );
 * ```
 *
 * @example Универсальный обработчик с проверкой типа
 * ```typescript
 * const handleAnyEvent = (eventData: TCloudPaymentsEventData) => {
 *   switch (eventData.eventName) {
 *     case EPaymentFormEventName.PAYMENT_SUCCESS:
 *       // TypeScript знает, что data имеет тип IPaymentSuccessEventData
 *       console.log('Успех! ID:', eventData.data.transactionId);
 *       break;
 *     case EPaymentFormEventName.PAYMENT_FAILED:
 *       // TypeScript знает, что data имеет тип IPaymentFailedEventData
 *       console.log('Ошибка:', eventData.data.message);
 *       break;
 *     case EPaymentFormEventName.PAYMENT_PROGRESS:
 *       // TypeScript знает, что data имеет тип IPaymentProgressEventData
 *       console.log('Прогресс:', eventData.data.stage);
 *       break;
 *     default:
 *       console.log('Другое событие:', eventData.eventName);
 *   }
 * };
 * ```
 *
 * @example Создание типизированного EventEmitter
 * ```typescript
 * import { EventEmitter } from 'react-native';
 *
 * class TypedPaymentEventEmitter extends EventEmitter {
 *   emit<K extends EPaymentFormEventName>(
 *     eventName: K,
 *     eventData: TCloudPaymentsEventData<K>
 *   ): boolean {
 *     return super.emit(eventName, eventData);
 *   }
 *
 *   addListener<K extends EPaymentFormEventName>(
 *     eventName: K,
 *     listener: (eventData: TCloudPaymentsEventData<K>) => void
 *   ): this {
 *     return super.addListener(eventName, listener);
 *   }
 * }
 * ```
 *
 * @since 1.0.0
 */
export type TCloudPaymentsEventData<
  K extends EPaymentFormEventName = EPaymentFormEventName,
> = IBaseEventData<EventDataMap[K]> & { eventName: K };

/**
 * Тип для статуса ожидания Intent
 *
 * @description Представляет HTTP статус код ответа при ожидании завершения
 * платежного намерения (Intent). Используется для отслеживания состояния
 * асинхронных операций оплаты.
 *
 * @example Обработка статуса Intent
 * ```typescript
 * import { PaymentService, EPaymentMethodType } from '@lmapp/react-native-cloudpayments';
 *
 * const waitForIntentCompletion = async () => {
 *   try {
 *     const status: TIntentWaitStatus = await PaymentService.getIntentWaitStatus(
 *       paymentData,
 *       EPaymentMethodType.TPAY
 *     );
 *
 *     switch (status) {
 *       case 200:
 *         console.log('Платеж успешно завершен');
 *         break;
 *       case 400:
 *         console.log('Ошибка в данных запроса');
 *         break;
 *       case 402:
 *         console.log('Платеж отклонен');
 *         break;
 *       case 408:
 *         console.log('Превышено время ожидания');
 *         break;
 *       case 500:
 *         console.log('Ошибка сервера');
 *         break;
 *       default:
 *         console.log('Неизвестный статус:', status);
 *     }
 *   } catch (error) {
 *     console.log('Ошибка при ожидании статуса:', error);
 *   }
 * };
 * ```
 *
 * @example Polling статуса с интервалом
 * ```typescript
 * const pollIntentStatus = async (intentId: string): Promise<TIntentWaitStatus> => {
 *   const maxAttempts = 30; // 30 попыток
 *   const interval = 2000; // 2 секунды
 *
 *   for (let attempt = 0; attempt < maxAttempts; attempt++) {
 *     const status: TIntentWaitStatus = await checkIntentStatus(intentId);
 *
 *     // Финальные статусы
 *     if (status === 200 || status === 402 || status >= 500) {
 *       return status;
 *     }
 *
 *     // Ждем перед следующей попыткой
 *     await new Promise(resolve => setTimeout(resolve, interval));
 *   }
 *
 *   return 408; // Timeout
 * };
 * ```
 *
 * @see {@link https://developer.mozilla.org/en-US/docs/Web/HTTP/Status} HTTP статус коды
 * @since 1.0.0
 */
export type TIntentWaitStatus = number;
