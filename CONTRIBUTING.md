# 🤝 Руководство по участию в разработке

Добро пожаловать в CloudPayments React Native SDK! Это руководство поможет вам понять процесс разработки и автоматической публикации модуля.

## 🚀 Автоматизированный процесс релизов

### 📝 Конвенция коммитов

Мы используем [Conventional Commits](https://www.conventionalcommits.org/) для автоматической генерации changelog и версионирования.

#### Формат коммитов:

```
<тип>[область]: <описание>

[тело коммита]

[подвал]
```

#### Типы коммитов:

| Тип | Описание | Влияние на версию |
|-----|----------|-------------------|
| `feat` | Новая функциональность | MINOR |
| `fix` | Исправление бага | PATCH |
| `perf` | Оптимизация производительности | PATCH |
| `refactor` | Рефакторинг кода | PATCH |
| `docs` | Изменения в документации | PATCH |
| `style` | Форматирование кода | PATCH |
| `test` | Добавление/изменение тестов | PATCH |
| `build` | Изменения в сборке | PATCH |
| `ci` | Изменения в CI/CD | PATCH |
| `chore` | Рутинные задачи | Не влияет |

#### Примеры коммитов:

```bash
# Новая функциональность
feat: добавлена поддержка Apple Pay

# Исправление бага
fix: исправлена ошибка валидации карты

# Breaking change (MAJOR версия)
feat!: изменен API инициализации SDK

# С областью
feat(android): добавлена поддержка Google Pay
fix(ios): исправлена утечка памяти в PaymentForm
```

### 🔄 Процесс разработки

#### 1. Создание ветки

```bash
git checkout main
git pull origin main
git checkout -b feature/new-feature-name
```

#### 2. Разработка

- Делайте коммиты согласно конвенции
- Пишите тесты для новой функциональности
- Обновляйте документацию при необходимости

#### 3. Создание Pull Request

- Создайте PR в ветку `main`
- Опишите изменения в PR
- Дождитесь code review

#### 4. Автоматическая публикация

После merge PR в `main` автоматически запускается процесс:

1. ✅ **Проверки качества**:
   - Запуск тестов (`yarn test`)
   - Проверка типов (`yarn typecheck`)
   - Линтинг (`yarn lint`)
   - Сборка пакета (`yarn prepare`)

2. 📊 **Анализ изменений**:
   - Проверка изменений в `src/`, `android/`, `ios/`
   - Если изменений нет - публикация пропускается

3. 📝 **Генерация релиза**:
   - Автоматическое определение версии по коммитам
   - Обновление `CHANGELOG.md`
   - Создание Git тега
   - Коммит изменений

4. 📦 **Публикация**:
   - Публикация в npm registry
   - Публикация в GitHub Packages
   - Создание GitHub Release

### 📋 Локальные команды

```bash
# Автоматическое версионирование
yarn release:auto

# Принудительное версионирование
yarn release:major  # 1.0.0 -> 2.0.0
yarn release:minor  # 1.0.0 -> 1.1.0
yarn release:patch  # 1.0.0 -> 1.0.1

# Тестирование
yarn test
yarn typecheck
yarn lint

# Сборка
yarn prepare
```

## 🛠️ Настройка окружения

### Требования

- Node.js 16+
- Yarn 3.6+
- React Native CLI
- Android Studio (для Android)
- Xcode (для iOS)

### Установка

```bash
# Клонирование репозитория
git clone https://github.com/leonidmolchanov/react-native-cloudpayments-sdk.git
cd react-native-cloudpayments-sdk

# Установка зависимостей
yarn install

# Настройка iOS (только для macOS)
cd ios && pod install && cd ..
```

## 🧪 Тестирование

### Запуск тестов

```bash
# Все тесты
yarn test

# Тесты с покрытием
yarn test --coverage

# Тесты в watch режиме
yarn test --watch
```

### Тестирование в example приложении

```bash
# Запуск example приложения
yarn example start

# iOS
yarn example ios

# Android
yarn example android
```

## 📚 Документация

### Локальная разработка документации

```bash
# Запуск dev сервера
yarn docs:dev

# Сборка документации
yarn docs:build
```

### Обновление API документации

API документация генерируется автоматически из JSDoc комментариев:

```bash
yarn generate-api
```

## 🔐 Настройка секретов (для мейнтейнеров)

В настройках GitHub репозитория добавьте:

- `NPM_TOKEN` - токен для публикации в npm

## 📊 Мониторинг релизов

### GitHub Actions

Все релизы отслеживаются в разделе [Actions](https://github.com/leonidmolchanov/react-native-cloudpayments-sdk/actions).

### npm & GitHub Packages

Опубликованные версии доступны в двух местах:

**npm registry:**
```bash
npm install @lm/react-native-cloudpayments
# или
yarn add @lm/react-native-cloudpayments
```

**GitHub Packages:**
```bash
# Настройка .npmrc
echo "@leonidmolchanov:registry=https://npm.pkg.github.com/" >> .npmrc

# Установка
npm install @leonidmolchanov/react-native-cloudpayments
# или
yarn add @leonidmolchanov/react-native-cloudpayments
```

- **npm**: [npm package](https://www.npmjs.com/package/@lm/react-native-cloudpayments)
- **GitHub Packages**: [GitHub Packages](https://github.com/leonidmolchanov/react-native-cloudpayments-sdk/pkgs/npm/react-native-cloudpayments)

### Changelog

Все изменения документируются в [CHANGELOG.md](./CHANGELOG.md).

## ❓ Часто задаваемые вопросы

### Как откатить релиз?

```bash
# Удаление тега
git tag -d v1.2.3
git push origin :refs/tags/v1.2.3

# Откат коммита
git revert HEAD
```

### Как пропустить автоматическую публикацию?

Добавьте `[skip ci]` в сообщение коммита:

```bash
git commit -m "docs: обновление README [skip ci]"
```

### Как сделать hotfix?

```bash
# Создание ветки от последнего релиза
git checkout v1.2.3
git checkout -b hotfix/critical-fix

# После фикса
git commit -m "fix: критическое исправление"

# Merge в main через PR
```

## 🆘 Поддержка

- 🐛 [GitHub Issues](https://github.com/leonidmolchanov/react-native-cloudpayments-sdk/issues)
- 💬 [Discussions](https://github.com/leonidmolchanov/react-native-cloudpayments-sdk/discussions)
- 📧 [Email](mailto:leonidmolchanov@yandex.ru)

---

**Спасибо за участие в развитии CloudPayments React Native SDK!** 🚀 