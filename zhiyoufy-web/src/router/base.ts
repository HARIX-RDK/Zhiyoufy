import { createRouter, createWebHistory } from 'vue-router'

import AuthLayout from '@/layout/AuthLayout.vue';
import MainLayout from '@/layout/MainLayout.vue';
import WrapperLayout from '@/layout/WrapperLayout.vue';

import LoginView from '@/views/auth/LoginView.vue';
import SignupView from '@/views/auth/SignupView.vue';

import PageForbiddenView from '@/views/error-page/PageForbiddenView.vue';
import PageNotFoundView from '@/views/error-page/PageNotFoundView.vue';

import HomeView from '@/views/HomeView.vue'

import EnvironmentManagementView from '@/views/ems/EnvironmentManagementView.vue';

import ActiveWorkerAppManagement from '@/views/wms/ActiveWorkerAppManagement.vue';
import WorkerAppManagement from '@/views/wms/WorkerAppManagement.vue';

import ProjectManagementView from '@/views/pms/ProjectManagementView.vue';
import JobTemplateManagement from '@/views/pms/JobTemplateManagement.vue';

import DashboardManagementView from '@/views/dms/DashboardManagementView.vue';

import UserManagementView from '@/views/ums/UserManagementView.vue';
import RoleManagementView from '@/views/ums/RoleManagementView.vue';
import PermissionManagementView from '@/views/ums/PermissionManagementView.vue';


export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      component: AuthLayout,
      redirect: '/auth/login',
      children: [
        {
          path: 'login',
          component: LoginView,
          name: 'login',
          meta: { title: 'Login' }
        },
        {
          path: 'signup',
          component: SignupView,
          name: 'signup',
          meta: { title: 'Signup' }
        },
      ]
    },
    {
      path: '/error-page',
      component: WrapperLayout,
      redirect: '/error-page/page-forbidden',
      children: [
        {
          path: 'page-forbidden',
          name: 'page-forbidden',
          component: PageForbiddenView,
          meta: { title: 'Forbidden' },
        },
      ],
    },
    {
      path: '/main',
      component: MainLayout,
      redirect: '/main/home',
      meta: {
        requiresAuth: true,
      },
      children: [
        {
          path: 'home',
          component: HomeView,
          name: 'home',
          meta: { title: 'Home' },
        },
        {
          path: 'pms',
          component: WrapperLayout,
          redirect: 'pms/project-management',
          meta: {
          },
          children: [
            {
              path: 'project-management',
              component: ProjectManagementView,
              name: 'project-management',
              meta: {
                title: 'Project Management',
                needRoles: [
                  'project.owner',
                  'project.editor',
                  'project.viewer',
                ],
              },
            },
            {
              path: 'job-template-management',
              component: JobTemplateManagement,
              name: 'job-template-management',
              meta: {
                title: 'Job Template Management',
              },
            },
          ],
        },
        {
          path: 'ems',
          component: WrapperLayout,
          redirect: 'ems/environment-management',
          meta: {
          },
          children: [
            {
              path: 'environment-management',
              component: EnvironmentManagementView,
              name: 'environment-management',
              meta: {
                title: 'Environment Management',
                needRoles: [
                  'environment.owner',
                  'environment.editor',
                  'environment.viewer',
                ],
              },
            },
          ],
        },
        {
          path: 'wms',
          component: WrapperLayout,
          redirect: 'wms/active-worker-app-management',
          meta: {
            needRoles: [
              'workerApp.owner',
              'workerApp.editor',
              'workerApp.viewer',
            ],
          },
          children: [
            {
              path: 'active-worker-app-management',
              component: ActiveWorkerAppManagement,
              name: 'active-worker-app-management',
              meta: {
                title: 'ActiveWorkerApp Management',
              },
            },
            {
              path: 'worker-app-management',
              component: WorkerAppManagement,
              name: 'worker-app-management',
              meta: {
                title: 'WorkerApp Management',
              },
            },
          ],
        },
        {
          path: 'dms',
          component: WrapperLayout,
          redirect: 'dms/dashboard-management',
          meta: {
          },
          children: [
            {
              path: 'dashboard-management',
              component: DashboardManagementView,
              name: 'dashboard-management',
              meta: {
                title: 'Dashboard Management',
              },
            },
          ],
        },
        {
          path: 'ums',
          component: WrapperLayout,
          redirect: 'ums/user-management',
          meta: {
            needRoles: [
              'admin',
            ],
          },
          children: [
            {
              path: 'user-management',
              component: UserManagementView,
              name: 'user-management',
              meta: {
                title: 'User Management',
              },
            },
            {
              path: 'role-management',
              component: RoleManagementView,
              name: 'role-management',
              meta: {
                title: 'Role Management',
                needRoles: [
                  'sysAdmin',
                ],
              },
            },
            {
              path: 'permission-management',
              component: PermissionManagementView,
              name: 'permission-management',
              meta: {
                title: 'Permission Management',
                needRoles: [
                  'sysAdmin',
                ],
              },
            },
          ],
        },
      ]
    },
    {
      path: '/',
      redirect: '/main/home',
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'page-not-found',
      component: PageNotFoundView,
      meta: { title: 'Not Found' },
    },
  ]
})
