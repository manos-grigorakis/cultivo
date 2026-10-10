<script setup lang="ts">
defineProps<{
  status: string
  createdAt: string
}>()
</script>

<template>
  <div class="px-4 py-3 mt-6 bg-white rounded-card drop-shadow-sm max-w-70">
    <h3 class="mb-2 text-xl text-primary-900 font-display">Details</h3>

    <ul>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Status</p>
        <p class="text-primary-900">{{ status }}</p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Added</p>
        <p class="text-primary-900">{{ new Date(createdAt).toLocaleDateString() }}</p>
      </li>
    </ul>
  </div>
</template>
