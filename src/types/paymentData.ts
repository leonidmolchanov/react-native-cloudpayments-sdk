/**
 * Периоды повторения для рекурентных платежей
 * @description Определяет доступные интервалы для автоматического списания средств
 * при настройке подписок и регулярных платежей через CloudPayments.
 *
 * @example Настройка различных типов подписок
 * ```typescript
 * // Ежедневная подписка (например, доступ к контенту)
 * const dailySubscription = {
 *   interval: RECURENT_PERIOD.DAY,
 *   period: 1
 * };
 *
 * // Еженедельная подписка (например, доставка продуктов)
 * const weeklySubscription = {
 *   interval: RECURENT_PERIOD.WEEK,
 *   period: 1
 * };
 *
 * // Ежемесячная подписка (самый популярный вариант)
 * const monthlySubscription = {
 *   interval: RECURENT_PERIOD.MONTH,
 *   period: 1
 * };
 * ```
 *
 * @see {@link https://docs.cloudpayments.ru/#rekurrentnye-platezhi} Документация по рекурентным платежам
 */
export enum RECURENT_PERIOD {
  /**
   * Ежедневное повторение
   * @description Платеж будет списываться каждый день или через указанное количество дней.
   * Подходит для ежедневных сервисов, контента, доступа к ресурсам.
   * @example Ежедневная подписка на новости, доступ к премиум контенту
   */
  DAY = 'Day',

  /**
   * Еженедельное повторение
   * @description Платеж будет списываться каждую неделю или через указанное количество недель.
   * Популярно для услуг доставки, еженедельных сервисов.
   * @example Еженедельная доставка продуктов, подписка на журналы
   */
  WEEK = 'Week',

  /**
   * Ежемесячное повторение
   * @description Платеж будет списываться каждый месяц или через указанное количество месяцев.
   * Самый распространенный тип подписок для большинства сервисов.
   * @example Подписка на SaaS, стриминговые сервисы, мобильные приложения
   */
  MONTH = 'Month',
}

/**
 * Настройки рекурентного (регулярного) платежа
 * @description Конфигурация для автоматического списания средств через определенные интервалы.
 * Позволяет настроить подписки, регулярные платежи и автоматическое продление услуг.
 * После первого успешного платежа система будет автоматически списывать указанную сумму
 * с сохраненной карты покупателя согласно заданному расписанию.
 *
 * @example Настройка месячной подписки с чеком
 * ```typescript
 * const monthlySubscription: Recurrent = {
 *   interval: RECURENT_PERIOD.MONTH,
 *   period: 1, // каждый месяц
 *   customerReceipt: {
 *     items: [{
 *       label: 'Подписка Premium на месяц',
 *       price: 999,
 *       quantity: 1,
 *       amount: 999,
 *       vat: 20,
 *       method: 4, // полная предоплата
 *       object: 4  // услуга
 *     }],
 *     taxationSystem: 1,
 *     email: 'user@example.com'
 *   }
 * };
 * ```
 *
 * @example Настройка квартальной подписки (каждые 3 месяца)
 * ```typescript
 * const quarterlySubscription: Recurrent = {
 *   interval: RECURENT_PERIOD.MONTH,
 *   period: 3, // каждые 3 месяца
 *   customerReceipt: {
 *     items: [{
 *       label: 'Квартальная подписка',
 *       price: 2700,
 *       quantity: 1,
 *       amount: 2700,
 *       method: 4,
 *       object: 4
 *     }],
 *     taxationSystem: 1,
 *     email: 'user@example.com'
 *   }
 * };
 * ```
 *
 * @example Еженедельная доставка
 * ```typescript
 * const weeklyDelivery: Recurrent = {
 *   interval: RECURENT_PERIOD.WEEK,
 *   period: 1, // каждую неделю
 *   customerReceipt: {
 *     items: [{
 *       label: 'Еженедельная доставка продуктов',
 *       price: 1500,
 *       quantity: 1,
 *       amount: 1500,
 *       method: 4,
 *       object: 4
 *     }],
 *     taxationSystem: 1
 *   }
 * };
 * ```
 *
 * @see {@link https://docs.cloudpayments.ru/#rekurrentnye-platezhi} Документация CloudPayments по рекурентным платежам
 * @see {@link https://docs.cloudpayments.ru/#rekvizitimy-dlya-onlayn-cheka} Требования к чекам для рекурентных платежей
 */
export type Recurrent = {
  /**
   * Единица измерения интервала повторения
   * @description Определяет базовую единицу времени для расчета периодичности платежей.
   * Используется совместно с полем `period` для точного определения расписания.
   * @example RECURENT_PERIOD.MONTH для месячных подписок
   */
  interval: RECURENT_PERIOD;

  /**
   * Количество единиц интервала между платежами
   * @description Множитель для интервала. Определяет через сколько единиц времени
   * (дней/недель/месяцев) будет происходить следующее списание.
   *
   * Примеры использования:
   * - period: 1 + interval: MONTH = каждый месяц
   * - period: 3 + interval: MONTH = каждые 3 месяца (квартал)
   * - period: 2 + interval: WEEK = каждые 2 недели
   * - period: 7 + interval: DAY = каждые 7 дней (еженедельно)
   *
   * @example 1 // Каждый интервал (каждый день/неделю/месяц)
   * @example 3 // Каждые 3 интервала (каждые 3 дня/недели/месяца)
   */
  period: number;

  /**
   * Чек для регулярных платежей
   * @description Шаблон чека, который будет формироваться при каждом автоматическом списании.
   * Обязателен при работе с онлайн-кассой согласно 54-ФЗ для российских мерчантов.
   *
   * **Важные особенности:**
   * - Чек должен соответствовать сумме рекурентного платежа
   * - Товары/услуги в чеке должны отражать подписку или регулярную услугу
   * - Email/телефон покупателя используется для отправки чека при каждом списании
   * - Система налогообложения должна соответствовать настройкам мерчанта
   *
   * **Отличия от обычного чека:**
   * - Формируется автоматически при каждом рекурентном списании
   * - Не требует участия покупателя в момент создания
   * - Должен содержать информацию о подписке/регулярной услуге
   *
   * @example Чек для подписки с НДС
   * ```typescript
   * customerReceipt: {
   *   items: [{
   *     label: 'Месячная подписка на сервис',
   *     price: 1000,
   *     quantity: 1,
   *     amount: 1000,
   *     vat: 20,        // НДС 20%
   *     method: 4,      // Полная предоплата
   *     object: 4       // Услуга
   *   }],
   *   taxationSystem: 0,  // ОСН
   *   email: 'user@example.com'
   * }
   * ```
   */
  customerReceipt: Receipt; //чек для регулярных платежей
};

/**
 * Элемент чека (товар/услуга)
 * @description Представляет одну позицию в чеке онлайн-кассы согласно 54-ФЗ.
 * Содержит всю необходимую информацию о товаре или услуге для формирования фискального документа.
 *
 * @example Создание товара для чека
 * ```typescript
 * const item: ReceiptItem = {
 *   label: 'Подписка Premium на 1 месяц',
 *   price: 999.00,
 *   quantity: 1,
 *   amount: 999.00,
 *   vat: 20, // НДС 20%
 *   method: 4, // Полная предварительная оплата
 *   object: 4  // Услуга
 * };
 * ```
 *
 * @see {@link https://docs.cloudpayments.ru/#rekvizitimy-dlya-onlayn-cheka} Документация CloudPayments
 * @see {@link https://www.nalog.ru/rn77/taxation/reference_work/conception_vnp/4687249/} 54-ФЗ на сайте ФНС
 */
export interface ReceiptItem {
  /**
   * Наименование товара/услуги
   * @description Человекочитаемое название позиции, которое будет отображаться в чеке.
   * Максимальная длина: 128 символов.
   * @example 'Подписка Premium на 1 месяц' | 'Доставка курьером' | 'Товар "Смартфон iPhone 15"'
   */
  label: string;

  /**
   * Цена за единицу товара/услуги (в рублях)
   * @description Стоимость одной единицы товара без учета количества.
   * Используется для расчета общей суммы: amount = price × quantity.
   * @example 999.50 для товара стоимостью 999 рублей 50 копеек
   */
  price: number;

  /**
   * Количество товара/услуги
   * @description Количество единиц данной позиции в заказе.
   * Может быть дробным для товаров, продаваемых на вес.
   * @example 1 | 2.5 (для 2.5 кг товара) | 0.5 (для пол-часа услуги)
   */
  quantity: number;

  /**
   * Общая стоимость позиции (в рублях)
   * @description Итоговая сумма за данную позицию (price × quantity).
   * Должна точно соответствовать произведению цены на количество.
   * @example 1999.00 для двух товаров по 999.50 каждый
   */
  amount: number;

  /**
   * Ставка НДС (опционально)
   * @description Размер налога на добавленную стоимость в процентах.
   * Если товар не облагается НДС, передавайте null или не указывайте поле.
   * @example 20 (НДС 20%) | 10 (НДС 10%) | 0 (НДС 0%) | null (без НДС)
   */
  vat?: number | null;

  /**
   * Способ расчета (согласно 54-ФЗ)
   * @description Указывает на особенности расчета за данную позицию.
   * Обязательное поле для соответствия требованиям 54-ФЗ.
   *
   * Возможные значения:
   * - 1: Предоплата 100%
   * - 2: Предоплата
   * - 3: Аванс
   * - 4: Полный расчет
   * - 5: Частичный расчет и кредит
   * - 6: Передача в кредит
   * - 7: Оплата кредита
   *
   * @example 4 // Полный расчет (самый распространенный случай)
   */
  method: number;

  /**
   * Предмет расчета (согласно 54-ФЗ)
   * @description Указывает, что именно продается/оплачивается.
   * Обязательное поле для соответствия требованиям 54-ФЗ.
   *
   * Возможные значения:
   * - 1: Товар
   * - 2: Подакцизный товар
   * - 3: Работа
   * - 4: Услуга
   * - 5: Ставка азартной игры
   * - 6: Выигрыш азартной игры
   * - 7: Лотерейный билет
   * - 8: Выигрыш лотереи
   * - 9: Предоставление РИД
   * - 10: Платеж
   * - 11: Агентское вознаграждение
   * - 12: Составной предмет расчета
   * - 13: Иной предмет расчета
   *
   * @example 4 // Услуга (для цифровых продуктов, подписок)
   * @example 1 // Товар (для физических товаров)
   */
  object: number;
}

/**
 * Суммы платежа по способам оплаты
 * @description Детализация общей суммы платежа по различным способам оплаты
 * согласно требованиям 54-ФЗ. Сумма всех полей должна равняться общей сумме платежа.
 *
 * @example Полная оплата электронными деньгами
 * ```typescript
 * const amounts: Amounts = {
 *   electronic: 1000.00,    // Вся сумма электронно
 *   advancePayment: 0,      // Без предоплаты
 *   credit: 0,              // Без кредита
 *   provision: 0            // Без задатка
 * };
 * ```
 *
 * @example Смешанная оплата с предоплатой
 * ```typescript
 * const amounts: Amounts = {
 *   electronic: 800.00,     // 800 руб электронно
 *   advancePayment: 200.00, // 200 руб предоплата
 *   credit: 0,
 *   provision: 0
 * };
 * ```
 */
export interface Amounts {
  /**
   * Сумма электронными деньгами (в рублях)
   * @description Размер оплаты электронными средствами платежа.
   * Включает банковские карты, электронные кошельки, мобильные платежи.
   * @example 1000.50 для оплаты картой на 1000 рублей 50 копеек
   */
  electronic: number;

  /**
   * Сумма предоплаты (в рублях)
   * @description Размер ранее полученной предоплаты, зачитываемой в счет текущего платежа.
   * Используется при частичной предоплате товаров/услуг.
   * @example 500.00 если покупатель ранее внес 500 рублей предоплаты
   */
  advancePayment: number;

  /**
   * Сумма кредита (в рублях)
   * @description Размер оплаты в кредит, который предоставляется покупателю.
   * Используется при рассрочке или отсроченном платеже.
   * @example 300.00 при оплате части суммы в рассрочку
   */
  credit: number;

  /**
   * Сумма задатка (в рублей)
   * @description Размер задатка, внесенного в счет будущих поставок товаров/услуг.
   * Отличается от предоплаты правовым статусом и порядком учета.
   * @example 1000.00 при внесении задатка за будущий заказ
   */
  provision: number;
}

/**
 * Структура чека онлайн-кассы
 * @description Полная информация для формирования фискального документа согласно 54-ФЗ.
 * Содержит все необходимые данные для отправки в ОФД (Оператор Фискальных Данных)
 * и соответствует требованиям российского законодательства об онлайн-кассах.
 *
 * @example Создание чека для интернет-магазина
 * ```typescript
 * const receipt: Receipt = {
 *   items: [
 *     {
 *       label: 'Смартфон iPhone 15',
 *       price: 89990,
 *       quantity: 1,
 *       amount: 89990,
 *       vat: 20,
 *       method: 4,
 *       object: 1
 *     }
 *   ],
 *   taxationSystem: 1,      // ОСН
 *   email: 'buyer@email.com',
 *   isBso: false,
 *   amounts: {
 *     electronic: 89990,
 *     advancePayment: 0,
 *     credit: 0,
 *     provision: 0
 *   }
 * };
 * ```
 *
 * @see {@link https://docs.cloudpayments.ru/#rekvizitimy-dlya-onlayn-cheka} CloudPayments документация
 * @see {@link https://www.consultant.ru/document/cons_doc_LAW_200383/} Федеральный закон 54-ФЗ
 */
export interface Receipt {
  /**
   * Список товаров/услуг в чеке
   * @description Массив позиций, входящих в данный чек.
   * Каждая позиция должна содержать полную информацию согласно 54-ФЗ.
   * Минимум: 1 позиция, максимум: не ограничено (практически до 100-200 позиций).
   */
  items: ReceiptItem[];

  /**
   * Система налогообложения организации
   * @description Код применяемой налогоплательщиком системы налогообложения.
   * Влияет на расчет и отображение налогов в чеке.
   *
   * Возможные значения:
   * - 0: Общая система налогообложения (ОСН)
   * - 1: Упрощенная система налогообложения (доходы) (УСН доходы)
   * - 2: Упрощенная система налогообложения (доходы минус расходы) (УСН доходы-расходы)
   * - 3: Единый налог на вмененный доход (ЕНВД)
   * - 4: Единый сельскохозяйственный налог (ЕСН)
   * - 5: Патентная система налогообложения (ПСН)
   *
   * @example 1 // УСН доходы (самая популярная для IT)
   * @example 0 // ОСН (для крупных компаний)
   */
  taxationSystem: number;

  /**
   * Email покупателя для отправки чека (опционально)
   * @description Адрес электронной почты, на который будет отправлен электронный чек.
   * Если не указан, чек не отправляется по email (только SMS или не отправляется вовсе).
   * Рекомендуется указывать для B2C операций.
   * @example 'customer@example.com'
   */
  email?: string;

  /**
   * Телефон покупателя для отправки чека (опционально)
   * @description Номер мобильного телефона для отправки чека по SMS.
   * Формат: +7XXXXXXXXXX (российский номер) или международный формат.
   * @example '+79991234567' | '+1234567890'
   */
  phone?: string;

  /**
   * Признак БСО (Бланк Строгой Отчетности)
   * @description Указывает, является ли документ бланком строгой отчетности
   * вместо обычного кассового чека. Используется для специфических видов деятельности
   * (например, оказание услуг без продажи товаров).
   *
   * - true: Документ является БСО
   * - false или не указано: Обычный кассовый чек
   *
   * @default false
   * @example false // Обычный интернет-магазин
   * @example true  // Услуги (консультации, ремонт и т.д.)
   */
  isBso?: boolean;

  /**
   * Детализация суммы по способам оплаты (опционально)
   * @description Разбивка общей суммы платежа по различным способам оплаты.
   * Если не указано, вся сумма считается электронной.
   * Сумма всех способов должна равняться общей сумме позиций в чеке.
   *
   * @example При указании amounts проверяется соответствие:
   * ```
   * amounts.electronic + amounts.advancePayment + amounts.credit + amounts.provision
   * === sum(items[].amount)
   * ```
   */
  amounts?: Amounts;
}

/**
 * Информация о плательщике (покупателе)
 * @description Персональные данные покупателя для формирования чека и выполнения
 * требований законодательства. Все поля опциональны, но рекомендуется заполнять
 * максимально полно для соответствия требованиям валютного законодательства
 * и для улучшения процессинга платежей.
 *
 * @example Полная информация о физическом лице
 * ```typescript
 * const payer: IPayer = {
 *   firstName: 'Иван',
 *   lastName: 'Петров',
 *   middleName: 'Сидорович',
 *   birth: '1985-03-15',
 *   address: 'г. Москва, ул. Тверская, д. 10, кв. 25',
 *   street: 'ул. Тверская',
 *   city: 'Москва',
 *   country: 'RU',
 *   phone: '+79991234567',
 *   postcode: '101000'
 * };
 * ```
 *
 * @example Минимальная информация
 * ```typescript
 * const payer: IPayer = {
 *   firstName: 'Иван',
 *   lastName: 'Петров',
 *   phone: '+79991234567'
 * };
 * ```
 *
 * @see {@link https://docs.cloudpayments.ru/#parametry-zaprosa} CloudPayments параметры
 */
export interface IPayer {
  /**
   * Имя покупателя
   * @description Личное имя плательщика (физического лица).
   * Рекомендуется указывать для персонализации чека и улучшения антифрод проверок.
   * @example 'Иван' | 'Мария' | 'Александр'
   */
  firstName?: string;

  /**
   * Фамилия покупателя
   * @description Фамилия плательщика (физического лица).
   * Вместе с именем используется для идентификации и антифрод проверок.
   * @example 'Петров' | 'Сидорова' | 'Иванов'
   */
  lastName?: string;

  /**
   * Отчество покупателя
   * @description Отчество плательщика (для российских граждан).
   * Опциональное поле, характерное для российской системы имен.
   * @example 'Иванович' | 'Петровна' | 'Александрович'
   */
  middleName?: string;

  /**
   * Дата рождения покупателя
   * @description Дата рождения в формате ISO 8601 (YYYY-MM-DD).
   * Используется для дополнительной верификации и антифрод проверок.
   * @example '1985-03-15' | '1990-12-31' | '1978-07-22'
   */
  birth?: string;

  /**
   * Полный адрес покупателя
   * @description Полный почтовый адрес плательщика в свободной форме.
   * Может дублировать информацию из полей street, city, но в удобном формате.
   * @example 'г. Москва, ул. Тверская, д. 10, кв. 25' | '195027, Санкт-Петербург, Невский пр., 100'
   */
  address?: string;

  /**
   * Улица адреса покупателя
   * @description Название улицы из адреса плательщика.
   * Структурированная часть адреса для более точной обработки.
   * @example 'ул. Тверская' | 'пр. Невский' | 'пер. Малый Гнездниковский'
   */
  street?: string;

  /**
   * Город покупателя
   * @description Город/населенный пункт из адреса плательщика.
   * Важен для определения региона и применения соответствующих правил.
   * @example 'Москва' | 'Санкт-Петербург' | 'Екатеринбург'
   */
  city?: string;

  /**
   * Страна покупателя
   * @description Код страны по стандарту ISO 3166-1 alpha-2.
   * Влияет на применение валютного законодательства и процессинг.
   * @example 'RU' (Россия) | 'BY' (Беларусь) | 'KZ' (Казахстан) | 'US' (США)
   */
  country?: string;

  /**
   * Телефон покупателя
   * @description Контактный номер телефона плательщика.
   * Формат: международный (+7XXXXXXXXXX) или любой удобный.
   * Используется для отправки SMS с чеком и связи при необходимости.
   * @example '+79991234567' | '+1234567890' | '8-999-123-45-67'
   */
  phone?: string;

  /**
   * Почтовый индекс
   * @description Почтовый индекс из адреса плательщика.
   * Помогает в структурировании адресной информации.
   * @example '101000' | '195027' | '620014'
   */
  postcode?: string;
}

/**
 * @fileoverview Интерфейсы данных платежей для CloudPayments SDK
 * @description Содержит типы данных для настройки и выполнения платежей
 * @author Leonid Molchanov
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
  jsonData?: Object;

  receipt?: Receipt;

  payer?: IPayer;
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
  receipt?: Receipt;
  payer?: IPayer;

  /**
   * Настройки рекурентного платежа (опционально)
   * @description Конфигурация для автоматического списания средств через определенные интервалы.
   * При указании этого поля первый платеж станет инициирующим для создания подписки,
   * а последующие платежи будут происходить автоматически согласно заданному расписанию.
   *
   * **Важные особенности рекурентных платежей:**
   * - Первый платеж проходит с участием пользователя (ввод карты, подтверждение)
   * - Карта автоматически сохраняется для будущих списаний
   * - Последующие платежи происходят без участия пользователя
   * - Чек формируется автоматически при каждом списании
   * - Пользователь может отменить подписку в любой момент
   *
   * **Применение:**
   * - Подписки на сервисы (месячные, годовые)
   * - Регулярные платежи (коммунальные услуги, страховка)
   * - Периодические доставки товаров
   * - Автоматическое пополнение счетов
   *
   * @example Настройка месячной подписки
   * ```typescript
   * const paymentData: IPaymentData = {
   *   publicId: 'pk_test_1234567890abcdef',
   *   amount: '999.00',
   *   currency: 'RUB',
   *   description: 'Подписка Premium',
   *
   *   // Настройка рекурентного платежа
   *   recurrent: {
   *     interval: RECURENT_PERIOD.MONTH,
   *     period: 1, // каждый месяц
   *     customerReceipt: {
   *       items: [{
   *         label: 'Подписка Premium на месяц',
   *         price: 999,
   *         quantity: 1,
   *         amount: 999,
   *         vat: 20,
   *         method: 4,
   *         object: 4
   *       }],
   *       taxationSystem: 1,
   *       email: 'user@example.com'
   *     }
   *   }
   * };
   * ```
   *
   * @example Настройка квартальной подписки со скидкой
   * ```typescript
   * recurrent: {
   *   interval: RECURENT_PERIOD.MONTH,
   *   period: 3, // каждые 3 месяца
   *   customerReceipt: {
   *     items: [{
   *       label: 'Квартальная подписка (скидка 10%)',
   *       price: 2700, // вместо 2997 (999*3)
   *       quantity: 1,
   *       amount: 2700,
   *       method: 4,
   *       object: 4
   *     }],
   *     taxationSystem: 1
   *   }
   * }
   * ```
   *
   * @see {@link https://docs.cloudpayments.ru/#rekurrentnye-platezhi} Документация по рекурентным платежам
   * @see {@link https://docs.cloudpayments.ru/#otmena-rekurrentnykh-platezhey} Отмена рекурентных платежей
   */
  recurrent?: Recurrent;
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
