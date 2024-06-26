<template>
  <el-card
    class="role-card"
  >
    <div class="role-card-title">{{ title }}</div>

    <el-form
      class="role-form"
      ref="roleFormRef"
      :model="roleForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="roleForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="roleForm.description"
          :rows="2"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="Enabled">
        <el-checkbox v-model="roleForm.enabled"/>
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

import type { UmsRole, UmsRoleParam } from "@/model/dto/ums";

import { gNotificationService } from '@/services';
import { gUserApi } from '@/api';

const props = defineProps({
  role: {
    type: Object as PropType<UmsRole>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const title = ref('New Role');
const roleFormRef = ref<FormInstance>()
const roleForm = reactive({
  name: '',
  description: '',
  enabled: true,
});

onMounted(() => {
  if (props.role) {
    title.value = 'Edit Role';
    roleForm.name = props.role.name;
    roleForm.description = props.role.description;
    roleForm.enabled = props.role.enabled;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input user name', trigger: 'blur' },
    { min: 3, max: 64, message: 'Length should be 3 to 64', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await roleFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (props.role == null) {
      let data: UmsRoleParam = {
        name: roleForm.name,
        description: roleForm.description,
        enabled: roleForm.enabled,
      };

      const rsp = await gUserApi.addRole(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: Partial<UmsRoleParam> = {};

      let propertyList = ['name', 'description', 'enabled'];

      for (let property of propertyList) {
        if ((roleForm as any)[property] !== (props.role as any)[property]) {
          (data as any)[property] = (roleForm as any)[property];
        }
      }

      const rsp = await gUserApi.updateRole({
        roleId: props.role.id,
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
.role-form {
  min-height: 400px;
}
</style>
