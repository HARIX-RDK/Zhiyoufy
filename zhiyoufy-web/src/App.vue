<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue';

import { RouterView } from 'vue-router';

onMounted(() => {
  window.addEventListener("resize", onResize);
  onResize();
});

onUnmounted(() => {
  window.removeEventListener("resize", onResize);
});

function onResize() {
  console.log(`window width ${window.innerWidth}`);
  console.log(`window height ${window.innerHeight}`);
  const appElement = document.getElementById('app');
  appElement?.style.setProperty("--window-width", `${window.innerWidth}px`);
  appElement?.style.setProperty("--window-height", `${window.innerHeight}px`);
}
</script>

<template>
  <RouterView />
</template>
