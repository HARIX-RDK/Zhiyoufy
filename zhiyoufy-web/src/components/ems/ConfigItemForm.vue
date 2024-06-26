<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="configItemFormRef"
      :model="configItemForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="configItemForm.name" />
      </el-form-item>

      <el-form-item label="ConfigValue" prop="configValue">
        <el-input
          v-model="configItemForm.configValue"
          :autosize="true"
          type="textarea"
        />
      </el-form-item>

      <el-form-item label="Tags" prop="tags">
        <el-input v-model="configItemForm.tags" />
      </el-form-item>

      <el-form-item label="Sort" prop="sort">
        <el-input-number v-model="configItemForm.sort" />
      </el-form-item>

      <el-form-item label="Disabled">
        <el-checkbox v-model="configItemForm.disabled"/>
      </el-form-item>

      <el-form-item v-if="configItem" label="InUse">
        <el-checkbox v-model="configItemForm.inUse" disabled/>
      </el-form-item>

      <el-form-item v-if="configItem" label="UsageId" prop="usageId">
        <el-input v-model="configItemForm.usageId" disabled />
      </el-form-item>

      <el-form-item v-if="configItem" label="UsageTimeoutAt" prop="usageTimeoutAt">
        <el-input v-model="configItemForm.usageTimeoutAt" disabled />
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
import { reactive, ref, onBeforeMount } from 'vue';
import type { PropType } from 'vue';

import type { FormInstance, FormRules } from 'element-plus';

import type { EmsEnvironment, EmsConfigCollection, EmsConfigItem, EmsConfigItemParam, EmsConfigItemUpdateParam } from "@/model/dto/ems";

import { gNotificationService } from '@/services';
import { gConfigItemApi } from '@/api';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
  configCollection: {
    type: Object as PropType<EmsConfigCollection>,
    required: true,
  },
  configItem: {
    type: Object as PropType<EmsConfigItem>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const configItemFormRef = ref<FormInstance>()
const configItemForm = reactive({
  name: '',
  configValue: '',
  tags: '',
  sort: 0,
  disabled: false,
  inUse: false,
  usageId: '',
  usageTimeoutAt: '',
});

onBeforeMount(() => {
  if (props.configItem) {
    configItemForm.name = props.configItem.name;
    configItemForm.configValue = props.configItem.configValue;
    configItemForm.tags = props.configItem.tags;
    configItemForm.sort = props.configItem.sort;
    configItemForm.disabled = props.configItem.disabled;
    configItemForm.inUse = props.configItem.inUse;
    configItemForm.usageId = props.configItem.usageId;
    configItemForm.usageTimeoutAt = props.configItem.usageTimeoutAt;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input config item name', trigger: 'blur' },
    { min: 3, message: 'Length should be at least 3', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await configItemFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.configItem) {
      let data: EmsConfigItemParam = {
        environmentId: props.environment.id,
        environmentName: props.environment.name,
        collectionId: props.configCollection.id,
        collectionName: props.configCollection.name,
        name: configItemForm.name,
        configValue: configItemForm.configValue,
        tags: configItemForm.tags,
        sort: configItemForm.sort,
        disabled: configItemForm.disabled,
      };

      const rsp = await gConfigItemApi.addConfigItem(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: EmsConfigItemUpdateParam = {};

      let propertyList = ['name', 'configValue', 'tags', 'sort', 'disabled'] as const;

      for (let property of propertyList) {
        if (configItemForm[property] !== props.configItem[property]) {
          (data as any)[property] = configItemForm[property];
        }
      }

      const rsp = await gConfigItemApi.updateConfigItem({
        configItemId: props.configItem.id,
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
  .header {
    display: flex;
    align-items: center;
    height: 64px;
    background-color: var(--el-color-primary);
    color: var(--el-color-white);
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
