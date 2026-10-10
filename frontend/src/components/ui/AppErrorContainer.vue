<script setup lang="ts">
defineProps<{
  errorMessage: string | null
}>()
</script>

<template>
  <div class="py-2 text-center text-red-900 bg-red-200 rounded-card">
    <p>{{ errorMessage }}</p>
  </div>
</template>
