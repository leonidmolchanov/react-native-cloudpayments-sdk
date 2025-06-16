/**
 * @fileoverview Базовые интерфейсы для CloudPayments SDK
 * @description Содержит базовые типы данных, используемые во всем SDK
 * @author Leonid Molchanov
 * @since 1.0.0
 */

import type { EPaymentFormEventName, EPaymentFormErrorCode } from './enums';

// ============================================================================
// BASE INTERFACES
// ============================================================================

/**
 * Базовый интерфейс для всех ответов CloudPayments API
 *
 * @description Стандартная структура ответа, которую возвращают все методы SDK.
 * Содержит информацию об успешности операции и сообщение для пользователя.
 *
 * @example Обработка базового ответа
 * ```typescript
 * import { PaymentService } from '@lmapp/react-native-cloudpayments';
 *
 * const result = await PaymentService.presentPaymentForm(paymentData);
 * if (result.success) {
 *   console.log('Операция успешна:', result.message);
 * } else {
 *   console.log('Ошибка:', result.message);
 * }
 * ```
 *
 * @since 1.0.0
 */
export interface IBaseResponse {
  /**
   * Флаг успешности операции
   * @description true - операция выполнена успешно, false - произошла ошибка
   */
  success: boolean;

  /**
   * Сообщение о результате операции
   * @description Текстовое описание результата операции.
   * В случае успеха содержит подтверждение, в случае ошибки - описание проблемы.
   */
  message: string;
}

/**
 * Расширенный базовый ответ с информацией о транзакции
 *
 * @description Наследует базовый ответ и добавляет идентификатор транзакции.
 * Используется для операций, которые создают или изменяют транзакции.
 *
 * @example Получение ID транзакции
 * ```typescript
 * const result = await PaymentService.presentPaymentForm(paymentData);
 * if (result.success && result.transactionId) {
 *   console.log('Транзакция создана с ID:', result.transactionId);
 *   // Сохраняем ID для дальнейшего отслеживания
 *   await saveTransactionId(result.transactionId);
 * }
 * ```
 *
 * @since 1.0.0
 */
export interface IBaseResponseWithTransaction extends IBaseResponse {
  /**
   * Уникальный идентификатор транзакции
   * @description Числовой ID транзакции в системе CloudPayments.
   * Используется для отслеживания статуса платежа и в отчетах.
   * Может отсутствовать если транзакция не была создана.
   */
  transactionId?: number;
}

/**
 * Базовый интерфейс для всех ошибок CloudPayments SDK
 *
 * @description Стандартная структура ошибки, которая содержит код ошибки
 * и человекочитаемое сообщение. Используется для унифицированной
 * обработки ошибок во всем SDK.
 *
 * @example Обработка ошибок с кодами
 * ```typescript
 * import { EPaymentFormErrorCode } from '@lmapp/react-native-cloudpayments';
 *
 * try {
 *   await PaymentService.presentPaymentForm(paymentData);
 * } catch (error: IBaseError) {
 *   switch (error.code) {
 *     case EPaymentFormErrorCode.CONFIGURATION_ERROR:
 *       showAlert('Ошибка настройки', 'Проверьте конфигурацию SDK');
 *       break;
 *     case EPaymentFormErrorCode.PAYMENT_FAILED:
 *       showAlert('Платеж отклонен', error.message);
 *       break;
 *     default:
 *       showAlert('Ошибка', error.message);
 *   }
 * }
 * ```
 *
 * @since 1.0.0
 */
export interface IBaseError {
  /**
   * Код ошибки из перечисления EPaymentFormErrorCode
   * @description Стандартизированный код ошибки, который позволяет
   * программно обрабатывать различные типы ошибок.
   */
  code: EPaymentFormErrorCode;

  /**
   * Человекочитаемое описание ошибки
   * @description Подробное сообщение об ошибке на русском языке.
   * Может быть показано пользователю или использовано для логирования.
   */
  message: string;
}

/**
 * Базовый интерфейс для данных событий платежной формы
 *
 * @description Стандартная структура данных события, которая содержит
 * название события и связанные с ним данные. Используется системой
 * событий SDK для уведомления о различных этапах платежного процесса.
 *
 * @template T - Тип данных события (по умолчанию Record<string, any>)
 *
 * @example Подписка на события с типизированными данными
 * ```typescript
 * import { eventEmitter, EPaymentFormEventName } from '@lmapp/react-native-cloudpayments';
 *
 * // Подписка на событие успешного платежа
 * eventEmitter.addListener(EPaymentFormEventName.PAYMENT_SUCCESS, (eventData) => {
 *   console.log('Событие:', eventData.eventName);
 *   console.log('ID транзакции:', eventData.data.transactionId);
 *   console.log('Сумма:', eventData.data.amount);
 * });
 *
 * // Подписка на событие ошибки
 * eventEmitter.addListener(EPaymentFormEventName.PAYMENT_FAILED, (eventData) => {
 *   console.log('Ошибка платежа:', eventData.data.message);
 *   console.log('Код ошибки:', eventData.data.errorCode);
 * });
 * ```
 *
 * @example Создание кастомного обработчика событий
 * ```typescript
 * interface PaymentSuccessData {
 *   transactionId: number;
 *   amount: string;
 *   currency: string;
 * }
 *
 * const handlePaymentSuccess = (eventData: IBaseEventData<PaymentSuccessData>) => {
 *   const { transactionId, amount, currency } = eventData.data;
 *   console.log(`Платеж на ${amount} ${currency} успешно завершен. ID: ${transactionId}`);
 * };
 * ```
 *
 * @since 1.0.0
 */
export interface IBaseEventData<T = Record<string, any>> {
  /**
   * Название события из перечисления EPaymentFormEventName
   * @description Идентификатор типа события, который позволяет
   * определить какое именно событие произошло в платежной форме.
   */
  eventName: EPaymentFormEventName;

  /**
   * Данные, связанные с событием
   * @description Объект с дополнительной информацией о событии.
   * Структура данных зависит от типа события. Например, для события
   * PAYMENT_SUCCESS содержит transactionId и сумму платежа.
   */
  data: T;
}
