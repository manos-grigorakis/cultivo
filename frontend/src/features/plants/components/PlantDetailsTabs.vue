<script setup lang="ts">
import AppNavTab from '@/components/ui/AppNavTab.vue'

defineProps<{
  plantId: string | string[] | undefined
  eventsCount: number
}>()
</script>

<template>
  <nav class="bg-primary-100 rounded-card mt-4 mb-6 p-2 shadow-xs">
    <ul class="flex items-center gap-2">
      <AppNavTab :id="plantId" router-name="plant-details-overview" label="overview" />

      <AppNavTab
        :id="plantId"
        router-name="plant-details-journal"
        label="journal"
        :count-badge="eventsCount"
      />
    </ul>
  </nav>
</template>
