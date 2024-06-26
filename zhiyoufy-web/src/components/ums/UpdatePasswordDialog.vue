<template>
  <el-card>
    <el-form
      ref="updatePasswordFormRef"
      :model="updatePasswordForm"
      :rules="rules"
      label-width="130px"
    >
      <el-form-item label="User name" prop="usernameConfirm">
        <el-input v-model="updatePasswordForm.usernameConfirm" />
      </el-form-item>

      <el-form-item label="Old Password" prop="oldPassword">
        <el-input v-model="updatePasswordForm.oldPassword" type="password" show-password />
      </el-form-item>

      <el-form-item label="Password" prop="password">
        <el-input v-model="updatePasswordForm.password" type="password" show-password />
      </el-form-item>

      <el-form-item label="PasswordConfirm" prop="passwordConfirm" required>
        <el-input v-model="updatePasswordForm.passwordConfirm" type="password" show-password />
      </el-form-item>

      <el-button
        :disabled="loading"
        type="primary"
        @click="onClickOk"
      >
        Save
      </el-button>

      <el-button
        class="ml-1"
        :disabled="loading"
        @click="onClickCancel"
      >
        Cancel
      </el-button>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';

import type { UpdateUserPasswordParam } from "@/model/dto/ums";

import { gNotificationService } from '@/services';
import { gUserApi } from '@/api';
import { TimeUtils } from '@/utils';

const props = defineProps({
  username: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['ok', 'cancel']);

const loading = ref(false);

const updatePasswordFormRef = ref<FormInstance>()
const updatePasswordForm = reactive({
  usernameConfirm: props.username,
  oldPassword: '',
  password: '',
  passwordConfirm: '',
});

const validatePasswordConfirm = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('Please input the password again'))
  } else if (value !== updatePasswordForm.password) {
    callback(new Error("Two inputs don't match!"))
  } else {
    callback()
  }
};

const rules = reactive<FormRules>({
  username: [
    { required: true, message: 'Please input user name', trigger: 'blur' },
    { min: 5, max: 64, message: 'Length should be 5 to 64', trigger: 'blur' },
  ],
  oldPassword: [
    { required: true, message: 'Please input old password', trigger: 'blur' },
    { min: 6, message: 'Length should be at least 6', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'blur' },
    { min: 6, message: 'Length should be at least 6', trigger: 'blur' },
  ],
  passwordConfirm: [{ validator: validatePasswordConfirm, trigger: 'blur' }],
});

async function onClickOk() {
  try {
    loading.value = true;

    const valid = await updatePasswordFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (valid) {
      let formData: UpdateUserPasswordParam = {
        username: updatePasswordForm.usernameConfirm,
        oldPassword: updatePasswordForm.oldPassword,
        newPassword: updatePasswordForm.password,
      };

      await TimeUtils.sleep(300);

      const signupRsp = await gUserApi.updateUserPassword(formData);

      if (signupRsp.data) {
        emit('ok');
      } else if (signupRsp.error) {
        gNotificationService.error(signupRsp.error.message);
      }
    }
  }
  finally {
    loading.value = false;
  }
}

function onClickCancel() {
  emit('cancel');
}
</script>

<style lang="scss" scoped>
</style>
