import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: '易用',
    description: (
      <>
        用户只需要登录一个网址就可以，启动测试、查看历史结果、查看面板等等。
      </>
    ),
  },
  {
    title: '模拟真实用户行为',
    description: (
      <>
        测试用例尽量模拟真实用户行为，模拟程序也尽量模拟真实程序，从而使得测试结果
        更贴近真实情况。
      </>
    ),
  },
  {
    title: '高覆盖',
    description: (
      <>
        对不同产品进行覆盖。
      </>
    ),
  },
];

function Feature({title, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
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
