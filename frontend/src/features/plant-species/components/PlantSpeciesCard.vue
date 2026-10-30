<script setup lang="ts">
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'

defineProps<{
  species: PlantSpeciesResponse
}>()
</script>

<template>
  <li class="rounded-card bg-white shadow-sm hover:shadow-lg">
    <RouterLink :to="{ name: 'plant-species-details', params: { id: species.id } }">
      <div class="rounded-tl-card rounded-tr-card bg-plant-placeholder aspect-video w-full" />

      <div class="px-3 py-3">
        <h2 class="font-display text-lg capitalize">{{ species.name }}</h2>
        <p class="text-content-muted my-1 italic">{{ species.scientificName }}</p>
        <p class="text-content-muted ml-1 text-xs">{{ species.plantCategory.name }}</p>
      </div>
    </RouterLink>
  </li>
</template>

<style scoped></style>
