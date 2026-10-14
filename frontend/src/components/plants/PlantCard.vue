<script setup lang="ts">
import type { PlantResponse } from '@/types/plant-response.interface.ts'

defineProps<{
  plant: PlantResponse
}>()
</script>

<template>
  <li class="rounded-card bg-white drop-shadow-sm">
    <RouterLink :to="{ name: 'plant-details', params: { id: plant.id } }">
      <div class="rounded-tl-card rounded-tr-card bg-plant-placeholder aspect-video w-full" />

      <div class="px-2 py-3">
        <h2 class="font-display truncate pl-1 text-lg">{{ plant.label }}</h2>
      </div>
    </RouterLink>
  </li>
</template>

<style scoped></style>
