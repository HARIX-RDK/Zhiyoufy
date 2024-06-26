<template>
  <el-card
    class="container"
  >
    <div class="title">{{ title }}</div>

    <el-form
      class="form"
      ref="permissionFormRef"
      :model="permissionForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="permissionForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="permissionForm.description"
          :rows="2"
          type="textarea"
        />
      </el-form-item>

      <el-button
        class="ok-btn"
        :disabled="handling"
        type="primary"
        @click="onClickOk"
      >
        Ok
      </el-button>

      <el-button
        :disabled="handling"
        @click="emit('cancel')"
      >
        Cancel
      </el-button>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted  } from 'vue';
import type { PropType } from 'vue';

import type { FormInstance, FormRules } from 'element-plus';

import type { UmsPermission, UmsPermissionParam } from "@/model/dto/ums";

import { gNotificationService } from '@/services';
import { gUserApi } from '@/api';

const props = defineProps({
  permission: {
    type: Object as PropType<UmsPermission>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const title = ref('New Permission');
const permissionFormRef = ref<FormInstance>()
const permissionForm = reactive({
  name: '',
  description: '',
});

onMounted(() => {
  if (props.permission) {
    title.value = 'Edit Permission';
    permissionForm.name = props.permission.name;
    permissionForm.description = props.permission.description;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input permission name', trigger: 'blur' },
    { min: 3, max: 64, message: 'Length should be 3 to 64', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await permissionFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (props.permission == null) {
      let data: UmsPermissionParam = {
        name: permissionForm.name,
        description: permissionForm.description,
      };

      const rsp = await gUserApi.addPermission(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: Partial<UmsPermissionParam> = {};

      let propertyList = ['name', 'description'];

      for (let property of propertyList) {
        if ((permissionForm as any)[property] !== (props.permission as any)[property]) {
          (data as any)[property] = (permissionForm as any)[property];
        }
      }

      const rsp = await gUserApi.updatePermission({
        permissionId: props.permission.id,
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
    handling.value = false;
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 400px;
}
</style>
