<script setup lang="ts">
import AppNavTab from '@/components/ui/AppNavTab.vue'

defineProps<{
  speciesId: number
  varietiesCount: number
  plantsCount: number
}>()
</script>

<template>
  <nav class="bg-primary-100 rounded-card mt-4 mb-6 p-2 shadow-xs">
    <ul class="flex items-center gap-2">
      <AppNavTab :id="speciesId" router-name="plant-species-details-overview" label="overview" />

      <AppNavTab
        :id="speciesId"
        router-name="plant-species-details-varieties"
        label="varieties"
        :count-badge="varietiesCount"
      />

      <AppNavTab
        :id="speciesId"
        router-name="plant-species-details-plants"
        label="plants"
        :count-badge="plantsCount"
      />
    </ul>
  </nav>
</template>

<style scoped></style>
