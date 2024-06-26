<template>
  <div class="sidebar">
    <div v-if="!sidebar.collapse" class="sidebar-title">
      <div>
        <p class="main">{{  title }}</p>
        <p class="sub">Help make zhiyou</p>
      </div>
    </div>
    <el-menu
        class="sidebar-el-menu"
        :default-active="onRoutes"
        :collapse="sidebar.collapse"
        unique-opened
        router
    >
        <template v-for="item in navItems">
            <template v-if="item.children">
                <el-sub-menu :index="item.index" :key="item.index">
                    <template #title>
                        <el-icon v-if="item.elIcon">
                            <component :is="item.elIcon"></component>
                        </el-icon>
                        <i v-if="item.iconifyIcon" class="el-icon">
                          <Icon :icon="item.iconifyIcon" />
                        </i>
                        <span>{{ item.title }}</span>
                    </template>
                    <template v-for="subItem in item.children">
                        <el-sub-menu
                            v-if="subItem.children"
                            :index="subItem.index"
                            :key="`${subItem.index}-if`"
                        >
                            <template #title>{{ subItem.title }}</template>
                            <el-menu-item v-for="threeItem in subItem.children" :key="threeItem.index" :index="threeItem.index">
                                {{ threeItem.title }}
                            </el-menu-item>
                        </el-sub-menu>
                        <el-menu-item v-else :index="subItem.index" :key="`${subItem.index}-else`">
                            {{ subItem.title }}
                        </el-menu-item>
                    </template>
                </el-sub-menu>
            </template>
            <template v-else>
                <el-menu-item :index="item.index" :key="item.index">
                    <el-icon v-if="item.elIcon">
                        <component :is="item.elIcon"></component>
                    </el-icon>
                    <i v-if="item.iconifyIcon" class="el-icon">
                      <Icon :icon="item.iconifyIcon" />
                    </i>
                    <template #title>{{ item.title }}</template>
                </el-menu-item>
            </template>
        </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';

import { Icon } from '@iconify/vue';

import { useSidebarStore } from '@/stores/sidebar';
import type { NavigationItem } from '@/model/bo';

import { defaultSettings } from '@/settings';

defineProps({
  navItems: {
    type: Array<NavigationItem>,
    required: true,
  },
})

const route = useRoute();
const onRoutes = computed(() => {
  return route.path;
});

const sidebar = useSidebarStore();

const title = defaultSettings.title;
</script>

<style lang="scss" scoped>
.sidebar {
  --el-menu-item-height: 40px;
  height: 100%;
  overflow-y: scroll;
  border-right: 1px solid var(--color-border);
}
.sidebar-title {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid var(--color-border);

  .main {
    font-weight: bold;
    font-size: 1.25rem;
  }

  .sub {
    font-size: 0.875rem;
  }
}
.sidebar::-webkit-scrollbar {
  width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
  width: 250px;
}
.sidebar > ul {
  height: 100%;
}
</style>
