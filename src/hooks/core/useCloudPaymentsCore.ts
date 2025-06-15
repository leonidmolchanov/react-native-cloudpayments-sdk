/**
 * @fileoverview Хук для инициализации CloudPayments SDK
 * @description Базовый хук для инициализации и управления состоянием SDK
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import { useCallback, useEffect, useRef } from 'react';

import { init } from '../../index';
import { EPaymentFormErrorCode } from '../../types';
import type {
  IUseCloudPaymentsCoreOptions,
  IUseCloudPaymentsCoreReturn,
} from './types';

// ============================================================================
// HOOK IMPLEMENTATION
// ============================================================================

/**
 * Хук для инициализации CloudPayments SDK
 *
 * @description Управляет инициализацией SDK и предоставляет информацию о статусе.
 * Автоматически инициализирует SDK при монтировании компонента (если включена автоинициализация).
 *
 * @param publicId - Публичный идентификатор мерчанта CloudPayments
 * @param options - Опции конфигурации хука
 * @returns Объект с методами инициализации и статусом
 *
 * @example Базовое использование
 * ```typescript
 * const { isInitialized, initializeSDK } = useCloudPaymentsCore(
 *   'pk_test_1234567890abcdef'
 * );
 *
 * // SDK автоматически инициализируется при монтировании
 * console.log('SDK инициализирован:', isInitialized);
 * ```
 *
 * @example Ручная инициализация
 * ```typescript
 * const { isInitialized, initializeSDK } = useCloudPaymentsCore(
 *   'pk_test_1234567890abcdef',
 *   { autoInitialize: false }
 * );
 *
 * const handleManualInit = async () => {
 *   if (!isInitialized) {
 *     await initializeSDK();
 *   }
 * };
 * ```
 *
 * @since 1.0.0
 */
export const useCloudPaymentsCore = (
  publicId: string,
  options: IUseCloudPaymentsCoreOptions = {}
): IUseCloudPaymentsCoreReturn => {
  // ============================================================================
  // STATE & REFS
  // ============================================================================

  const isInitializedRef = useRef<boolean>(false);
  const { autoInitialize = true } = options;

  // ============================================================================
  // FUNCTIONS
  // ============================================================================

  /**
   * Инициализация SDK
   */
  const initializeSDK = useCallback(async (): Promise<void> => {
    if (isInitializedRef.current) {
      return;
    }

    try {
      await init(publicId);
      isInitializedRef.current = true;
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : 'Ошибка инициализации SDK';

      // Выбрасываем ошибку с кодом для обработки в вызывающем коде
      const sdkError = new Error(errorMessage);
      (sdkError as any).code = EPaymentFormErrorCode.SERVICE_UNINITIALIZED;
      throw sdkError;
    }
  }, [publicId]);

  // ============================================================================
  // EFFECTS
  // ============================================================================

  /**
   * Автоматическая инициализация SDK при монтировании
   */
  useEffect(() => {
    if (autoInitialize) {
      initializeSDK().catch((error) => {
        console.error('Ошибка автоинициализации CloudPayments SDK:', error);
      });
    }
  }, [autoInitialize, initializeSDK]);

  // ============================================================================
  // RETURN
  // ============================================================================

  return {
    isInitialized: isInitializedRef.current,
    initializeSDK,
  };
};
