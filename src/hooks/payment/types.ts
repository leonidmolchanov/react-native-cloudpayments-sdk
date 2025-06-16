/**
 * @fileoverview Типы для хуков платежей CloudPayments SDK
 * @description Содержит интерфейсы и типы для хуков различных методов оплаты
 * @author Leonid Molchanov
 * @since 1.0.0
 */

import type { IPaymentData, IPaymentFormResponse } from '../../types';
import type {
  ICloudPaymentsBaseState,
  ICloudPaymentsEventCallbacks,
} from '../core/types';

// ============================================================================
// PAYMENT TYPES
// ============================================================================

/**
 * Состояние хука платежной формы
 *
 * @description Расширяет базовое состояние специфичными для платежной формы полями.
 *
 * @since 1.0.0
 */
export interface IPaymentFormState extends ICloudPaymentsBaseState {
  // Пока наследует все от базового состояния
  // В будущем можно добавить специфичные для платежной формы поля
}

/**
 * Опции для хука платежной формы
 *
 * @description Конфигурация хука для работы с платежной формой CloudPayments.
 *
 * @since 1.0.0
 */
export interface IUsePaymentFormOptions extends ICloudPaymentsEventCallbacks {
  // Пока наследует все от базовых callback'ов
  // В будущем можно добавить специфичные для платежной формы опции
}

/**
 * Возвращаемый тип хука платежной формы
 *
 * @description Функция для запуска платежной формы, которая возвращает результат операции.
 *
 * @since 1.0.0
 */
export type TUsePaymentFormReturn = (
  paymentData: IPaymentData
) => Promise<IPaymentFormResponse>;
