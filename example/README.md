# CloudPayments SDK Example App

Демонстрационное приложение для тестирования и изучения возможностей CloudPayments React Native SDK.

## 🚀 Быстрый старт

### ⚠️ Важно: Настройка PUBLIC_ID

**Перед запуском приложения обязательно укажите ваш публичный ключ CloudPayments!**

1. Откройте файл `src/App.tsx`
2. Найдите строку с `PUBLIC_ID`
3. Замените тестовый ключ на ваш:

```typescript
// Замените этот ключ на ваш
const PUBLIC_ID = 'pk_ваш_ключ_здесь';
```

### 📋 Где получить PUBLIC_ID

1. Войдите в [личный кабинет CloudPayments](https://merchant.cloudpayments.ru/)
2. Перейдите в раздел **"Настройки"** → **"API"**
3. Скопируйте **"Public ID"** (формат: `pk_xxxxxxxxxxxxxxxxxxxxxxxx`)

## 🛠️ Установка и запуск

### Предварительные требования

Убедитесь, что у вас установлены:

- [Node.js](https://nodejs.org/) (версия 18 или выше)
- [React Native CLI](https://reactnative.dev/docs/environment-setup)
- [Android Studio](https://developer.android.com/studio) (для Android)
- [Xcode](https://developer.apple.com/xcode/) (для iOS, только на macOS)

### Шаг 1: Установка зависимостей

```bash
# Установка npm зависимостей
npm install

# Для iOS: установка CocoaPods зависимостей
cd ios && pod install && cd ..
```

### Шаг 2: Запуск Metro

```bash
npm start
```

### Шаг 3: Запуск приложения

#### Android

```bash
npm run android
```

#### iOS

```bash
npm run ios
```

## 📱 Возможности Example App

### 🎯 Текущие функции

- **Оплата через форму** - Демонстрация стандартной платежной формы CloudPayments
- **Отображение статуса** - Реальное время отслеживания состояния SDK
- **Обработка событий** - Показ всех callback'ов (успех, ошибка, отмена, прогресс)
- **Красивый UI** - Современный интерфейс с Material Design

### 🔮 Планируемые функции

- **Прямая оплата картой** - Ввод данных карты в приложении
- **Apple Pay** - Интеграция с Apple Pay
- **Google Pay** - Интеграция с Google Pay
- **Настройки** - Конфигурация различных параметров SDK

## 🧪 Тестирование

### Тестовые данные

Приложение использует следующие тестовые данные для платежей:

- **Сумма**: 1000.00 RUB
- **Описание**: "Тестовый платеж из Example App"
- **Email**: test@example.com
- **ID пользователя**: user_12345

### Тестовые карты CloudPayments

Для тестирования используйте следующие карты:

| Номер карты           | Результат           |
| --------------------- | ------------------- |
| `4242 4242 4242 4242` | Успешный платеж     |
| `4000 0000 0000 0002` | Отклонен банком     |
| `4000 0000 0000 0069` | Истек срок действия |

**Дата истечения**: любая будущая дата (например, 12/25)  
**CVV**: любые 3 цифры (например, 123)

## 🏗️ Структура проекта

```
example/
├── src/
│   ├── App.tsx          # Главный компонент приложения
│   └── form.ts          # Схемы валидации (устаревший файл)
├── android/             # Android специфичные файлы
├── ios/                 # iOS специфичные файлы
├── package.json         # Зависимости проекта
└── README.md           # Этот файл
```

## 🎨 Кастомизация

### Изменение тестовых данных

Отредактируйте объект `SAMPLE_PAYMENT_DATA` в `src/App.tsx`:

```typescript
const SAMPLE_PAYMENT_DATA: IPaymentData = {
  amount: '2000.00', // Ваша сумма
  currency: 'USD', // Ваша валюта
  description: 'Ваше описание',
  email: 'your@email.com',
  accountId: 'your_user_id',
  requireEmail: true,
  showResultScreen: true,
};
```

### Добавление новых секций

1. Добавьте новый тип в `TSection`
2. Добавьте секцию в массив `SECTIONS`
3. Реализуйте логику в `renderSectionContent()`

## 🐛 Устранение неполадок

### Проблемы с запуском

1. **Metro не запускается**:

   ```bash
   npx react-native start --reset-cache
   ```

2. **Ошибки сборки Android**:

   ```bash
   cd android && ./gradlew clean && cd ..
   npm run android
   ```

3. **Ошибки сборки iOS**:
   ```bash
   cd ios && pod install && cd ..
   npm run ios
   ```

### Проблемы с SDK

1. **Ошибка "Invalid PUBLIC_ID"**:

   - Проверьте правильность ключа
   - Убедитесь, что ключ активен в личном кабинете

2. **Платежи не проходят**:
   - Проверьте настройки мерчанта
   - Убедитесь, что тестовый режим включен

## 📞 Поддержка

- **Документация SDK**: [Ссылка на документацию]
- **Техподдержка CloudPayments**: support@cloudpayments.ru
- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)

## 📄 Лицензия

Этот example проект распространяется под той же лицензией, что и основной SDK.

---

**Удачного тестирования! 🚀**
