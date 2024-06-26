<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="jobScheduleFormRef"
      :model="jobScheduleForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="jobScheduleForm.name" disabled />
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
        <el-input v-model="jobScheduleForm.runTag" />
      </el-form-item>

      <el-form-item label="Run Num" prop="runNum">
        <el-input-number v-model="jobScheduleForm.runNum" :min="1" />
      </el-form-item>

      <el-form-item label="Parallel Num" prop="parallelNum">
        <el-input-number v-model="jobScheduleForm.parallelNum" :min="1" />
      </el-form-item>

      <el-form-item label="Include Tag">
        <el-input v-model="jobScheduleForm.includeTags" />
      </el-form-item>

      <el-form-item label="Exclude Tag">
        <el-input v-model="jobScheduleForm.excludeTags" />
      </el-form-item>

      <el-form-item label="Add Tag">
        <el-input v-model="jobScheduleForm.addTags" />
      </el-form-item>

      <el-form-item label="Remove Tag">
        <el-input v-model="jobScheduleForm.removeTags" />
      </el-form-item>

      <el-form-item label="Crontab Config" prop="crontabConfig">
        <el-input v-model="jobScheduleForm.crontabConfig" />
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
import type { JmsJobScheduleParam, JmsJobSchedule, JmsJobScheduleUpdateParam } from '@/model/dto/jms';

import { gNotificationService } from '@/services';
import { gJobScheduleApi } from '@/api';

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
  jobSchedule: {
    type: Object as PropType<JmsJobSchedule>,
  }
});

const emit = defineEmits(['ok', 'cancel']);

onBeforeMount(() => {
  if (props.jobSchedule) {
    jobScheduleForm.name = props.jobSchedule.name,
    jobScheduleForm.runTag = props.jobSchedule.runTag;
    jobScheduleForm.runNum = props.jobSchedule.runNum;
    jobScheduleForm.parallelNum = props.jobSchedule.parallelNum;
    jobScheduleForm.includeTags = props.jobSchedule.includeTags;
    jobScheduleForm.excludeTags = props.jobSchedule.excludeTags;
    jobScheduleForm.addTags = props.jobSchedule.addTags;
    jobScheduleForm.removeTags = props.jobSchedule.removeTags;
    jobScheduleForm.crontabConfig = props.jobSchedule.crontabConfig;
  }
});

//#region Input Transform
const activeProjectBase = computed(() => {
  if (props.jobSchedule) {
    return {
      id: props.jobSchedule.projectId,
      name: props.jobSchedule.projectName,
    }
  } else {
    return props.projectBase!;
  }
});

const activeEnvironmentBase = computed(() => {
  if (props.jobSchedule) {
    return {
      id: props.jobSchedule.environmentId,
      name: props.jobSchedule.environmentName,
    }
  } else {
    return props.environmentBase!;
  }
});

const activeWorkerAppBase = computed(() => {
  if (props.jobSchedule) {
    return {
      id: props.jobSchedule.workerAppId,
      name: props.jobSchedule.workerAppName,
    }
  } else {
    return props.workerAppBase!;
  }
});

const activeWorkerGroupBase = computed(() => {
  if (props.jobSchedule) {
    return {
      id: props.jobSchedule.workerGroupId,
      name: props.jobSchedule.workerGroupName,
    }
  } else {
    return props.activeWorkerGroupBase!;
  }
});

const activeJobTemplate = computed(() => {
  if (props.jobSchedule) {
    return {
      id: props.jobSchedule.templateId,
      name: props.jobSchedule.templateName,
    }
  } else {
    return props.jobTemplate!;
  }
});
//#endregion

const handling = ref(false);

const jobScheduleFormRef = ref<FormInstance>()
const jobScheduleForm = reactive({
  name: uuidv4(),
  runTag: 'SCHED',
  runNum: 1,
  parallelNum: 1,
  includeTags: '',
  excludeTags: '',
  addTags: '',
  removeTags: '',
  crontabConfig: '',
});

const validateParallelNum = (rule: any, value: any, callback: any) => {
  if (value > jobScheduleForm.runNum) {
    callback(new Error("Parallel Num should be less than or equal to Run Num"))
  } else {
    callback()
  }
};

const rules = reactive<FormRules>({
  parallelNum: [
    { validator: validateParallelNum, trigger: 'change' }
  ],
  crontabConfig: [
    { required: true, message: 'Please input crontabConfig', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await jobScheduleFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.jobSchedule) {
      let data: JmsJobScheduleParam = {
        name: jobScheduleForm.name,
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
        runTag: jobScheduleForm.runTag,
        runNum: jobScheduleForm.runNum,
        parallelNum: jobScheduleForm.parallelNum,
        includeTags: jobScheduleForm.includeTags,
        excludeTags: jobScheduleForm.excludeTags,
        addTags: jobScheduleForm.addTags,
        removeTags: jobScheduleForm.removeTags,
        crontabConfig: jobScheduleForm.crontabConfig,
      }

      const rsp = await gJobScheduleApi.addJobSchedule(data);

      if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      } else {
        emit('ok');
      }
    } else {
      let data: JmsJobScheduleUpdateParam = {
        runTag: jobScheduleForm.runTag,
        runNum: jobScheduleForm.runNum,
        parallelNum: jobScheduleForm.parallelNum,
        includeTags: jobScheduleForm.includeTags,
        excludeTags: jobScheduleForm.excludeTags,
        addTags: jobScheduleForm.addTags,
        removeTags: jobScheduleForm.removeTags,
        crontabConfig: jobScheduleForm.crontabConfig,
      }

      const rsp = await gJobScheduleApi.updateJobSchedule({
        jobScheduleId: props.jobSchedule.id,
        data,
      });

      if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      } else {
        emit('ok');
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
