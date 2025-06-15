---
sidebar_position: 1
---

# CloudPayments React Native SDK

Добро пожаловать в документацию **CloudPayments React Native SDK** — мощного и простого в использовании решения для интеграции платежей CloudPayments в ваши React Native приложения.

:::info Автор проекта
Создано **Leonid Molchanov** ([@leonidmolchanov](https://github.com/leonidmolchanov)) как независимый open-source проект для интеграции с платежной системой CloudPayments.

**MIT License** - свободная лицензия с открытым исходным кодом. Вы можете свободно использовать, изменять и распространять этот код в коммерческих и некоммерческих проектах.
:::

## 🚀 Что это такое?

CloudPayments React Native SDK предоставляет полный набор инструментов для интеграции платежной системы CloudPayments в мобильные приложения на React Native. SDK поддерживает все современные способы оплаты и обеспечивает безопасную обработку платежных данных.

## ✨ Основные возможности

- 💳 **Платежная форма** — готовая к использованию форма оплаты с поддержкой всех способов оплаты
- 🔒 **Безопасность** — шифрование данных карт и соответствие стандартам PCI DSS
- 📱 **Нативная интеграция** — полная поддержка iOS и Android с нативными компонентами
- 🎨 **Кастомизация** — гибкие настройки внешнего вида и поведения
- ⚡ **TypeScript** — полная типизация для лучшего developer experience
- 🌐 **Альтернативные способы оплаты** — Tinkoff Pay, СБП, SberPay

## 🎯 Поддерживаемые способы оплаты

| Способ оплаты | iOS | Android | Описание |
|---------------|-----|---------|----------|
| 💳 Банковские карты | ✅ | ✅ | Visa, MasterCard, МИР |
| 🍎 Apple Pay | ✅ | ❌ | Быстрая оплата через Touch/Face ID |
| 🤖 Google Pay | ❌ | ✅ | Быстрая оплата через Google |
| 🏦 Tinkoff Pay | ✅ | ✅ | Оплата через приложение Тинькофф |
| ⚡ СБП | ✅ | ✅ | Система быстрых платежей |
| 🟢 SberPay | ✅ | ✅ | Оплата через Сбербанк Онлайн |

## 🏁 Быстрый старт

### 1. Установка

```bash
npm install @lm/react-native-cloudpayments
# или
yarn add @lm/react-native-cloudpayments
```

### 2. Инициализация

```typescript
import { PaymentService } from '@lm/react-native-cloudpayments';

// Инициализация SDK с вашим Public ID
await PaymentService.init('pk_test_your_public_id');
```

### 3. Запуск платежной формы

```typescript
import { usePaymentForm } from '@lm/react-native-cloudpayments';

const MyComponent = () => {
  const presentPaymentForm = usePaymentForm('pk_test_your_public_id');

  const handlePayment = async () => {
    try {
      const result = await presentPaymentForm({
        amount: '1000.00',
        currency: 'RUB',
        description: 'Покупка товара',
        email: 'user@example.com'
      });

      if (result.success) {
        console.log('Платеж успешен!', result.transactionId);
      }
    } catch (error) {
      console.error('Ошибка платежа:', error);
    }
  };

  return (
    <Button title="Оплатить" onPress={handlePayment} />
  );
};
```

## 📋 Требования

- **React Native**: 0.70.0 или выше
- **iOS**: 12.0 или выше
- **Android**: API level 21 (Android 5.0) или выше
- **Node.js**: 16.0 или выше

## 🔑 Получение ключей

Для работы с SDK вам понадобится **Public ID** из личного кабинета CloudPayments:

1. Зарегистрируйтесь на [cloudpayments.ru](https://cloudpayments.ru)
2. Войдите в [личный кабинет](https://merchant.cloudpayments.ru)
3. Перейдите в раздел **API** → **Ключи**
4. Скопируйте **Public ID** (начинается с `pk_`)

:::tip Тестовый режим
Для разработки используйте тестовый Public ID (начинается с `pk_test_`). Он позволяет проводить тестовые платежи без реального списания средств.
:::

## 📚 Что дальше?

- [🚀 Установка и настройка](./getting-started) — подробное руководство по установке
- [📱 Настройка платформ](./platforms/android) — специфичные настройки для Android и iOS
- [💳 Использование платежной формы](./usage/payment-form) — работа с платежами
- [📖 API Reference](./api/overview) — полная документация API

## 🆘 Нужна помощь?

- 📖 [Документация CloudPayments](https://developers.cloudpayments.ru)
- 💬 [Telegram чат](https://t.me/cloudpayments_dev)
- 🐛 [GitHub Issues](https://github.com/leonidmolchanov/react-native-cloudpayments-sdk/issues)
- 📧 [Техподдержка](mailto:support@cloudpayments.ru)

---

**Готовы начать?** Переходите к [установке](./getting-started) и создайте свой первый платеж за 5 минут! 🚀
