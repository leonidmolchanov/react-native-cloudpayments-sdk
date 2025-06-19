---
sidebar_position: 3
---

# 🔄 Рекурентные платежи

Полное руководство по настройке автоматических регулярных платежей и подписок.

## 📋 Обзор

**Рекурентные платежи** позволяют автоматически списывать деньги с сохраненной карты покупателя через определенные интервалы времени. Идеально подходят для:

✅ **Подписочные сервисы** (SaaS, стриминг, контент)  
✅ **Регулярные услуги** (коммунальные платежи, страховка)  
✅ **Периодические доставки** (продукты, товары)  
✅ **Автоматическое пополнение** (мобильная связь, игры)

## 🎯 Как это работает

### Жизненный цикл рекурентного платежа:

1. **Первый платеж** 👤

   - Пользователь вводит данные карты
   - Проходит обычная оплата с участием пользователя
   - Карта автоматически сохраняется для будущих списаний

2. **Регулярные списания** 🤖

   - Происходят автоматически по расписанию
   - Без участия пользователя
   - С сохраненной карты

3. **Автоматические чеки** 🧾
   - Формируются при каждом списании
   - Отправляются покупателю по email/SMS
   - Соответствуют требованиям 54-ФЗ

## ⚠️ Важно: Передача данных в CloudPayments API

CloudPayments API имеет определенную специфику работы с параметрами. **Данные можно передавать двумя способами:**

### 1. **Через параметры модуля** (рекомендуемый способ)

```typescript
const paymentData: IPaymentData = {
  publicId: 'pk_test_...',
  amount: '1000.00',
  currency: 'RUB',
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
    recurrent: {
      /* настройки подписки */
    },
  },
};
```

> **💡 Важно**: В силу определенных нюансов CloudPayments API, данные на уровне модуля могут собираться или разбираться как на нашей стороне, так и на стороне SDK. **Если что-то не получается передать через параметры модуля или не работает корректно - попробуйте передать эти данные через `jsonData`!**

## 🏗️ Структура типов

### RECURENT_PERIOD - Периоды повторения

```typescript
enum RECURENT_PERIOD {
  DAY = 'Day', // Ежедневно
  WEEK = 'Week', // Еженедельно
  MONTH = 'Month', // Ежемесячно
}
```

### Recurrent - Настройки рекурентного платежа

```typescript
interface Recurrent {
  interval: RECURENT_PERIOD; // Единица времени (день/неделя/месяц)
  period: number; // Количество единиц (1, 2, 3...)
  customerReceipt: Receipt; // Шаблон чека для каждого списания
}
```

**Примеры периодичности:**

- `interval: MONTH, period: 1` = каждый месяц
- `interval: MONTH, period: 3` = каждые 3 месяца (квартал)
- `interval: WEEK, period: 2` = каждые 2 недели
- `interval: DAY, period: 7` = каждые 7 дней

## 🎯 Практические примеры

### 1. Месячная подписка на SaaS

```typescript
import {
  IPaymentData,
  RECURENT_PERIOD,
  PaymentService,
} from '@lmapp/react-native-cloudpayments';

const createMonthlySubscription = async () => {
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
    return result;
  } catch (error) {
    console.log('Ошибка создания подписки:', error);
    throw error;
  }
};
```

### 2. Квартальная подписка со скидкой

```typescript
const createQuarterlySubscription = async () => {
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

  return await PaymentService.presentPaymentForm(paymentData);
};
```

### 3. Еженедельная доставка продуктов

```typescript
const createWeeklyDelivery = async () => {
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

  return await PaymentService.presentPaymentForm(paymentData);
};
```

### 4. Ежедневная подписка на контент

```typescript
const createDailyContent = async () => {
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

  return await PaymentService.presentPaymentForm(paymentData);
};
```

### 5. Гибкая подписка с пробным периодом

```typescript
const createTrialSubscription = async () => {
  // Первый платеж - пробный период (7 дней за 1 рубль)
  const trialPayment: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef',
    amount: '1.00', // Символическая плата
    currency: 'RUB',
    description: 'Пробный период Premium (7 дней)',

    recurrent: {
      interval: RECURENT_PERIOD.DAY,
      period: 7, // через 7 дней

      // После пробного периода - полная стоимость
      customerReceipt: {
        items: [
          {
            label: 'Подписка Premium (после пробного периода)',
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
  };

  return await PaymentService.presentPaymentForm(trialPayment);
};
```

## 🔧 Передача через jsonData

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

  return await PaymentService.presentPaymentForm(paymentData);
};
```

## ⚠️ Важные особенности

### 1. **Автоматическое формирование чеков**

- Чек создается автоматически при каждом списании
- Использует шаблон из `customerReceipt`
- Не требует участия покупателя
- Соответствует всем требованиям 54-ФЗ

### 2. **Отправка покупателю**

- Email/SMS из `customerReceipt` используются для каждого чека
- Покупатель получает уведомление о каждом списании
- Чек отправляется сразу после успешного платежа

### 3. **Управление подписками**

```typescript
// Отмена рекурентного платежа (через CloudPayments API)
const cancelSubscription = async (subscriptionId: string) => {
  // Используйте CloudPayments API для отмены
  // https://developers.cloudpayments.ru/#otmena-rekurrentnykh-platezhey

  const response = await fetch(
    'https://api.cloudpayments.ru/subscriptions/cancel',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(publicId + ':' + apiSecret),
      },
      body: JSON.stringify({
        Id: subscriptionId,
      }),
    }
  );

  return await response.json();
};
```

### 4. **Обработка неуспешных платежей**

```typescript
// Мониторинг статуса подписки
const checkSubscriptionStatus = async (subscriptionId: string) => {
  const response = await fetch(
    'https://api.cloudpayments.ru/subscriptions/get',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa(publicId + ':' + apiSecret),
      },
      body: JSON.stringify({
        Id: subscriptionId,
      }),
    }
  );

  const result = await response.json();

  if (result.Model.Status === 'Rejected') {
    // Подписка отклонена (проблемы с картой)
    console.log('Подписка отклонена:', result.Model.StatusDescription);
  }

  return result;
};
```

## 💡 Лучшие практики

### 1. **Тестирование**

```typescript
// Всегда тестируйте в тестовом режиме
const testSubscription = async () => {
  const paymentData: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef', // Тестовый ключ
    amount: '1.00', // Минимальная сумма для теста
    currency: 'RUB',
    description: 'Тест подписки',

    recurrent: {
      interval: RECURENT_PERIOD.DAY,
      period: 1, // Каждый день для быстрого тестирования
      customerReceipt: {
        items: [
          {
            label: 'Тестовая подписка',
            price: 1,
            quantity: 1,
            amount: 1,
            method: 4,
            object: 4,
          },
        ],
        taxationSystem: 1,
        email: 'test@example.com',
      },
    },
  };

  return await PaymentService.presentPaymentForm(paymentData);
};
```

### 2. **Уведомления пользователей**

```typescript
// Информируйте пользователей о предстоящих списаниях
const notifyUpcomingPayment = async (
  userId: string,
  amount: number,
  date: Date
) => {
  // Отправка push-уведомления или email
  await sendNotification(userId, {
    title: 'Предстоящий платеж',
    message: `${amount} руб. будет списано ${date.toLocaleDateString()}`,
    action: 'manage_subscription',
  });
};
```

### 3. **Простая отмена подписки**

```typescript
// Предоставьте простой способ отмены
const SubscriptionManager = () => {
  const cancelSubscription = async () => {
    Alert.alert(
      'Отмена подписки',
      'Вы уверены, что хотите отменить подписку?',
      [
        { text: 'Нет', style: 'cancel' },
        {
          text: 'Да, отменить',
          style: 'destructive',
          onPress: async () => {
            try {
              await cancelSubscription(subscriptionId);
              Alert.alert('Подписка отменена');
            } catch (error) {
              Alert.alert('Ошибка отмены подписки');
            }
          }
        }
      ]
    );
  };

  return (
    <Button
      title="Отменить подписку"
      onPress={cancelSubscription}
      color="red"
    />
  );
};
```

### 4. **Резервные карты**

```typescript
// Рассмотрите возможность привязки нескольких карт
const addBackupCard = async (userId: string) => {
  // Сохранение дополнительной карты для резерва
  const paymentData: IPaymentData = {
    publicId: 'pk_test_1234567890abcdef',
    amount: '1.00', // Минимальная сумма для привязки
    currency: 'RUB',
    description: 'Привязка резервной карты',
    accountId: userId,
    // Без recurrent - только сохранение карты
  };

  return await PaymentService.presentPaymentForm(paymentData);
};
```

## 📊 Аналитика и мониторинг

### Отслеживание метрик подписок:

```typescript
interface SubscriptionMetrics {
  totalSubscriptions: number;
  activeSubscriptions: number;
  churnRate: number; // Процент отмен
  averageLifetime: number; // Средняя продолжительность подписки
  monthlyRecurringRevenue: number; // MRR
}

const calculateMetrics = async (): Promise<SubscriptionMetrics> => {
  // Получение данных через CloudPayments API
  // и расчет ключевых метрик
};
```

## 📖 Дополнительные ресурсы

- 🔄 [CloudPayments API - Рекурентные платежи](https://developers.cloudpayments.ru/#rekurrentnye-platezhi)
- 📋 [Управление подписками](https://developers.cloudpayments.ru/#upravlenie-rekurrentnymi-platezhami)
- 🧾 [Чеки для рекурентных платежей](https://developers.cloudpayments.ru/#rekvizitimy-dlya-onlayn-cheka)
- ⚠️ [Обработка ошибок](https://developers.cloudpayments.ru/#obrabotka-oshibok)

---

> **💡 Совет**: Начните с простой месячной подписки и постепенно добавляйте сложные сценарии (пробные периоды, скидки, семейные планы). Всегда тестируйте в тестовом режиме перед запуском в продакшн!
