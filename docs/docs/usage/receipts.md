---
sidebar_position: 2
---

# 🧾 Работа с чеками (54-ФЗ)

Подробное руководство по работе с онлайн-чеками согласно российскому законодательству.

## 📋 Обзор

CloudPayments SDK полностью поддерживает требования **Федерального закона №54-ФЗ** "О применении контрольно-кассовой техники". SDK автоматически формирует и отправляет электронные чеки в налоговую через ОФД (Оператор Фискальных Данных).

### Что включает поддержка 54-ФЗ:

✅ **Автоматическое формирование чеков** для всех платежей  
✅ **Отправка в налоговую** через сертифицированный ОФД  
✅ **Email/SMS уведомления** покупателям  
✅ **Все типы предметов расчета** (товары, услуги, работы)  
✅ **Различные системы налогообложения** (ОСН, УСН, ЕНВД и др.)  
✅ **БСО поддержка** для услуг  
✅ **Рекурентные платежи** с автоматическими чеками

## ⚠️ Важно: Передача данных в CloudPayments API

CloudPayments API имеет определенную специфику работы с параметрами. **Данные можно передавать двумя способами:**

### 1. **Через параметры модуля** (рекомендуемый способ)

```typescript
const paymentData: IPaymentData = {
  publicId: 'pk_test_...',
  amount: '1000.00',
  currency: 'RUB',
  receipt: {
    /* чек */
  },
  payer: {
    /* данные покупателя */
  },
  recurrent: {
    /* настройки подписки */
  },
};
```

### 2. **Через jsonData** (альтернативный способ)

```typescript
const paymentData: IPaymentData = {
  publicId: 'pk_test_...',
  amount: '1000.00',
  currency: 'RUB',
  jsonData: {
    receipt: {
      /* чек */
    },
    payer: {
      /* данные покупателя */
    },
    recurrent: {
      /* настройки подписки */
    },
  },
};
```

> **💡 Важно**: В силу определенных нюансов CloudPayments API, данные на уровне модуля могут собираться или разбираться как на нашей стороне, так и на стороне SDK. **Если что-то не получается передать через параметры модуля или не работает корректно - попробуйте передать эти данные через `jsonData`!**

## 🏗️ Структура типов

### Receipt - Чек онлайн-кассы

```typescript
interface Receipt {
  items: ReceiptItem[]; // Список товаров/услуг (обязательно)
  taxationSystem: number; // Система налогообложения (обязательно)
  email?: string; // Email для отправки чека
  phone?: string; // Телефон для SMS
  isBso?: boolean; // Признак БСО (false = чек, true = БСО)
  amounts?: Amounts; // Детализация по способам оплаты
}
```

### ReceiptItem - Позиция в чеке

```typescript
interface ReceiptItem {
  label: string; // Название товара/услуги
  price: number; // Цена за единицу
  quantity: number; // Количество
  amount: number; // Общая сумма (price × quantity)
  vat?: number | null; // Ставка НДС (20, 10, 0 или null)
  method: number; // Способ расчета (1-7)
  object: number; // Предмет расчета (1-13)
}
```

### Amounts - Способы оплаты

```typescript
interface Amounts {
  electronic: number; // Электронные средства (карты, кошельки)
  advancePayment: number; // Предоплата (ранее внесенная)
  credit: number; // В кредит/рассрочку
  provision: number; // Задаток
}
```

### IPayer - Информация о покупателе

```typescript
interface IPayer {
  firstName?: string; // Имя
  lastName?: string; // Фамилия
  middleName?: string; // Отчество
  birth?: string; // Дата рождения (YYYY-MM-DD)
  address?: string; // Полный адрес
  street?: string; // Улица
  city?: string; // Город
  country?: string; // Код страны (RU, US, etc.)
  phone?: string; // Телефон
  postcode?: string; // Почтовый индекс
}
```

## 📊 Справочники кодов

### Системы налогообложения (taxationSystem)

| Код | Система                | Описание                                  |
| --- | ---------------------- | ----------------------------------------- |
| `0` | **ОСН**                | Общая система налогообложения             |
| `1` | **УСН доходы**         | Упрощенная система (доходы)               |
| `2` | **УСН доходы-расходы** | Упрощенная система (доходы минус расходы) |
| `3` | **ЕНВД**               | Единый налог на вмененный доход           |
| `4` | **ЕСН**                | Единый сельскохозяйственный налог         |
| `5` | **ПСН**                | Патентная система налогообложения         |

> **💡 Совет**: Для IT-стартапов чаще всего используется `1` (УСН доходы)

### Способы расчета (method)

| Код | Способ                    | Когда использовать               |
| --- | ------------------------- | -------------------------------- |
| `1` | Предоплата 100%           | Полная предоплата                |
| `2` | Предоплата                | Частичная предоплата             |
| `3` | Аванс                     | Авансовый платеж                 |
| `4` | **Полный расчет**         | **Обычная оплата (95% случаев)** |
| `5` | Частичный расчет и кредит | Оплата частями                   |
| `6` | Передача в кредит         | Продажа в кредит                 |
| `7` | Оплата кредита            | Погашение кредита                |

### Предметы расчета (object)

| Код  | Предмет                   | Примеры                          |
| ---- | ------------------------- | -------------------------------- |
| `1`  | **Товар**                 | Физические товары, оборудование  |
| `2`  | Подакцизный товар         | Алкоголь, табак, автомобили      |
| `3`  | Работа                    | Ремонт, строительство, установка |
| `4`  | **Услуга**                | **Подписки, ПО, консультации**   |
| `5`  | Ставка азартной игры      | Казино, букмекеры                |
| `6`  | Выигрыш азартной игры     | Выплаты выигрышей                |
| `7`  | Лотерейный билет          | Покупка лотерейных билетов       |
| `8`  | Выигрыш лотереи           | Выплата выигрышей                |
| `9`  | Предоставление РИД        | Интеллектуальная собственность   |
| `10` | Платеж                    | Комиссии, штрафы                 |
| `11` | Агентское вознаграждение  | Комиссии посредников             |
| `12` | Составной предмет расчета | Комплексные услуги               |
| `13` | Иной предмет расчета      | Прочие виды                      |

## 🎯 Практические примеры

### 1. Интернет-магазин (физические товары)

```typescript
const ecommercePayment = async () => {
  const receiptItems: ReceiptItem[] = [
    {
      label: 'Смартфон iPhone 15 Pro',
      price: 89990,
      quantity: 1,
      amount: 89990,
      vat: 20, // НДС 20%
      method: 4, // Полный расчет
      object: 1, // Товар
    },
    {
      label: 'Чехол для iPhone',
      price: 2990,
      quantity: 1,
      amount: 2990,
      vat: 20,
      method: 4,
      object: 1,
    },
    {
      label: 'Доставка курьером',
      price: 500,
      quantity: 1,
      amount: 500,
      vat: null, // Без НДС
      method: 4,
      object: 4, // Услуга
    },
  ];

  const receipt: Receipt = {
    items: receiptItems,
    taxationSystem: 1, // УСН доходы
    amounts: {
      electronic: 93480, // Вся сумма картой
      advancePayment: 0,
      credit: 0,
      provision: 0,
    },
  };
};
```

### 2. SaaS подписка (цифровая услуга)

```typescript
const saasSubscription = async () => {
  const receiptItems: ReceiptItem[] = [
    {
      label: 'Подписка "Pro" на 1 месяц',
      price: 2990,
      quantity: 1,
      amount: 2990,
      vat: 20, // НДС с ПО
      method: 4, // Полный расчет
      object: 4, // Услуга
    },
  ];

  const payer: IPayer = {
    firstName: 'Анна',
    lastName: 'Смирнова',
    email: 'anna@company.com',
    phone: '+79995551234',
    country: 'RU',
  };

  const receipt: Receipt = {
    items: receiptItems,
    taxationSystem: 1, // УСН доходы
    email: 'anna@company.com', // Отправка чека на email
    amounts: {
      electronic: 2990,
      advancePayment: 0,
      credit: 0,
      provision: 0,
    },
  };

  const result = await presentPaymentForm({
    amount: '2990.00',
    currency: 'RUB',
    description: 'Подписка Pro на месяц',
    email: 'anna@company.com',
    payer: payer,
    receipt: receipt,
  });
};
```

### 3. Образование (услуги с предоплатой)

```typescript
const educationPayment = async () => {
  const receiptItems: ReceiptItem[] = [
    {
      label: 'Курс "React Native Разработка"',
      price: 45000,
      quantity: 1,
      amount: 45000,
      vat: null, // Образование без НДС
      method: 1, // Предоплата 100%
      object: 4, // Услуга
    },
  ];

  const receipt: Receipt = {
    items: receiptItems,
    taxationSystem: 1,
    amounts: {
      electronic: 45000, // Полная предоплата
      advancePayment: 0,
      credit: 0,
      provision: 0,
    },
  };
};
```

### 4. Ресторан доставка (БСО для услуг)

```typescript
const deliveryService = async () => {
  const receiptItems: ReceiptItem[] = [
    {
      label: 'Пицца Маргарита',
      price: 890,
      quantity: 2,
      amount: 1780,
      vat: 20,
      method: 4,
      object: 1, // Товар (еда)
    },
    {
      label: 'Доставка на дом',
      price: 200,
      quantity: 1,
      amount: 200,
      vat: null,
      method: 4,
      object: 4, // Услуга
    },
  ];

  const receipt: Receipt = {
    items: receiptItems,
    taxationSystem: 1,
    isBso: false, // Обычный чек (не БСО)
    amounts: {
      electronic: 1980,
      advancePayment: 0,
      credit: 0,
      provision: 0,
    },
  };
};
```

### 5. Смешанная оплата (предоплата + доплата)

```typescript
const mixedPayment = async () => {
  const receiptItems: ReceiptItem[] = [
    {
      label: 'Разработка мобильного приложения',
      price: 500000,
      quantity: 1,
      amount: 500000,
      vat: 20,
      method: 4, // Полный расчет
      object: 3, // Работа
    },
  ];

  const receipt: Receipt = {
    items: receiptItems,
    taxationSystem: 1,
    amounts: {
      electronic: 300000, // Доплата картой
      advancePayment: 200000, // Ранее внесенная предоплата
      credit: 0,
      provision: 0,
    },
  };
};
```

## ⚠️ Важные моменты

### 1. Валидация данных

```typescript
// ❌ Неправильно - сумма не сходится
const wrongReceipt: Receipt = {
  items: [
    { label: 'Товар', price: 100, quantity: 2, amount: 150 }, // 100×2 ≠ 150
  ],
  // ...
};

// ✅ Правильно - сумма сходится
const correctReceipt: Receipt = {
  items: [
    { label: 'Товар', price: 100, quantity: 2, amount: 200 }, // 100×2 = 200
  ],
  // ...
};
```

### 2. Баланс способов оплаты

```typescript
// Сумма всех amounts должна равняться сумме всех items
const amounts: Amounts = {
  electronic: 800,
  advancePayment: 200,
  credit: 0,
  provision: 0,
}; // Итого: 1000

const items: ReceiptItem[] = [{ amount: 500 }, { amount: 500 }]; // Итого: 1000 ✅
```

### 3. НДС и система налогообложения

```typescript
// Для УСН (код 1) НДС обычно не облагается
const usnReceipt: Receipt = {
  items: [
    {
      label: 'Консультация',
      vat: null, // УСН без НДС
    },
  ],
  taxationSystem: 1, // УСН доходы
};

// Для ОСН (код 0) НДС обязателен
const osnReceipt: Receipt = {
  items: [
    {
      label: 'Товар',
      vat: 20, // ОСН с НДС 20%
    },
  ],
  taxationSystem: 0, // ОСН
};
```

## 🔧 Полезные утилиты

### Функция создания простого чека

```typescript
const createSimpleReceipt = (
  label: string,
  amount: number,
  taxationSystem: number,
  isService: boolean = true
): Receipt => {
  return {
    items: [
      {
        label,
        price: amount,
        quantity: 1,
        amount,
        vat: taxationSystem === 0 ? 20 : null, // НДС только для ОСН
        method: 4, // Полный расчет
        object: isService ? 4 : 1, // Услуга или товар
      },
    ],
    taxationSystem,
    amounts: {
      electronic: amount,
      advancePayment: 0,
      credit: 0,
      provision: 0,
    },
  };
};

// Использование
const receipt = createSimpleReceipt(
  'Подписка Premium',
  1990,
  1, // УСН доходы
  true // Услуга
);
```

### Валидация чека

```typescript
const validateReceipt = (receipt: Receipt): boolean => {
  const itemsTotal = receipt.items.reduce((sum, item) => sum + item.amount, 0);

  if (receipt.amounts) {
    const amountsTotal =
      receipt.amounts.electronic +
      receipt.amounts.advancePayment +
      receipt.amounts.credit +
      receipt.amounts.provision;

    return Math.abs(itemsTotal - amountsTotal) < 0.01; // Учет погрешности
  }

  return true;
};
```

## 📖 Дополнительные ресурсы

- 📋 [Федеральный закон №54-ФЗ](https://www.consultant.ru/document/cons_doc_LAW_200383/)
- 🏛️ [Документация ФНС](https://www.nalog.ru/rn77/taxation/reference_work/conception_vnp/)
- ⚡ [CloudPayments API](https://developers.cloudpayments.ru/#onlayn-cheki)

## 🔄 Рекурентные платежи и чеки

**Рекурентные платежи** - это автоматические регулярные списания с сохраненной карты покупателя. Особенно важны для подписочных моделей бизнеса.

### 🎯 Основные принципы

1. **Первый платеж** - обычный платеж с участием пользователя
2. **Сохранение карты** - автоматически при успешном первом платеже
3. **Регулярные списания** - без участия пользователя по расписанию
4. **Автоматические чеки** - формируются при каждом списании

### 📊 Структура типов для рекурентных платежей

```typescript
// Периоды повторения
enum RECURENT_PERIOD {
  DAY = 'Day', // Ежедневно
  WEEK = 'Week', // Еженедельно
  MONTH = 'Month', // Ежемесячно
}

// Настройки рекурентного платежа
interface Recurrent {
  interval: RECURENT_PERIOD; // Единица времени
  period: number; // Количество единиц
  customerReceipt: Receipt; // Шаблон чека для каждого списания
}
```

### 🎯 Практические примеры рекурентных платежей

#### 1. Месячная подписка на SaaS

```typescript
import {
  IPaymentData,
  RECURENT_PERIOD,
} from '@lmapp/react-native-cloudpayments';

const monthlySubscription = async () => {
  const paymentData: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef',
    amount: '999.00',
    currency: 'RUB',
    description: 'Подписка Premium',
    email: 'user@example.com',

    // Настройка рекурентного платежа
    recurrent: {
      interval: RECURENT_PERIOD.MONTH,
      period: 1, // каждый месяц

      // Чек для каждого автоматического списания
      customerReceipt: {
        items: [
          {
            label: 'Подписка Premium на месяц',
            price: 999,
            quantity: 1,
            amount: 999,
            vat: 20, // НДС 20%
            method: 4, // Полный расчет
            object: 4, // Услуга
          },
        ],
        taxationSystem: 1, // УСН доходы
        email: 'user@example.com', // Чек будет отправляться на этот email
      },
    },
  };

  try {
    const result = await PaymentService.presentPaymentForm(paymentData);
    console.log('Подписка создана:', result.transactionId);
  } catch (error) {
    console.log('Ошибка создания подписки:', error);
  }
};
```

#### 2. Квартальная подписка со скидкой

```typescript
const quarterlySubscription = async () => {
  const paymentData: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef',
    amount: '2700.00', // Скидка 10% (вместо 2997)
    currency: 'RUB',
    description: 'Квартальная подписка Premium',

    recurrent: {
      interval: RECURENT_PERIOD.MONTH,
      period: 3, // каждые 3 месяца

      customerReceipt: {
        items: [
          {
            label: 'Квартальная подписка Premium (скидка 10%)',
            price: 2700,
            quantity: 1,
            amount: 2700,
            vat: 20,
            method: 4,
            object: 4,
          },
        ],
        taxationSystem: 1,
        email: 'user@example.com',
      },
    },
  };
};
```

#### 3. Еженедельная доставка продуктов

```typescript
const weeklyDelivery = async () => {
  const paymentData: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef',
    amount: '1500.00',
    currency: 'RUB',
    description: 'Еженедельная доставка продуктов',

    recurrent: {
      interval: RECURENT_PERIOD.WEEK,
      period: 1, // каждую неделю

      customerReceipt: {
        items: [
          {
            label: 'Набор продуктов "Базовый"',
            price: 1200,
            quantity: 1,
            amount: 1200,
            vat: 10, // НДС 10% на продукты
            method: 4,
            object: 1, // Товар
          },
          {
            label: 'Доставка',
            price: 300,
            quantity: 1,
            amount: 300,
            vat: null, // Доставка без НДС
            method: 4,
            object: 4, // Услуга
          },
        ],
        taxationSystem: 1,
        phone: '+79991234567', // SMS с чеком
      },
    },
  };
};
```

#### 4. Ежедневная подписка на контент

```typescript
const dailyContent = async () => {
  const paymentData: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef',
    amount: '99.00',
    currency: 'RUB',
    description: 'Ежедневный доступ к премиум контенту',

    recurrent: {
      interval: RECURENT_PERIOD.DAY,
      period: 1, // каждый день

      customerReceipt: {
        items: [
          {
            label: 'Премиум контент на 1 день',
            price: 99,
            quantity: 1,
            amount: 99,
            vat: null, // УСН без НДС
            method: 4,
            object: 4, // Услуга
          },
        ],
        taxationSystem: 1,
        email: 'user@example.com',
      },
    },
  };
};
```

### 🔧 Передача через jsonData (альтернативный способ)

Если возникают проблемы с передачей рекурентных данных через параметры модуля:

```typescript
const subscriptionViaJsonData = async () => {
  const paymentData: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef',
    amount: '999.00',
    currency: 'RUB',
    description: 'Подписка Premium',

    // Передача через jsonData
    jsonData: {
      recurrent: {
        interval: 'Month',
        period: 1,
        customerReceipt: {
          items: [
            {
              label: 'Подписка Premium на месяц',
              price: 999,
              quantity: 1,
              amount: 999,
              vat: 20,
              method: 4,
              object: 4,
            },
          ],
          taxationSystem: 1,
          email: 'user@example.com',
        },
      },
    },
  };
};
```

### ⚠️ Важные особенности рекурентных чеков

#### 1. **Автоматическое формирование**

- Чек создается автоматически при каждом списании
- Использует шаблон из `customerReceipt`
- Не требует участия покупателя

#### 2. **Отправка покупателю**

- Email/SMS указанные в `customerReceipt` используются для каждого чека
- Покупатель получает уведомление о каждом списании
- Чек отправляется сразу после успешного платежа

#### 3. **Соответствие 54-ФЗ**

- Каждый рекурентный платеж = отдельный чек
- Все требования 54-ФЗ применяются к каждому чеку
- Чеки отправляются в налоговую автоматически

#### 4. **Управление подписками**

```typescript
// Отмена рекурентного платежа (через API)
const cancelSubscription = async (subscriptionId: string) => {
  // Используйте CloudPayments API для отмены
  // https://developers.cloudpayments.ru/#otmena-rekurrentnykh-platezhey
};
```

### 💡 Рекомендации

1. **Тестирование**: Всегда тестируйте рекурентные платежи в тестовом режиме
2. **Уведомления**: Информируйте пользователей о предстоящих списаниях
3. **Отмена**: Предоставьте простой способ отмены подписки
4. **Мониторинг**: Отслеживайте неуспешные рекурентные платежи
5. **Резервные карты**: Рассмотрите возможность привязки нескольких карт

## 📖 Дополнительные ресурсы
