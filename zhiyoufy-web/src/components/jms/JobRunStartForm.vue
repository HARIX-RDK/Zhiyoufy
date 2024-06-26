<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="jobRunStartFormRef"
      :model="jobRunStartForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="RunGuid" prop="runGuid">
        <el-input v-model="jobRunStartForm.runGuid" disabled />
      </el-form-item>

      <el-form-item label="WorkerAppName">
        <el-input v-model="activeWorkerAppBase.name" disabled />
      </el-form-item>

      <el-form-item label="workerGroupName">
        <el-input v-model="activeWorkerGroupBase.name" disabled />
      </el-form-item>

      <el-form-item label="EnvironmentName">
        <el-input v-model="activeEnvironmentBase.name" disabled />
      </el-form-item>

      <el-form-item label="JobTemplateName">
        <el-input v-model="activeJobTemplate.name" disabled />
      </el-form-item>

      <el-form-item label="Run Tag">
        <el-input v-model="jobRunStartForm.runTag" />
      </el-form-item>

      <el-form-item label="Run Num">
        <el-input-number v-model="jobRunStartForm.runNum" :min="1" />
      </el-form-item>

      <el-form-item label="Parallel Num" prop="parallelNum">
        <el-input-number v-model="jobRunStartForm.parallelNum" :min="1" />
      </el-form-item>

      <el-form-item label="Include Tag">
        <el-input v-model="jobRunStartForm.includeTags" />
      </el-form-item>

      <el-form-item label="Exclude Tag">
        <el-input v-model="jobRunStartForm.excludeTags" />
      </el-form-item>

      <el-form-item label="Add Tag">
        <el-input v-model="jobRunStartForm.addTags" />
      </el-form-item>

      <el-form-item label="Remove Tag">
        <el-input v-model="jobRunStartForm.removeTags" />
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
import { reactive, ref, onBeforeMount, computed  } from 'vue';
import type { PropType } from 'vue';

import type { FormInstance, FormRules } from 'element-plus';

import { v4 as uuidv4 } from 'uuid';

import type { PmsProjectBase, PmsJobTemplate } from "@/model/dto/pms";
import type { EmsEnvironmentBase } from '@/model/dto/ems';
import type { WmsWorkerAppBase, WmsActiveWorkerGroupBase } from '@/model/dto/wms';
import type { JmsJobRunResultFull, JmsStartJobRunParam } from '@/model/dto/jms';

import { gNotificationService } from '@/services';
import { gJobRunApi } from '@/api';

const props = defineProps({
  projectBase: {
    type: Object as PropType<PmsProjectBase>,
  },
  environmentBase: {
    type: Object as PropType<EmsEnvironmentBase>,
  },
  workerAppBase: {
    type: Object as PropType<WmsWorkerAppBase>,
  },
  activeWorkerGroupBase: {
    type: Object as PropType<WmsActiveWorkerGroupBase>,
  },
  jobTemplate: {
    type: Object as PropType<PmsJobTemplate>,
  },
  jobRunResult: {
    type: Object as PropType<JmsJobRunResultFull>,
  }
});

const emit = defineEmits(['ok', 'cancel']);

onBeforeMount(() => {
  if (props.jobRunResult) {
    if (props.jobRunResult.runTag !== 'SCHED') {
      jobRunStartForm.runTag = props.jobRunResult.runTag;
    }
    jobRunStartForm.runNum = props.jobRunResult.runNum;
    jobRunStartForm.parallelNum = props.jobRunResult.parallelNum;
  }
})

//#region Input Transform
const activeProjectBase = computed(() => {
  if (props.jobRunResult) {
    return {
      id: props.jobRunResult.projectId,
      name: props.jobRunResult.projectName,
    }
  } else {
    return props.projectBase!;
  }
});

const activeEnvironmentBase = computed(() => {
  if (props.jobRunResult) {
    return {
      id: props.jobRunResult.environmentId,
      name: props.jobRunResult.environmentName,
    }
  } else {
    return props.environmentBase!;
  }
});

const activeWorkerAppBase = computed(() => {
  if (props.jobRunResult) {
    return {
      id: props.jobRunResult.workerAppId,
      name: props.jobRunResult.workerAppName,
    }
  } else {
    return props.workerAppBase!;
  }
});

const activeWorkerGroupBase = computed(() => {
  if (props.jobRunResult) {
    return {
      id: props.jobRunResult.workerGroupId,
      name: props.jobRunResult.workerGroupName,
    }
  } else {
    return props.activeWorkerGroupBase!;
  }
});

const activeJobTemplate = computed(() => {
  if (props.jobRunResult) {
    return {
      id: props.jobRunResult.templateId,
      name: props.jobRunResult.templateName,
    }
  } else {
    return props.jobTemplate!;
  }
});
//#endregion

const handling = ref(false);

const jobRunStartFormRef = ref<FormInstance>()
const jobRunStartForm = reactive({
  runGuid: uuidv4(),
  runTag: 'EXEC_IMMED',
  runNum: 1,
  parallelNum: 1,
  includeTags: '',
  excludeTags: '',
  addTags: '',
  removeTags: '',
  username: '',
});

const validateParallelNum = (rule: any, value: any, callback: any) => {
  if (value > jobRunStartForm.runNum) {
    callback(new Error("Parallel Num should be less than or equal to Run Num"))
  } else {
    callback()
  }
};

const rules = reactive<FormRules>({
  parallelNum: [
    { validator: validateParallelNum, trigger: 'change' }
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await jobRunStartFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    let data: JmsStartJobRunParam = {
      runGuid: jobRunStartForm.runGuid,
      workerAppId: activeWorkerAppBase.value.id,
      workerAppName: activeWorkerAppBase.value.name,
      workerGroupId: activeWorkerGroupBase.value.id,
      workerGroupName: activeWorkerGroupBase.value.name,
      environmentId: activeEnvironmentBase.value.id,
      environmentName: activeEnvironmentBase.value.name,
      projectId: activeProjectBase.value.id,
      projectName: activeProjectBase.value.name,
      templateId: activeJobTemplate.value.id,
      templateName: activeJobTemplate.value.name,
      runTag: jobRunStartForm.runTag,
      runNum: jobRunStartForm.runNum,
      parallelNum: jobRunStartForm.parallelNum,
      includeTags: jobRunStartForm.includeTags,
      excludeTags: jobRunStartForm.excludeTags,
      addTags: jobRunStartForm.addTags,
      removeTags: jobRunStartForm.removeTags,
    }

    const rsp = await gJobRunApi.startJobRun(data);

    if (rsp.error) {
      gNotificationService.error(rsp.error.message);
    } else {
      emit('ok');
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
