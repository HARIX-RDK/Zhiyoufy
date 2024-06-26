<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="envFormRef"
      :model="envForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Parent">
        <el-select
          v-model="parentEnvId"
          class="parent-env-select"
        >
          <el-option
            v-for="item in parentEnvironmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="Name" prop="name">
        <el-input v-model="envForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="envForm.description"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="WorkerLabels" prop="workerLabels">
        <el-input
          v-model="envForm.workerLabels"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="ExtraArgs" prop="extraArgs">
        <el-input
          v-model="envForm.extraArgs"
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
import { reactive, ref, onMounted, watch  } from 'vue';
import type { PropType } from 'vue';

import type { FormInstance, FormRules } from 'element-plus';

import type { EmsEnvironment, EmsEnvironmentBase, EmsEnvironmentParam, EmsEnvironmentUpdateParam } from "@/model/dto/ems";

import { gNotificationService } from '@/services';
import { gEnvironmentApi } from '@/api';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
  },
  environmentBaseList: {
    type: Array as PropType<Array<EmsEnvironmentBase>>,
    required: true,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const noParent = {
  name: 'No Parent',
  id: 0,
};

const unknownOldParent: any = {
  name: 'Unknown Old Parent',
};

let parentEnv = noParent;
const parentEnvId = ref(0);
let parentEnvironmentList: any[] = [];

const handling = ref(false);

const envFormRef = ref<FormInstance>()
const envForm = reactive({
  name: '',
  description: '',
  workerLabels: '',
  extraArgs: '',
});

watch(parentEnvId, (value) => {
  const found = parentEnvironmentList.find(
    env => env.id === value);
  parentEnv = found;
  console.log(`parentEnv changed to ${JSON.stringify(parentEnv)}`);
})

onMounted(() => {
  parentEnvironmentList = [noParent, ...props.environmentBaseList];

  if (props.environment) {
    const selfIdx = parentEnvironmentList.findIndex(
      env => env.id === props.environment!.id);
    parentEnvironmentList.splice(selfIdx, 1);

    if (props.environment.parentId) {
      const found = props.environmentBaseList.find(
        env => env.id === props.environment!.parentId);

      if (!found) {
        unknownOldParent.id = props.environment.parentId;
        parentEnvironmentList.unshift(unknownOldParent);
        parentEnvId.value = unknownOldParent.id;
      } else {
        parentEnvId.value = found.id;
      }
    } else {
      parentEnvId.value = noParent.id;
    }

    envForm.name = props.environment.name;
    envForm.description = props.environment.description;
    envForm.workerLabels = props.environment.workerLabels;
    envForm.extraArgs = props.environment.extraArgs;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input environment name', trigger: 'blur' },
    { min: 3, max: 64, message: 'Length should be 3 to 64', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await envFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.environment) {
      let data: EmsEnvironmentParam = {
        name: envForm.name,
        description: envForm.description,
        workerLabels: envForm.workerLabels,
        extraArgs: envForm.extraArgs,
      };

      if (parentEnv !== noParent) {
        data.parentId = parentEnv.id;
        data.parentName = parentEnv.name;
      }

      const rsp = await gEnvironmentApi.addEnvironment(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: EmsEnvironmentUpdateParam = {};

      if (props.environment.parentId !== parentEnv.id) {
        data.parentId = parentEnv.id;
        data.parentName = parentEnv.name;
      }

      let propertyList = ['name', 'description', 'workerLabels', 'extraArgs'] as const;

      for (let property of propertyList) {
        if (envForm[property] !== props.environment[property]) {
          data[property] = envForm[property];
        }
      }

      const rsp = await gEnvironmentApi.updateEnvironment({
        environmentId: props.environment.id,
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

  .parent-env-select {
    min-width: 300px;
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
