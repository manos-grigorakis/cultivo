<script setup lang="ts">
import type { PlantCategoryResponse } from '@/types/plant-category-response.interface.ts'

defineProps<{
  plantCategory: PlantCategoryResponse
}>()
</script>

<template>
  <li
    class="rounded-card bg-white px-2 py-4 shadow-sm"
  >
    <RouterLink :to="{name: 'plant-category-view', params: {id: plantCategory.id}}">
      <h2 class="font-display pl-1 text-lg capitalize">{{ plantCategory.name }}</h2>
    </RouterLink>
  </li>
</template>

<style scoped></style>
