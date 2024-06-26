import axios from 'axios';

import { gNotificationService } from '@/services';
import { useUserStore } from '@/stores/user';

import { translateError } from './translate';

// create an axios instance
export const axiosInst = axios.create({
  timeout: 10000,
})

// request interceptor
axiosInst.interceptors.request.use(
  config => {
    const userStore = useUserStore();

    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }

    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
axiosInst.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data

    if (res.error) {
      gNotificationService.error(translateError(res.error));

      return Promise.reject(res);
    } else {
      return res;
    }
  },
  error => {
    console.log('err' + error, error); // for debug

    gNotificationService.error(error.message);

    if (error.response.status == 401) {
      const userStore = useUserStore();

      userStore.reset();
      location.reload();
    }

    return Promise.reject(error);
  }
);
