<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="environmentUserRelationFormRef"
      :model="environmentUserRelationForm"
      label-width="140px"
    >
      <el-form-item label="IsOwner">
        <el-checkbox v-model="environmentUserRelationForm.isOwner"/>
      </el-form-item>

      <el-form-item label="IsEditor">
        <el-checkbox v-model="environmentUserRelationForm.isEditor"/>
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
import type { EmsEnvironment, EmsEnvironmentUserRelationFull, EmsEnvironmentUserRelationParam, EmsEnvironmentUserRelationUpdateParam } from "@/model/dto/ems";

import { gNotificationService } from '@/services';
import { gEnvironmentApi } from '@/api';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
  environmentUserRelation: {
    type: Object as PropType<EmsEnvironmentUserRelationFull>,
  },
  userBase: {
    type: Object as PropType<UmsUserBase>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const environmentUserRelationFormRef = ref<FormInstance>()
const environmentUserRelationForm = reactive({
  isOwner: false,
  isEditor: false,
});

onBeforeMount(() => {
  if (props.environmentUserRelation) {
    environmentUserRelationForm.isOwner = props.environmentUserRelation.isOwner;
    environmentUserRelationForm.isEditor = props.environmentUserRelation.isEditor;
  }
});

async function onClickOk() {
  try {
    handling.value = true;

    if (!props.environmentUserRelation) {
      let data: EmsEnvironmentUserRelationParam = {
        environmentId: props.environment.id,
        userId: props.userBase!.id,
        username: props.userBase!.username,
        isOwner: environmentUserRelationForm.isOwner,
        isEditor: environmentUserRelationForm.isEditor,
      };

      const rsp = await gEnvironmentApi.addEnvironmentUser(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: EmsEnvironmentUserRelationUpdateParam = {};

      let propertyList = ['isOwner', 'isEditor'];

      for (let property of propertyList) {
        if ((environmentUserRelationForm as any)[property] !== (props.environmentUserRelation as any)[property]) {
          (data as any)[property] = (environmentUserRelationForm as any)[property];
        }
      }

      if (Object.keys(data).length === 0) {
        emit('ok');
        return;
      }

      const rsp = await gEnvironmentApi.updateEnvironmentUser({
        relationId: props.environmentUserRelation.id,
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
