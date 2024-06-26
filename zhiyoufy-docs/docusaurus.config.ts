import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

var baseUrl = '/'
const isProd = process.env.NODE_ENV === 'production';

if (isProd) {
  baseUrl = '/Zhiyoufy/'
}

const config: Config = {
  title: 'Zhiyoufy文档',
  tagline: '自动化 功能测试 性能测试',
  favicon: 'img/favicon.ico',

  url: 'https://harix-rdk.github.io/',
  baseUrl: baseUrl,

  organizationName: 'zhiyoufy',
  projectName: 'zhiyoufy-docs',

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  i18n: {
    defaultLocale: 'zh-Hans',
    locales: ['zh-Hans'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          routeBasePath: '/',
          sidebarPath: './sidebars.ts',
        },
        blog: false,
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    image: 'img/docusaurus-social-card.jpg',
    navbar: {
      title: 'Zhiyoufy文档',
      logo: {
        alt: 'Zhiyoufy Logo',
        src: 'img/logo.png',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'docSidebar',
          position: 'left',
          label: 'Docs',
        },
        {
          href: 'https://github.com/HARIX-RDK/Zhiyoufy',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: '上手指南',
              to: '/getting-started/intro',
            },
          ],
        },
        {
          title: '更多',
          items: [
            {
              label: 'Docusaurus',
              href: 'https://github.com/facebook/docusaurus',
            },
            {
              label: 'Markdown语法',
              href: 'https://daringfireball.net/projects/markdown/syntax',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Zhiyoufy.`,
    },
    prism: {
      additionalLanguages: ['java'],
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
    docs: {
      sidebar: {
        hideable: true
      }
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
