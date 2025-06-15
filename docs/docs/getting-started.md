---
sidebar_position: 2
---

# 🚀 Быстрый старт

Это руководство поможет вам интегрировать CloudPayments SDK в ваше React Native приложение за несколько минут.

## 📋 Предварительные требования

Убедитесь, что у вас установлены:

- **React Native**: 0.70.0 или выше
- **Node.js**: 16.0 или выше
- **iOS**: Xcode 14+ для разработки под iOS
- **Android**: Android Studio с API level 21+

## 📦 Установка

### 1. Установка пакета

```bash
npm install @lm/react-native-cloudpayments
```

или с Yarn:

```bash
yarn add @lm/react-native-cloudpayments
```

### 2. Установка зависимостей для iOS

```bash
cd ios && pod install && cd ..
```

### 3. Настройка автолинковки

SDK использует React Native автолинковку, поэтому дополнительная настройка не требуется для большинства случаев.

## 🔑 Получение ключей CloudPayments

1. **Регистрация**: Зарегистрируйтесь на [cloudpayments.ru](https://cloudpayments.ru)
2. **Личный кабинет**: Войдите в [merchant.cloudpayments.ru](https://merchant.cloudpayments.ru)
3. **API ключи**: Перейдите в **Настройки** → **API**
4. **Копирование**: Скопируйте ваш **Public ID**

:::tip Тестовые ключи
- **Тестовый Public ID**: начинается с `pk_test_`
- **Боевой Public ID**: начинается с `pk_live_`

Всегда используйте тестовые ключи во время разработки!
:::

## ⚡ Первый платеж

### 1. Инициализация SDK

```typescript
import { PaymentService } from '@lm/react-native-cloudpayments';

// В начале приложения (например, в App.tsx)
const initializePayments = async () => {
  try {
    await PaymentService.init('pk_test_your_public_id_here');
    console.log('CloudPayments SDK инициализирован');
  } catch (error) {
    console.error('Ошибка инициализации:', error);
  }
};

// Вызовите при запуске приложения
initializePayments();
```

### 2. Создание компонента оплаты

```typescript
import React from 'react';
import { View, Button, Alert } from 'react-native';
import { usePaymentForm } from '@lm/react-native-cloudpayments';

const PaymentScreen = () => {
  const presentPaymentForm = usePaymentForm('pk_test_your_public_id_here');

  const handlePayment = async () => {
    try {
      const result = await presentPaymentForm({
        amount: '1000.00',
        currency: 'RUB',
        description: 'Тестовый платеж',
        email: 'test@example.com',
        requireEmail: true,
        showResultScreen: true,
      });

      if (result.success) {
        Alert.alert(
          'Успех!', 
          `Платеж прошел успешно!\nID транзакции: ${result.transactionId}`
        );
      } else {
        Alert.alert('Ошибка', result.message || 'Платеж не удался');
      }
    } catch (error) {
      Alert.alert('Ошибка', 'Произошла ошибка при обработке платежа');
      console.error('Payment error:', error);
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', padding: 20 }}>
      <Button
        title="Оплатить 1000 ₽"
        onPress={handlePayment}
      />
    </View>
  );
};

export default PaymentScreen;
```

### 3. Обработка событий платежа

```typescript
import { eventEmitter, EPaymentFormEventName } from '@lm/react-native-cloudpayments';

// Подписка на события платежной формы
useEffect(() => {
  const subscription = eventEmitter.addListener(
    EPaymentFormEventName.PAYMENT_FORM,
    (event) => {
      switch (event.action) {
        case 'willDisplay':
          console.log('Форма готовится к показу');
          break;
        case 'didDisplay':
          console.log('Форма отображена');
          break;
        case 'willHide':
          console.log('Форма скрывается');
          break;
        case 'didHide':
          console.log('Форма скрыта');
          break;
        case 'transaction':
          if (event.statusCode) {
            console.log('Платеж успешен:', event.transactionId);
          } else {
            console.log('Ошибка платежа:', event.message);
          }
          break;
      }
    }
  );

  return () => subscription.remove();
}, []);
```

## 🧪 Тестирование

### Тестовые карты

Для тестирования используйте следующие номера карт:

| Номер карты | Результат | CVV | Срок |
|-------------|-----------|-----|------|
| `4111 1111 1111 1111` | Успешный платеж | `123` | `12/25` |
| `4000 0000 0000 0002` | Отклонен банком | `123` | `12/25` |
| `4000 0000 0000 0077` | Требует 3DS | `123` | `12/25` |

### Тестовые суммы

- **Суммы < 10 ₽**: Всегда успешные
- **Суммы 10-99 ₽**: Случайный результат
- **Суммы ≥ 100 ₽**: Требуют 3DS подтверждение

## ✅ Проверка установки

Запустите ваше приложение и убедитесь, что:

1. ✅ Приложение собирается без ошибок
2. ✅ SDK инициализируется успешно
3. ✅ Платежная форма открывается
4. ✅ Тестовый платеж проходит

## 🚨 Частые проблемы

### iOS

```bash
# Если возникают ошибки сборки
cd ios
rm -rf Pods Podfile.lock
pod install
cd ..
```

### Android

```bash
# Очистка кеша
cd android
./gradlew clean
cd ..
```

### Metro

```bash
# Сброс кеша Metro
npx react-native start --reset-cache
```

## 📱 Следующие шаги

Теперь, когда базовая интеграция готова, изучите:

- [📱 Настройка Android](./platforms/android) — специфичные настройки для Android
- [🍎 Настройка iOS](./platforms/ios) — настройки для iOS и Apple Pay
- [💳 Работа с платежной формой](./usage/payment-form) — расширенные возможности
- [⚡ События и хуки](./usage/events) — обработка событий платежей

---

**Поздравляем!** 🎉 Вы успешно интегрировали CloudPayments в ваше приложение! 