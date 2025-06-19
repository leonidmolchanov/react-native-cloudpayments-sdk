---
sidebar_position: 4
title: 📷 Сканер банковских карт (CardIO)
description: Интеграция CardIO для сканирования банковских карт с помощью камеры устройства
keywords: [cardio, сканер карт, камера, банковские карты, мобильные платежи]
---

# 📷 Сканер банковских карт (CardIO)

CardIO - это библиотека для сканирования банковских карт с помощью камеры мобильного устройства. Вместо ручного ввода номера карты и срока действия, пользователь может просто навести камеру на карту.

## 🎯 Основные возможности

- **Автоматическое распознавание** номера карты и срока действия
- **Поддержка всех основных** платежных систем (Visa, MasterCard, МИР и др.)
- **Гибкая настройка** интерфейса и поведения сканера
- **Локализация** на 12+ языков
- **Material Design** цветовые схемы

## 🏗️ Архитектура и совместимость

### Поддерживаемые платформы

- **Android:** API 21+ (Android 5.0+) - полная поддержка
- **iOS:** iOS 11.0+ - **только новая архитектура (M-серия чипов)**

:::warning Важно для iOS
В модуле используется **кастомный скомпилированный фреймворк CardIO** специально адаптированный под новую архитектуру iOS. 

**Старая архитектура iOS НЕ ПОДДЕРЖИВАЕТСЯ** - работает только на устройствах с чипами M-серии и новой архитектурой React Native.
:::

## 🚀 Быстрый старт

### Включение сканера

Для включения CardIO сканера в платежной форме используйте поле `enableCardScanner`:

```typescript
import { useCloudPayments } from '@lmapp/react-native-cloudpayments';
import type { IPaymentData } from '@lmapp/react-native-cloudpayments';

const paymentData: IPaymentData = {
  publicId: 'pk_test_1234567890abcdef',
  amount: '1000.00',
  currency: 'RUB',
  description: 'Тестовый платеж',
  enableCardScanner: true, // ✅ Включаем сканер карт
};

const [presentPaymentForm] = useCloudPayments('pk_test_1234567890abcdef');

// Запуск платежной формы со сканером
await presentPaymentForm(paymentData);
```

### Базовая конфигурация

```typescript
import { 
  ECardIOLanguage, 
  ECardIOColorScheme 
} from '@lmapp/react-native-cloudpayments';
import type { ICardIOConfig, IPaymentData } from '@lmapp/react-native-cloudpayments';

const cardScannerConfig: ICardIOConfig = {
  requireExpiry: true,
  requireCVV: false,
  hideCardIOLogo: true,
  actionBarColor: ECardIOColorScheme.MATERIAL_BLUE,
  guideColor: ECardIOColorScheme.MATERIAL_GREEN,
  language: ECardIOLanguage.RUSSIAN
};

const paymentData: IPaymentData = {
  publicId: 'pk_test_1234567890abcdef',
  amount: '1000.00',
  currency: 'RUB',
  description: 'Тестовый платеж',
  enableCardScanner: true,
  cardScannerConfig: cardScannerConfig // ✅ Детальная настройка
};
```

## ⚙️ Конфигурация ICardIOConfig

### Поля карты

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| `requireExpiry` | `boolean` | `true` | Требовать ввод срока действия карты |
| `requireCVV` | `boolean` | `false` | Требовать ввод CVV кода ⚠️ **НЕ РЕКОМЕНДУЕТСЯ** |
| `requirePostalCode` | `boolean` | `false` | Требовать ввод почтового индекса |
| `requireCardholderName` | `boolean` | `false` | Требовать ввод имени держателя карты |

```typescript
const fieldsConfig: ICardIOConfig = {
  requireExpiry: true,        // ✅ Обязательно для валидации
  requireCVV: false,          // ❌ Небезопасно, лучше вводить отдельно
  requirePostalCode: false,   // Для дополнительной верификации
  requireCardholderName: true // Полезно для идентификации
};
```

### Настройки интерфейса

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| `hideCardIOLogo` | `boolean` | `true` | Скрыть логотип CardIO |
| `usePayPalLogo` | `boolean` | `false` | Использовать логотип PayPal |
| `suppressManualEntry` | `boolean` | `false` | Скрыть кнопку ручного ввода |

```typescript
const interfaceConfig: ICardIOConfig = {
  hideCardIOLogo: true,        // ✅ Для брендинга приложения
  usePayPalLogo: false,        // Только если интегрированы с PayPal
  suppressManualEntry: false   // Оставить возможность ручного ввода
};
```

### Цветовая схема

| Параметр | Тип | Описание |
|----------|-----|----------|
| `actionBarColor` | `ECardIOColorScheme \| string` | Цвет заголовка сканера |
| `guideColor` | `ECardIOColorScheme \| string` | Цвет рамки сканирования |

```typescript
// Использование предустановленных цветов
const colorsConfig: ICardIOConfig = {
  actionBarColor: ECardIOColorScheme.MATERIAL_BLUE,
  guideColor: ECardIOColorScheme.MATERIAL_GREEN,
};

// Использование кастомных hex-кодов
const customColorsConfig: ICardIOConfig = {
  actionBarColor: '#1976D2',
  guideColor: '#4CAF50',
};
```

#### Доступные цветовые схемы

```typescript
enum ECardIOColorScheme {
  MATERIAL_BLUE = '#2196F3',
  MATERIAL_GREEN = '#4CAF50',
  MATERIAL_RED = '#F44336',
  MATERIAL_ORANGE = '#FF9800',
  MATERIAL_PURPLE = '#9C27B0',
  MATERIAL_INDIGO = '#3F51B5',
  MATERIAL_CYAN = '#00BCD4',
  MATERIAL_PINK = '#E91E63',
  BLACK = '#000000',
  WHITE = '#FFFFFF',
  GRAY = '#9E9E9E',
}
```

### Локализация

| Параметр | Тип | Описание |
|----------|-----|----------|
| `language` | `ECardIOLanguage \| string` | Язык интерфейса сканера |

```typescript
const localizationConfig: ICardIOConfig = {
  language: ECardIOLanguage.RUSSIAN, // Русский интерфейс
};
```

#### Поддерживаемые языки

```typescript
enum ECardIOLanguage {
  ENGLISH = 'en',                    // Английский
  RUSSIAN = 'ru',                    // Русский
  FRENCH = 'fr',                     // Французский
  GERMAN = 'de',                     // Немецкий
  ITALIAN = 'it',                    // Итальянский
  JAPANESE = 'ja',                   // Японский
  KOREAN = 'ko',                     // Корейский
  PORTUGUESE = 'pt',                 // Португальский
  SWEDISH = 'sv',                    // Шведский
  CHINESE_SIMPLIFIED = 'zh-Hans',    // Китайский упрощенный
  CHINESE_TRADITIONAL = 'zh-Hant',   // Китайский традиционный
  SPANISH = 'es',                    // Испанский
}
```

### Дополнительные настройки

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| `suppressConfirmation` | `boolean` | `false` | Отключить вибрацию при сканировании |
| `suppressScan` | `boolean` | `false` | Отключить звук при сканировании |
| `keepApplicationTheme` | `boolean` | `false` | Сохранить тему приложения |

```typescript
const additionalConfig: ICardIOConfig = {
  suppressConfirmation: false, // Оставить вибрацию для UX
  suppressScan: false,         // Оставить звук для обратной связи
  keepApplicationTheme: true   // ✅ Использовать тему приложения
};
```

## 📋 Полные примеры

### Минимальная конфигурация

```typescript
import type { IPaymentData } from '@lmapp/react-native-cloudpayments';

const paymentData: IPaymentData = {
  publicId: 'pk_test_1234567890abcdef',
  amount: '1000.00',
  currency: 'RUB',
  description: 'Тестовый платеж',
  enableCardScanner: true // Используется конфигурация по умолчанию
};
```

### Рекомендуемая конфигурация

```typescript
import { 
  ECardIOLanguage, 
  ECardIOColorScheme 
} from '@lmapp/react-native-cloudpayments';

const recommendedConfig: ICardIOConfig = {
  // Поля карты
  requireExpiry: true,          // ✅ Обязательно для валидации
  requireCVV: false,            // ❌ Безопасность превыше всего
  requirePostalCode: false,
  requireCardholderName: false,

  // Интерфейс
  hideCardIOLogo: true,         // ✅ Чистый брендинг
  usePayPalLogo: false,
  suppressManualEntry: false,   // ✅ Оставить выбор пользователю

  // Цвета
  actionBarColor: ECardIOColorScheme.MATERIAL_BLUE,
  guideColor: ECardIOColorScheme.MATERIAL_GREEN,

  // Локализация
  language: ECardIOLanguage.RUSSIAN,

  // Дополнительно
  suppressConfirmation: false,
  suppressScan: false,
  keepApplicationTheme: true    // ✅ Единообразие с приложением
};
```

### Максимальная конфигурация

```typescript
const fullConfig: ICardIOConfig = {
  // Все поля карты
  requireExpiry: true,
  requireCVV: false,            // ⚠️ НЕ РЕКОМЕНДУЕТСЯ
  requirePostalCode: true,
  requireCardholderName: true,

  // Полная настройка интерфейса
  hideCardIOLogo: true,
  usePayPalLogo: false,
  suppressManualEntry: false,

  // Кастомные цвета
  actionBarColor: '#1976D2',    // Material Blue 700
  guideColor: '#388E3C',        // Material Green 700

  // Локализация
  language: ECardIOLanguage.RUSSIAN,

  // Все дополнительные настройки
  suppressConfirmation: false,
  suppressScan: false,
  keepApplicationTheme: true
};

const paymentData: IPaymentData = {
  publicId: 'pk_test_1234567890abcdef',
  amount: '1000.00',
  currency: 'RUB',
  description: 'Премиум платеж с полным сканером',
  email: 'user@example.com',
  accountId: 'user_12345',
  enableCardScanner: true,
  cardScannerConfig: fullConfig
};
```

## 🔄 Логика активации

CardIO сканер активируется по следующим правилам:

1. **Автоматическая активация:** Если передана `cardScannerConfig`, сканер включается автоматически
2. **Явная активация:** Установка `enableCardScanner: true`
3. **По умолчанию:** Если ничего не указано, создается базовый сканер с настройками по умолчанию

```typescript
// 1. Автоматическая активация
const paymentData1 = {
  // ... базовые поля
  cardScannerConfig: { requireExpiry: true } // ✅ Сканер включится автоматически
};

// 2. Явная активация
const paymentData2 = {
  // ... базовые поля
  enableCardScanner: true // ✅ Явное включение
};

// 3. Активация с конфигурацией
const paymentData3 = {
  // ... базовые поля
  enableCardScanner: true,
  cardScannerConfig: { /* настройки */ } // ✅ Лучший подход
};
```

## 📊 Возвращаемые данные

После успешного сканирования CardIO возвращает:

- **Номер карты** - распознанный номер банковской карты
- **Месяц истечения** - месяц срока действия (если `requireExpiry: true`)
- **Год истечения** - год срока действия (если `requireExpiry: true`)
- **CVV** - код безопасности (если `requireCVV: true`)
- **Имя держателя** - имя на карте (если `requireCardholderName: true`)

Данные автоматически подставляются в поля платежной формы.

## 🔒 Безопасность

### ✅ Рекомендации

1. **CVV:** Не собирайте CVV через сканер - вводите в защищенной форме
2. **Шифрование:** Все данные шифруются перед отправкой в CloudPayments
3. **Локальная обработка:** Сканирование происходит локально на устройстве

### ⚠️ Ограничения

- **Разрешения:** Требуется разрешение на использование камеры
- **Освещение:** Качество зависит от освещения
- **Архитектура iOS:** Только новая архитектура с M-чипами

## 🐛 Отладка

### Проверка активации

```typescript
const paymentData: IPaymentData = {
  // ... базовые поля
  enableCardScanner: true,
  cardScannerConfig: {
    requireExpiry: true,
    language: ECardIOLanguage.RUSSIAN
  }
};

console.log('CardIO конфигурация:', paymentData.cardScannerConfig);
```

### Частые проблемы

| Проблема | Причина | Решение |
|----------|---------|---------|
| Сканер не запускается | Нет разрешения камеры | Проверить разрешения в настройках |
| Неверные цвета | Неправильный hex-код | Использовать `ECardIOColorScheme` |
| Локализация не работает | Неподдерживаемый язык | Проверить `ECardIOLanguage` |
| iOS не работает | Старая архитектура | Обновить до новой архитектуры |

## 🎨 Кастомизация интерфейса

### Цветовая схема бренда

```typescript
// Пример для синего бренда
const brandBlueConfig: ICardIOConfig = {
  actionBarColor: '#1565C0',    // Темно-синий заголовок
  guideColor: '#42A5F5',        // Светло-синяя рамка
  keepApplicationTheme: true
};

// Пример для зеленого бренда
const brandGreenConfig: ICardIOConfig = {
  actionBarColor: '#2E7D32',    // Темно-зеленый заголовок
  guideColor: '#66BB6A',        // Светло-зеленая рамка
  keepApplicationTheme: true
};
```

### Минималистичный дизайн

```typescript
const minimalConfig: ICardIOConfig = {
  hideCardIOLogo: true,         // Убрать все логотипы
  usePayPalLogo: false,
  actionBarColor: '#FFFFFF',    // Белый заголовок
  guideColor: '#000000',        // Черная рамка
  suppressConfirmation: true,   // Убрать вибрацию
  suppressScan: true,           // Убрать звуки
  keepApplicationTheme: true
};
```

## 📱 Интеграция с React Native

### Использование с хуками

```typescript
import { useCloudPayments } from '@lmapp/react-native-cloudpayments';

function PaymentScreen() {
  const [presentPaymentForm, { isLoading }] = useCloudPayments(
    'pk_test_1234567890abcdef',
    {
      onSuccess: (data) => {
        console.log('Платеж успешен:', data.transactionId);
      },
      onError: (data) => {
        console.error('Ошибка:', data.message);
      }
    }
  );

  const handlePayment = async () => {
    const paymentData: IPaymentData = {
      publicId: 'pk_test_1234567890abcdef',
      amount: '1000.00',
      currency: 'RUB',
      description: 'Покупка в приложении',
      enableCardScanner: true,
      cardScannerConfig: {
        requireExpiry: true,
        language: ECardIOLanguage.RUSSIAN,
        actionBarColor: ECardIOColorScheme.MATERIAL_BLUE
      }
    };

    await presentPaymentForm(paymentData);
  };

  return (
    <TouchableOpacity onPress={handlePayment} disabled={isLoading}>
      <Text>Оплатить со сканером карт</Text>
    </TouchableOpacity>
  );
}
```

---

## 🔗 Связанные разделы

- [Платежная форма](./payment-form.md) - Основные возможности платежной формы
- [Чеки и 54-ФЗ](./receipts.md) - Работа с онлайн-кассами
- [Android платформа](../platforms/android.md) - Особенности Android
- [iOS платформа](../platforms/ios.md) - Особенности iOS

## 💡 Полезные ссылки

- [CardIO GitHub](https://github.com/card-io/card.io-Android-SDK) - Официальный репозиторий
- [CloudPayments API](https://docs.cloudpayments.ru/) - Документация API
- [Material Design Colors](https://material.io/design/color/) - Гайд по цветам 