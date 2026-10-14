<script setup lang="ts">
import type { PlantResponse } from '@/types/plant-response.interface.ts'

defineProps<{
  plant: PlantResponse
}>()
</script>

<template>
  <li class="rounded-card bg-white drop-shadow-sm">
    <RouterLink :to="{ name: 'plant-details', params: { id: plant.id } }">
      <div
        class="rounded-tl-card rounded-tr-card aspect-video w-full"
        :style="{
          background: `repeating-linear-gradient(135deg,#EFFBE5 0px,#EFFBE5 18px,#DDEECC 18px,#DDEECC 36px)`,
        }"
      />

      <div class="px-2 py-3">
        <h2 class="font-display truncate pl-1 text-lg">{{ plant.label }}</h2>
      </div>
    </RouterLink>
  </li>
</template>

<style scoped></style>
