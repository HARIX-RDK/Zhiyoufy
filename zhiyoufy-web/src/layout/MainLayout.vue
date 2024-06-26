<template>
  <div class="main-container"
    :class="{collapse: sidebarStore.collapse}">
    <AppSidebar
      class="app-sidebar"
      :nav-items="mainNavItemsActive">
    </AppSidebar>
    <div class="main-right">
      <AppHeader>
      </AppHeader>
      <div class="main-content">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/base/AppHeader.vue';
import AppSidebar from '@/components/base/AppSidebar.vue';

import { mainNavItemsFull } from '@/router';
import { useSidebarStore } from '@/stores/sidebar';
import { NavigationUtils } from '@/utils';


const sidebarStore = useSidebarStore();

const mainNavItemsActive = NavigationUtils.processNavigationItems(mainNavItemsFull);
</script>

<style lang="scss" scoped>
.main-container {
  height: 100%;

  .app-sidebar {
    float: left;
    width: 250px;
  }

  .main-right {
    position: absolute;
    left: 250px;
    right: 0;
    top: 0;
    bottom: 0;

    .main-content {
      height: calc(100% - 64px);
      width: 100%;
    }
  }

  &.collapse {
    .app-sidebar {
      width: 64px;
    }

    .main-right {
      left: 64px;
    }
  }
}
</style>
