<template>
  <div>
    <el-card
      class="login-card"
    >
      <div class="login-card-title">Login</div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="User name" prop="username">
          <el-input v-model="loginForm.username" />
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input v-model="loginForm.password" type="password" show-password />
        </el-form-item>

        <el-button
          class="login-btn"
          :disabled="loading"
          type="primary"
          @click="onClickLogin"
        >
          Login
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { LocationQuery } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';

import * as _ from "lodash";

import { useUserStore } from '@/stores/user';
import { gUserService } from '@/services/user';
import { CollectionUtils, TimeUtils } from '@/utils';


const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

let redirect: string | null;
let otherQuery: LocationQuery | undefined;

watch([route], () => {
  const query = route.query;

  redirect = null;
  otherQuery = undefined;

  if (query) {
    redirect = CollectionUtils.getFirst(query.redirect);
    otherQuery = _.omit(query, ['redirect']);
  }
}, {
  immediate: true,
});

const loading = ref(false);

const loginFormRef = ref<FormInstance>()
const loginForm = reactive({
  username: '',
  password: '',
});

const rules = reactive<FormRules>({
  username: [
    { required: true, message: 'Please input user name', trigger: 'blur' },
    { min: 5, max: 64, message: 'Length should be 5 to 64', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'blur' },
    { min: 6, message: 'Length should be at least 6', trigger: 'blur' },
  ],
});

async function onClickLogin() {
  try {
    loading.value = true;

    const valid = await loginFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (valid) {
      let formLoginData = {
        username: loginForm.username,
        password: loginForm.password,
      };

      await TimeUtils.sleep(300);

      await gUserService.formLogin(formLoginData);

      if (userStore.token) {
        router.push({ path: redirect || '/', query: otherQuery });
      }
    }
  }
  finally {
    loading.value = false;
  }
}
</script>

<style lang="scss" scoped>
.login-card {
  max-width: 500px;
  margin: 0 auto;

  .login-card-title  {
    font-size: 1.6rem;
    margin-bottom: 10px;
  }

  .login-btn {
    margin-left: 100px;
  }
}
</style>
