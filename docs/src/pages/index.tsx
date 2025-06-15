import type { ReactNode } from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
//@ts-ignore
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import Heading from '@theme/Heading';

import styles from './index.module.css';

function HomepageHeader() {
  const { siteConfig } = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
          {siteConfig.title}
        </Heading>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link
            className="button button--secondary button--lg"
            to="/docs/intro"
          >
            🚀 Начать работу - 5 минут ⏱️
          </Link>
        </div>
        <div className={styles.badges}>
          <img
            src="https://img.shields.io/npm/v/@lmapp/react-native-cloudpayments.svg"
            alt="npm version"
          />
          <img
            src="https://img.shields.io/npm/dm/@lmapp/react-native-cloudpayments.svg"
            alt="npm downloads"
          />
          <img
            src="https://img.shields.io/github/license/leonidmolchanov/react-native-cloudpayments.svg"
            alt="license"
          />
        </div>
        <div className={styles.authorInfo}>
          <p>
            Создано <strong>Leonid Molchanov</strong> •
            <a
              href="https://github.com/leonidmolchanov"
              target="_blank"
              rel="noopener noreferrer"
            >
              {' '}
              @leonidmolchanov
            </a>{' '}
            • MIT License
          </p>
        </div>
      </div>
    </header>
  );
}

export default function Home(): ReactNode {
  const { siteConfig } = useDocusaurusContext();
  return (
    <Layout
      title={`${siteConfig.title} - Документация`}
      description="Мощный и простой в использовании SDK для интеграции платежей CloudPayments в React Native приложения"
    >
      <HomepageHeader />
      <main>
        <HomepageFeatures />

        {/* Quick Start Section */}
        <section className={styles.quickStart}>
          <div className="container">
            <div className="row">
              <div className="col col--12">
                <h2>⚡ Быстрый старт</h2>
                <div className={styles.codeBlock}>
                  <pre>
                    <code>{`# Установка
npm install @lmapp/react-native-cloudpayments

# Использование
import { usePaymentForm } from '@lmapp/react-native-cloudpayments';

const MyComponent = () => {
  const presentPaymentForm = usePaymentForm('pk_test_your_public_id');

  const handlePayment = async () => {
    const result = await presentPaymentForm({
      amount: '1000.00',
      currency: 'RUB',
      description: 'Покупка товара'
    });

    if (result.success) {
      console.log('Платеж успешен!', result.transactionId);
    }
  };

  return <Button title="Оплатить" onPress={handlePayment} />;
};`}</code>
                  </pre>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Payment Methods Section */}
        <section className={styles.paymentMethods}>
          <div className="container">
            <div className="row">
              <div className="col col--12">
                <h2>💳 Поддерживаемые способы оплаты</h2>
                <div className={styles.methodsGrid}>
                  <div className={styles.methodCard}>
                    <div className={styles.methodIcon}>💳</div>
                    <h3>Банковские карты</h3>
                    <p>Visa, MasterCard, МИР</p>
                    <div className={styles.platforms}>
                      <span className={styles.ios}>iOS</span>
                      <span className={styles.android}>Android</span>
                    </div>
                  </div>

                  <div className={styles.methodCard}>
                    <div className={styles.methodIcon}>🍎</div>
                    <h3>Apple Pay</h3>
                    <p>Быстрая оплата через Touch/Face ID</p>
                    <div className={styles.platforms}>
                      <span className={styles.ios}>iOS</span>
                    </div>
                  </div>

                  <div className={styles.methodCard}>
                    <div className={styles.methodIcon}>🤖</div>
                    <h3>Google Pay</h3>
                    <p>Быстрая оплата через Google</p>
                    <div className={styles.platforms}>
                      <span className={styles.android}>Android</span>
                    </div>
                  </div>

                  <div className={styles.methodCard}>
                    <div className={styles.methodIcon}>🏦</div>
                    <h3>Tinkoff Pay</h3>
                    <p>Оплата через приложение Тинькофф</p>
                    <div className={styles.platforms}>
                      <span className={styles.ios}>iOS</span>
                      <span className={styles.android}>Android</span>
                    </div>
                  </div>

                  <div className={styles.methodCard}>
                    <div className={styles.methodIcon}>⚡</div>
                    <h3>СБП</h3>
                    <p>Система быстрых платежей</p>
                    <div className={styles.platforms}>
                      <span className={styles.ios}>iOS</span>
                      <span className={styles.android}>Android</span>
                    </div>
                  </div>

                  <div className={styles.methodCard}>
                    <div className={styles.methodIcon}>🟢</div>
                    <h3>SberPay</h3>
                    <p>Оплата через Сбербанк Онлайн</p>
                    <div className={styles.platforms}>
                      <span className={styles.ios}>iOS</span>
                      <span className={styles.android}>Android</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* CTA Section */}
        <section className={styles.cta}>
          <div className="container">
            <div className="row">
              <div className="col col--12">
                <h2>🚀 Готовы начать?</h2>
                <p>
                  Интегрируйте CloudPayments в ваше React Native приложение за 5
                  минут
                </p>
                <div className={styles.ctaButtons}>
                  <Link
                    className="button button--primary button--lg"
                    to="/docs/getting-started"
                  >
                    📖 Документация
                  </Link>
                  <Link
                    className="button button--secondary button--lg"
                    to="/docs/platforms/android"
                  >
                    📱 Настройка платформ
                  </Link>
                  <Link
                    className="button button--outline button--lg"
                    href="https://github.com/leonidmolchanov/react-native-cloudpayments-sdk"
                  >
                    ⭐ GitHub
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
    </Layout>
  );
}
