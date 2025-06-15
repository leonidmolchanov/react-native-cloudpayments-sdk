---
sidebar_position: 1
---

# 🤖 Настройка Android

Подробное руководство по настройке CloudPayments SDK для Android платформы.

## 📋 Требования

- **Android API Level**: 21 (Android 5.0) или выше
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Java**: 8 или выше
- **Kotlin**: 1.8.0 или выше

## ⚙️ Настройка проекта

### 1. Обновление `android/build.gradle`

```gradle title="android/build.gradle"
buildscript {
    ext {
        buildToolsVersion = "34.0.0"
        minSdkVersion = 21
        compileSdkVersion = 34
        targetSdkVersion = 34
        ndkVersion = "25.1.8937393"
        kotlinVersion = "1.8.0"
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("com.facebook.react:react-native-gradle-plugin")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}
```

### 2. Настройка `android/app/build.gradle`

```gradle title="android/app/build.gradle"
android {
    compileSdkVersion rootProject.ext.compileSdkVersion

    defaultConfig {
        applicationId "com.yourcompany.yourapp"
        minSdkVersion rootProject.ext.minSdkVersion
        targetSdkVersion rootProject.ext.targetSdkVersion
        versionCode 1
        versionName "1.0"
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    // Другие зависимости...
}
```

## 🔐 Настройка безопасности

### 1. Network Security Config

Создайте файл `android/app/src/main/res/xml/network_security_config.xml`:

```xml title="android/app/src/main/res/xml/network_security_config.xml"
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.cloudpayments.ru</domain>
        <domain includeSubdomains="true">widget.cloudpayments.ru</domain>
    </domain-config>
    
    <!-- Для тестирования в debug режиме -->
    <debug-overrides>
        <trust-anchors>
            <certificates src="system"/>
            <certificates src="user"/>
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

### 2. Обновление AndroidManifest.xml

```xml title="android/app/src/main/AndroidManifest.xml"
<application
    android:name=".MainApplication"
    android:label="@string/app_name"
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:allowBackup="false"
    android:theme="@style/AppTheme"
    android:networkSecurityConfig="@xml/network_security_config">
    
    <!-- Разрешения для интернета -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- Основная активность -->
    <activity
        android:name=".MainActivity"
        android:exported="true"
        android:launchMode="singleTask"
        android:windowSoftInputMode="adjustResize">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

## 💳 Google Pay

### 1. Настройка Google Pay

Добавьте в `android/app/src/main/AndroidManifest.xml`:

```xml title="android/app/src/main/AndroidManifest.xml"
<application>
    <!-- Google Pay -->
    <meta-data
        android:name="com.google.android.gms.wallet.api.enabled"
        android:value="true" />
</application>
```

### 2. Зависимости для Google Pay

```gradle title="android/app/build.gradle"
dependencies {
    implementation 'com.google.android.gms:play-services-wallet:19.2.1'
}
```

### 3. Настройка в коде

```typescript
// Проверка доступности Google Pay
import { PaymentService } from '@lm/react-native-cloudpayments';

const checkGooglePay = async () => {
  try {
    const isAvailable = await PaymentService.isGooglePayAvailable();
    console.log('Google Pay доступен:', isAvailable);
  } catch (error) {
    console.log('Google Pay недоступен:', error);
  }
};
```

## 🔗 Deep Links

### 1. Настройка для банковских приложений

```xml title="android/app/src/main/AndroidManifest.xml"
<activity android:name=".MainActivity">
    <!-- Основные intent-filter -->
    
    <!-- Для возврата из банковских приложений -->
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https"
              android:host="yourapp.com"
              android:pathPrefix="/payment" />
    </intent-filter>
    
    <!-- Для Tinkoff Pay -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="yourapp" />
    </intent-filter>
</activity>
```

### 2. Обработка в React Native

```typescript
import { Linking } from 'react-native';

useEffect(() => {
  const handleDeepLink = (url: string) => {
    console.log('Deep link received:', url);
    // Обработка возврата из банковского приложения
  };

  const subscription = Linking.addEventListener('url', handleDeepLink);
  
  return () => subscription?.remove();
}, []);
```

## 🎨 Кастомизация UI

### 1. Темы и стили

Создайте `android/app/src/main/res/values/styles.xml`:

```xml title="android/app/src/main/res/values/styles.xml"
<resources>
    <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
        <!-- Основные цвета -->
        <item name="colorPrimary">#007AFF</item>
        <item name="colorPrimaryDark">#0056CC</item>
        <item name="colorAccent">#007AFF</item>
        
        <!-- Цвета для платежной формы -->
        <item name="cloudpayments_primary_color">#007AFF</item>
        <item name="cloudpayments_accent_color">#34C759</item>
    </style>
</resources>
```

### 2. Цвета для темной темы

Создайте `android/app/src/main/res/values-night/styles.xml`:

```xml title="android/app/src/main/res/values-night/styles.xml"
<resources>
    <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
        <item name="colorPrimary">#0A84FF</item>
        <item name="colorPrimaryDark">#0056CC</item>
        <item name="colorAccent">#0A84FF</item>
    </style>
</resources>
```

## 🔧 ProGuard

### 1. Настройка ProGuard

Добавьте в `android/app/proguard-rules.pro`:

```proguard title="android/app/proguard-rules.pro"
# CloudPayments SDK
-keep class ru.cloudpayments.** { *; }
-keep class com.cloudpayments.** { *; }

# Сохранение моделей данных
-keep class * implements java.io.Serializable { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
```

### 2. Включение ProGuard

```gradle title="android/app/build.gradle"
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
        }
    }
}
```

## 🚨 Решение проблем

### 1. Ошибки сборки

```bash
# Очистка проекта
cd android
./gradlew clean

# Пересборка
./gradlew assembleDebug

# Если проблемы с кешем
./gradlew clean build --refresh-dependencies
```

### 2. Проблемы с зависимостями

```gradle title="android/app/build.gradle"
android {
    packagingOptions {
        pickFirst '**/libc++_shared.so'
        pickFirst '**/libjsc.so'
    }
}
```

### 3. Ошибки Kotlin

```gradle title="android/build.gradle"
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url "https://www.jitpack.io" }
    }
}
```

## 📱 Тестирование

### 1. Запуск на эмуляторе

```bash
# Запуск Metro
npx react-native start

# Запуск на Android
npx react-native run-android
```

### 2. Отладка

```bash
# Просмотр логов
adb logcat | grep -i cloudpayments

# Подключение отладчика
adb shell input keyevent 82
```

### 3. Тестирование платежей

- Используйте тестовые карты из [документации](../getting-started#тестовые-карты)
- Проверьте работу в разных версиях Android
- Протестируйте на реальных устройствах

## ✅ Чек-лист

Убедитесь, что выполнены все пункты:

- [ ] ✅ Минимальная версия Android API 21
- [ ] ✅ Настроен Network Security Config
- [ ] ✅ Добавлены разрешения в AndroidManifest
- [ ] ✅ Настроены Deep Links
- [ ] ✅ Добавлены правила ProGuard
- [ ] ✅ Протестирована сборка в release режиме
- [ ] ✅ Проверена работа на разных устройствах

---

**Готово!** 🎉 Ваше Android приложение готово к приему платежей через CloudPayments! 