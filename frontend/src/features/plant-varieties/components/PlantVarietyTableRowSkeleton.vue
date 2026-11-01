<script setup lang="ts">
defineProps<{
  showActions: boolean
}>()
</script>

<template>
  <tr class="animate-pulse">
    <td class="px-6 py-4">
      <div class="bg-primary-200 h-4 w-8 rounded-md"></div>
    </td>

    <td class="px-6 py-4">
      <div class="bg-primary-200 h-4 w-32 rounded-md"></div>
    </td>

    <td class="px-6 py-4">
      <div class="bg-surface-muted h-4 w-24 rounded-md"></div>
    </td>

    <td class="px-6 py-4">
      <div class="bg-surface-muted h-4 w-24 rounded-md"></div>
    </td>

    <td v-if="showActions" class="px-6 py-4">
      <div class="flex items-center gap-2">
        <div class="bg-primary-200 h-9 w-9 rounded-lg"></div>
        <div class="bg-primary-200 h-9 w-9 rounded-lg"></div>
      </div>
    </td>
  </tr>
</template>

<style scoped></style>
