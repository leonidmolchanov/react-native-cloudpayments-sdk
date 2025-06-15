/**
 * @fileoverview Интерфейсы сервисов CloudPayments SDK
 * @description Содержит типы для основных сервисов SDK: платежей и работы с картами
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import type {
  IPaymentData,
  ICreateIntentPaymentData,
  IPaymentFormResponse,
  IIntentResponse,
  IBankInfo,
  IPublicKeyResponse,
  EPaymentMethodType,
} from './index';

// ============================================================================
// SERVICE INTERFACES
// ============================================================================

/**
 * Интерфейс сервиса для работы с банковскими картами
 *
 * @description Сервис для валидации карт, получения информации о банке-эмитенте
 * и работы с публичными ключами для шифрования данных карт. Используется
 * при создании собственных платежных форм или для дополнительной валидации.
 *
 * @example Базовое использование CardService
 * ```typescript
 * import { CardService } from '@lmapp/react-native-cloudpayments';
 *
 * // Проверка корректности номера карты
 * const isValid = CardService.isValidCardNumber('4111111111111111');
 * console.log('Карта валидна:', isValid);
 *
 * // Получение информации о банке
 * const bankInfo = await CardService.getBankInfo('4111111111111111');
 * console.log('Банк:', bankInfo.bankName);
 * ```
 *
 * @example Работа с публичными ключами
 * ```typescript
 * // Получение публичного ключа для шифрования
 * const publicKey = await CardService.getPublicKey();
 * console.log('Версия ключа:', publicKey.Version);
 *
 * // Создание криптограммы (требует дополнительной реализации)
 * const cryptogram = createCryptogram(cardData, publicKey.Pem);
 * ```
 *
 * @since 1.0.0
 */
export interface ICardService {
  /**
   * Проверка корректности номера банковской карты
   *
   * @description Валидирует номер карты с помощью алгоритма Луна (Luhn algorithm).
   * Проверяет только математическую корректность номера, не гарантирует
   * существование карты или возможность проведения платежа.
   *
   * @param cardNumber - Номер карты (может содержать пробелы и дефисы)
   * @returns Promise с результатом валидации
   *
   * @example Валидация различных форматов номеров
   * ```typescript
   * // Номер без пробелов
   * const isValid1 = await CardService.isValidCardNumber('4111111111111111');
   * console.log('Visa:', isValid1); // true
   *
   * // Номер с пробелами
   * const isValid2 = await CardService.isValidCardNumber('4111 1111 1111 1111');
   * console.log('Visa с пробелами:', isValid2); // true
   *
   * // Номер с дефисами
   * const isValid3 = await CardService.isValidCardNumber('4111-1111-1111-1111');
   * console.log('Visa с дефисами:', isValid3); // true
   *
   * // Некорректный номер
   * const isValid4 = await CardService.isValidCardNumber('1234567890123456');
   * console.log('Некорректный:', isValid4); // false
   * ```
   *
   * @example Валидация в реальном времени
   * ```typescript
   * const handleCardNumberChange = async (cardNumber: string) => {
   *   const isValid = await CardService.isValidCardNumber(cardNumber);
   *   setCardNumberValid(isValid);
   *
   *   if (isValid) {
   *     // Можно получить информацию о банке
   *     CardService.getBankInfo(cardNumber).then(bankInfo => {
   *       setBankName(bankInfo.bankName);
   *     });
   *   }
   * };
   * ```
   *
   * @since 1.0.0
   */
  isValidCardNumber(cardNumber: string): Promise<boolean>;

  /**
   * Получение информации о банке-эмитенте карты
   *
   * @description Определяет банк-эмитент по номеру карты и возвращает
   * информацию о банке, включая название и логотип. Использует BIN
   * (Bank Identification Number) - первые 6-8 цифр номера карты.
   *
   * @param cardNumber - Номер банковской карты
   * @returns Promise с информацией о банке-эмитенте
   *
   * @example Получение информации о банке
   * ```typescript
   * const bankInfo = await CardService.getBankInfo('4111111111111111');
   * console.log('Банк:', bankInfo.bankName);
   * if (bankInfo.logoUrl) {
   *   console.log('Логотип:', bankInfo.logoUrl);
   * }
   * ```
   *
   * @example Отображение логотипа банка в UI
   * ```typescript
   * const [bankInfo, setBankInfo] = useState<IBankInfo | null>(null);
   *
   * const handleCardNumberChange = async (cardNumber: string) => {
   *   if (CardService.isValidCardNumber(cardNumber)) {
   *     try {
   *       const info = await CardService.getBankInfo(cardNumber);
   *       setBankInfo(info);
   *     } catch (error) {
   *       console.log('Не удалось определить банк');
   *       setBankInfo(null);
   *     }
   *   }
   * };
   *
   * // В JSX
   * {bankInfo && (
   *   <View>
   *     <Text>{bankInfo.bankName}</Text>
   *     {bankInfo.logoUrl && <Image source={{ uri: bankInfo.logoUrl }} />}
   *   </View>
   * )}
   * ```
   *
   * @throws {Error} Если не удалось определить банк или номер карты некорректен
   * @since 1.0.0
   */
  getBankInfo(cardNumber: string): Promise<IBankInfo>;

  /**
   * Получение публичного ключа для шифрования данных карты
   *
   * @description Возвращает актуальный публичный ключ CloudPayments для
   * шифрования чувствительных данных карты (номер, CVV, срок действия).
   * Ключ используется для создания криптограммы при отправке данных на сервер.
   *
   * @returns Promise с публичным ключом и его версией
   *
   * @example Получение публичного ключа
   * ```typescript
   * const publicKey = await CardService.getPublicKey();
   * console.log('PEM ключ:', publicKey.Pem);
   * console.log('Версия:', publicKey.Version);
   * ```
   *
   * @example Использование для создания криптограммы
   * ```typescript
   * // Получаем публичный ключ
   * const publicKey = await CardService.getPublicKey();
   *
   * // Подготавливаем данные карты
   * const cardData = {
   *   cardNumber: '4111111111111111',
   *   expDate: '12/25',
   *   cvv: '123',
   *   cardHolderName: 'JOHN DOE'
   * };
   *
   * // Создаем криптограмму (требует дополнительной библиотеки для RSA)
   * const cryptogram = createCryptogram(cardData, publicKey.Pem);
   *
   * // Отправляем криптограмму на сервер
   * const paymentResult = await sendPaymentToServer({
   *   cryptogram,
   *   amount: '1000.00',
   *   currency: 'RUB'
   * });
   * ```
   *
   * @throws {Error} Если не удалось получить публичный ключ
   * @see {@link https://developers.cloudpayments.ru/#kriptogramma-karty} Документация по криптограммам
   * @since 1.0.0
   */
  getPublicKey(): Promise<IPublicKeyResponse>;
}

/**
 * Интерфейс сервиса для работы с платежами
 *
 * @description Основной сервис CloudPayments SDK, предоставляющий методы для
 * проведения платежей, создания Intent'ов и работы с альтернативными способами оплаты.
 * Все методы асинхронные и возвращают Promise с соответствующими типами данных.
 *
 * @example Базовое использование PaymentService
 * ```typescript
 * import { PaymentService, IPaymentData } from '@lmapp/react-native-cloudpayments';
 *
 * // Инициализация SDK
 * await PaymentService.init('pk_test_1234567890abcdef');
 *
 * // Создание данных платежа
 * const paymentData: IPaymentData = {
 *   publicId: 'pk_test_1234567890abcdef',
 *   amount: '1000.00',
 *   currency: 'RUB',
 *   description: 'Тестовый платеж',
 *   email: 'test@example.com'
 * };
 *
 * // Запуск платежной формы
 * try {
 *   const result = await PaymentService.presentPaymentForm(paymentData);
 *   console.log('Платеж успешен:', result.transactionId);
 * } catch (error) {
 *   console.log('Ошибка:', error.message);
 * }
 * ```
 *
 * @example Работа с альтернативными способами оплаты
 * ```typescript
 * import { EPaymentMethodType } from '@lmapp/react-native-cloudpayments';
 *
 * // Оплата через Tinkoff Pay
 * const tpayResult = await PaymentService.getIntentWaitStatus(
 *   paymentData,
 *   EPaymentMethodType.TPAY
 * );
 *
 * // Оплата через СБП
 * const sbpResult = await PaymentService.getIntentWaitStatus(
 *   paymentData,
 *   EPaymentMethodType.SBP
 * );
 * ```
 *
 * @since 1.0.0
 */
export interface IPaymentService {
  /**
   * Инициализация CloudPayments SDK
   *
   * @description Обязательный метод для инициализации SDK с вашим Public ID.
   * Должен быть вызван перед использованием любых других методов SDK.
   * Public ID можно получить в личном кабинете CloudPayments.
   *
   * @param publicId - Публичный идентификатор мерчанта из личного кабинета
   *
   * @example Инициализация SDK
   * ```typescript
   * // Для тестового режима
   * await PaymentService.init('pk_test_1234567890abcdef');
   *
   * // Для боевого режима
   * await PaymentService.init('pk_live_1234567890abcdef');
   * ```
   *
   * @throws {Error} Если Public ID некорректен или недоступен
   * @since 1.0.0
   */
  init(publicId: string): Promise<void>;

  /**
   * Отображение стандартной платежной формы CloudPayments
   *
   * @description Показывает встроенную платежную форму CloudPayments с поддержкой
   * всех способов оплаты: банковские карты, Apple Pay, Google Pay и другие.
   * Форма автоматически адаптируется под платформу и настройки мерчанта.
   *
   * @param paymentData - Данные платежа и конфигурация формы
   * @returns Promise с результатом платежа, включая ID транзакции при успехе
   *
   * @example Простой платеж
   * ```typescript
   * const paymentData: IPaymentData = {
   *   publicId: 'pk_test_1234567890abcdef',
   *   amount: '500.00',
   *   currency: 'RUB',
   *   description: 'Покупка товара'
   * };
   *
   * const result = await PaymentService.presentPaymentForm(paymentData);
   * if (result.success) {
   *   console.log('Транзакция:', result.transactionId);
   * }
   * ```
   *
   * @example Платеж с дополнительными настройками
   * ```typescript
   * const paymentData: IPaymentData = {
   *   publicId: 'pk_test_1234567890abcdef',
   *   amount: '1500.00',
   *   currency: 'RUB',
   *   description: 'Подписка Premium',
   *   email: 'user@example.com',
   *   requireEmail: true,
   *   applePayMerchantId: 'merchant.com.myapp.payments',
   *   showResultScreen: true
   * };
   *
   * const result = await PaymentService.presentPaymentForm(paymentData);
   * ```
   *
   * @throws {Error} Если данные платежа некорректны или произошла ошибка
   * @since 1.0.0
   */
  presentPaymentForm(paymentData: IPaymentData): Promise<IPaymentFormResponse>;

  /**
   * Создание платежного намерения (Intent)
   *
   * @description Создает Intent - предварительное платежное намерение, которое
   * используется для альтернативных способов оплаты (TPay, СБП, SberPay).
   * Intent содержит всю информацию о платеже и может быть использован
   * для генерации ссылок или QR-кодов.
   *
   * @param paymentData - Данные для создания Intent
   * @returns Promise с информацией о созданном Intent
   *
   * @example Создание Intent
   * ```typescript
   * const intentData: ICreateIntentPaymentData = {
   *   publicId: 'pk_test_1234567890abcdef',
   *   amount: '2000.00',
   *   currency: 'RUB',
   *   description: 'Оплата заказа №12345',
   *   email: 'customer@example.com'
   * };
   *
   * const intent = await PaymentService.createIntent(intentData);
   * console.log('Intent ID:', intent.id);
   * console.log('Статус:', intent.status);
   * console.log('URL для оплаты:', intent.paymentUrl);
   * ```
   *
   * @throws {Error} Если не удалось создать Intent
   * @see {@link https://developers.cloudpayments.ru/#intent} Документация по Intent API
   * @since 1.0.0
   */
  createIntent(paymentData: ICreateIntentPaymentData): Promise<IIntentResponse>;

  /**
   * Ожидание статуса Intent с запуском альтернативного способа оплаты
   *
   * @description Комбинированный метод, который создает Intent, запускает
   * выбранный способ оплаты (TPay, СБП, SberPay) и ожидает завершения платежа.
   * Автоматически обрабатывает переходы в приложения банков и отслеживает статус.
   *
   * @param paymentData - Данные платежа
   * @param paymentMethod - Способ оплаты из перечисления EPaymentMethodType
   * @returns Promise с результатом платежа
   *
   * @example Оплата через Tinkoff Pay
   * ```typescript
   * import { EPaymentMethodType } from '@lmapp/react-native-cloudpayments';
   *
   * const result = await PaymentService.getIntentWaitStatus(
   *   paymentData,
   *   EPaymentMethodType.TPAY
   * );
   *
   * if (result.success) {
   *   console.log('Оплата через TPay успешна');
   * }
   * ```
   *
   * @example Оплата через СБП
   * ```typescript
   * const sbpResult = await PaymentService.getIntentWaitStatus(
   *   paymentData,
   *   EPaymentMethodType.SBP
   * );
   *
   * if (sbpResult.success) {
   *   console.log('Оплата через СБП успешна');
   * }
   * ```
   *
   * @example Оплата через SberPay
   * ```typescript
   * const sberResult = await PaymentService.getIntentWaitStatus(
   *   paymentData,
   *   EPaymentMethodType.SBERPAY
   * );
   * ```
   *
   * @throws {Error} Если способ оплаты не поддерживается или произошла ошибка
   * @since 1.0.0
   */
  getIntentWaitStatus(
    paymentData: ICreateIntentPaymentData,
    paymentMethod: EPaymentMethodType
  ): Promise<IPaymentFormResponse>;
}
