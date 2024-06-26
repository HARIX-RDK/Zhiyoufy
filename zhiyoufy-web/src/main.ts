import { createApp } from 'vue'
import { createPinia } from 'pinia'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import * as ElementPlusIconsVue from "@element-plus/icons-vue";

import App from './App.vue'
import { router } from './router'
import './router/permission';

import { setGNotificationService, ElementNotificationService } from '@/services/notification';

import './styles/index.scss'

import { useProjectStore } from '@/stores/project';
import { useEnvironmentStore } from '@/stores/environment';
import { useWorkerStore } from '@/stores/worker';

import dayjs from 'dayjs';
import isoWeek from 'dayjs/plugin/isoWeek';
import quarterOfYear from 'dayjs/plugin/quarterOfYear';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import Chart from 'chart.js/auto';

dayjs.extend(isoWeek);
dayjs.extend(quarterOfYear);

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.use(ElementPlus)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

setGNotificationService(new ElementNotificationService());

const projectStore = useProjectStore();
const environmentStore = useEnvironmentStore();
const workerStore = useWorkerStore();

projectStore.restoreFromStorage();
environmentStore.restoreFromStorage();
workerStore.restoreFromStorage();

app.mount('#app')
