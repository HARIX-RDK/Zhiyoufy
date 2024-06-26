<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="workerAppFormRef"
      :model="workerAppForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="workerAppForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="workerAppForm.description"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="WorkerLabels" prop="workerLabels">
        <el-input
          v-model="workerAppForm.workerLabels"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="NeedConfigBeJson">
        <el-checkbox v-model="workerAppForm.needConfigBeJson"/>
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

import type { WmsWorkerApp, WmsWorkerAppParam } from "@/model/dto/wms";

import { gNotificationService } from '@/services';
import { gWorkerAppApi } from '@/api';

const props = defineProps({
  workerApp: {
    type: Object as PropType<WmsWorkerApp>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const workerAppFormRef = ref<FormInstance>()
const workerAppForm = reactive({
  name: '',
  description: '',
  workerLabels: '',
  needConfigBeJson: false,
});

onBeforeMount(() => {
  if (props.workerApp) {
    workerAppForm.name = props.workerApp.name;
    workerAppForm.description = props.workerApp.description;
    workerAppForm.workerLabels = props.workerApp.workerLabels;
    workerAppForm.needConfigBeJson = props.workerApp.needConfigBeJson;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input workerApp name', trigger: 'blur' },
    { min: 3, max: 64, message: 'Length should be 3 to 64', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await workerAppFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.workerApp) {
      let data: WmsWorkerAppParam = {
        name: workerAppForm.name,
        description: workerAppForm.description,
        workerLabels: workerAppForm.workerLabels,
        needConfigBeJson: workerAppForm.needConfigBeJson,
      };

      const rsp = await gWorkerAppApi.addWorkerApp(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: Partial<WmsWorkerAppParam> = {};

      let propertyList = ['name', 'description', 'workerLabels', 'needConfigBeJson'];

      for (let property of propertyList) {
        if ((workerAppForm as any)[property] !== (props.workerApp as any)[property]) {
          (data as any)[property] = (workerAppForm as any)[property];
        }
      }

      const rsp = await gWorkerAppApi.updateWorkerApp({
        workerAppId: props.workerApp.id,
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
  .action-bar {
    margin-top: 20px;
    margin-left: 140px;

    .el-button {
      min-width: 80px;
    }
  }
}
</style>
