<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="jobTemplateFormRef"
      :model="jobTemplateForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="jobTemplateForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="jobTemplateForm.description"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="JobPath" prop="jobPath">
        <el-input v-model="jobTemplateForm.jobPath" />
      </el-form-item>

      <el-form-item label="WorkerLabels" prop="workerLabels">
        <el-input
          v-model="jobTemplateForm.workerLabels"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="TimeoutSeconds" prop="timeoutSeconds">
        <el-input-number
          v-model="jobTemplateForm.timeoutSeconds"
        />
      </el-form-item>

      <el-form-item v-if="!jobTemplateForm.isPerf" label="BaseConfPath" prop="baseConfPath">
        <el-input v-model="jobTemplateForm.baseConfPath" />
      </el-form-item>

      <el-form-item v-if="!jobTemplateForm.isPerf" label="PrivateConfPath" prop="privateConfPath">
        <el-input v-model="jobTemplateForm.privateConfPath" />
      </el-form-item>

      <el-form-item label="ConfigSingles" prop="configSingles">
        <el-input
          v-model="jobTemplateForm.configSingles"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="ConfigCollections" prop="configCollections">
        <el-input
          v-model="jobTemplateForm.configCollections"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="ExtraArgs" prop="extraArgs">
        <el-input
          v-model="jobTemplateForm.extraArgs"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="IsPerf">
        <el-checkbox v-model="jobTemplateForm.isPerf"/>
      </el-form-item>

      <el-form-item v-if="jobTemplateForm.isPerf" label="DashboardAddr" prop="dashboardAddr">
        <el-input v-model="jobTemplateForm.dashboardAddr" />
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

import type { PmsProjectBase, PmsJobFolder, PmsJobTemplate, PmsJobTemplateParam, PmsJobTemplateUpdateParam } from "@/model/dto/pms";

import { gNotificationService } from '@/services';
import { gJobTemplateApi } from '@/api';

const props = defineProps({
  projectBase: {
    type: Object as PropType<PmsProjectBase>,
    required: true,
  },
  jobFolder: {
    type: Object as PropType<PmsJobFolder>,
  },
  jobTemplate: {
    type: Object as PropType<PmsJobTemplate>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const jobTemplateFormRef = ref<FormInstance>()
const jobTemplateForm = reactive({
  name: '',
  description: '',
  jobPath: '',
  workerLabels: '',
  timeoutSeconds: 0,
  baseConfPath: '',
  privateConfPath: '',
  configSingles: '',
  configCollections: '',
  extraArgs: '',
  isPerf: false,
  dashboardAddr: '',
});

onBeforeMount(() => {
  if (props.jobTemplate) {
    jobTemplateForm.name = props.jobTemplate.name;
    jobTemplateForm.description = props.jobTemplate.description;
    jobTemplateForm.jobPath = props.jobTemplate.jobPath;
    jobTemplateForm.workerLabels = props.jobTemplate.workerLabels;
    jobTemplateForm.timeoutSeconds = props.jobTemplate.timeoutSeconds;
    jobTemplateForm.baseConfPath = props.jobTemplate.baseConfPath;
    jobTemplateForm.privateConfPath = props.jobTemplate.privateConfPath;
    jobTemplateForm.configSingles = props.jobTemplate.configSingles;
    jobTemplateForm.configCollections = props.jobTemplate.configCollections;
    jobTemplateForm.extraArgs = props.jobTemplate.extraArgs;
    jobTemplateForm.isPerf = props.jobTemplate.isPerf;
    jobTemplateForm.dashboardAddr = props.jobTemplate.dashboardAddr;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input job template name', trigger: 'blur' },
    { min: 3, message: 'Length should be at least 3', trigger: 'blur' },
  ],
  jobPath: [
    { required: true, message: 'Please input job path', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await jobTemplateFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.jobTemplate) {
      let data: PmsJobTemplateParam = {
        projectId: props.projectBase.id,
        projectName: props.projectBase.name,
        folderId: props.jobFolder!.id,
        folderName: props.jobFolder!.name,
        name: jobTemplateForm.name,
        description: jobTemplateForm.description,
        jobPath: jobTemplateForm.jobPath,
        workerLabels: jobTemplateForm.workerLabels,
        timeoutSeconds: jobTemplateForm.timeoutSeconds,
        baseConfPath: jobTemplateForm.baseConfPath,
        privateConfPath: jobTemplateForm.privateConfPath,
        configSingles: jobTemplateForm.configSingles,
        configCollections: jobTemplateForm.configCollections,
        extraArgs: jobTemplateForm.extraArgs,
        isPerf: jobTemplateForm.isPerf,
        dashboardAddr: jobTemplateForm.dashboardAddr,
      };

      const rsp = await gJobTemplateApi.addJobTemplate(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: PmsJobTemplateUpdateParam = {};

      let propertyList = ['name', 'description', 'jobPath', 'workerLabels', 'timeoutSeconds', 'baseConfPath',
          'privateConfPath', 'configSingles', 'configCollections', 'extraArgs', 'isPerf', 'dashboardAddr'] as const;

      for (let property of propertyList) {
        if (jobTemplateForm[property] !== props.jobTemplate[property]) {
          data[property] = jobTemplateForm[property] as any;
        }
      }

      const rsp = await gJobTemplateApi.updateJobTemplate({
        jobTemplateId: props.jobTemplate.id,
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
