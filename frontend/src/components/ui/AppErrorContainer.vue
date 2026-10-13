<script setup lang="ts">
defineProps<{
  errorMessage: string | null
}>()
</script>

<template>
  <div class="rounded-card bg-red-200 py-2 text-center text-red-900">
    <p>{{ errorMessage }}</p>
  </div>
</template>
