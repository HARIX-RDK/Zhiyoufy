<template>
  <el-card
    class="user-card"
  >
    <div class="user-card-title">{{ title }}</div>

    <el-form
      class="user-form"
      ref="userFormRef"
      :model="userForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Email" prop="email">
        <el-input v-model="userForm.email" />
      </el-form-item>

      <el-form-item label="User name" prop="username">
        <el-input v-model="userForm.username" />
      </el-form-item>

      <el-form-item label="Password" prop="password">
        <el-input v-model="userForm.password" type="password" show-password />
      </el-form-item>

      <el-form-item label="PasswordConfirm" prop="passwordConfirm" required>
        <el-input v-model="userForm.passwordConfirm" type="password" show-password />
      </el-form-item>

      <el-form-item label="Enabled">
        <el-checkbox v-model="userForm.enabled"/>
      </el-form-item>

      <el-form-item v-if="userStore.isSysAdmin" label="SysAdmin">
        <el-checkbox v-model="userForm.sysAdmin" />
      </el-form-item>

      <el-form-item v-if="userStore.isSysAdmin" label="Admin">
        <el-checkbox v-model="userForm.admin" />
      </el-form-item>

      <el-form-item label="Note" prop="note">
        <el-input
          v-model="userForm.note"
          :rows="2"
          type="textarea"
        />
      </el-form-item>

      <el-button
        class="ok-btn"
        :disabled="loading"
        type="primary"
        @click="onClickOk"
      >
        Ok
      </el-button>

      <el-button
        :disabled="loading"
        @click="onClickCancel"
      >
        Cancel
      </el-button>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted  } from 'vue';
import type { PropType } from 'vue'
import type { FormInstance, FormRules } from 'element-plus';

import { useUserStore } from '@/stores/user';
import type { UmsUserDTO, UmsUserParam, UmsUserUpdateParam } from "@/model/dto/ums";

import { gNotificationService } from '@/services';

import { gUserApi } from '@/api';

const props = defineProps({
  user: {
    type: Object as PropType<UmsUserDTO>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const passwordPlaceholder = 'ChangeIt';

const userStore = useUserStore();

const loading = ref(false);

const title = ref('New User');
const userFormRef = ref<FormInstance>()
const userForm = reactive({
  email: '',
  username: '',
  password: '',
  passwordConfirm: '',
  note: '',
  enabled: true,
  sysAdmin: false,
  admin: false,
});

onMounted(() => {
  if (props.user) {
    title.value = 'Edit User';
    userForm.email = props.user.email;
    userForm.username = props.user.username;
    userForm.password = passwordPlaceholder;
    userForm.passwordConfirm = passwordPlaceholder;
    userForm.note = props.user.note;
    userForm.enabled = props.user.enabled;
    userForm.sysAdmin = props.user.sysAdmin;
    userForm.admin = props.user.admin;
  }
});

const validatePasswordConfirm = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('Please input the password again'))
  } else if (value !== userForm.password) {
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

async function onClickOk() {
  try {
    loading.value = true;

    const valid = await userFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (props.user == null) {
      let data: UmsUserParam = {
        email: userForm.email,
        username: userForm.username,
        password: userForm.password,
        note: userForm.note,
        enabled: userForm.enabled,
      };

      if (userStore.isSysAdmin) {
        data.sysAdmin = userForm.sysAdmin;
        data.admin = userForm.admin;
      }

      const rsp = await gUserApi.addUser(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: UmsUserUpdateParam = {};

      let propertyList = ['email', 'username', 'note', 'enabled'];

      if (userStore.isSysAdmin) {
        propertyList.push('sysAdmin', 'admin');
      }

      for (let property of propertyList) {
        if ((userForm as any)[property] !== (props.user as any)[property]) {
          (data as any)[property] = (userForm as any)[property];
        }
      }

      if (userForm.password !== passwordPlaceholder) {
        data.password = userForm.password;
      }

      const rsp = await gUserApi.updateUser({
        userId: props.user.id,
        data,
      });

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    }
  }
  finally {
    loading.value = false;
  }
}

async function onClickCancel() {
  emit('cancel');
}
</script>

<style lang="scss" scoped>
.user-form {
  .ok-btn {
    margin-left: 140px;
  }

  min-height: 400px;
}
</style>
