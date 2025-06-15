/**
 * @fileoverview Интерфейсы ответов API CloudPayments SDK
 * @description Содержит типы данных для ответов от CloudPayments API и внутренних операций SDK
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

import type { IBaseResponseWithTransaction, IBaseError } from './base';

// ============================================================================
// RESPONSE INTERFACES
// ============================================================================

/**
 * Информация о транзакции CloudPayments
 *
 * @description Базовая информация о созданной транзакции в системе CloudPayments.
 * Содержит уникальный идентификатор и может быть расширена дополнительными полями.
 *
 * @example Использование информации о транзакции
 * ```typescript
 * const handlePaymentSuccess = (transaction: ITransaction) => {
 *   console.log('Транзакция создана с ID:', transaction.id);
 *
 *   // Сохраняем ID для отслеживания
 *   await saveTransactionToDatabase({
 *     transactionId: transaction.id,
 *     timestamp: new Date(),
 *     status: 'completed'
 *   });
 * };
 * ```
 *
 * @since 1.0.0
 */
export interface ITransaction {
  /**
   * Уникальный идентификатор транзакции
   * @description Числовой ID транзакции в системе CloudPayments.
   * Используется для отслеживания статуса, возвратов и в отчетах.
   */
  id: number;
  // другие поля транзакции могут быть добавлены здесь
}

/**
 * Ответ при создании платежного намерения (Intent)
 *
 * @description Содержит полную информацию о созданном Intent, включая
 * уникальный идентификатор, сумму, статус и URL для оплаты.
 * Intent используется для альтернативных способов оплаты.
 *
 * @example Обработка созданного Intent
 * ```typescript
 * import { PaymentService } from '@lmapp/react-native-cloudpayments';
 *
 * const intent = await PaymentService.createIntent(paymentData);
 *
 * console.log('Intent создан:', intent.id);
 * console.log('Сумма:', intent.amount, intent.currency);
 * console.log('Статус:', intent.status);
 *
 * if (intent.paymentUrl) {
 *   console.log('Ссылка для оплаты:', intent.paymentUrl);
 *   // Можно открыть в браузере или показать QR-код
 * }
 *
 * if (intent.transaction) {
 *   console.log('Транзакция:', intent.transaction.id);
 * }
 * ```
 *
 * @example Проверка статуса Intent
 * ```typescript
 * const checkIntentStatus = (intent: IIntentResponse) => {
 *   switch (intent.status) {
 *     case 'Created':
 *       console.log('Intent создан, ожидает оплаты');
 *       break;
 *     case 'Succeeded':
 *       console.log('Оплата успешно завершена');
 *       break;
 *     case 'Failed':
 *       console.log('Оплата отклонена');
 *       break;
 *     default:
 *       console.log('Неизвестный статус:', intent.status);
 *   }
 * };
 * ```
 *
 * @see {@link https://developers.cloudpayments.ru/#intent} Документация по Intent API
 * @since 1.0.0
 */
export interface IIntentResponse {
  /**
   * Уникальный идентификатор Intent
   * @description Строковый ID платежного намерения в системе CloudPayments.
   * Используется для отслеживания статуса и связи с транзакциями.
   * @example 'intent_1234567890abcdef'
   */
  id: string;

  /**
   * Сумма платежа
   * @description Сумма в указанной валюте в числовом формате.
   * @example 1000.50
   */
  amount: number;

  /**
   * Валюта платежа
   * @description Трехбуквенный код валюты по стандарту ISO 4217.
   * @example 'RUB'
   */
  currency: string;

  /**
   * Статус платежного намерения
   * @description Текущее состояние Intent в процессе оплаты.
   * Возможные значения: 'Created', 'Pending', 'Succeeded', 'Failed', 'Cancelled'
   * @example 'Created'
   */
  status: string;

  /**
   * Описание платежа (опционально)
   * @description Назначение платежа, переданное при создании Intent.
   * @example 'Оплата заказа №12345'
   */
  description?: string;

  /**
   * URL для оплаты (опционально)
   * @description Ссылка для завершения оплаты через веб-интерфейс
   * или для генерации QR-кода. Доступна для некоторых способов оплаты.
   * @example 'https://checkout.cloudpayments.ru/pay/intent_123'
   */
  paymentUrl?: string;

  /**
   * Информация о связанной транзакции (опционально)
   * @description Данные о транзакции, если она была создана.
   * Появляется после успешного завершения платежа.
   */
  transaction?: ITransaction;
}

/**
 * Ответ API при оплате через Intent с криптограммой
 *
 * @description Специальный ответ для операций оплаты через Intent
 * с использованием криптограммы карты. Содержит HTTP статус код
 * и детальную информацию об операции.
 *
 * @example Обработка ответа API оплаты
 * ```typescript
 * const apiResponse: IIntentApiPayResponse = await apiPayWithCryptogram(
 *   paymentData,
 *   cryptogram,
 *   intentId
 * );
 *
 * if (apiResponse.statusCode === 200) {
 *   console.log('Оплата успешна');
 *   console.log('Intent ID:', apiResponse.response.id);
 *   console.log('Статус:', apiResponse.response.status);
 * } else {
 *   console.log('Ошибка оплаты, код:', apiResponse.statusCode);
 * }
 * ```
 *
 * @since 1.0.0
 */
export interface IIntentApiPayResponse {
  /**
   * HTTP статус код ответа
   * @description Стандартный HTTP код результата операции.
   * 200 - успех, 400 - ошибка запроса, 500 - ошибка сервера.
   * @example 200
   */
  statusCode: number;

  /**
   * Детальная информация об Intent
   * @description Полные данные о платежном намерении после операции оплаты.
   */
  response: IIntentResponse;
}

/**
 * Информация о банке-эмитенте карты
 *
 * @description Содержит данные о банке, выпустившем карту,
 * включая название и логотип. Получается по BIN номера карты.
 *
 * @example Отображение информации о банке
 * ```typescript
 * import { CardService } from '@lmapp/react-native-cloudpayments';
 *
 * const bankInfo = await CardService.getBankInfo('4111111111111111');
 *
 * console.log('Банк-эмитент:', bankInfo.bankName);
 *
 * if (bankInfo.logoUrl) {
 *   // Показываем логотип банка в UI
 *   setBankLogo(bankInfo.logoUrl);
 * }
 * ```
 *
 * @example Использование в компоненте React Native
 * ```typescript
 * const [bankInfo, setBankInfo] = useState<IBankInfo | null>(null);
 *
 * const handleCardInput = async (cardNumber: string) => {
 *   if (cardNumber.length >= 6) {
 *     try {
 *       const info = await CardService.getBankInfo(cardNumber);
 *       setBankInfo(info);
 *     } catch (error) {
 *       setBankInfo(null);
 *     }
 *   }
 * };
 *
 * return (
 *   <View>
 *     <TextInput onChangeText={handleCardInput} placeholder="Номер карты" />
 *     {bankInfo && (
 *       <View style={styles.bankInfo}>
 *         <Text>{bankInfo.bankName}</Text>
 *         {bankInfo.logoUrl && (
 *           <Image source={{ uri: bankInfo.logoUrl }} style={styles.logo} />
 *         )}
 *       </View>
 *     )}
 *   </View>
 * );
 * ```
 *
 * @since 1.0.0
 */
export interface IBankInfo {
  /**
   * Название банка-эмитента
   * @description Полное или сокращенное название банка на русском языке.
   * @example 'Сбербанк России', 'Тинькофф Банк', 'ВТБ'
   */
  bankName: string;

  /**
   * URL логотипа банка (опционально)
   * @description Ссылка на изображение логотипа банка.
   * Может отсутствовать для некоторых банков или при ошибке определения.
   * @example 'https://static.cloudpayments.ru/banks/sberbank.png'
   */
  logoUrl?: string;
}

/**
 * Ответ платежной формы CloudPayments
 *
 * @description Стандартный ответ от платежной формы, наследующий базовую
 * структуру с информацией о транзакции. Используется как основной тип
 * для результатов операций платежной формы.
 *
 * @example Обработка ответа платежной формы
 * ```typescript
 * import { PaymentService } from '@lmapp/react-native-cloudpayments';
 *
 * try {
 *   const result: IPaymentFormResponse = await PaymentService.presentPaymentForm(paymentData);
 *
 *   if (result.success) {
 *     console.log('Платеж успешен!');
 *     console.log('Сообщение:', result.message);
 *
 *     if (result.transactionId) {
 *       console.log('ID транзакции:', result.transactionId);
 *       // Сохраняем или отправляем на сервер
 *       await processSuccessfulPayment(result.transactionId);
 *     }
 *   } else {
 *     console.log('Платеж не удался:', result.message);
 *   }
 * } catch (error) {
 *   console.log('Ошибка при оплате:', error);
 * }
 * ```
 *
 * @since 1.0.0
 */
export interface IPaymentFormResponse extends IBaseResponseWithTransaction {}

/**
 * Успешный ответ платежной формы
 *
 * @description Специализированный тип для гарантированно успешных операций.
 * Содержит флаг success: true и обязательную информацию о транзакции.
 *
 * @example Типизированная обработка успешного платежа
 * ```typescript
 * const handleSuccessfulPayment = (response: IPaymentFormSuccessResponse) => {
 *   // TypeScript гарантирует, что success === true
 *   console.log('Платеж точно успешен');
 *   console.log('Сообщение:', response.message);
 *
 *   // transactionId может быть undefined, но success точно true
 *   if (response.transactionId) {
 *     sendAnalytics('payment_success', {
 *       transactionId: response.transactionId,
 *       timestamp: Date.now()
 *     });
 *   }
 * };
 * ```
 *
 * @since 1.0.0
 */
export interface IPaymentFormSuccessResponse
  extends IBaseResponseWithTransaction {
  /**
   * Флаг успешности операции
   * @description Всегда true для этого типа ответа.
   */
  success: true;
}

/**
 * Ошибка платежной формы
 *
 * @description Специализированный тип для ошибок платежной формы,
 * наследующий базовую структуру ошибок с кодом и сообщением.
 *
 * @example Обработка ошибок платежной формы
 * ```typescript
 * import { EPaymentFormErrorCode } from '@lmapp/react-native-cloudpayments';
 *
 * const handlePaymentError = (error: IPaymentFormError) => {
 *   console.log('Код ошибки:', error.code);
 *   console.log('Сообщение:', error.message);
 *
 *   switch (error.code) {
 *     case EPaymentFormErrorCode.PAYMENT_FAILED:
 *       showUserMessage('Платеж отклонен банком', 'error');
 *       break;
 *     case EPaymentFormErrorCode.CONFIGURATION_ERROR:
 *       showUserMessage('Ошибка настройки приложения', 'error');
 *       logError('Payment configuration error', error);
 *       break;
 *     default:
 *       showUserMessage('Произошла ошибка при оплате', 'error');
 *   }
 * };
 * ```
 *
 * @since 1.0.0
 */
export interface IPaymentFormError extends IBaseError {}

/**
 * Ответ с публичным ключом для шифрования
 *
 * @description Содержит публичный ключ CloudPayments в формате PEM
 * и его версию. Используется для шифрования данных карты при создании
 * криптограммы для безопасной передачи на сервер.
 *
 * @example Получение и использование публичного ключа
 * ```typescript
 * import { CardService } from '@lmapp/react-native-cloudpayments';
 *
 * const publicKey = await CardService.getPublicKey();
 *
 * console.log('Версия ключа:', publicKey.Version);
 * console.log('PEM ключ получен, длина:', publicKey.Pem.length);
 *
 * // Используем для создания криптограммы
 * const cryptogram = await createCardCryptogram({
 *   cardNumber: '4111111111111111',
 *   expDate: '12/25',
 *   cvv: '123'
 * }, publicKey.Pem, publicKey.Version);
 * ```
 *
 * @example Кэширование публичного ключа
 * ```typescript
 * let cachedPublicKey: IPublicKeyResponse | null = null;
 * let keyExpirationTime = 0;
 *
 * const getPublicKey = async (): Promise<IPublicKeyResponse> => {
 *   const now = Date.now();
 *
 *   // Кэшируем ключ на 1 час
 *   if (cachedPublicKey && now < keyExpirationTime) {
 *     return cachedPublicKey;
 *   }
 *
 *   cachedPublicKey = await CardService.getPublicKey();
 *   keyExpirationTime = now + 60 * 60 * 1000; // 1 час
 *
 *   return cachedPublicKey;
 * };
 * ```
 *
 * @see {@link https://developers.cloudpayments.ru/#kriptogramma-karty} Документация по криптограммам
 * @since 1.0.0
 */
export interface IPublicKeyResponse {
  /**
   * Публичный ключ в формате PEM
   * @description RSA публичный ключ для шифрования данных карты.
   * Представлен в стандартном формате PEM (Privacy-Enhanced Mail).
   * @example '-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEF...'
   */
  Pem: string;

  /**
   * Версия публичного ключа
   * @description Числовая версия ключа для отслеживания обновлений.
   * Увеличивается при ротации ключей CloudPayments.
   * @example 1
   */
  Version: number;
}
