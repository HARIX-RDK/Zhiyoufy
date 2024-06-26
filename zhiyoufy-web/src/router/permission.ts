import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style

import { router } from './base';
import { useUserStore } from '@/stores/user';
import { gUserService } from '@/services/user';

import { NavigationUtils, PermissionUtils } from '@/utils';


router.beforeEach(async (to, from, next) => {
  console.log(`global beforeEach Enter for ${to.path}`)

  // start progress bar
  NProgress.start()

  // set page title
  document.title = NavigationUtils.getPageTitle(to.meta.title)

  const useStore = useUserStore();

  // determine whether the user has logged in
  const token = useStore.token

  if (token) {
    if (to.name === 'login') {
      // if is logged in, redirect to the home page
      next({ name: 'home' })
      NProgress.done() // hack: https://github.com/PanJiaChen/vue-element-admin/pull/2939
    }
    else {
      if (!useStore.infoReadAt) {
        try {
          await gUserService.getUserInfo();
        } catch (err) {
          useStore.reset();
        }
      }

      if (PermissionUtils.hasRoutePermission(to)) {
        next();
      } else {
        next({name: 'page-forbidden', replace: true});
        NProgress.done();
      }
    }
  } else {
    /* has no token*/
    if (!to.matched.some(record => record.meta.requiresAuth)) {
      next()
    } else {
      console.log(`need token to access ${to.path}, redirect to login page`)
      // other pages that do not have permission to access are redirected to the login page.
      next(`/auth/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
