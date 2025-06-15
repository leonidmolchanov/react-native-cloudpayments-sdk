# @lm/react-native-cloudpayments

CloudPayments SDK for React Native with TypeScript support.

## Installation

```sh
npm install @lm/react-native-cloudpayments
```

or

```sh
yarn add @lm/react-native-cloudpayments
```

### iOS Setup

```sh
cd ios && pod install
```

### Android Setup

No additional setup required for Android.

## Usage

```typescript
import { PaymentService, CardService } from '@lm/react-native-cloudpayments';

// Initialize the SDK
await PaymentService.initialize('your_public_id');

// Present payment form
const result = await PaymentService.presentPaymentForm({
  amount: 1000,
  currency: 'RUB',
  invoiceId: 'invoice_123',
  description: 'Test payment',
  email: 'user@example.com',
});

console.log('Payment result:', result);
```

## Features

- ✅ TypeScript support
- ✅ iOS and Android support
- ✅ Payment forms
- ✅ Card validation
- ✅ Event listeners
- ✅ Apple Pay support (iOS)
- ✅ SBP, T-Pay, SberPay support

## API Documentation

See the [TypeScript definitions](./src/types/) for complete API documentation.

## License

MIT

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## Made with create-react-native-library

This library was bootstrapped using [`create-react-native-library`](https://github.com/callstack/react-native-builder-bob).
