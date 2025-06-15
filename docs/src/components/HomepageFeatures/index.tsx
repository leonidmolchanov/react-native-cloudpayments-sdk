import type { ReactNode } from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: '🚀 Простота интеграции',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        CloudPayments SDK разработан для быстрой и простой интеграции платежей в
        ваше React Native приложение. Всего несколько строк кода для начала
        работы.
      </>
    ),
  },
  {
    title: '💳 Все способы оплаты',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        Поддержка банковских карт, Apple Pay, Google Pay, СБП, Tinkoff Pay,
        SberPay. Один SDK для всех популярных способов оплаты в России.
      </>
    ),
  },
  {
    title: '🔒 Безопасность PCI DSS',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        Полное соответствие стандартам PCI DSS. Данные карт обрабатываются на
        защищенных серверах CloudPayments, не проходя через ваше приложение.
      </>
    ),
  },
];

function Feature({ title, Svg, description }: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
