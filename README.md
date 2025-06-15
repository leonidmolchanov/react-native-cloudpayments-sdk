# CloudPayments React Native SDK

<div align="center">

![CloudPayments Logo](https://cloudpayments.ru/Content/img/cp_logo.svg)

**Мощный и простой в использовании SDK для интеграции платежей CloudPayments в React Native приложения**

[![npm version](https://img.shields.io/npm/v/@lm/react-native-cloudpayments.svg)](https://www.npmjs.com/package/@lm/react-native-cloudpayments)
[![npm downloads](https://img.shields.io/npm/dm/@lm/react-native-cloudpayments.svg)](https://www.npmjs.com/package/@lm/react-native-cloudpayments)
[![License](https://img.shields.io/github/license/lm/react-native-cloudpayments.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-iOS%20%7C%20Android-lightgrey.svg)](https://reactnative.dev/)

[📖 Документация](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/) • [🚀 Быстрый старт](#-быстрый-старт) • [💳 Способы оплаты](#-способы-оплаты) • [🔧 API](#-api)

</div>

## ✨ Возможности

- 💳 **Платежная форма** — готовая к использованию форма оплаты
- 🔒 **Безопасность** — шифрование данных карт и соответствие PCI DSS
- 📱 **Нативная интеграция** — полная поддержка iOS и Android
- 🎨 **Кастомизация** — гибкие настройки внешнего вида
- ⚡ **TypeScript** — полная типизация для лучшего DX
- 🌐 **Альтернативные способы оплаты** — Apple Pay, Google Pay, Tinkoff Pay, СБП, SberPay

## 💳 Способы оплаты

| Способ оплаты | iOS | Android | Описание |
|---------------|-----|---------|----------|
| 💳 Банковские карты | ✅ | ✅ | Visa, MasterCard, МИР |
| 🍎 Apple Pay | ✅ | ❌ | Быстрая оплата через Touch/Face ID |
| 🤖 Google Pay | ❌ | ✅ | Быстрая оплата через Google |
| 🏦 Tinkoff Pay | ✅ | ✅ | Оплата через приложение Тинькофф |
| ⚡ СБП | ✅ | ✅ | Система быстрых платежей |
| 🟢 SberPay | ✅ | ✅ | Оплата через Сбербанк Онлайн |

## 🚀 Быстрый старт

### 1. Установка

```bash
npm install @lm/react-native-cloudpayments
# или
yarn add @lm/react-native-cloudpayments
```

### 2. Установка зависимостей

```bash
# iOS
cd ios && pod install && cd ..

# Android - автолинковка работает автоматически
```

### 3. Использование

```typescript
import React from 'react';
import { Button, Alert } from 'react-native';
import { usePaymentForm, PaymentService } from '@lm/react-native-cloudpayments';

// Инициализация SDK
await PaymentService.init('pk_test_your_public_id');

const PaymentScreen = () => {
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
        Alert.alert('Успех!', `Платеж прошел! ID: ${result.transactionId}`);
      } else {
        Alert.alert('Ошибка', result.message);
      }
    } catch (error) {
      Alert.alert('Ошибка', 'Произошла ошибка при обработке платежа');
    }
  };

  return <Button title="Оплатить 1000 ₽" onPress={handlePayment} />;
};
```

## 📋 Требования

- **React Native**: 0.70.0+
- **iOS**: 12.0+
- **Android**: API level 21 (Android 5.0)+
- **Node.js**: 16.0+

## 🔧 API

### PaymentService

```typescript
// Инициализация SDK
await PaymentService.init('pk_test_your_public_id');

// Запуск платежной формы
const result = await PaymentService.presentPaymentForm({
  amount: '1000.00',
  currency: 'RUB',
  description: 'Покупка товара'
});
```

### usePaymentForm Hook

```typescript
const presentPaymentForm = usePaymentForm('pk_test_your_public_id');

const result = await presentPaymentForm({
  amount: '1000.00',
  currency: 'RUB',
  description: 'Покупка товара'
});
```

### События

```typescript
import { eventEmitter, EPaymentFormEventName } from '@lm/react-native-cloudpayments';

eventEmitter.addListener(EPaymentFormEventName.PAYMENT_FORM, (event) => {
  switch (event.action) {
    case 'willDisplay':
      console.log('Форма готовится к показу');
      break;
    case 'transaction':
      if (event.statusCode) {
        console.log('Платеж успешен:', event.transactionId);
      }
      break;
  }
});
```

## 🧪 Тестирование

### Тестовые карты

| Номер карты | Результат | CVV | Срок |
|-------------|-----------|-----|------|
| `4111 1111 1111 1111` | Успешный платеж | `123` | `12/25` |
| `4000 0000 0000 0002` | Отклонен банком | `123` | `12/25` |
| `4000 0000 0000 0077` | Требует 3DS | `123` | `12/25` |

> ⚠️ **Важно**: Используйте тестовый Public ID (начинается с `pk_test_`) для разработки!

## 📱 Настройка платформ

### Android

1. **Минимальная версия**: API level 21
2. **Network Security Config**: Настройте для CloudPayments домена
3. **ProGuard**: Добавьте правила для CloudPayments SDK

Подробнее: [📖 Настройка Android](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/docs/platforms/android)

### iOS

1. **Минимальная версия**: iOS 12.0
2. **Apple Pay**: Настройте Merchant ID в Xcode
3. **URL Schemes**: Добавьте схемы для банковских приложений

Подробнее: [📖 Настройка iOS](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/docs/platforms/ios)

## 🔑 Получение ключей

1. Зарегистрируйтесь на [cloudpayments.ru](https://cloudpayments.ru)
2. Войдите в [личный кабинет](https://merchant.cloudpayments.ru)
3. Перейдите в **Настройки** → **API**
4. Скопируйте ваш **Public ID**

## 📚 Документация

- [📖 Полная документация](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/)
- [🚀 Быстрый старт](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/docs/getting-started)
- [📱 Настройка платформ](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/docs/platforms/android)
- [💳 Использование](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/docs/usage/payment-form)
- [💳 Использование платежной формы](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/docs/usage/payment-form)

## 🤝 Поддержка

- 📖 [Документация CloudPayments](https://developers.cloudpayments.ru)
- 💬 [Telegram чат](https://t.me/cloudpayments_dev)
- 🐛 [GitHub Issues](https://github.com/leonidmolchanov/react-native-cloudpayments-sdk/issues)
- 📧 [Техподдержка](mailto:support@cloudpayments.ru)

## 📄 Лицензия

MIT License. См. [LICENSE](LICENSE) для деталей.

## 👨‍💻 Автор

Создано **Leonid Molchanov** ([@leonidmolchanov](https://github.com/leonidmolchanov)) как независимый open-source проект для интеграции с платежной системой CloudPayments.

## 🙏 Благодарности

- [CloudPayments](https://cloudpayments.ru) за отличный платежный API
- [React Native](https://reactnative.dev) сообщество
- Всем контрибьюторам проекта

## 📄 Лицензия

**MIT License** - свободная лицензия с открытым исходным кодом.

Вы можете свободно использовать, изменять и распространять этот код в коммерческих и некоммерческих проектах. См. [LICENSE](LICENSE) для деталей.

---

<div align="center">

**Сделано с ❤️ Leonid Molchanov для React Native разработчиков**

[⭐ Поставьте звезду на GitHub](https://github.com/leonidmolchanov/react-native-cloudpayments-sdk) • [📖 Документация](https://leonidmolchanov.github.io/react-native-cloudpayments-sdk/)

</div>
