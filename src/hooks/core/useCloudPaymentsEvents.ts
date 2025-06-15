/**
 * @fileoverview Хук для управления событиями CloudPayments SDK
 * @description Базовый хук для подписки на события и управления состоянием SDK
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import { useCallback, useEffect, useRef, useState } from 'react';
import type { EmitterSubscription } from 'react-native';

import { eventEmitter } from '../../events';
import { EPaymentFormEventName, EPaymentFormErrorCode } from '../../types';
import type { IPaymentFormEvent } from '../../types';
import type {
  TCloudPaymentsStatus,
  ICloudPaymentsBaseState,
  ICloudPaymentsError,
  ICloudPaymentsProgress,
  IUseCloudPaymentsEventsOptions,
  IUseCloudPaymentsEventsReturn,
} from './types';

// ============================================================================
// CONSTANTS
// ============================================================================

/**
 * События по умолчанию для подписки
 * @description Базовый набор событий, на которые подписывается хук если не указаны enabledEvents
 */
const DEFAULT_ENABLED_EVENTS: EPaymentFormEventName[] = [
  EPaymentFormEventName.PAYMENT_FORM,
];

/**
 * Начальное состояние SDK
 */
const INITIAL_STATE: ICloudPaymentsBaseState = {
  isLoading: false,
  isError: false,
  isSuccess: false,
  status: 'idle',
  error: null,
  transactionId: null,
  progress: null,
};

// ============================================================================
// HOOK IMPLEMENTATION
// ============================================================================

/**
 * Хук для управления событиями CloudPayments SDK
 *
 * @description Базовый хук для подписки на события SDK и управления состоянием.
 * Предоставляет методы для обновления состояния и автоматически подписывается
 * на события платежной формы.
 *
 * @param options - Опции конфигурации хука
 * @returns Объект с состоянием и методами управления
 *
 * @example Базовое использование
 * ```typescript
 * const { state, actions } = useCloudPaymentsEvents({
 *   onSuccess: (data) => console.log('Успех:', data.transactionId),
 *   onError: (data) => console.log('Ошибка:', data.message)
 * });
 *
 * // Использование методов управления состоянием
 * const handleStart = () => {
 *   actions.setStatus('processing');
 * };
 *
 * const handleError = (message: string) => {
 *   actions.setError(message, EPaymentFormErrorCode.PAYMENT_FAILED);
 * };
 * ```
 *
 * @example Кастомные события
 * ```typescript
 * const { state, actions } = useCloudPaymentsEvents({
 *   enabledEvents: [EPaymentFormEventName.PAYMENT_FORM],
 *   onSuccess: (data) => {
 *     actions.setStatus('success', { transactionId: data.transactionId });
 *   }
 * });
 * ```
 *
 * @since 1.0.0
 */
export const useCloudPaymentsEvents = (
  options: IUseCloudPaymentsEventsOptions = {}
): IUseCloudPaymentsEventsReturn => {
  // ============================================================================
  // STATE & REFS
  // ============================================================================

  const [state, setState] = useState<ICloudPaymentsBaseState>(INITIAL_STATE);
  const subscriptionsRef = useRef<EmitterSubscription[]>([]);
  const paymentResultProcessedRef = useRef<boolean>(false); // Флаг для отслеживания обработанных результатов

  const {
    enabledEvents = DEFAULT_ENABLED_EVENTS,
    onSuccess,
    onError,
    onCancel,
  } = options;

  // ============================================================================
  // HELPER FUNCTIONS
  // ============================================================================

  /**
   * Обновление состояния хука
   */
  const updateState = useCallback(
    (updates: Partial<ICloudPaymentsBaseState>) => {
      setState((prevState) => ({ ...prevState, ...updates }));
    },
    []
  );

  /**
   * Установка статуса с автоматическим обновлением флагов
   */
  const setStatus = useCallback(
    (
      status: TCloudPaymentsStatus,
      additionalUpdates: Partial<ICloudPaymentsBaseState> = {}
    ) => {
      updateState({
        status,
        isLoading: status === 'initializing' || status === 'processing',
        isError: status === 'error',
        isSuccess: status === 'success',
        ...additionalUpdates,
      });
    },
    [updateState]
  );

  /**
   * Установка ошибки
   */
  const setError = useCallback(
    (
      message: string,
      code?: EPaymentFormErrorCode,
      details?: Record<string, any>
    ) => {
      const error: ICloudPaymentsError = { message, code, details };
      setStatus('error', { error });
      paymentResultProcessedRef.current = true; // Помечаем результат как обработанный
    },
    [setStatus]
  );

  /**
   * Установка прогресса
   */
  const setProgress = useCallback(
    (stage: string, percentage?: number) => {
      const progress: ICloudPaymentsProgress = { stage, percentage };
      updateState({ progress });
    },
    [updateState]
  );

  /**
   * Очистка состояния
   */
  const resetState = useCallback(() => {
    setState(INITIAL_STATE);
    paymentResultProcessedRef.current = false; // Сбрасываем флаг
  }, []);

  // ============================================================================
  // EVENT HANDLERS
  // ============================================================================

  /**
   * Обработчик событий платежной формы
   */
  const handlePaymentFormEvent = useCallback(
    (event: IPaymentFormEvent) => {
      switch (event.action) {
        case 'willDisplay':
          setStatus('initializing');
          paymentResultProcessedRef.current = false; // Сбрасываем флаг при начале нового платежа
          break;

        case 'didDisplay':
          setStatus('processing');
          break;

        case 'willHide':
          // Форма готовится к скрытию
          break;

        case 'didHide':
          // Форма скрыта - вызываем onCancel только если результат еще не был обработан
          if (
            !paymentResultProcessedRef.current &&
            state.status !== 'success' &&
            state.status !== 'error'
          ) {
            setStatus('cancelled');
            onCancel?.();
          }
          // Сбрасываем флаг после обработки
          paymentResultProcessedRef.current = false;
          break;

        case 'transaction':
          if (event.statusCode) {
            // Успешная транзакция
            const transactionId = event.transactionId || null;
            setStatus('success', { transactionId });
            paymentResultProcessedRef.current = true; // Помечаем результат как обработанный

            if (onSuccess && transactionId) {
              onSuccess({ transactionId, message: event.message });
            }
          } else {
            // Ошибка транзакции
            const message =
              event.message || 'Произошла ошибка при выполнении платежа';
            const code = event.errorCode;
            setError(message, code);

            if (onError) {
              onError({ message, code });
            }
          }
          break;

        default:
          console.warn('Неизвестное действие платежной формы:', event.action);
      }
    },
    [state.status, setStatus, setError, onSuccess, onError, onCancel]
  );

  // ============================================================================
  // EFFECTS
  // ============================================================================

  /**
   * Подписка на события при монтировании компонента
   */
  useEffect(() => {
    const subscriptions: EmitterSubscription[] = [];

    // Подписываемся только на поддерживаемые события
    enabledEvents.forEach((eventName) => {
      switch (eventName) {
        case EPaymentFormEventName.PAYMENT_FORM:
          subscriptions.push(
            eventEmitter.addListener(eventName, handlePaymentFormEvent)
          );
          break;

        case EPaymentFormEventName.PAYMENT:
        case EPaymentFormEventName.CARD:
        case EPaymentFormEventName.THREE_DS:
          // Пока не реализованы, но готовы к добавлению
          console.log(
            `Событие ${eventName} будет поддержано в будущих версиях`
          );
          break;

        default:
          console.warn(`Неподдерживаемое событие: ${eventName}`);
      }
    });

    subscriptionsRef.current = subscriptions;

    // Очистка подписок при размонтировании
    return () => {
      subscriptions.forEach((subscription) => subscription.remove());
      subscriptionsRef.current = [];
    };
  }, [enabledEvents, handlePaymentFormEvent]);

  // ============================================================================
  // RETURN
  // ============================================================================

  return {
    state,
    actions: {
      setStatus,
      setError,
      setProgress,
      updateState,
      resetState,
    },
  };
};
