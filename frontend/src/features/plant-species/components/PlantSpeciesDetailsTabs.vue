<script setup lang="ts">
defineProps<{
  speciesId: number
  varietiesCount: number
  plantsCount: number
}>()
</script>

<template>
  <nav class="bg-primary-100 rounded-card mt-4 mb-6 p-2 shadow-xs">
    <ul class="flex items-center gap-2">
      <li>
        <RouterLink
          :to="{ name: 'plant-species-details-overview', params: { id: speciesId } }"
          class="rounded-card block px-8 py-4 text-xl font-medium"
          active-class="bg-white shadow-sm"
          >Overview</RouterLink
        >
      </li>
      <li>
        <RouterLink
          :to="{ name: 'plant-species-details-varieties', params: { id: speciesId } }"
          class="rounded-card block px-8 py-4 text-xl font-medium"
          active-class="bg-white shadow-sm"
          >Varieties
          <span class="text-content-muted text-sm">{{ varietiesCount }}</span></RouterLink
        >
      </li>
      <li>
        <RouterLink
          :to="{ name: 'plant-species-details-plants', params: { id: speciesId } }"
          class="rounded-card block px-8 py-4 text-xl font-medium"
          active-class="bg-white shadow-sm"
          >Plants <span class="text-content-muted text-sm">{{ plantsCount }}</span></RouterLink
        >
      </li>
    </ul>
  </nav>
</template>

<style scoped></style>
