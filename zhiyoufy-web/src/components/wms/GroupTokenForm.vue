<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="groupTokenFormRef"
      :model="groupTokenForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="groupTokenForm.name" />
      </el-form-item>

      <el-form-item label="Secret" prop="secret">
        <el-input v-model="groupTokenForm.secret" />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="groupTokenForm.description"
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

import type { WmsWorkerApp, WmsWorkerGroup, WmsGroupToken, WmsGroupTokenParam, WmsGroupTokenUpdateParam } from "@/model/dto/wms";

import { gNotificationService } from '@/services';
import { gGroupTokenApi } from '@/api';

const props = defineProps({
  workerApp: {
    type: Object as PropType<WmsWorkerApp>,
    required: true,
  },
  workerGroup: {
    type: Object as PropType<WmsWorkerGroup>,
    required: true,
  },
  groupToken: {
    type: Object as PropType<WmsGroupToken>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const groupTokenFormRef = ref<FormInstance>()
const groupTokenForm = reactive({
  name: '',
  secret: '',
  description: '',
});

onBeforeMount(() => {
  if (props.groupToken) {
    groupTokenForm.name = props.groupToken.name;
    groupTokenForm.description = props.groupToken.description;
    groupTokenForm.secret = props.groupToken.secret;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input groupToken name', trigger: 'blur' },
    { min: 3, max: 64, message: 'Length should be 3 to 64', trigger: 'blur' },
  ],
  secret: [
    { required: true, message: 'Please input groupToken secret', trigger: 'blur' },
    { min: 8, message: 'Length should be at least 8', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await groupTokenFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.groupToken) {
      let data: WmsGroupTokenParam = {
        workerAppId: props.workerApp.id,
        workerAppName: props.workerApp.name,
        workerGroupId: props.workerGroup.id,
        workerGroupName: props.workerGroup.name,
        name: groupTokenForm.name,
        secret: groupTokenForm.secret,
        description: groupTokenForm.description,
      };

      const rsp = await gGroupTokenApi.addGroupToken(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: WmsGroupTokenUpdateParam = {};

      const propertyList = ['name', 'description', 'secret'] as const;

      for (let property of propertyList) {
        if (groupTokenForm[property] !== props.groupToken[property]) {
          data[property] = groupTokenForm[property];
        }
      }

      const rsp = await gGroupTokenApi.updateGroupToken({
        groupTokenId: props.groupToken.id,
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
