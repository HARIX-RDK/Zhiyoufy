<template>
  <div class="full-wh">
    <el-container>
      <el-header class="auth-header">
        <span class="header-title">{{ title }}</span>

        <div class="spacer"></div>

        <el-button
          v-if="showSignin"
          @click="onClickSignin"
        >
          Signin
        </el-button>

        <el-button
          v-if="showSignup"
          class="ml-2"
          @click="onClickSignup"
        >
          Signup
        </el-button>
      </el-header>

      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';

import { defaultSettings } from '@/settings';
import { computed } from 'vue';

const title = defaultSettings.title;

const router = useRouter();
const route = useRoute();

const showSignin = computed(() => {
  return route.name !== 'login' ? true : false;
});

const showSignup = computed(() => {
  return route.name !== 'signup' ? true : false;
});

function onClickSignin() {
  router.push({name: "login"});
}

function onClickSignup() {
  router.push({name: "signup"});
}
</script>

<style lang="scss" scoped>
.auth-header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--color-border);

  .header-title {
    font-weight: 500;
  }
}
</style>
