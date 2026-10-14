<script setup lang="ts">
import type { PlantResponse } from '@/types/plant-response.interface.ts'
import PlantDetailsCard from '@/components/plants/PlantDetailsCard.vue'

defineProps<{
  plant: PlantResponse
}>()
</script>

<template>
  <section>
    <PlantDetailsCard
      :status="plant.status"
      :created-at="plant.createdAt"
      :archived-at="plant.archivedAt"
    />
  </section>
</template>
