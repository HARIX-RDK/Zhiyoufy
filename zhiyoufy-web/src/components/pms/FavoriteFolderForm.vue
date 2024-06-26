<template>
  <el-card
    class="container"
  >
    <el-form
      class="form"
      ref="favoriteFolderFormRef"
      :model="favoriteFolderForm"
      :rules="rules"
      label-width="140px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="favoriteFolderForm.name" />
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

import type { PmsProjectBase, PmsFavoriteFolder, PmsFavoriteFolderParam, PmsFavoriteFolderUpdateParam } from "@/model/dto/pms";

import { gNotificationService } from '@/services';
import { gFavoriteFolderApi } from '@/api';

const props = defineProps({
  projectBase: {
    type: Object as PropType<PmsProjectBase>,
    required: true,
  },
  favoriteFolder: {
    type: Object as PropType<PmsFavoriteFolder>,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const handling = ref(false);

const favoriteFolderFormRef = ref<FormInstance>()
const favoriteFolderForm = reactive({
  name: '',
});

onBeforeMount(() => {
  if (props.favoriteFolder) {
    favoriteFolderForm.name = props.favoriteFolder.name;
  }
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: 'Please input favorite folder name', trigger: 'blur' },
    { min: 3, message: 'Length should be at least 3', trigger: 'blur' },
  ],
});

async function onClickOk() {
  try {
    handling.value = true;

    const valid = await favoriteFolderFormRef.value!.validate((valid, fields) => {
      if (!valid) {
        console.log('error submit!', fields)
      }
    })

    if (!valid) return;

    if (!props.favoriteFolder) {
      let data: PmsFavoriteFolderParam = {
        projectId: props.projectBase.id,
        projectName: props.projectBase.name,
        name: favoriteFolderForm.name,
      };

      const rsp = await gFavoriteFolderApi.addFavoriteFolder(data);

      if (rsp.data) {
        emit('ok');
      } else if (rsp.error) {
        gNotificationService.error(rsp.error.message);
      }
    } else {
      let data: PmsFavoriteFolderUpdateParam = {};

      let propertyList = ['name'] as const;

      for (let property of propertyList) {
        if (favoriteFolderForm[property] !== props.favoriteFolder[property]) {
          data[property] = favoriteFolderForm[property];
        }
      }

      if (Object.keys(data).length === 0) {
        emit('cancel');
        return;
      }

      const rsp = await gFavoriteFolderApi.updateFavoriteFolder({
        favoriteFolderId: props.favoriteFolder.id,
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
