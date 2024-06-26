<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="jobFolderFormRef"
      :model="jobFolderForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="jobFolderForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="jobFolderForm.description"
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

import type { PmsProjectBase, PmsJobFolder, PmsJobFolderParam, PmsJobFolderUpdateParam } from "@/model/dto/pms";

import { gNotificationService } from '@/services';
import { gJobFolderApi } from '@/api';

const props = defineProps({
  projectBase: {
    type: Object as PropType<PmsProjectBase>,
    required: true,
  },
  parentJobFolder: {
    type: Object as PropType<PmsJobFolder>,
    required: true,
  },
  jobFolder: {
    type: Object as PropType<PmsJobFolder>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const jobFolderFormRef = ref<FormInstance>()
const jobFolderForm = reactive({
  name: '',
  description: '',
});

onBeforeMount(() => {
  if (props.jobFolder) {
    jobFolderForm.name = props.jobFolder.name;
    jobFolderForm.description = props.jobFolder.description;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input job folder name', trigger: 'blur' },
    { min: 3, message: 'Length should be at least 3', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await jobFolderFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.jobFolder) {
      let data: PmsJobFolderParam = {
        projectId: props.projectBase.id,
        projectName: props.projectBase.name,
        parentId: props.parentJobFolder.id,
        parentName: props.parentJobFolder.name,
        name: jobFolderForm.name,
        description: jobFolderForm.description,
      };

      const rsp = await gJobFolderApi.addJobFolder(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: PmsJobFolderUpdateParam = {};

      let propertyList = ['name', 'description'] as const;

      for (let property of propertyList) {
        if (jobFolderForm[property] !== props.jobFolder[property]) {
          data[property] = jobFolderForm[property];
        }
      }

      const rsp = await gJobFolderApi.updateJobFolder({
        jobFolderId: props.jobFolder.id,
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
