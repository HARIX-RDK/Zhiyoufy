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
      Sorry, page not found
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

function back() {
  if (route.query?.noGoBack) {
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
