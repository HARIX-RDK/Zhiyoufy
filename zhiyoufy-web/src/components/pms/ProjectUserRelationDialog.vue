<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="projectUserRelationFormRef"
      :model="projectUserRelationForm"
      label-width="140px"
    >
      <el-form-item label="IsOwner">
        <el-checkbox v-model="projectUserRelationForm.isOwner"/>
      </el-form-item>

      <el-form-item label="IsEditor">
        <el-checkbox v-model="projectUserRelationForm.isEditor"/>
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

import type { FormInstance } from 'element-plus';

import type { UmsUserBase } from '@/model/dto/ums';
import type { PmsProject, PmsProjectUserRelationFull, PmsProjectUserRelationParam, PmsProjectUserRelationUpdateParam } from "@/model/dto/pms";

import { gNotificationService } from '@/services';
import { gProjectApi } from '@/api';

const props = defineProps({
  project: {
    type: Object as PropType<PmsProject>,
    required: true,
  },
  projectUserRelation: {
    type: Object as PropType<PmsProjectUserRelationFull>,
  },
  userBase: {
    type: Object as PropType<UmsUserBase>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const projectUserRelationFormRef = ref<FormInstance>()
const projectUserRelationForm = reactive({
  isOwner: false,
  isEditor: false,
});

onBeforeMount(() => {
  if (props.projectUserRelation) {
    projectUserRelationForm.isOwner = props.projectUserRelation.isOwner;
    projectUserRelationForm.isEditor = props.projectUserRelation.isEditor;
  }
});

async function onClickOk() {
  try {
    handling.value = true;

    if (!props.projectUserRelation) {
      let data: PmsProjectUserRelationParam = {
        projectId: props.project.id,
        userId: props.userBase!.id,
        username: props.userBase!.username,
        isOwner: projectUserRelationForm.isOwner,
        isEditor: projectUserRelationForm.isEditor,
      };

      const rsp = await gProjectApi.addProjectUser(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: PmsProjectUserRelationUpdateParam = {};

      let propertyList = ['isOwner', 'isEditor'] as const;

      for (let property of propertyList) {
        if (projectUserRelationForm[property] !== props.projectUserRelation[property]) {
          data[property] = projectUserRelationForm[property];
        }
      }

      if (Object.keys(data).length === 0) {
        emit('ok');
        return;
      }

      const rsp = await gProjectApi.updateProjectUser({
        relationId: props.projectUserRelation.id,
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
