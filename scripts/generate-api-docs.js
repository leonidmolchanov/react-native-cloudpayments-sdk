#!/usr/bin/env node

/**
 * Скрипт для генерации API документации из JSDoc комментариев
 * Создает Markdown файлы для Docusaurus на основе TypeScript типов
 */

const fs = require('fs');
const path = require('path');

// Пути к файлам
const SRC_DIR = path.join(__dirname, '../src');
const DOCS_DIR = path.join(__dirname, '../docs/docs/api');
const TYPES_DIR = path.join(SRC_DIR, 'types');

// Создаем директории если их нет
if (!fs.existsSync(DOCS_DIR)) {
  fs.mkdirSync(DOCS_DIR, { recursive: true });
}

// Функция для извлечения JSDoc комментариев
function extractJSDoc(content) {
  const jsdocRegex = /\/\*\*([\s\S]*?)\*\//g;
  const matches = [];
  let match;

  while ((match = jsdocRegex.exec(content)) !== null) {
    matches.push(match[1]);
  }

  return matches;
}

// Функция для парсинга JSDoc
function parseJSDoc(jsdoc) {
  const lines = jsdoc.split('\n').map((line) => line.replace(/^\s*\*\s?/, ''));

  const result = {
    description: '',
    params: [],
    returns: '',
    example: '',
    since: '',
    deprecated: false,
  };

  let currentSection = 'description';
  let currentExample = [];

  for (const line of lines) {
    if (line.startsWith('@param')) {
      currentSection = 'params';
      const paramMatch = line.match(/@param\s+\{([^}]+)\}\s+(\w+)\s*-?\s*(.*)/);
      if (paramMatch) {
        result.params.push({
          type: paramMatch[1],
          name: paramMatch[2],
          description: paramMatch[3],
        });
      }
    } else if (line.startsWith('@returns') || line.startsWith('@return')) {
      currentSection = 'returns';
      result.returns = line.replace(/@returns?\s*/, '');
    } else if (line.startsWith('@example')) {
      currentSection = 'example';
      currentExample = [];
    } else if (line.startsWith('@since')) {
      result.since = line.replace(/@since\s*/, '');
    } else if (line.startsWith('@deprecated')) {
      result.deprecated = true;
    } else if (currentSection === 'description' && line.trim()) {
      result.description += line + '\n';
    } else if (currentSection === 'example') {
      currentExample.push(line);
    }
  }

  if (currentExample.length > 0) {
    result.example = currentExample.join('\n');
  }

  return result;
}

// Функция для генерации Markdown
function generateMarkdown(title, content, jsdocs) {
  let markdown = `---
sidebar_position: 1
---

# ${title}

${content}

`;

  // Добавляем JSDoc документацию
  jsdocs.forEach((jsdoc, index) => {
    const parsed = parseJSDoc(jsdoc);

    if (parsed.description) {
      markdown += `## Описание

${parsed.description.trim()}

`;
    }

    if (parsed.params.length > 0) {
      markdown += `## Параметры

| Параметр | Тип | Описание |
|----------|-----|----------|
`;
      parsed.params.forEach((param) => {
        markdown += `| \`${param.name}\` | \`${param.type}\` | ${param.description} |\n`;
      });
      markdown += '\n';
    }

    if (parsed.returns) {
      markdown += `## Возвращает

${parsed.returns}

`;
    }

    if (parsed.example) {
      markdown += `## Пример

\`\`\`typescript
${parsed.example}
\`\`\`

`;
    }

    if (parsed.since) {
      markdown += `> **Доступно с версии:** ${parsed.since}

`;
    }

    if (parsed.deprecated) {
      markdown += `> ⚠️ **Устарело:** Этот API устарел и может быть удален в будущих версиях.

`;
    }
  });

  return markdown;
}

// Основная функция
function generateAPIDocs() {
  console.log('🚀 Генерация API документации...');

  // Создаем обзорную страницу
  const overviewContent = `---
sidebar_position: 1
---

# API Reference

Полная документация API для CloudPayments React Native SDK.

## Основные модули

- [PaymentService](./payment-service) — Сервис для работы с платежами
- [CardService](./card-service) — Сервис для работы с картами
- [Хуки](./hooks/use-payment-form) — React хуки для упрощения интеграции
- [Типы данных](./types/payment-data) — TypeScript типы и интерфейсы
- [События](./events/payment-form) — Система событий SDK

## Быстрые ссылки

- [usePaymentForm](./hooks/use-payment-form) — Основной хук для платежей
- [IPaymentData](./types/payment-data) — Интерфейс данных платежа
- [EPaymentFormEventName](./types/enums) — Перечисления событий

## Примеры использования

### Базовый платеж

\`\`\`typescript
import { usePaymentForm } from '@lm/react-native-cloudpayments';

const presentPaymentForm = usePaymentForm('pk_test_your_public_id');

const result = await presentPaymentForm({
  amount: '1000.00',
  currency: 'RUB',
  description: 'Покупка товара'
});
\`\`\`

### Обработка событий

\`\`\`typescript
import { eventEmitter, EPaymentFormEventName } from '@lm/react-native-cloudpayments';

eventEmitter.addListener(EPaymentFormEventName.PAYMENT_FORM, (event) => {
  console.log('Событие платежной формы:', event);
});
\`\`\`
`;

  fs.writeFileSync(path.join(DOCS_DIR, 'overview.md'), overviewContent);

  // Обрабатываем файлы типов
  const typeFiles = [
    'events.ts',
    'enums.ts',
    'paymentData.ts',
    'responses.ts',
    'services.ts',
  ];

  typeFiles.forEach((file) => {
    const filePath = path.join(TYPES_DIR, file);
    if (fs.existsSync(filePath)) {
      const content = fs.readFileSync(filePath, 'utf8');
      const jsdocs = extractJSDoc(content);

      const fileName = path.basename(file, '.ts');
      const title = fileName.charAt(0).toUpperCase() + fileName.slice(1);

      const markdown = generateMarkdown(
        title,
        `Документация для ${title}`,
        jsdocs
      );

      const outputPath = path.join(DOCS_DIR, 'types', `${fileName}.md`);
      fs.mkdirSync(path.dirname(outputPath), { recursive: true });
      fs.writeFileSync(outputPath, markdown);

      console.log(`✅ Создан ${outputPath}`);
    }
  });

  console.log('🎉 API документация сгенерирована!');
}

// Запускаем генерацию
if (require.main === module) {
  generateAPIDocs();
}

module.exports = { generateAPIDocs };
