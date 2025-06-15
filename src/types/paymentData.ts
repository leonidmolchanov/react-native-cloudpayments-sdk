/**
 * @fileoverview Интерфейсы данных платежей для CloudPayments SDK
 * @description Содержит типы данных для настройки и выполнения платежей
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import type { ECardIOLanguage, ECardIOColorScheme } from './enums';

// ============================================================================
// PAYMENT DATA INTERFACES
// ============================================================================

/**
 * Базовые данные платежа, обязательные для всех операций
 *
 * @description Содержит минимально необходимую информацию для проведения платежа
 * через CloudPayments. Все поля кроме publicId, amount и currency являются опциональными.
 *
 * @example Создание базовых данных платежа
 * ```typescript
 * import { IBasePaymentData } from '@lmapp/react-native-cloudpayments';
 *
 * const paymentData: IBasePaymentData = {
 *   publicId: 'pk_test_1234567890abcdef', // Ваш Public ID из личного кабинета
 *   amount: '1000.00',                    // Сумма в рублях
 *   currency: 'RUB',                      // Валюта платежа
 *   description: 'Оплата заказа №12345',  // Описание платежа
 *   email: 'user@example.com',            // Email покупателя
 *   accountId: 'user_123',                // ID пользователя в вашей системе
 *   jsonData: JSON.stringify({            // Дополнительные данные
 *     orderId: 12345,
 *     source: 'mobile_app'
 *   })
 * };
 * ```
 *
 * @see {@link https://developers.cloudpayments.ru/#parametry-zaprosa} Документация по параметрам
 * @since 1.0.0
 */
export interface IBasePaymentData {
  /**
   * Публичный идентификатор мерчанта
   * @description Public ID из личного кабинета CloudPayments.
   * Начинается с 'pk_' для тестового режима или 'pk_live_' для боевого.
   * @example 'pk_test_1234567890abcdef'
   */
  publicId: string;

  /**
   * Сумма платежа
   * @description Сумма к списанию в указанной валюте.
   * Передается в виде строки с точностью до копеек.
   * @example '1000.00' для 1000 рублей
   */
  amount: string;

  /**
   * Валюта платежа
   * @description Трехбуквенный код валюты по стандарту ISO 4217.
   * Поддерживаемые валюты: RUB, USD, EUR, GBP и другие.
   * @example 'RUB'
   */
  currency: string;

  /**
   * Описание платежа (опционально)
   * @description Назначение платежа, которое будет отображаться
   * в личном кабинете и выписках. Максимум 512 символов.
   * @example 'Оплата заказа №12345'
   */
  description?: string;

  /**
   * Email плательщика (опционально)
   * @description Электронная почта покупателя для отправки чека.
   * Обязательно при включенной отправке чеков по 54-ФЗ.
   * @example 'user@example.com'
   */
  email?: string;

  /**
   * Идентификатор плательщика (опционально)
   * @description Уникальный идентификатор покупателя в вашей системе.
   * Используется для привязки платежа к пользователю и аналитики.
   * @example 'user_123'
   */
  accountId?: string;

  /**
   * Дополнительные данные в формате JSON (опционально)
   * @description Произвольные данные, которые будут переданы в уведомлениях.
   * Должны быть сериализованы в JSON строку. Максимум 4096 символов.
   * @example '{"orderId": 12345, "source": "mobile_app"}'
   */
  jsonData?: string;

  /**
   * Включение сканера банковских карт (опционально)
   * @description Если true, в платежной форме будет доступна кнопка
   * для сканирования банковской карты с помощью камеры устройства.
   * Использует CardIO библиотеку для распознавания номера карты и срока действия.
   * @default false
   * @platform android
   */
  enableCardScanner?: boolean;

  /**
   * Настройки CardIO сканера (опционально)
   * @description Детальная конфигурация поведения и внешнего вида CardIO сканера.
   * Применяется только если enableCardScanner = true.
   * @platform android
   */
  cardScannerConfig?: ICardIOConfig;
}

/**
 * Конфигурация платежной формы и дополнительных возможностей
 *
 * @description Настройки поведения платежной формы, включая требования к email,
 * настройки Apple Pay, URL для редиректов и другие опции.
 *
 * @example Настройка платежной формы
 * ```typescript
 * import { IPaymentConfigurationData } from '@lmapp/react-native-cloudpayments';
 *
 * const config: IPaymentConfigurationData = {
 *   requireEmail: true,                    // Обязательный ввод email
 *   useDualMessagePayment: false,          // Одностадийный платеж
 *   disableApplePay: false,                // Apple Pay включен
 *   applePayMerchantId: 'merchant.com.example.app', // ID для Apple Pay
 *   successRedirectUrl: 'https://example.com/success', // URL успеха
 *   failRedirectUrl: 'https://example.com/fail',       // URL ошибки
 *   saveCardSinglePaymentMode: true,       // Сохранение карт
 *   showResultScreen: true                 // Показ экрана результата
 * };
 * ```
 *
 * @since 1.0.0
 */
export interface IPaymentConfigurationData {
  /**
   * Обязательность ввода email (опционально)
   * @description Если true, пользователь обязан указать email.
   * Рекомендуется включать при работе с чеками по 54-ФЗ.
   * @default false
   */
  requireEmail?: boolean;

  /**
   * Использование двухстадийных платежей (опционально)
   * @description true - двухстадийный платеж (авторизация + подтверждение),
   * false - одностадийный платеж (сразу списание).
   * @default false
   */
  useDualMessagePayment?: boolean;

  /**
   * Отключение Apple Pay (опционально)
   * @description Если true, Apple Pay не будет отображаться в форме оплаты.
   * Полезно для тестирования или если Apple Pay не настроен.
   * @default false
   * @platform ios
   */
  disableApplePay?: boolean;

  /**
   * Merchant ID для Apple Pay (опционально)
   * @description Идентификатор мерчанта для Apple Pay из Apple Developer Console.
   * Обязателен для работы Apple Pay. Формат: merchant.com.yourcompany.appname
   * @example 'merchant.com.example.app'
   * @platform ios
   */
  applePayMerchantId?: string;

  /**
   * URL для редиректа при успешном платеже (опционально)
   * @description Адрес, на который будет перенаправлен пользователь
   * после успешного завершения платежа в веб-форме.
   * @example 'https://example.com/payment/success'
   */
  successRedirectUrl?: string;

  /**
   * URL для редиректа при неудачном платеже (опционально)
   * @description Адрес, на который будет перенаправлен пользователь
   * при ошибке или отмене платежа в веб-форме.
   * @example 'https://example.com/payment/fail'
   */
  failRedirectUrl?: string;

  /**
   * Режим сохранения карт для разовых платежей (опционально)
   * @description Если true, пользователь сможет сохранить карту
   * для будущих платежей даже при разовой оплате.
   * @default false
   */
  saveCardSinglePaymentMode?: boolean;

  /**
   * Показ экрана с результатом платежа (опционально)
   * @description Если true, после завершения платежа будет показан
   * экран с результатом операции перед закрытием формы.
   * @default true
   */
  showResultScreen?: boolean;
}

/**
 * Полные данные платежа, объединяющие базовую информацию и конфигурацию
 *
 * @description Основной интерфейс для передачи данных в методы платежной формы.
 * Содержит всю необходимую информацию для проведения платежа и настройки UI.
 *
 * @example Создание полных данных платежа
 * ```typescript
 * import { IPaymentData, PaymentService } from '@lmapp/react-native-cloudpayments';
 *
 * const paymentData: IPaymentData = {
 *   // Базовые данные платежа
 *   publicId: 'pk_test_1234567890abcdef',
 *   amount: '1500.00',
 *   currency: 'RUB',
 *   description: 'Подписка Premium на 1 месяц',
 *   email: 'user@example.com',
 *   accountId: 'user_456',
 *
 *   // Конфигурация формы
 *   requireEmail: true,
 *   disableApplePay: false,
 *   applePayMerchantId: 'merchant.com.myapp.payments',
 *   showResultScreen: true,
 *   saveCardSinglePaymentMode: true
 * };
 *
 * // Запуск платежной формы
 * try {
 *   const result = await PaymentService.presentPaymentForm(paymentData);
 *   console.log('Платеж успешен:', result.transactionId);
 * } catch (error) {
 *   console.log('Ошибка платежа:', error.message);
 * }
 * ```
 *
 * @since 1.0.0
 */
export interface IPaymentData
  extends IBasePaymentData,
    IPaymentConfigurationData {}

/**
 * Данные для создания платежного намерения (Intent)
 *
 * @description Специальный тип данных для создания Intent - предварительного
 * платежного намерения, которое используется для альтернативных способов оплаты
 * (TPay, СБП, SberPay). Intent создается до начала платежа и содержит всю
 * необходимую информацию для его завершения.
 *
 * @example Создание Intent для TPay
 * ```typescript
 * import { ICreateIntentPaymentData, PaymentService, EPaymentMethodType } from '@lmapp/react-native-cloudpayments';
 *
 * const intentData: ICreateIntentPaymentData = {
 *   publicId: 'pk_test_1234567890abcdef',
 *   amount: '2500.00',
 *   currency: 'RUB',
 *   description: 'Оплата через Tinkoff Pay',
 *   email: 'customer@example.com',
 *   accountId: 'customer_789'
 * };
 *
 * try {
 *   // Создаем Intent
 *   const intent = await PaymentService.createIntent(intentData);
 *   console.log('Intent создан:', intent.id);
 *
 *   // Запускаем оплату через TPay
 *   const result = await PaymentService.getIntentWaitStatus(
 *     intentData,
 *     EPaymentMethodType.TPAY
 *   );
 *   console.log('Статус платежа:', result.status);
 * } catch (error) {
 *   console.log('Ошибка создания Intent:', error.message);
 * }
 * ```
 *
 * @example Создание Intent для СБП
 * ```typescript
 * const sbpIntentData: ICreateIntentPaymentData = {
 *   publicId: 'pk_test_1234567890abcdef',
 *   amount: '750.50',
 *   currency: 'RUB',
 *   description: 'Быстрая оплата через СБП',
 *   email: 'user@domain.ru'
 * };
 *
 * const sbpResult = await PaymentService.getIntentWaitStatus(
 *   sbpIntentData,
 *   EPaymentMethodType.SBP
 * );
 * ```
 *
 * @see {@link https://developers.cloudpayments.ru/#intent} Документация по Intent API
 * @since 1.0.0
 */
export interface ICreateIntentPaymentData
  extends IBasePaymentData,
    IPaymentConfigurationData {}

// ============================================================================
// CARDIO CONFIGURATION INTERFACE
// ============================================================================

/**
 * Конфигурация CardIO сканера банковских карт
 *
 * @description Детальные настройки поведения и внешнего вида CardIO сканера.
 * Позволяет настроить требования к полям карты, цветовую схему, локализацию
 * и дополнительные параметры интерфейса.
 *
 * @example Базовая конфигурация
 * ```typescript
 * import { ECardIOLanguage, ECardIOColorScheme } from '@lmapp/react-native-cloudpayments';
 *
 * const cardScannerConfig: ICardIOConfig = {
 *   requireExpiry: true,
 *   requireCVV: false,
 *   hideCardIOLogo: true,
 *   actionBarColor: ECardIOColorScheme.MATERIAL_BLUE,
 *   guideColor: ECardIOColorScheme.MATERIAL_GREEN,
 *   language: ECardIOLanguage.RUSSIAN
 * };
 * ```
 *
 * @example Расширенная конфигурация
 * ```typescript
 * const advancedConfig: ICardIOConfig = {
 *   // Поля карты
 *   requireExpiry: true,
 *   requireCVV: false,
 *   requirePostalCode: false,
 *   requireCardholderName: true,
 *
 *   // Интерфейс
 *   hideCardIOLogo: true,
 *   usePayPalLogo: false,
 *   suppressManualEntry: false,
 *
 *   // Цвета
 *   actionBarColor: '#1976D2',
 *   guideColor: ECardIOColorScheme.MATERIAL_GREEN,
 *
 *   // Локализация
 *   language: ECardIOLanguage.RUSSIAN,
 *
 *   // Дополнительно
 *   suppressConfirmation: false,
 *   suppressScan: false,
 *   keepApplicationTheme: true
 * };
 * ```
 *
 * @since 1.0.0
 * @platform android
 */
export interface ICardIOConfig {
  // ============================================================================
  // ПОЛЯ КАРТЫ
  // ============================================================================

  /**
   * Требовать ввод срока действия карты
   *
   * @description Если `true`, пользователь должен будет ввести месяц и год истечения карты.
   * Рекомендуется оставлять `true` для полной валидации карты.
   *
   * @default true
   * @example true
   */
  requireExpiry?: boolean;

  /**
   * Требовать ввод CVV кода
   *
   * @description Если `true`, пользователь должен будет ввести CVV код карты.
   *
   * ⚠️ **НЕ РЕКОМЕНДУЕТСЯ** для безопасности - CVV лучше вводить отдельно
   * в защищенной форме платежа.
   *
   * @default false
   * @example false
   */
  requireCVV?: boolean;

  /**
   * Требовать ввод почтового индекса
   *
   * @description Если `true`, пользователь должен будет ввести почтовый индекс.
   * Используется для дополнительной верификации в некоторых странах.
   *
   * @default false
   * @example false
   */
  requirePostalCode?: boolean;

  /**
   * Требовать ввод имени держателя карты
   *
   * @description Если `true`, пользователь должен будет ввести имя на карте.
   * Полезно для дополнительной идентификации плательщика.
   *
   * @default false
   * @example true
   */
  requireCardholderName?: boolean;

  // ============================================================================
  // НАСТРОЙКИ ИНТЕРФЕЙСА
  // ============================================================================

  /**
   * Скрыть логотип CardIO
   *
   * @description Если `true`, логотип CardIO не будет отображаться в верхней части экрана.
   * Рекомендуется для брендинга собственного приложения.
   *
   * @default true
   * @example true
   */
  hideCardIOLogo?: boolean;

  /**
   * Использовать логотип PayPal
   *
   * @description Если `true`, будет показан логотип PayPal вместо CardIO.
   * Используется если приложение интегрировано с PayPal экосистемой.
   *
   * @default false
   * @example false
   */
  usePayPalLogo?: boolean;

  /**
   * Скрыть кнопку ручного ввода
   *
   * @description Если `true`, кнопка "Ввести вручную" не будет отображаться.
   * Принуждает пользователя использовать только сканирование.
   *
   * @default false
   * @example false
   */
  suppressManualEntry?: boolean;

  // ============================================================================
  // ЦВЕТОВАЯ СХЕМА
  // ============================================================================

  /**
   * Цвет ActionBar (заголовка)
   *
   * @description Цвет для верхней панели сканера.
   * Можно использовать предустановленные цвета из ECardIOColorScheme или кастомный hex-код.
   *
   * @example ECardIOColorScheme.MATERIAL_BLUE
   * @example '#2196F3'
   */
  actionBarColor?: ECardIOColorScheme | string;

  /**
   * Цвет рамки сканирования
   *
   * @description Цвет для рамки вокруг области сканирования карты.
   * Яркие цвета (зеленый, синий) лучше видны на камере.
   * Можно использовать предустановленные цвета из ECardIOColorScheme или кастомный hex-код.
   *
   * @example ECardIOColorScheme.MATERIAL_GREEN
   * @example '#4CAF50'
   */
  guideColor?: ECardIOColorScheme | string;

  // ============================================================================
  // ЛОКАЛИЗАЦИЯ
  // ============================================================================

  /**
   * Язык интерфейса
   *
   * @description Код языка для интерфейса CardIO.
   * Если не указан, используется язык устройства.
   * Можно использовать значения из ECardIOLanguage или строковый код языка.
   *
   * @example ECardIOLanguage.RUSSIAN
   * @example ECardIOLanguage.ENGLISH
   * @example 'ru'
   */
  language?: ECardIOLanguage | string;

  // ============================================================================
  // ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ
  // ============================================================================

  /**
   * Отключить вибрацию при сканировании
   *
   * @description Если `true`, устройство не будет вибрировать при успешном сканировании.
   * Полезно для тихих режимов или экономии батареи.
   *
   * @default false
   * @example false
   */
  suppressConfirmation?: boolean;

  /**
   * Отключить звук при сканировании
   *
   * @description Если `true`, звук сканирования будет отключен.
   * Рекомендуется для приложений с собственными звуковыми эффектами.
   *
   * @default false
   * @example false
   */
  suppressScan?: boolean;

  /**
   * Сохранить тему приложения
   *
   * @description Если `true`, CardIO будет использовать тему основного приложения
   * вместо собственной темы.
   *
   * @default false
   * @example true
   */
  keepApplicationTheme?: boolean;
}
