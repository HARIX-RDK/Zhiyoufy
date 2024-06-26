
## 初始化工程

<https://vuejs.org/guide/quick-start.html#creating-a-vue-application>

```bash
npm init vue@latest

√ Project name: ... zhiyoufy-web
√ Add TypeScript? ... Yes
√ Add JSX Support? ... No
√ Add Vue Router for Single Page Application development? ... Yes
√ Add Pinia for state management? ... Yes
√ Add Vitest for Unit Testing? ... Yes
√ Add an End-to-End Testing Solution? » Cypress
√ Add ESLint for code quality? ... Yes
√ Add Prettier for code formatting? ... No
```

## 使用Volar的Take Over模式

<https://vuejs.org/guide/typescript/overview.html#volar-takeover-mode>

- 删掉对应Readme描述
- 删掉`.vscode/extensions.json`中的recommendation

## 添加element plus

<https://element-plus.org/zh-CN/guide/installation.html>

<https://element-plus.org/zh-CN/guide/quickstart.html>

<https://element-plus.org/zh-CN/component/icon.html>

```bash
npm install element-plus

npm install @element-plus/icons-vue
```

修改`src\main.ts`

```ts title="src\main.ts"
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import * as ElementPlusIconsVue from "@element-plus/icons-vue";

app.use(ElementPlus)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
```

## 添加axios

```bash
npm install axios
```

## 添加.editorconfig

<https://raw.githubusercontent.com/PanJiaChen/vue-element-admin/master/.editorconfig>

- `indent_size = 2`

### 安装extention

- `EditorConfig.EditorConfig`

## 添加sass

<https://vitejs.dev/guide/features.html#css-pre-processors>

<https://sass-lang.com/documentation/style-rules>

```bash
npm add -D sass
```

```css title="src\styles\index.scss"
.alert, .warning {
  ul, p {
    margin-right: 0;
    margin-left: 0;
    padding-bottom: 0;
  }
}
```

<https://dev.to/ahmedsarhan/how-to-build-your-own-utility-framework-using-scss-25dh>

添加css utility，比如spacing和display相关的

## 配置vitest

### tsconfig.vitest.json

还是习惯使用单独的tests目录，另外使能vitest globals

```json
  "include": ["env.d.ts", "src/**/*", "src/**/*.vue",
    "tests/**/*", "tests/**/*.vue"],

    "types": ["node", "jsdom", "vitest/globals"]
```

### vite.config.ts

```ts
  test: {
    globals: true,
    environment: 'jsdom',
  },
```

### IDE VSCode debug

<https://vitest.dev/guide/debugging.html#vscode>

## debug pinia

<https://pinia.vuejs.org/cookbook/testing.html#unit-testing-a-store>

## 添加vueuse

- watchDebounced: <https://vueuse.org/shared/watchDebounced/>

