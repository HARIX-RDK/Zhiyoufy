<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="projectFormRef"
      :model="projectForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="projectForm.name" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="projectForm.description"
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

import type { PmsProject, PmsProjectParam, PmsProjectUpdateParam } from "@/model/dto/pms";

import { gNotificationService } from '@/services';
import { gProjectApi } from '@/api';

const props = defineProps({
  project: {
    type: Object as PropType<PmsProject>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const projectFormRef = ref<FormInstance>()
const projectForm = reactive({
  name: '',
  description: '',
});

onBeforeMount(() => {
  if (props.project) {
    projectForm.name = props.project.name;
    projectForm.description = props.project.description;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input project name', trigger: 'blur' },
    { min: 3, max: 64, message: 'Length should be 3 to 64', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await projectFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.project) {
      let data: PmsProjectParam = {
        name: projectForm.name,
        description: projectForm.description,
      };

      const rsp = await gProjectApi.addProject(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: PmsProjectUpdateParam = {};

      let propertyList = ['name', 'description'] as const;

      for (let property of propertyList) {
        if (projectForm[property] !== props.project[property]) {
          data[property] = projectForm[property];
        }
      }

      const rsp = await gProjectApi.updateProject({
        projectId: props.project.id,
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
