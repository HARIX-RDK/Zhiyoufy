<template>
  <div>
    <el-card
      class="signup-card"
    >
      <div class="signup-card-title">Signup</div>

      <el-form
        ref="signupFormRef"
        :model="signupForm"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="Email" prop="email">
          <el-input v-model="signupForm.email" />
        </el-form-item>

        <el-form-item label="IdCode" required>
          <div class="d-flex align-center">
            <el-form-item prop="idCode">
              <el-input v-model="signupForm.idCode"/>
            </el-form-item>
            <el-button
              class="ml-4 request-id-code-btn"
              :disabled="sendIdCodeDisabled"
              @click="onClickSendIdCode"
            >
              <template v-if="requestIdCodeEnableTime">
                <span class="full-w">
                  {{ requestIdCodeEnableTime }}
                </span>
              </template>
              <template v-else>
                <el-icon
                  class="mr-1"
                >
                  <Message />
                </el-icon>
                Request IdCode
              </template>
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="User name" prop="username">
          <el-input v-model="signupForm.username" />
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input v-model="signupForm.password" type="password" show-password />
        </el-form-item>

        <el-form-item label="PasswordConfirm" prop="passwordConfirm" required>
          <el-input v-model="signupForm.passwordConfirm" type="password" show-password />
        </el-form-item>

        <el-button
          class="signup-btn"
          :disabled="loading"
          type="primary"
          @click="onClickSignup"
        >
          Signup
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';

import type { RequestIdentificationCodeParam, UmsUserParam } from "@/model/dto/ums";

import { gNotificationService, gStorageService } from '@/services';
import { gUserApi } from '@/api';
import { TimeUtils, ValidateUtils } from '@/utils';


const router = useRouter();

const loading = ref(false);

const signupFormRef = ref<FormInstance>()
const signupForm = reactive({
  email: '',
  idCode: '',
  username: '',
  password: '',
  passwordConfirm: '',
});

//#region Request IdCode
const requestIdCodeKey = 'request-id-code';
const minRequestIdCodeInterval = 10;
const requestIdCodeEnableTime = ref('');
let lastRequestTime = loadLastRequestTime();
let updateEnableTimeIntervalId = -1;

console.log(`loaded lastRequestTime: ${lastRequestTime?.toISOString()}`);

if (lastRequestTime) {
  const curTime = new Date();
  const leftSeconds = minRequestIdCodeInterval - (curTime.getMilliseconds()
    - lastRequestTime.getMilliseconds()) / 1000;

  if (leftSeconds > 0 && leftSeconds <= minRequestIdCodeInterval) {
    requestIdCodeEnableTime.value = `${leftSeconds}s`;
    updateEnableTimeIntervalId = window.setInterval(onUpdateEnableTime, 1000);
  } else {
    gStorageService.removeItem(requestIdCodeKey);
  }
}

function loadLastRequestTime(): Date | null {
  const lastRequestTimeSaved = gStorageService.getItem(requestIdCodeKey);

  if (!lastRequestTimeSaved) {
    return null;
  }

  return new Date(lastRequestTimeSaved);
}

async function onClickSendIdCode() {
  const requestIdCodeData: RequestIdentificationCodeParam = {
    kind: 'email',
    email: signupForm.email,
  };

  try {
    loading.value = true;

    await TimeUtils.sleep(300);
    await gUserApi.requestIdCode(requestIdCodeData);

    lastRequestTime = new Date();
    gStorageService.setItem(requestIdCodeKey, lastRequestTime.toJSON());
    requestIdCodeEnableTime.value = `${minRequestIdCodeInterval}s`;
    updateEnableTimeIntervalId = window.setInterval(onUpdateEnableTime, 1000);
  } finally {
    loading.value = false;
  }
}

function onUpdateEnableTime() {
  const curTime = new Date();
  const leftSeconds = Math.round(minRequestIdCodeInterval - (curTime.getTime()
    - lastRequestTime!.getTime()) / 1000);

  if (leftSeconds > 0 && leftSeconds <= minRequestIdCodeInterval) {
    requestIdCodeEnableTime.value = `${leftSeconds}s`;
  } else {
    clearInterval(updateEnableTimeIntervalId);
    updateEnableTimeIntervalId = -1;
    requestIdCodeEnableTime.value = '';
  }

  console.log(`onUpdateEnableTime: requestIdCodeEnableTime ${requestIdCodeEnableTime.value}`);
}

const sendIdCodeDisabled = computed(() => {
  if (!signupFormRef.value) {
    return true;
  }

  if (!signupForm.email || !ValidateUtils.validateEmail(signupForm.email)) {
    return true;
  }

  if (loading.value || !!requestIdCodeEnableTime.value) {
    return true;
  }

  return false;
});
//#endregion

const validatePasswordConfirm = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('Please input the password again'))
  } else if (value !== signupForm.password) {
    callback(new Error("Two inputs don't match!"))
  } else {
    callback()
  }
};

const rules = reactive<FormRules>({
  email: [
    {
      required: true,
      message: 'Please input email address',
      trigger: 'blur',
    },
    {
      type: 'email',
      message: 'Please input correct email address',
      trigger: ['blur', 'change'],
    },
  ],
  idCode: [
    { required: true, message: 'Please input idCode', trigger: 'blur' },
    { min: 6, message: 'Length should be at least 6', trigger: 'blur' },
  ],
  username: [
    { required: true, message: 'Please input user name', trigger: 'blur' },
    { min: 5, max: 64, message: 'Length should be 5 to 64', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'blur' },
    { min: 6, message: 'Length should be at least 6', trigger: 'blur' },
  ],
  passwordConfirm: [{ validator: validatePasswordConfirm, trigger: 'blur' }],
});

async function onClickSignup() {
  try {
    loading.value = true;

    const valid = await signupFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (valid) {
      let formSignupData: UmsUserParam = {
        email: signupForm.email,
        idCode: signupForm.idCode,
        username: signupForm.username,
        password: signupForm.password,
      };

      await TimeUtils.sleep(300);

      const signupRsp = await gUserApi.register(formSignupData);

      if (signupRsp.data) {
        router.push({ name: 'login' });
      } else if (signupRsp.error) {
        gNotificationService.error(signupRsp.error.message);
      }
    }
  }
  finally {
    loading.value = false;
  }
}
</script>

<style lang="scss" scoped>
.signup-card {
  max-width: 500px;
  margin: 0 auto;

  .signup-card-title  {
    font-size: 1.6rem;
    margin-bottom: 10px;
  }

  .request-id-code-btn {
    min-width: 152px;
  }

  .signup-btn {
    margin-left: 120px;
  }
}
</style>
