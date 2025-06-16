/**
 * @fileoverview Интерфейсы событий CloudPayments SDK
 * @description Содержит типы данных для событий платежной формы и пользовательских взаимодействий
 * @author Leonid Molchanov
 * @since 1.0.0
 */

import type {
  EPaymentFormAction,
  EPaymentFormErrorCode,
  EPaymentMethodType,
} from './enums';

// ============================================================================
// EVENT INTERFACES
// ============================================================================

/**
 * Базовое событие CloudPayments
 *
 * @description Базовый интерфейс для всех событий CloudPayments SDK.
 * Содержит поле action для определения типа события.
 *
 * @since 1.0.0
 */
export interface ICloudPaymentsBaseEvent {
  /**
   * Тип действия события
   * @description Определяет конкретный тип события внутри категории.
   */
  action: string;
}

/**
 * Событие платежной формы
 *
 * @description Событие жизненного цикла платежной формы, связанное с отображением
 * и скрытием UI элементов, а также выполнением транзакций.
 *
 * @example Обработка событий платежной формы
 * ```typescript
 * import { eventEmitter, EPaymentFormEventName } from '@lmapp/react-native-cloudpayments';
 *
 * eventEmitter.addListener(EPaymentFormEventName.PAYMENT_FORM, (event: IPaymentFormEvent) => {
 *   switch (event.action) {
 *     case 'willDisplay':
 *       console.log('Форма готовится к показу');
 *       setIsLoading(true);
 *       break;
 *     case 'didDisplay':
 *       console.log('Форма отображена');
 *       setIsLoading(false);
 *       break;
 *     case 'willHide':
 *       console.log('Форма готовится к скрытию');
 *       break;
 *     case 'didHide':
 *       console.log('Форма скрыта');
 *       setPaymentFormVisible(false);
 *       break;
 *     case 'transaction':
 *       if (event.statusCode) {
 *         console.log('Платеж успешен:', event.transactionId);
 *       } else {
 *         console.log('Ошибка платежа:', event.message);
 *       }
 *       break;
 *   }
 * });
 * ```
 *
 * @since 1.0.0
 */
export interface IPaymentFormEvent extends ICloudPaymentsBaseEvent {
  /**
   * Действие платежной формы
   * @description Тип события из жизненного цикла платежной формы.
   */
  action: 'willDisplay' | 'didDisplay' | 'willHide' | 'didHide' | 'transaction';

  /**
   * Код статуса (для транзакций)
   * @description Присутствует только для action='transaction'.
   * true - успешная транзакция, false - ошибка.
   */
  statusCode?: boolean;

  /**
   * Идентификатор транзакции (для успешных транзакций)
   * @description ID созданной транзакции в системе CloudPayments.
   * Присутствует только при успешном завершении платежа.
   */
  transactionId?: number;

  /**
   * Сообщение (для ошибок транзакций)
   * @description Текстовое описание ошибки.
   * Присутствует при неудачном завершении платежа.
   */
  message?: string;

  /**
   * Код ошибки (для ошибок транзакций)
   * @description Стандартизированный код ошибки.
   */
  errorCode?: EPaymentFormErrorCode;
}

/**
 * Событие платежа
 *
 * @description Событие для операций с платежами (создание, обработка).
 *
 * @since 1.0.0
 */
export interface IPaymentEvent extends ICloudPaymentsBaseEvent {
  /**
   * Действие платежа
   * @description Тип операции с платежом.
   */
  action: string;

  /**
   * Дополнительные данные
   * @description Произвольные данные, связанные с операцией.
   */
  [key: string]: any;
}

/**
 * Событие карты
 *
 * @description Событие для операций с картами (валидация, получение информации).
 *
 * @since 1.0.0
 */
export interface ICardEvent extends ICloudPaymentsBaseEvent {
  /**
   * Действие с картой
   * @description Тип операции с картой.
   */
  action: string;

  /**
   * Дополнительные данные
   * @description Произвольные данные, связанные с операцией.
   */
  [key: string]: any;
}

/**
 * Событие 3DS
 *
 * @description Событие для операций 3D Secure аутентификации.
 *
 * @since 1.0.0
 */
export interface IThreeDSEvent extends ICloudPaymentsBaseEvent {
  /**
   * Действие 3DS
   * @description Тип операции 3D Secure.
   */
  action: string;

  /**
   * Дополнительные данные
   * @description Произвольные данные, связанные с операцией.
   */
  [key: string]: any;
}

// ============================================================================
// LEGACY INTERFACES (для обратной совместимости)
// ============================================================================

/**
 * @deprecated Используйте IPaymentFormEvent с action='willDisplay'|'didDisplay'|'willHide'|'didHide'
 */
export interface IPaymentFormUIEvent {
  action:
    | EPaymentFormAction.WILL_DISPLAY
    | EPaymentFormAction.DID_DISPLAY
    | EPaymentFormAction.WILL_HIDE
    | EPaymentFormAction.DID_HIDE;
}

/**
 * @deprecated Используйте IPaymentFormEvent с action='transaction'
 */
export interface IPaymentFormTransactionEvent {
  action: EPaymentFormAction.TRANSACTION;
  status: boolean;
  transactionId?: number;
  message?: string;
}

/**
 * @deprecated Используйте IPaymentFormEvent с action='transaction' и statusCode=true
 */
export interface IPaymentSuccessEventData {
  transactionId: number;
  message?: string;
}

/**
 * @deprecated Используйте IPaymentFormEvent с action='transaction' и statusCode=false
 */
export interface IPaymentFailedEventData {
  message: string;
  code?: EPaymentFormErrorCode;
}

/**
 * @deprecated Будет удалено в следующих версиях
 */
export interface IPaymentProgressEventData {
  stage: string;
  progress?: number;
}

/**
 * @deprecated Будет удалено в следующих версиях
 */
export interface IPaymentMethodSelectedEventData {
  method: EPaymentMethodType;
  details?: Record<string, any>;
}
