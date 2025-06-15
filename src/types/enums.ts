/**
 * @fileoverview Перечисления (Enums) для CloudPayments SDK
 * @description Содержит все enum'ы, используемые в CloudPayments SDK для React Native
 * @author CloudPayments SDK Team
 * @since 1.0.0
 */

// ============================================================================
// ENUMS
// ============================================================================

/**
 * Поддерживаемые способы оплаты в CloudPayments SDK
 *
 * @description Перечисление доступных методов оплаты, которые можно использовать
 * для проведения платежей через CloudPayments. Каждый способ требует
 * предварительного подключения в личном кабинете CloudPayments.
 *
 * @example Использование способов оплаты
 * ```typescript
 * import { EPaymentMethodType } from '@lmapp/react-native-cloudpayments';
 *
 * // Проверка статуса для разных способов оплаты
 * const tpayStatus = await PaymentService.getIntentWaitStatus(
 *   paymentData,
 *   EPaymentMethodType.TPAY
 * );
 *
 * const sbpStatus = await PaymentService.getIntentWaitStatus(
 *   paymentData,
 *   EPaymentMethodType.SBP
 * );
 * ```
 *
 * @see {@link https://merchant.cloudpayments.ru/} Личный кабинет CloudPayments
 * @see {@link https://developers.cloudpayments.ru/#sposoby-oplaty} Документация по способам оплаты
 * @since 1.0.0
 */
export enum EPaymentMethodType {
  /**
   * Tinkoff Pay - быстрая оплата через приложение Тинькофф Банка
   * @description Позволяет клиентам оплачивать покупки через мобильное
   * приложение Тинькофф Банка без ввода данных карты. Требует установленного
   * приложения Тинькофф Банка на устройстве пользователя.
   */
  TPAY = 'tpay',

  /**
   * Tinkoff Pay (альтернативное название)
   * @description Альтернативный идентификатор для Tinkoff Pay.
   * Используется для обратной совместимости.
   */
  TINKOFFPAY = 'tinkoffpay',

  /**
   * Система быстрых платежей (СБП)
   * @description Мгновенные переводы между банками России по номеру телефона
   * через QR-код или ссылку. Работает 24/7 без комиссии для физических лиц.
   * Поддерживается большинством российских банков.
   */
  SBP = 'sbp',

  /**
   * Sberbank Pay - оплата через приложение Сбербанка
   * @description Быстрая оплата через мобильное приложение Сбербанк Онлайн
   * без ввода данных карты. Требует установленного приложения Сбербанк Онлайн.
   */
  SBERPAY = 'sberpay',
}

/**
 * Коды ошибок, которые может возвращать платежная форма CloudPayments
 *
 * @description Перечисление всех возможных ошибок, которые могут возникнуть
 * при работе с CloudPayments SDK. Используйте эти коды для обработки
 * различных сценариев ошибок в вашем приложении.
 *
 * @example Обработка ошибок платежа
 * ```typescript
 * import { PaymentService, EPaymentFormErrorCode } from '@lmapp/react-native-cloudpayments';
 *
 * try {
 *   const result = await PaymentService.presentPaymentForm(paymentData);
 * } catch (error) {
 *   switch (error.code) {
 *     case EPaymentFormErrorCode.CONFIGURATION_ERROR:
 *       console.log('Ошибка конфигурации SDK');
 *       // Проверьте правильность инициализации
 *       break;
 *     case EPaymentFormErrorCode.MERCHANT_CONFIG_ERROR:
 *       console.log('Неверный Public ID или мерчант заблокирован');
 *       break;
 *     case EPaymentFormErrorCode.PAYMENT_FAILED:
 *       console.log('Платеж отклонен банком');
 *       break;
 *     default:
 *       console.log('Неизвестная ошибка:', error.message);
 *   }
 * }
 * ```
 *
 * @since 1.0.0
 */
export enum EPaymentFormErrorCode {
  /**
   * Ошибка конфигурации платежной формы
   * @description Неверно указаны параметры инициализации SDK или отсутствуют
   * обязательные поля в PaymentData. Проверьте корректность всех параметров.
   */
  CONFIGURATION_ERROR = 'CONFIGURATION_ERROR',

  /**
   * Ошибка создания платежного намерения (Intent)
   * @description Не удалось создать Intent на стороне CloudPayments.
   * Проверьте корректность данных платежа и доступность сервиса.
   */
  CREATE_INTENT_ERROR = 'CREATE_INTENT_ERROR',

  /**
   * Ошибка генерации ссылки для Tinkoff Pay
   * @description Не удалось сформировать ссылку для перехода в приложение
   * Тинькофф Банка. Убедитесь, что TPay подключен в личном кабинете.
   */
  TPAY_LINK_ERROR = 'TPAY_LINK_ERROR',

  /**
   * Ошибка генерации ссылки для СБП
   * @description Не удалось создать QR-код или ссылку для оплаты через
   * Систему быстрых платежей. Проверьте подключение СБП в настройках.
   */
  SBP_LINK_ERROR = 'SBP_LINK_ERROR',

  /**
   * Ошибка генерации ссылки для Sberbank Pay
   * @description Не удалось сформировать ссылку для перехода в приложение
   * Сбербанк Онлайн. Проверьте настройки SberPay.
   */
  SBERPAY_LINK_ERROR = 'SBERPAY_LINK_ERROR',

  /**
   * Ошибка конфигурации мерчанта
   * @description Неверный Public ID или мерчант заблокирован.
   * Проверьте настройки в личном кабинете CloudPayments.
   */
  MERCHANT_CONFIG_ERROR = 'MERCHANT_CONFIG_ERROR',

  /**
   * Неподдерживаемый тип платежа
   * @description Указанный способ оплаты не подключен или недоступен
   * для данного мерчанта. Обратитесь к менеджеру CloudPayments.
   */
  INVALID_PAYMENT_TYPE = 'INVALID_PAYMENT_TYPE',

  /**
   * Ошибка ожидания статуса Intent
   * @description Превышено время ожидания подтверждения платежа
   * или произошла ошибка при проверке статуса транзакции.
   */
  INTENT_WAIT_STATUS_ERROR = 'INTENT_WAIT_STATUS_ERROR',

  /**
   * Ошибка получения публичного ключа
   * @description Не удалось получить публичный ключ для шифрования
   * данных карты. Проверьте подключение к интернету и доступность API.
   */
  GET_PUBLIC_KEY_ERROR = 'GET_PUBLIC_KEY_ERROR',

  /**
   * Ошибка обновления Intent
   * @description Не удалось обновить данные платежного намерения.
   * Возможно, Intent уже обработан или истек срок его действия.
   */
  PATCH_INTENT_ERROR = 'PATCH_INTENT_ERROR',

  /**
   * Ошибка API оплаты
   * @description Общая ошибка при выполнении запроса к API CloudPayments.
   * Проверьте параметры запроса и статус сервиса.
   */
  API_PAY_ERROR = 'API_PAY_ERROR',

  /**
   * Intent ID не найден
   * @description Указанный идентификатор платежного намерения не существует
   * или был удален. Создайте новый Intent.
   */
  INTENT_ID_NOT_FOUND = 'INTENT_ID_NOT_FOUND',

  /**
   * Ошибка получения информации о банке
   * @description Не удалось определить банк-эмитент по номеру карты.
   * Проверьте корректность номера карты.
   */
  BANK_INFO_ERROR = 'BANK_INFO_ERROR',

  /**
   * Платеж отклонен
   * @description Платеж был отклонен банком или платежной системой.
   * Возможные причины: недостаток средств, заблокированная карта,
   * превышение лимитов, неверные данные карты.
   */
  PAYMENT_FAILED = 'PAYMENT_FAILED',

  /**
   * Сервис не инициализирован
   * @description SDK не был инициализирован. Вызовите init() с вашим
   * Public ID перед использованием других методов.
   */
  SERVICE_UNINITIALIZED = 'SERVICE_UNINITIALIZED',

  /**
   * Отсутствует View Controller (только iOS)
   * @description Не удалось найти активный контроллер для отображения
   * платежной формы. Убедитесь, что вызов происходит из активного экрана.
   * @platform ios
   */
  NO_VIEW_CONTROLLER = 'NO_VIEW_CONTROLLER',
}

/**
 * События CloudPayments SDK
 *
 * @description Перечисление событий, поддерживаемых нативным модулем CloudPayments.
 * Нативный модуль эмитит только 4 основных события, внутри которых передается
 * дополнительная информация через поле 'action'.
 *
 * @example Подписка на события платежной формы
 * ```typescript
 * import { eventEmitter, EPaymentFormEventName } from '@lmapp/react-native-cloudpayments';
 *
 * // Подписка на события платежной формы
 * eventEmitter.addListener(
 *   EPaymentFormEventName.PAYMENT_FORM,
 *   (data) => {
 *     switch (data.action) {
 *       case 'willDisplay':
 *         console.log('Форма готовится к отображению');
 *         break;
 *       case 'didDisplay':
 *         console.log('Форма отображена');
 *         break;
 *       case 'transaction':
 *         if (data.statusCode) {
 *           console.log('Платеж успешен:', data.transactionId);
 *         } else {
 *           console.log('Ошибка платежа:', data.message);
 *         }
 *         break;
 *     }
 *   }
 * );
 * ```
 *
 * @since 1.0.0
 */
export enum EPaymentFormEventName {
  /**
   * События платежной формы
   * @description Основное событие для всех действий с платежной формой.
   * Содержит поле 'action' с конкретным типом события.
   */
  PAYMENT_FORM = 'PaymentForm',

  /**
   * События платежей
   * @description Событие для операций с платежами (создание, обработка).
   */
  PAYMENT = 'Payment',

  /**
   * События карт
   * @description Событие для операций с картами (валидация, получение информации).
   */
  CARD = 'Card',

  /**
   * События 3DS
   * @description Событие для операций 3D Secure аутентификации.
   */
  THREE_DS = '3DS',
}

/**
 * Действия платежной формы для внутреннего использования
 *
 * @description Перечисление действий, которые может выполнять платежная форма.
 * Используется для внутренней логики SDK и обработки событий.
 *
 * @internal
 * @since 1.0.0
 */
export enum EPaymentFormAction {
  /** Форма готовится к отображению */
  WILL_DISPLAY = 'willDisplay',
  /** Форма отображена */
  DID_DISPLAY = 'didDisplay',
  /** Форма готовится к скрытию */
  WILL_HIDE = 'willHide',
  /** Форма скрыта */
  DID_HIDE = 'didHide',
  /** Выполняется транзакция */
  TRANSACTION = 'transaction',
}

// ============================================================================
// CARDIO ENUMS
// ============================================================================

/**
 * Поддерживаемые языки интерфейса CardIO сканера
 *
 * @description Перечисление языков, поддерживаемых CardIO для локализации
 * интерфейса сканера банковских карт. Если язык не указан, используется
 * язык устройства по умолчанию.
 *
 * @example Использование языков CardIO
 * ```typescript
 * import { ECardIOLanguage } from '@lmapp/react-native-cloudpayments';
 *
 * const cardScannerConfig = {
 *   language: ECardIOLanguage.RUSSIAN,
 *   requireExpiry: true,
 *   hideCardIOLogo: true
 * };
 * ```
 *
 * @since 1.0.0
 * @platform android
 */
export enum ECardIOLanguage {
  /** Английский язык */
  ENGLISH = 'en',
  /** Русский язык */
  RUSSIAN = 'ru',
  /** Французский язык */
  FRENCH = 'fr',
  /** Немецкий язык */
  GERMAN = 'de',
  /** Итальянский язык */
  ITALIAN = 'it',
  /** Японский язык */
  JAPANESE = 'ja',
  /** Корейский язык */
  KOREAN = 'ko',
  /** Португальский язык */
  PORTUGUESE = 'pt',
  /** Шведский язык */
  SWEDISH = 'sv',
  /** Китайский упрощенный */
  CHINESE_SIMPLIFIED = 'zh-Hans',
  /** Китайский традиционный */
  CHINESE_TRADITIONAL = 'zh-Hant',
  /** Испанский язык */
  SPANISH = 'es',
}

/**
 * Предустановленные цветовые схемы для CardIO сканера
 *
 * @description Перечисление готовых цветов в стиле Material Design
 * для настройки внешнего вида CardIO сканера. Можно использовать
 * как для цвета ActionBar, так и для цвета рамки сканирования.
 *
 * @example Использование цветовых схем
 * ```typescript
 * import { ECardIOColorScheme } from '@lmapp/react-native-cloudpayments';
 *
 * const cardScannerConfig = {
 *   actionBarColor: ECardIOColorScheme.MATERIAL_BLUE,
 *   guideColor: ECardIOColorScheme.MATERIAL_GREEN,
 *   language: 'ru'
 * };
 * ```
 *
 * @since 1.0.0
 * @platform android
 */
export enum ECardIOColorScheme {
  /** Material Design синий цвет (#2196F3) */
  MATERIAL_BLUE = '#2196F3',
  /** Material Design зеленый цвет (#4CAF50) */
  MATERIAL_GREEN = '#4CAF50',
  /** Material Design красный цвет (#F44336) */
  MATERIAL_RED = '#F44336',
  /** Material Design оранжевый цвет (#FF9800) */
  MATERIAL_ORANGE = '#FF9800',
  /** Material Design фиолетовый цвет (#9C27B0) */
  MATERIAL_PURPLE = '#9C27B0',
  /** Material Design темно-синий цвет (#3F51B5) */
  MATERIAL_INDIGO = '#3F51B5',
  /** Material Design голубой цвет (#00BCD4) */
  MATERIAL_CYAN = '#00BCD4',
  /** Material Design розовый цвет (#E91E63) */
  MATERIAL_PINK = '#E91E63',
  /** Черный цвет */
  BLACK = '#000000',
  /** Белый цвет */
  WHITE = '#FFFFFF',
  /** Серый цвет */
  GRAY = '#9E9E9E',
}
