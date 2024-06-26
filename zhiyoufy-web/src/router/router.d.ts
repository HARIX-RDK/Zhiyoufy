export {}

import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    needRoles?: string[];
    title?: string;
    requiresAuth?: boolean;
  }
}
