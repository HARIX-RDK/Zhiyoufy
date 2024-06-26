<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="configSingleFormRef"
      :model="configSingleForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="configSingleForm.name" />
      </el-form-item>

      <el-form-item label="ConfigValue" prop="configValue">
        <el-input
          v-model="configSingleForm.configValue"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <div class="action-bar">
        <el-button
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
      </div>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onBeforeMount  } from 'vue';
import type { PropType } from 'vue';

import type { FormInstance, FormRules } from 'element-plus';

import type { EmsEnvironment, EmsConfigSingle, EmsConfigSingleParam, EmsConfigSingleUpdateParam } from "@/model/dto/ems";

import { gNotificationService } from '@/services';
import { gConfigSingleApi } from '@/api';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
  configSingle: {
    type: Object as PropType<EmsConfigSingle>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const configSingleFormRef = ref<FormInstance>()
const configSingleForm = reactive({
  name: '',
  configValue: '',
});

onBeforeMount(() => {
  if (props.configSingle) {
    configSingleForm.name = props.configSingle.name;
    configSingleForm.configValue = props.configSingle.configValue;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input config single name', trigger: 'blur' },
    { min: 3, message: 'Length should be at least 3', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await configSingleFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.configSingle) {
      let data: EmsConfigSingleParam = {
        environmentId: props.environment.id,
        environmentName: props.environment.name,
        name: configSingleForm.name,
        configValue: configSingleForm.configValue,
      };

      const rsp = await gConfigSingleApi.addConfigSingle(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: EmsConfigSingleUpdateParam = {};

      let propertyList = ['name', 'configValue'] as const;

      for (let property of propertyList) {
        if (configSingleForm[property] !== props.configSingle[property]) {
          data[property] = configSingleForm[property];
        }
      }

      const rsp = await gConfigSingleApi.updateConfigSingle({
        configSingleId: props.configSingle.id,
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
  .header {
    display: flex;
    align-items: center;
    height: 64px;
    background-color: var(--el-color-primary);
    color: var(--el-color-white);
  }

  .action-bar {
    margin-top: 20px;
    margin-left: 140px;

    .el-button {
      min-width: 80px;
    }
  }
}
</style>
