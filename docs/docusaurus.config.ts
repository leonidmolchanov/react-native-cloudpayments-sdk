import { themes as prismThemes } from 'prism-react-renderer';
import type { Config } from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const config: Config = {
  title: 'CloudPayments React Native SDK',
  tagline:
    'Мощный и простой в использовании SDK для интеграции платежей CloudPayments в React Native приложения',
  favicon: 'img/favicon.ico',

  // Future flags, see https://docusaurus.io/docs/api/docusaurus-config#future
  future: {
    v4: true, // Improve compatibility with the upcoming Docusaurus v4
  },

  // Set the production url of your site here
  url: 'https://leonidmolchanov.github.io',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/react-native-cloudpayments-sdk/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'leonidmolchanov', // Usually your GitHub org/user name.
  projectName: 'react-native-cloudpayments-sdk', // Usually your repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Only Russian language
  i18n: {
    defaultLocale: 'ru',
    locales: ['ru'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          // Remove edit page links
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    // image: 'img/cloudpayments-social-card.jpg',
    navbar: {
      title: 'CloudPayments RN SDK',
      // logo: {
      //   alt: 'CloudPayments Logo',
      //   src: 'img/logo.svg',
      // },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: 'Документация',
        },
        {
          href: 'https://github.com/leonidmolchanov/react-native-cloudpayments-sdk',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Документация',
          items: [
            {
              label: 'Введение',
              to: '/docs/intro',
            },
            {
              label: 'Быстрый старт',
              to: '/docs/getting-started',
            },
            {
              label: 'Платежная форма',
              to: '/docs/usage/payment-form',
            },
          ],
        },
        {
          title: 'Платформы',
          items: [
            {
              label: 'Android',
              to: '/docs/platforms/android',
            },
            {
              label: 'iOS',
              to: '/docs/platforms/ios',
            },
          ],
        },
        {
          title: 'CloudPayments',
          items: [
            {
              label: 'Официальный сайт',
              href: 'https://cloudpayments.ru',
            },
            {
              label: 'Личный кабинет',
              href: 'https://merchant.cloudpayments.ru',
            },
            {
              label: 'API Документация',
              href: 'https://developers.cloudpayments.ru',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Leonid Molchanov. Создано с ❤️ для React Native разработчиков. Свободная MIT лицензия.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: [
        'bash',
        'diff',
        'json',
        'kotlin',
        'swift',
        'objectivec',
      ],
    },
    colorMode: {
      defaultMode: 'light',
      disableSwitch: false,
      respectPrefersColorScheme: true,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
