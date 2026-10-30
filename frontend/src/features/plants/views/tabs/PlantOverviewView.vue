<script lang="ts" setup>
import PlantDetailsCard from '@/features/plants/components/PlantDetailsCard.vue'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'

defineProps<{
  plant: PlantResponse
}>()
</script>

<template>
  <section>
    <PlantDetailsCard
      :archived-at="plant.archivedAt"
      :created-at="plant.createdAt"
      :status="plant.status"
    />
  </section>
</template>
