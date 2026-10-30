<script setup lang="ts">
defineProps<{
  status: string
  createdAt: string
  archivedAt: string | null
}>()
</script>

<template>
  <div class="rounded-card mt-6 max-w-70 bg-white px-4 py-3 drop-shadow-sm">
    <h3 class="text-primary-900 font-display mb-2 text-xl">Details</h3>

    <ul>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Status</p>
        <p class="text-primary-900">{{ status }}</p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Added</p>
        <p class="text-primary-900">{{ new Date(createdAt).toLocaleDateString() }}</p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Archived</p>
        <p class="text-primary-900">
          {{ archivedAt != null ? new Date(archivedAt).toLocaleDateString() : '-' }}
        </p>
      </li>
    </ul>
  </div>
</template>
