<template>
  <div class="page-container d-flex flex-column ma-4">
    <div>
      <el-button
        @click="back"
      >
        Back
      </el-button>
    </div>

    <div class="mx-10 text-h4 flex-grow-1 d-flex justify-center align-center">
      Sorry, no necessary permission to access.
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';

function back() {
  const router = useRouter();
  const route = useRoute();

  if (route.query.noGoBack) {
    router.push({ name: 'home' });
  } else {
    router.go(-1);
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  height: 100%;
}
</style>
