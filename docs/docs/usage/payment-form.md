---
sidebar_position: 1
---

# 💳 Платежная форма

Подробное руководство по использованию платежной формы CloudPayments SDK.

## 🚀 Основы

Платежная форма — это готовый к использованию UI компонент, который обрабатывает весь процесс оплаты от ввода данных карты до получения результата.

### Простейший пример

```typescript
import { usePaymentForm } from '@lm/react-native-cloudpayments';

const PaymentScreen = () => {
  const presentPaymentForm = usePaymentForm('pk_test_your_public_id');

  const handlePayment = async () => {
    const result = await presentPaymentForm({
      amount: '1000.00',
      currency: 'RUB',
      description: 'Покупка товара',
    });

    if (result.success) {
      console.log('Платеж успешен!', result.transactionId);
    }
  };

  return <Button title="Оплатить" onPress={handlePayment} />;
};
```

## 📊 Параметры платежа

### Обязательные параметры

```typescript
interface IPaymentData {
  /** Сумма платежа в формате "1000.00" */
  amount: string;
  
  /** Валюта платежа (RUB, USD, EUR) */
  currency: string;
  
  /** Описание платежа */
  description: string;
}
```

### Дополнительные параметры

```typescript
interface IPaymentData {
  // Обязательные поля выше...
  
  /** Email плательщика */
  email?: string;
  
  /** Обязательность ввода email */
  requireEmail?: boolean;
  
  /** Номер счета/заказа */
  invoiceId?: string;
  
  /** Дополнительные данные */
  jsonData?: Record<string, any>;
  
  /** Показывать экран результата */
  showResultScreen?: boolean;
  
  /** Apple Pay Merchant ID */
  applePayMerchantId?: string;
  
  /** Показывать кнопку Apple Pay */
  showApplePay?: boolean;
  
  /** Показывать кнопку Google Pay */
  showGooglePay?: boolean;
}
```

## 🎯 Примеры использования

### 1. Базовый платеж

```typescript
const basicPayment = async () => {
  const result = await presentPaymentForm({
    amount: '500.00',
    currency: 'RUB',
    description: 'Покупка кофе',
  });
  
  if (result.success) {
    Alert.alert('Успех!', `Платеж прошел. ID: ${result.transactionId}`);
  } else {
    Alert.alert('Ошибка', result.message);
  }
};
```

### 2. Платеж с email

```typescript
const paymentWithEmail = async () => {
  const result = await presentPaymentForm({
    amount: '2500.00',
    currency: 'RUB',
    description: 'Подписка Premium',
    email: 'user@example.com',
    requireEmail: true, // Email обязателен
    showResultScreen: true, // Показать экран результата
  });
};
```

### 3. Платеж с дополнительными данными

```typescript
const paymentWithMetadata = async () => {
  const result = await presentPaymentForm({
    amount: '1500.00',
    currency: 'RUB',
    description: 'Заказ #12345',
    invoiceId: 'ORDER_12345',
    email: 'customer@example.com',
    jsonData: {
      userId: '12345',
      productId: 'premium_subscription',
      promoCode: 'SAVE20',
      metadata: {
        source: 'mobile_app',
        version: '1.0.0',
      },
    },
  });
};
```

### 4. Платеж с Apple Pay

```typescript
const applePayPayment = async () => {
  const result = await presentPaymentForm({
    amount: '3000.00',
    currency: 'RUB',
    description: 'Покупка в магазине',
    applePayMerchantId: 'merchant.com.yourcompany.yourapp',
    showApplePay: true,
    email: 'user@example.com',
  });
};
```

## 🎨 Кастомизация

### 1. Настройка внешнего вида

```typescript
// Настройка цветовой схемы (выполняется в нативном коде)
// См. документацию по платформам для деталей

const customizedPayment = async () => {
  const result = await presentPaymentForm({
    amount: '1000.00',
    currency: 'RUB',
    description: 'Стильный платеж',
    showResultScreen: true,
  });
};
```

### 2. Локализация

SDK автоматически определяет язык устройства и показывает форму на соответствующем языке:

- 🇷🇺 Русский
- 🇺🇸 Английский
- 🇺🇦 Украинский

## ⚡ Обработка событий

### 1. Подписка на события формы

```typescript
import { eventEmitter, EPaymentFormEventName } from '@lm/react-native-cloudpayments';

useEffect(() => {
  const subscription = eventEmitter.addListener(
    EPaymentFormEventName.PAYMENT_FORM,
    (event) => {
      switch (event.action) {
        case 'willDisplay':
          console.log('Форма готовится к показу');
          setLoading(true);
          break;
          
        case 'didDisplay':
          console.log('Форма отображена');
          setLoading(false);
          break;
          
        case 'willHide':
          console.log('Форма скрывается');
          break;
          
        case 'didHide':
          console.log('Форма скрыта');
          setPaymentFormVisible(false);
          break;
          
        case 'transaction':
          if (event.statusCode) {
            console.log('Платеж успешен:', event.transactionId);
            handleSuccessfulPayment(event.transactionId);
          } else {
            console.log('Ошибка платежа:', event.message);
            handlePaymentError(event.message);
          }
          break;
      }
    }
  );

  return () => subscription.remove();
}, []);
```

### 2. Обработка результата

```typescript
const handlePaymentResult = (result: IPaymentFormResponse) => {
  if (result.success) {
    // Успешный платеж
    console.log('Transaction ID:', result.transactionId);
    
    // Отправка на сервер для подтверждения
    confirmPaymentOnServer(result.transactionId);
    
    // Показ экрана успеха
    navigation.navigate('PaymentSuccess', {
      transactionId: result.transactionId,
    });
  } else {
    // Ошибка платежа
    console.log('Payment failed:', result.message);
    
    // Логирование ошибки
    logPaymentError(result.message);
    
    // Показ ошибки пользователю
    Alert.alert('Ошибка платежа', result.message);
  }
};
```

## 🔒 Безопасность

### 1. Валидация на сервере

:::warning Важно
Всегда проверяйте результат платежа на вашем сервере! Никогда не полагайтесь только на клиентский результат.
:::

```typescript
const confirmPaymentOnServer = async (transactionId: number) => {
  try {
    const response = await fetch('/api/payments/confirm', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ transactionId }),
    });
    
    const data = await response.json();
    
    if (data.confirmed) {
      console.log('Платеж подтвержден на сервере');
    } else {
      console.log('Платеж не подтвержден:', data.reason);
    }
  } catch (error) {
    console.error('Ошибка подтверждения:', error);
  }
};
```

### 2. Обработка чувствительных данных

```typescript
// ❌ Неправильно - не сохраняйте данные карт
const badExample = {
  cardNumber: '4111111111111111', // Никогда не делайте так!
  cvv: '123',
};

// ✅ Правильно - используйте только результат платежа
const goodExample = {
  transactionId: result.transactionId,
  amount: '1000.00',
  status: 'completed',
};
```

## 🧪 Тестирование

### Тестовые карты

| Номер карты | Результат | CVV | Срок |
|-------------|-----------|-----|------|
| `4111 1111 1111 1111` | Успешный платеж | `123` | `12/25` |
| `4000 0000 0000 0002` | Отклонен банком | `123` | `12/25` |
| `4000 0000 0000 0077` | Требует 3DS | `123` | `12/25` |

:::tip Важно
Всегда используйте тестовый Public ID (начинается с `pk_test_`) для разработки!
:::

## 🚨 Обработка ошибок

### 1. Типы ошибок

```typescript
const handlePaymentError = (error: any) => {
  if (error.code) {
    switch (error.code) {
      case 'NETWORK_ERROR':
        Alert.alert('Ошибка сети', 'Проверьте подключение к интернету');
        break;
        
      case 'INVALID_CARD':
        Alert.alert('Неверные данные карты', 'Проверьте номер карты и срок действия');
        break;
        
      case 'INSUFFICIENT_FUNDS':
        Alert.alert('Недостаточно средств', 'На карте недостаточно средств для оплаты');
        break;
        
      case 'CARD_BLOCKED':
        Alert.alert('Карта заблокирована', 'Обратитесь в ваш банк');
        break;
        
      default:
        Alert.alert('Ошибка платежа', error.message || 'Неизвестная ошибка');
    }
  } else {
    Alert.alert('Ошибка', 'Произошла неожиданная ошибка');
  }
};
```

### 2. Retry логика

```typescript
const paymentWithRetry = async (maxRetries = 3) => {
  let attempt = 0;
  
  while (attempt < maxRetries) {
    try {
      const result = await presentPaymentForm({
        amount: '1000.00',
        currency: 'RUB',
        description: 'Платеж с повторами',
      });
      
      if (result.success) {
        return result;
      } else if (result.message?.includes('network')) {
        // Повторяем только при сетевых ошибках
        attempt++;
        await new Promise(resolve => setTimeout(resolve, 1000 * attempt));
      } else {
        // Не повторяем при других ошибках
        throw new Error(result.message);
      }
    } catch (error) {
      if (attempt === maxRetries - 1) {
        throw error;
      }
      attempt++;
    }
  }
};
```

## 📱 Лучшие практики

### 1. UX рекомендации

```typescript
const PaymentComponent = () => {
  const [isLoading, setIsLoading] = useState(false);
  const presentPaymentForm = usePaymentForm('pk_test_your_public_id');

  const handlePayment = async () => {
    setIsLoading(true);
    
    try {
      // Показываем индикатор загрузки
      const result = await presentPaymentForm({
        amount: '1000.00',
        currency: 'RUB',
        description: 'Покупка товара',
        showResultScreen: true, // Показываем результат в форме
      });
      
      if (result.success) {
        // Переходим на экран успеха
        navigation.navigate('Success');
      }
    } catch (error) {
      // Обрабатываем ошибки
      handlePaymentError(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View>
      <Button
        title={isLoading ? 'Обработка...' : 'Оплатить 1000 ₽'}
        onPress={handlePayment}
        disabled={isLoading}
      />
    </View>
  );
};
```

### 2. Производительность

```typescript
// ✅ Мемоизируйте функцию платежа
const presentPaymentForm = useMemo(
  () => usePaymentForm(publicId),
  [publicId]
);

// ✅ Предзагружайте данные
useEffect(() => {
  // Предзагрузка публичного ключа
  PaymentService.init(publicId);
}, []);

// ✅ Очищайте подписки
useEffect(() => {
  const subscription = eventEmitter.addListener(/* ... */);
  return () => subscription.remove();
}, []);
```

---

**Готово!** 🎉 Теперь вы знаете все о работе с платежной формой CloudPayments! 