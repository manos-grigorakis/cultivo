<script setup lang="ts">
import AppNavigationBar from './components/layout/AppNavigationBar.vue'
</script>

<template>
  <div class="flex flex-col md:flex-row min-h-dvh">
    <AppNavigationBar />

    <main class="flex-1 p-6">
      <RouterView />
    </main>
  </div>
</template>

<style scoped></style>
