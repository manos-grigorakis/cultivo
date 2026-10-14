<script lang="ts" setup>
import PlantDetailsCard from '@/components/plants/PlantDetailsCard.vue'
import type { PlantResponse } from '@/types/plant-response.interface.ts'

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
