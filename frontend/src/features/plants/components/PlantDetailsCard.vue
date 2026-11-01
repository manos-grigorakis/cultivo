<script setup lang="ts">
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'

defineProps<{
  plant: PlantResponse
}>()
</script>

<template>
  <div class="rounded-card mt-6 max-w-70 bg-white px-4 py-3 drop-shadow-sm">
    <h3 class="text-primary-900 font-display mb-2 text-xl">Details</h3>

    <ul class="flex flex-col space-y-1">
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Species</p>
        <p class="text-primary-900">
          {{ plant.species != null ? plant.species.name : '-' }}
        </p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Variety</p>
        <p class="text-primary-900">
          {{ plant.variety != null ? plant.variety.name : '-' }}
        </p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Status</p>
        <p class="text-primary-900">{{ plant.status }}</p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Added</p>
        <p class="text-primary-900">{{ new Date(plant.createdAt).toLocaleDateString() }}</p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Archived</p>
        <p class="text-primary-900">
          {{ plant.archivedAt != null ? new Date(plant.archivedAt).toLocaleDateString() : '-' }}
        </p>
      </li>
    </ul>
  </div>
</template>
