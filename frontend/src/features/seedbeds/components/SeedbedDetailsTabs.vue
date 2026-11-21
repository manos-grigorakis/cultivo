<script setup lang="ts">
import AppNavTab from '@/components/ui/AppNavTab.vue'

defineProps<{
  seedbedId: string | string[] | undefined
  sowingsCount: number
}>()
</script>

<template>
  <nav class="bg-primary-100 rounded-card mt-4 mb-6 p-2 shadow-xs">
    <ul class="flex items-center gap-2">
      <AppNavTab :id="seedbedId" router-name="seedbed-details-overview" label="overview" />
      <AppNavTab
        :id="seedbedId"
        router-name="seedbed-details-sowings"
        label="sowings"
        :count-badge="sowingsCount"
      />
    </ul>
  </nav>
</template>

<style scoped></style>
