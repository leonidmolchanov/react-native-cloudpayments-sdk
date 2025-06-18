import type { SidebarsConfig } from '@docusaurus/plugin-content-docs';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */
const sidebars: SidebarsConfig = {
  // By default, Docusaurus generates a sidebar from the docs folder structure
  tutorialSidebar: [
    'intro',
    {
      type: 'category',
      label: '🚀 Начало работы',
      items: ['getting-started'],
    },
    {
      type: 'category',
      label: '📱 Платформы',
      items: ['platforms/android', 'platforms/ios'],
    },
    {
      type: 'category',
      label: '💳 Использование',
      items: [
        'usage/payment-form',
        'usage/receipts',
        'usage/recurrent-payments',
      ],
    },
  ],

  // API Reference sidebar - временно отключен до создания документов
  // apiSidebar: [
  //   'api/overview',
  //   {
  //     type: 'category',
  //     label: '🔌 Сервисы',
  //     items: [
  //       'api/payment-service',
  //       'api/card-service',
  //     ],
  //   },
  //   {
  //     type: 'category',
  //     label: '🎣 Хуки',
  //     items: [
  //       'api/hooks/use-payment-form',
  //       'api/hooks/use-payment-events',
  //     ],
  //   },
  //   {
  //     type: 'category',
  //     label: '📊 Типы данных',
  //     items: [
  //       'api/types/payment-data',
  //       'api/types/events',
  //       'api/types/responses',
  //       'api/types/enums',
  //     ],
  //   },
  //   {
  //     type: 'category',
  //     label: '⚡ События',
  //     items: [
  //       'api/events/payment-form',
  //       'api/events/payment-lifecycle',
  //     ],
  //   },
  // ],
};

export default sidebars;
