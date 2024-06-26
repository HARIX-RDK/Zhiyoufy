<template>
  <div class="app-header">
    <div class="ml-2" @click="sidebarStore.handleCollapse">
      <el-icon v-if="!sidebarStore.collapse" :size="30"><Fold /></el-icon>
      <el-icon v-else :size="30"><Expand /></el-icon>
    </div>
    <div class="spacer"></div>
    <div class="mr-2">
      <el-dropdown trigger="click" @command="handleCommand">
        <el-button type="primary">
          <el-icon class="mr-1"><Avatar /></el-icon> {{ userStore.name }}
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="update-password">Update Password</el-dropdown-item>
            <el-dropdown-item divided command="logout">Signout {{ userStore.name }}</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

  <el-dialog
    v-model="updatePasswordDialogVisible"
    title="Update Password"
  >
    <UpdatePasswordDialog :username="userStore.name" @ok="updatePasswordDialogVisible = false" @cancel="updatePasswordDialogVisible = false">
    </UpdatePasswordDialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import { useSidebarStore } from '@/stores/sidebar';
import { useUserStore } from '@/stores/user';
import { gUserService } from '@/services';

import UpdatePasswordDialog from '../ums/UpdatePasswordDialog.vue';

const updatePasswordDialogVisible = ref(false);

const router = useRouter();
const sidebarStore = useSidebarStore();
const userStore = useUserStore();

async function handleCommand(command: string) {
  console.log(`click on item ${command}`);

  if (command === 'update-password') {
    updatePasswordDialogVisible.value = true;
  } else if (command === 'logout') {
    try {
      await gUserService.logout();
    } finally {
      router.push({name: "login"});
    }
  }
}
</script>

<style lang="scss" scoped>
.app-header {
  display: flex;
  align-items: center;
  height: 64px;
  border-bottom: 1px solid var(--color-border);
}
</style>
