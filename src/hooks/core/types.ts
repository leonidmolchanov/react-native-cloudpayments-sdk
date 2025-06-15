/**
 * @fileoverview Типы для базовых хуков CloudPayments SDK
 * @description Содержит интерфейсы и типы для core хуков (события, состояние, инициализация)
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import type { EPaymentFormEventName, EPaymentFormErrorCode } from '../../types';

// ============================================================================
// CORE TYPES
// ============================================================================

/**
 * Статус выполнения операции в CloudPayments SDK
 *
 * @description Перечисление возможных состояний SDK во время жизненного цикла операций.
 * Используется для отображения соответствующего UI и обработки пользовательских действий.
 *
 * @since 1.0.0
 */
export type TCloudPaymentsStatus =
  | 'idle' // Начальное состояние, готов к работе
  | 'initializing' // Инициализация SDK
  | 'processing' // Выполнение операции
  | 'success' // Операция успешно завершена
  | 'error' // Произошла ошибка
  | 'cancelled'; // Операция отменена пользователем

/**
 * Данные об ошибке в CloudPayments SDK
 *
 * @description Структура информации об ошибке, которая возникла во время работы с SDK.
 * Содержит человекочитаемое сообщение и опциональный код ошибки для программной обработки.
 *
 * @since 1.0.0
 */
export interface ICloudPaymentsError {
  /**
   * Человекочитаемое сообщение об ошибке
   * @description Текст ошибки, который можно показать пользователю
   */
  message: string;

  /**
   * Код ошибки (опционально)
   * @description Стандартизированный код из EPaymentFormErrorCode для программной обработки
   */
  code?: EPaymentFormErrorCode;

  /**
   * Дополнительные данные об ошибке (опционально)
   * @description Произвольные данные, связанные с ошибкой
   */
  details?: Record<string, any>;
}

/**
 * Данные о прогрессе выполнения операции
 *
 * @description Информация о текущем этапе выполнения операции.
 * Используется для отображения прогресс-бара или статуса операции.
 *
 * @since 1.0.0
 */
export interface ICloudPaymentsProgress {
  /**
   * Название текущего этапа
   * @description Строковое описание текущего этапа выполнения операции
   */
  stage: string;

  /**
   * Процент выполнения (опционально)
   * @description Числовое значение от 0 до 100
   */
  percentage?: number;
}

/**
 * Базовое состояние CloudPayments SDK
 *
 * @description Объект, содержащий всю информацию о текущем состоянии SDK.
 * Используется как основа для всех специализированных состояний.
 *
 * @since 1.0.0
 */
export interface ICloudPaymentsBaseState {
  /**
   * Индикатор загрузки
   * @description true если выполняется какая-либо операция
   */
  isLoading: boolean;

  /**
   * Индикатор ошибки
   * @description true если произошла ошибка
   */
  isError: boolean;

  /**
   * Индикатор успешного завершения
   * @description true если операция успешно завершена
   */
  isSuccess: boolean;

  /**
   * Текущий статус операции
   * @description Детальное состояние SDK
   */
  status: TCloudPaymentsStatus;

  /**
   * Информация об ошибке (если есть)
   * @description Объект с деталями ошибки, null если ошибки нет
   */
  error: ICloudPaymentsError | null;

  /**
   * ID успешной транзакции (если есть)
   * @description Идентификатор транзакции в системе CloudPayments, null если операция не завершена
   */
  transactionId: number | null;

  /**
   * Информация о прогрессе (если есть)
   * @description Данные о текущем этапе выполнения операции, null если прогресс не отслеживается
   */
  progress: ICloudPaymentsProgress | null;
}

/**
 * Callback функции для обработки событий
 *
 * @description Интерфейс для опциональных callback функций, которые вызываются
 * при различных событиях SDK.
 *
 * @since 1.0.0
 */
export interface ICloudPaymentsEventCallbacks {
  /**
   * Callback для успешного завершения операции
   * @description Вызывается при успешном завершении операции
   */
  onSuccess?: (data: { transactionId: number; message?: string }) => void;

  /**
   * Callback для ошибки операции
   * @description Вызывается при возникновении ошибки
   */
  onError?: (data: { message: string; code?: EPaymentFormErrorCode }) => void;

  /**
   * Callback для отмены операции
   * @description Вызывается когда пользователь отменяет операцию
   */
  onCancel?: () => void;
}

/**
 * Опции для хука событий
 *
 * @description Конфигурация хука для управления событиями SDK.
 *
 * @since 1.0.0
 */
export interface IUseCloudPaymentsEventsOptions
  extends ICloudPaymentsEventCallbacks {
  /**
   * Список событий для подписки
   * @description Массив событий, на которые хук будет подписываться.
   * Если не указан, подписывается на все основные события.
   */
  enabledEvents?: EPaymentFormEventName[];
}

/**
 * Возвращаемый тип хука событий
 *
 * @description Объект с методами управления состоянием и текущим состоянием.
 *
 * @since 1.0.0
 */
export interface IUseCloudPaymentsEventsReturn {
  /**
   * Текущее состояние SDK
   */
  state: ICloudPaymentsBaseState;

  /**
   * Методы управления состоянием
   */
  actions: {
    /**
     * Установка статуса с автоматическим обновлением флагов
     */
    setStatus: (
      status: TCloudPaymentsStatus,
      additionalUpdates?: Partial<ICloudPaymentsBaseState>
    ) => void;

    /**
     * Установка ошибки
     */
    setError: (
      message: string,
      code?: EPaymentFormErrorCode,
      details?: Record<string, any>
    ) => void;

    /**
     * Установка прогресса
     */
    setProgress: (stage: string, percentage?: number) => void;

    /**
     * Очистка состояния
     */
    resetState: () => void;

    /**
     * Обновление состояния
     */
    updateState: (updates: Partial<ICloudPaymentsBaseState>) => void;
  };
}

/**
 * Опции для хука инициализации
 *
 * @description Конфигурация хука для инициализации SDK.
 *
 * @since 1.0.0
 */
export interface IUseCloudPaymentsCoreOptions {
  /**
   * Автоматическая инициализация SDK
   * @description Если true (по умолчанию), SDK будет автоматически инициализирован при монтировании хука
   */
  autoInitialize?: boolean;
}

/**
 * Возвращаемый тип хука инициализации
 *
 * @description Объект с методами инициализации и статусом.
 *
 * @since 1.0.0
 */
export interface IUseCloudPaymentsCoreReturn {
  /**
   * Статус инициализации SDK
   */
  isInitialized: boolean;

  /**
   * Функция инициализации SDK
   */
  initializeSDK: () => Promise<void>;
}
