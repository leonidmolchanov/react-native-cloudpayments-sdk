/**
 * @fileoverview Интерфейсы данных платежей для CloudPayments SDK
 * @description Содержит типы данных для настройки и выполнения платежей
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

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

  enableCardScanner?:boolean
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
