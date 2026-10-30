<script setup lang="ts">
defineProps<{
  plantId: string | string[] | undefined
  eventsCount: number
}>()
</script>

<template>
  <nav class="bg-primary-100 rounded-card mt-4 mb-6 p-2 shadow-xs">
    <ul class="flex items-center gap-2">
      <li>
        <RouterLink
          :to="{ name: 'plant-details-overview', params: { id: plantId } }"
          class="rounded-card block px-8 py-4 text-xl font-medium"
          active-class="bg-white shadow-sm"
          >Overview</RouterLink
        >
      </li>
      <li>
        <RouterLink
          :to="{ name: 'plant-details-journal', params: { id: plantId } }"
          class="rounded-card block px-8 py-4 text-xl font-medium"
          active-class="bg-white shadow-sm"
          >Journal <span class="text-content-muted text-sm">{{ eventsCount }}</span></RouterLink
        >
      </li>
    </ul>
  </nav>
</template>
