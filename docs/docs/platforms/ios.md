---
sidebar_position: 2
---

# 🍎 Настройка iOS

Подробное руководство по настройке CloudPayments SDK для iOS платформы.

## 📋 Требования

- **iOS**: 12.0 или выше
- **Xcode**: 14.0 или выше
- **Swift**: 5.7 или выше
- **CocoaPods**: 1.11.0 или выше

## ⚙️ Настройка проекта

### 1. Обновление Podfile

```ruby title="ios/Podfile"
require_relative '../node_modules/react-native/scripts/react_native_pods'
require_relative '../node_modules/@react-native-community/cli-platform-ios/native_modules'

platform :ios, '12.0'
install! 'cocoapods', :deterministic_uuids => false

target 'YourApp' do
  config = use_native_modules!

  use_react_native!(
    :path => config[:reactNativePath],
    :hermes_enabled => true,
    :fabric_enabled => false,
    :flipper_configuration => FlipperConfiguration.enabled,
    :app_path => "#{Pod::Config.instance.installation_root}/.."
  )

  # CloudPayments SDK зависимости
  pod 'CloudPayments', '~> 1.4.0'

  target 'YourAppTests' do
    inherit! :complete
  end

  post_install do |installer|
    react_native_post_install(
      installer,
      config[:reactNativePath],
      :mac_catalyst_enabled => false
    )

    # Настройка deployment target
    installer.pods_project.targets.each do |target|
      target.build_configurations.each do |config|
        config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '12.0'
      end
    end
  end
end
```

### 2. Установка зависимостей

```bash
cd ios
pod install
cd ..
```

## 🍎 Apple Pay

### 1. Настройка Apple Pay в Xcode

1. Откройте проект в Xcode: `ios/YourApp.xcworkspace`
2. Выберите ваш проект в навигаторе
3. Перейдите на вкладку **Signing & Capabilities**
4. Нажмите **+ Capability** и добавьте **Apple Pay**
5. Настройте Merchant ID

### 2. Создание Merchant ID

1. Войдите в [Apple Developer Console](https://developer.apple.com)
2. Перейдите в **Certificates, Identifiers & Profiles**
3. Выберите **Identifiers** → **Merchant IDs**
4. Создайте новый Merchant ID (например: `merchant.com.yourcompany.yourapp`)

### 3. Настройка в коде

```typescript
// Проверка доступности Apple Pay
import { PaymentService } from '@lmapp/react-native-cloudpayments';

const checkApplePay = async () => {
  try {
    const isAvailable = await PaymentService.isApplePayAvailable();
    console.log('Apple Pay доступен:', isAvailable);
  } catch (error) {
    console.log('Apple Pay недоступен:', error);
  }
};
```

### 4. Настройка платежной формы с Apple Pay

```typescript
const paymentData = {
  amount: '1000.00',
  currency: 'RUB',
  description: 'Покупка товара',
  email: 'user@example.com',
  applePayMerchantId: 'merchant.com.yourcompany.yourapp', // Ваш Merchant ID
  showApplePay: true,
};
```

## 🔐 Настройка безопасности

### 1. App Transport Security (ATS)

Добавьте в `ios/YourApp/Info.plist`:

```xml title="ios/YourApp/Info.plist"
<dict>
    <!-- Другие настройки -->

    <key>NSAppTransportSecurity</key>
    <dict>
        <key>NSExceptionDomains</key>
        <dict>
            <key>cloudpayments.ru</key>
            <dict>
                <key>NSExceptionRequiresForwardSecrecy</key>
                <false/>
                <key>NSExceptionMinimumTLSVersion</key>
                <string>TLSv1.2</string>
                <key>NSIncludesSubdomains</key>
                <true/>
            </dict>
            <key>api.cloudpayments.ru</key>
            <dict>
                <key>NSExceptionRequiresForwardSecrecy</key>
                <false/>
                <key>NSExceptionMinimumTLSVersion</key>
                <string>TLSv1.2</string>
            </dict>
        </dict>
    </dict>
</dict>
```

### 2. Разрешения камеры (для сканирования карт)

```xml title="ios/YourApp/Info.plist"
<key>NSCameraUsageDescription</key>
<string>Приложение использует камеру для сканирования банковских карт</string>
```

## 🔗 URL Schemes

### 1. Настройка для банковских приложений

Добавьте в `ios/YourApp/Info.plist`:

```xml title="ios/YourApp/Info.plist"
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLName</key>
        <string>com.yourcompany.yourapp.payments</string>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>yourapp</string>
        </array>
    </dict>
</array>

<!-- Для открытия банковских приложений -->
<key>LSApplicationQueriesSchemes</key>
<array>
    <string>tinkoffbank</string>
    <string>sberpay</string>
    <string>sbbol</string>
    <string>alfabank</string>
    <string>vtb24</string>
</array>
```

### 2. Обработка в React Native

```typescript
import { Linking } from 'react-native';

useEffect(() => {
  const handleURL = (url: string) => {
    console.log('URL received:', url);
    // Обработка возврата из банковского приложения
  };

  const subscription = Linking.addEventListener('url', handleURL);

  return () => subscription?.remove();
}, []);
```

## 🎨 Кастомизация UI

### 1. Настройка цветов

```swift title="ios/YourApp/AppDelegate.mm"
#import <CloudPayments/CloudPayments-Swift.h>

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  // Настройка цветовой схемы CloudPayments
  [CloudPaymentsSDK setThemeColor:[UIColor colorWithRed:0.0 green:0.48 blue:1.0 alpha:1.0]];
  [CloudPaymentsSDK setAccentColor:[UIColor colorWithRed:0.2 green:0.78 blue:0.35 alpha:1.0]];

  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}
```

### 2. Темная тема

SDK автоматически поддерживает темную тему iOS. Для кастомизации:

```swift
// Настройка для темной темы
if (@available(iOS 13.0, *)) {
    if (UITraitCollection.currentTraitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
        [CloudPaymentsSDK setDarkThemeColor:[UIColor colorWithRed:0.04 green:0.52 blue:1.0 alpha:1.0]];
    }
}
```

## 🔧 Build Settings

### 1. Настройка в Xcode

Откройте `ios/YourApp.xcworkspace` и настройте:

**Build Settings:**

- **iOS Deployment Target**: 12.0
- **Swift Language Version**: Swift 5
- **Enable Bitcode**: No (для React Native)

**Signing & Capabilities:**

- Настройте Team и Bundle Identifier
- Добавьте Apple Pay capability
- Настройте Push Notifications (если нужны)

### 2. Схемы сборки

```xml title="ios/YourApp/Info.plist"
<!-- Для Debug сборки -->
<key>NSAllowsArbitraryLoads</key>
<true/>

<!-- Для Release сборки уберите эту настройку -->
```

## 📱 Тестирование

### 1. Симулятор iOS

```bash
# Запуск на симуляторе
npx react-native run-ios

# Конкретный симулятор
npx react-native run-ios --simulator="iPhone 14 Pro"
```

### 2. Реальное устройство

```bash
# Запуск на подключенном устройстве
npx react-native run-ios --device
```

### 3. Тестирование Apple Pay

:::warning Важно
Apple Pay работает только на реальных устройствах! В симуляторе Apple Pay недоступен.
:::

Для тестирования Apple Pay:

1. Добавьте тестовую карту в Wallet
2. Используйте Sandbox окружение
3. Проверьте настройки региона (Apple Pay доступен не во всех странах)

## 🚨 Решение проблем

### 1. Ошибки сборки

```bash
# Очистка кеша
cd ios
rm -rf Pods Podfile.lock
pod install
cd ..

# Очистка Xcode
# В Xcode: Product → Clean Build Folder
```

### 2. Проблемы с CocoaPods

```bash
# Обновление CocoaPods
sudo gem install cocoapods

# Обновление репозитория
pod repo update

# Переустановка зависимостей
cd ios
pod deintegrate
pod install
```

### 3. Ошибки линковки

Если возникают ошибки линковки:

1. Откройте `ios/YourApp.xcworkspace` в Xcode
2. Перейдите в **Build Phases** → **Link Binary With Libraries**
3. Убедитесь, что все необходимые фреймворки добавлены

### 4. Проблемы с Apple Pay

```typescript
// Проверка настроек Apple Pay
const checkApplePaySetup = async () => {
  try {
    const canMakePayments = await PaymentService.canMakeApplePayPayments();
    const canSetupCards = await PaymentService.canSetupApplePayCards();

    console.log('Can make payments:', canMakePayments);
    console.log('Can setup cards:', canSetupCards);
  } catch (error) {
    console.error('Apple Pay setup error:', error);
  }
};
```

## 📋 Отладка

### 1. Логирование

```swift title="ios/YourApp/AppDelegate.mm"
// Включение подробного логирования для отладки
#ifdef DEBUG
  [CloudPaymentsSDK setDebugMode:YES];
#endif
```

### 2. Просмотр логов

```bash
# Просмотр логов устройства
xcrun simctl spawn booted log stream --predicate 'subsystem contains "CloudPayments"'

# Или через Console.app на Mac
```

## ✅ Чек-лист

Убедитесь, что выполнены все пункты:

- [ ] ✅ iOS Deployment Target 12.0+
- [ ] ✅ Установлены CocoaPods зависимости
- [ ] ✅ Настроен Apple Pay (если используется)
- [ ] ✅ Добавлены URL Schemes
- [ ] ✅ Настроен ATS в Info.plist
- [ ] ✅ Добавлены разрешения камеры
- [ ] ✅ Протестировано на реальном устройстве
- [ ] ✅ Проверена работа в темной теме

---

**Готово!** 🎉 Ваше iOS приложение готово к приему платежей через CloudPayments!
