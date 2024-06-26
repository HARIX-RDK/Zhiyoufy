import { ref } from 'vue'

import { defineStore } from 'pinia';

export const useSidebarStore = defineStore('sidebar', () => {
  const collapse = ref(false);

  function handleCollapse() {
    collapse.value = !collapse.value;
  }

	return {
    collapse,

    handleCollapse,
  }
});
