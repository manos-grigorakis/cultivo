<script setup lang="ts">
defineProps<{
  message?: string
  id?: string
}>()
</script>

<template>
  <p v-if="message" :id="id" class="ml-2 text-sm text-red-500" role="alert">
    {{ message }}
  </p>
</template>

<style scoped></style>
