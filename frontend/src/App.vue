<script setup lang="ts">
import AppNavigationBar from './components/layout/AppNavigationBar.vue'
</script>

<template>
  <div class="flex min-h-dvh flex-col md:flex-row">
    <AppNavigationBar />

    <main class="min-w-0 flex-1 p-6 pb-28">
      <RouterView />
    </main>
  </div>
</template>

<style scoped></style>
