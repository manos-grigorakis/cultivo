<script setup lang="ts">
defineProps<{
  plantId: string | string[] | undefined
  eventsCount: number
}>()
</script>

<template>
  <nav class="p-2 mt-4 mb-6 shadow-xs bg-primary-100 rounded-card">
    <ul class="flex items-center gap-2">
      <li>
        <RouterLink
          :to="{ name: 'plant-details-overview', params: { id: plantId } }"
          class="block px-8 py-4 text-xl font-medium rounded-card"
          active-class="bg-white shadow-sm"
          >Overview</RouterLink
        >
      </li>
      <li>
        <RouterLink
          :to="{ name: 'plant-details-journal', params: { id: plantId } }"
          class="block px-8 py-4 text-xl font-medium rounded-card"
          active-class="bg-white shadow-sm"
          >Journal <span class="text-sm text-content-muted">{{ eventsCount }}</span></RouterLink
        >
      </li>
    </ul>
  </nav>
</template>
