/**
 * @fileoverview Главный React хук для работы с CloudPayments SDK
 * @description Объединяет все базовые хуки для удобного использования
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import { useCloudPaymentsEvents, useCloudPaymentsCore } from './core';
import { usePaymentForm } from './payment';
import type {
  IUseCloudPaymentsOptions,
  TUseCloudPaymentsReturn,
} from './types';

// ============================================================================
// HOOK IMPLEMENTATION
// ============================================================================

/**
 * Главный React хук для работы с CloudPayments SDK
 *
 * @description Объединяет все базовые хуки (события, инициализация, платежная форма)
 * для предоставления единого удобного интерфейса. Автоматически управляет
 * инициализацией SDK, подпиской на события и очисткой ресурсов.
 *
 * @param publicId - Публичный идентификатор мерчанта CloudPayments
 * @param options - Опциональные настройки хука
 * @returns Кортеж [функция запуска платежа, состояние хука]
 *
 * @example Базовое использование
 * ```typescript
 * import { useCloudPayments } from '@lmapp/react-native-cloudpayments';
 *
 * const PaymentScreen = () => {
 *   const [presentPaymentForm, { isLoading, isError, error, transactionId }] = useCloudPayments(
 *     'pk_test_1234567890abcdef'
 *   );
 *
 *   const handlePayment = async () => {
 *     try {
 *       const result = await presentPaymentForm({
 *         amount: '1000.00',
 *         currency: 'RUB',
 *         description: 'Покупка товара',
 *         email: 'user@example.com'
 *       });
 *
 *       if (result.success) {
 *         console.log('Платеж успешен:', result.transactionId);
 *       }
 *     } catch (error) {
 *       console.log('Ошибка:', error.message);
 *     }
 *   };
 *
 *   return (
 *     <View>
 *       <Button
 *         title="Оплатить"
 *         onPress={handlePayment}
 *         disabled={isLoading}
 *       />
 *       {isLoading && <ActivityIndicator />}
 *       {isError && <Text>Ошибка: {error?.message}</Text>}
 *       {transactionId && <Text>Платеж успешен! ID: {transactionId}</Text>}
 *     </View>
 *   );
 * };
 * ```
 *
 * @example Использование с callback'ами
 * ```typescript
 * const [presentPaymentForm, state] = useCloudPayments(publicId, {
 *   onSuccess: (data) => {
 *     console.log('Платеж успешен:', data.transactionId);
 *     navigation.navigate('Success', { transactionId: data.transactionId });
 *   },
 *   onError: (data) => {
 *     console.log('Ошибка платежа:', data.message);
 *     Alert.alert('Ошибка оплаты', data.message);
 *   },
 *   onProgress: (data) => {
 *     console.log('Прогресс:', data.stage);
 *   }
 * });
 * ```
 *
 * @example Расширенная конфигурация
 * ```typescript
 * const [presentPaymentForm, state] = useCloudPayments(publicId, {
 *   enabledEvents: [
 *     EPaymentFormEventName.PAYMENT_SUCCESS,
 *     EPaymentFormEventName.PAYMENT_FAILED,
 *     EPaymentFormEventName.PAYMENT_PROGRESS
 *   ],
 *   autoInitialize: true,
 *   onSuccess: (data) => {
 *     Analytics.track('payment_success', {
 *       transactionId: data.transactionId,
 *       amount: paymentData.amount
 *     });
 *   }
 * });
 * ```
 *
 * @since 1.0.0
 */
export const useCloudPayments = (
  publicId: string,
  options: IUseCloudPaymentsOptions = {}
): TUseCloudPaymentsReturn => {
  // ============================================================================
  // DECOMPOSED HOOKS
  // ============================================================================

  const { enabledEvents, autoInitialize, onSuccess, onError, onCancel } =
    options;

  // Хук для инициализации SDK
  useCloudPaymentsCore(publicId, { autoInitialize });

  // Хук для управления событиями и состоянием
  const { state } = useCloudPaymentsEvents({
    enabledEvents,
    onSuccess,
    onError,
    onCancel,
  });

  // Хук для платежной формы
  const presentPaymentForm = usePaymentForm(publicId);

  // ============================================================================
  // RETURN
  // ============================================================================

  return [presentPaymentForm, state];
};
