import { ref, computed, watch } from 'vue'
import { defineStore } from 'pinia'

import { gStorageService } from '@/services'

const logPrefix = 'userStore:';
const userStateKey = 'userState';

export interface UserState {
  name?: string;
  roles?: string[];
  infoReadAt?: string;
  token?: string;
  expireAt?: string;
}

export const useUserStore = defineStore('user', () => {
  const name = ref('anonymous');
  const roles = ref([] as string[]);
  const infoReadAt = ref('');
  const token = ref('');
  const expireAt = ref('');

  const state: UserState = gStorageService.getItem(userStateKey);

  if (state) {
    const expireAtParsed = Date.parse(state.expireAt || '');

    if (isNaN(expireAtParsed) || Date.now() > expireAtParsed) {
      gStorageService.removeItem(userStateKey);
    } else {
      state.name != null && (name.value = state.name);
      state.roles != null && (roles.value = state.roles);
      state.infoReadAt != null && (infoReadAt.value = state.infoReadAt);
      state.token != null && (token.value = state.token);
      state.expireAt != null && (expireAt.value = state.expireAt);
    }
  }

  const isAdmin = computed(() => {
    return roles.value.some(role => {
      return role === 'admin';
    });
  });

  const isSysAdmin = computed(() => {
    return roles.value.some(role => {
      return role === 'sysAdmin';
    });
  });

  const expireAtMs = computed(() => {
    return Date.parse(expireAt.value);
  })

  function reset() {
    name.value = 'anonymous';
    roles.value = [];
    infoReadAt.value = '';
    token.value = '';
    expireAt.value = '';
  }

  function store() {
    const curState = {
      name: name.value,
      roles: roles.value,
      infoReadAt: infoReadAt.value,
      token: token.value,
      expireAt: expireAt.value,
    };

    console.log(`${logPrefix} store ${JSON.stringify(curState, null, 2)}`);

    gStorageService.setItem(userStateKey, curState);
  }

  watch([name, roles, token, expireAt], () => {
    store();
  })

  return {
    isAdmin,
    isSysAdmin,
    name,
    roles,
    infoReadAt,
    token,
    expireAt,
    expireAtMs,
    reset,
    store,
  };
})
