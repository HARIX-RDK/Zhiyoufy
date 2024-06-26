import type { NavigationItem } from '@/model/bo';


export const mainNavItemsFull: NavigationItem[] = [
  {
    elIcon: 'House',
    title: 'Home',
    index: '/main/home',
  },
  {
    iconifyIcon: 'mdi:alpha-p-box-outline',
    title: 'Project',
    index: '/main/pms',
    needRoles: [
      'project.owner',
      'project.editor',
      'project.viewer',
    ],
    children: [
      {
        title: 'Project Management',
        index: '/main/pms/project-management',
      },
      {
        title: 'Job Template Management',
        index: '/main/pms/job-template-management',
      },
    ],
  },
  {
    iconifyIcon: 'mdi:alpha-e-box-outline',
    title: 'Environment',
    index: '/main/ems',
    needRoles: [
      'environment.owner',
      'environment.editor',
      'environment.viewer',
    ],
    children: [
      {
        title: 'Environment Management',
        index: '/main/ems/environment-management',
      },
    ],
  },
  {
    iconifyIcon: 'mdi:alpha-w-box-outline',
    title: 'Worker',
    index: '/main/wms',
    needRoles: [
      'environment.owner',
      'environment.editor',
      'environment.viewer',
    ],
    children: [
      {
        title: 'ActiveWorkerApp Management',
        index: '/main/wms/active-worker-app-management',
      },
      {
        title: 'WorkerApp Management',
        index: '/main/wms/worker-app-management',
      },
    ],
  },
  {
    iconifyIcon: 'mdi:alpha-d-box-outline',
    title: 'Dashboard',
    index: '/main/dms',
    children: [
      {
        title: 'Dashboard Management',
        index: '/main/dms/dashboard-management',
      },
    ],
  },
  {
    elIcon: 'User',
    title: 'User',
    index: '/main/ums',
    needRoles: [
      'admin',
    ],
    children: [
      {
        title: 'User Management',
        index: '/main/ums/user-management',
      },
      {
        title: 'Role Management',
        index: '/main/ums/role-management',
        needRoles: [
          'sysAdmin',
        ],
      },
      {
        title: 'Permission Management',
        index: '/main/ums/permission-management',
        needRoles: [
          'sysAdmin',
        ],
      },
    ],
  },
];
