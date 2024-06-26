<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="configCollectionFormRef"
      :model="configCollectionForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="configCollectionForm.name" />
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

import type { EmsEnvironment, EmsConfigCollection, EmsConfigCollectionParam, EmsConfigCollectionUpdateParam } from "@/model/dto/ems";

import { gNotificationService } from '@/services';
import { gConfigCollectionApi } from '@/api';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
  configCollection: {
    type: Object as PropType<EmsConfigCollection>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const configCollectionFormRef = ref<FormInstance>()
const configCollectionForm = reactive({
  name: '',
});

onBeforeMount(() => {
  if (props.configCollection) {
    configCollectionForm.name = props.configCollection.name;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input config collection name', trigger: 'blur' },
    { min: 3, message: 'Length should be at least 3', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await configCollectionFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.configCollection) {
      let data: EmsConfigCollectionParam = {
        environmentId: props.environment.id,
        environmentName: props.environment.name,
        name: configCollectionForm.name,
      };

      const rsp = await gConfigCollectionApi.addConfigCollection(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: EmsConfigCollectionUpdateParam = {};

      let propertyList = ['name'] as const;

      for (let property of propertyList) {
        if (configCollectionForm[property] !== props.configCollection[property]) {
          data[property] = configCollectionForm[property];
        }
      }

      const rsp = await gConfigCollectionApi.updateConfigCollection({
        configCollectionId: props.configCollection.id,
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
