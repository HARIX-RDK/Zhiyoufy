<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="workerAppUserRelationFormRef"
      :model="workerAppUserRelationForm"
      label-width="140px"
    >
      <el-form-item label="IsOwner">
        <el-checkbox v-model="workerAppUserRelationForm.isOwner"/>
      </el-form-item>

      <el-form-item label="IsEditor">
        <el-checkbox v-model="workerAppUserRelationForm.isEditor"/>
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
import type { WmsWorkerApp, WmsWorkerAppUserRelationFull, WmsWorkerAppUserRelationParam, WmsWorkerAppUserRelationUpdateParam } from "@/model/dto/wms";

import { gNotificationService } from '@/services';
import { gWorkerAppApi } from '@/api';

const props = defineProps({
  workerApp: {
    type: Object as PropType<WmsWorkerApp>,
    required: true,
  },
  workerAppUserRelation: {
    type: Object as PropType<WmsWorkerAppUserRelationFull>,
  },
  userBase: {
    type: Object as PropType<UmsUserBase>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const workerAppUserRelationFormRef = ref<FormInstance>()
const workerAppUserRelationForm = reactive({
  isOwner: false,
  isEditor: false,
});

onBeforeMount(() => {
  if (props.workerAppUserRelation) {
    workerAppUserRelationForm.isOwner = props.workerAppUserRelation.isOwner;
    workerAppUserRelationForm.isEditor = props.workerAppUserRelation.isEditor;
  }
});

async function onClickOk() {
  try {
    handling.value = true;

    if (!props.workerAppUserRelation) {
      let data: WmsWorkerAppUserRelationParam = {
        workerAppId: props.workerApp.id,
        userId: props.userBase!.id,
        username: props.userBase!.username,
        isOwner: workerAppUserRelationForm.isOwner,
        isEditor: workerAppUserRelationForm.isEditor,
      };

      const rsp = await gWorkerAppApi.addWorkerAppUser(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: WmsWorkerAppUserRelationUpdateParam = {};

      let propertyList = ['isOwner', 'isEditor'] as const;

      for (let property of propertyList) {
        if (workerAppUserRelationForm[property] !== props.workerAppUserRelation[property]) {
          data[property] = workerAppUserRelationForm[property];
        }
      }

      if (Object.keys(data).length === 0) {
        emit('ok');
        return;
      }

      const rsp = await gWorkerAppApi.updateWorkerAppUser({
        relationId: props.workerAppUserRelation.id,
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
