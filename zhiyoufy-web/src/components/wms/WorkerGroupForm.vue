<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="workerGroupFormRef"
      :model="workerGroupForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="workerGroupForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="workerGroupForm.description"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="WorkerLabels" prop="workerLabels">
        <el-input
          v-model="workerGroupForm.workerLabels"
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

import type { WmsWorkerApp, WmsWorkerGroup, WmsWorkerGroupParam, WmsWorkerGroupUpdateParam } from "@/model/dto/wms";

import { gNotificationService } from '@/services';
import { gWorkerGroupApi } from '@/api';

const props = defineProps({
  workerApp: {
    type: Object as PropType<WmsWorkerApp>,
    required: true,
  },
  workerGroup: {
    type: Object as PropType<WmsWorkerGroup>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const workerGroupFormRef = ref<FormInstance>()
const workerGroupForm = reactive({
  name: '',
  description: '',
  workerLabels: '',
});

onBeforeMount(() => {
  if (props.workerGroup) {
    workerGroupForm.name = props.workerGroup.name;
    workerGroupForm.description = props.workerGroup.description;
    workerGroupForm.workerLabels = props.workerGroup.workerLabels;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input workerGroup name', trigger: 'blur' },
    { min: 3, max: 64, message: 'Length should be 3 to 64', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await workerGroupFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.workerGroup) {
      let data: WmsWorkerGroupParam = {
        workerAppId: props.workerApp.id,
        workerAppName: props.workerApp.name,
        name: workerGroupForm.name,
        description: workerGroupForm.description,
        workerLabels: workerGroupForm.workerLabels,
      };

      const rsp = await gWorkerGroupApi.addWorkerGroup(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: WmsWorkerGroupUpdateParam = {};

      const propertyList = ['name', 'description', 'workerLabels'] as const;

      for (let property of propertyList) {
        if (workerGroupForm[property] !== props.workerGroup[property]) {
          data[property] = workerGroupForm[property];
        }
      }

      const rsp = await gWorkerGroupApi.updateWorkerGroup({
        workerGroupId: props.workerGroup.id,
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
