/**
 * @fileoverview Хук для работы с платежной формой CloudPayments
 * @description Специализированный хук для запуска платежной формы CloudPayments
 * @author Leonid Molchanov
 * @since 1.0.0
 */

import { useCallback } from 'react';

import { PaymentService } from '../../services';
import type { IPaymentData, IPaymentFormResponse } from '../../types';
import type {
  // IUsePaymentFormOptions,
  TUsePaymentFormReturn,
} from './types';

// ============================================================================
// HOOK IMPLEMENTATION
// ============================================================================

/**
 * Хук для работы с платежной формой CloudPayments
 *
 * @description Предоставляет функцию для запуска стандартной платежной формы CloudPayments.
 * Этот хук инкапсулирует логику работы с платежной формой и может быть переиспользован
 * в различных сценариях оплаты.
 *
 * @param publicId - Публичный идентификатор мерчанта CloudPayments
 * @param options - Опции конфигурации хука (пока не используются, зарезервированы для будущего)
 * @returns Функция для запуска платежной формы
 *
 * @example Базовое использование
 * ```typescript
 * const presentPaymentForm = usePaymentForm('pk_test_1234567890abcdef');
 *
 * const handlePayment = async () => {
 *   try {
 *     const result = await presentPaymentForm({
 *       amount: '1000.00',
 *       currency: 'RUB',
 *       description: 'Покупка товара',
 *       email: 'user@example.com'
 *     });
 *
 *     if (result.success) {
 *       console.log('Платеж успешен:', result.transactionId);
 *     } else {
 *       console.log('Платеж отклонен:', result.message);
 *     }
 *   } catch (error) {
 *     console.error('Ошибка платежа:', error);
 *   }
 * };
 * ```
 *
 * @example Использование с дополнительными параметрами
 * ```typescript
 * const presentPaymentForm = usePaymentForm('pk_test_1234567890abcdef');
 *
 * const result = await presentPaymentForm({
 *   amount: '2500.00',
 *   currency: 'RUB',
 *   description: 'Подписка Premium',
 *   email: 'user@example.com',
 *   requireEmail: true,
 *   applePayMerchantId: 'merchant.com.myapp.payments',
 *   showResultScreen: true
 * });
 * ```
 *
 * @since 1.0.0
 */
export const usePaymentForm = (
  publicId: string
  // options: IUsePaymentFormOptions = {}
): TUsePaymentFormReturn => {
  /**
   * Функция запуска платежной формы
   */
  const presentPaymentForm = useCallback(
    async (paymentData: IPaymentData): Promise<IPaymentFormResponse> => {
      try {
        // Запускаем платежную форму с объединенными данными
        const result: IPaymentFormResponse =
          await PaymentService.presentPaymentForm({
            ...paymentData,
            publicId,
          });

        return result;
      } catch (error) {
        const errorMessage =
          error instanceof Error ? error.message : 'Неизвестная ошибка';

        // Возвращаем результат с ошибкой
        const errorResult = {
          success: false,
          message: errorMessage,
          transactionId: undefined,
        };
        return errorResult;
      }
    },
    [publicId]
  );

  return presentPaymentForm;
};
