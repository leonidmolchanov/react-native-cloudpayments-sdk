/**
 * @fileoverview Типы для главного хука CloudPayments SDK
 * @description Содержит интерфейсы и типы для основного хука useCloudPayments
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import type { EPaymentFormEventName } from '../types';
import type {
  ICloudPaymentsBaseState,
  ICloudPaymentsEventCallbacks,
} from './core/types';
import type { TUsePaymentFormReturn } from './payment/types';

// ============================================================================
// MAIN HOOK TYPES
// ============================================================================

/**
 * Опции для главного хука CloudPayments
 *
 * @description Объединяет все опции для конфигурации основного хука.
 * Включает настройки событий, инициализации и callback'ов.
 *
 * @since 1.0.0
 */
export interface IUseCloudPaymentsOptions extends ICloudPaymentsEventCallbacks {
  /**
   * Список событий для подписки
   * @description Массив событий, на которые хук будет подписываться.
   * Если не указан, подписывается на все основные события.
   */
  enabledEvents?: EPaymentFormEventName[];

  /**
   * Автоматическая инициализация SDK
   * @description Если true (по умолчанию), SDK будет автоматически инициализирован при монтировании хука
   */
  autoInitialize?: boolean;
}

/**
 * Возвращаемый тип главного хука CloudPayments
 *
 * @description Кортеж в стиле react-hook-form: [функция_запуска, объект_состояния].
 * Первый элемент - функция для запуска платежной формы,
 * второй элемент - объект с текущим состоянием хука.
 *
 * @since 1.0.0
 */
export type TUseCloudPaymentsReturn = [
  TUsePaymentFormReturn,
  ICloudPaymentsBaseState,
];
