<script setup lang="ts">
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import { formatDate } from '@/utils/format-date.utils.ts'

defineProps<{
  plants: PlantResponse[]
  isLoading: boolean
}>()
</script>

<template>
  <div
    class="rounded-card border-content-muted/40 bg-surface relative mt-4 overflow-x-auto border shadow-xs"
  >
    <table class="w-full text-left text-sm">
      <thead class="bg-primary-200 text-primary-900">
        <tr>
          <th scope="col" class="px-6 py-3 font-medium">#</th>
          <th scope="col" class="px-6 py-3 font-medium">Label</th>
          <th scope="col" class="px-6 py-3 font-medium">Created</th>
          <th scope="col" class="px-6 py-3 font-medium">Last Update</th>
        </tr>
      </thead>
      <tbody class="divide-border divide-y">
        <!-- Loading Skeleton -->
        <template v-if="isLoading">
          <tr v-for="index in 10" :key="index">
            <td v-for="column in 4" :key="column" class="px-6 py-4">
              <div
                class="bg-primary-100 h-4 animate-pulse rounded"
                :class="column === 2 ? 'w-3/4' : 'w-1/2'"
              />
            </td>
          </tr>
        </template>

        <!-- Data -->
        <tr
          v-else
          v-for="plant in plants"
          :key="plant.id"
          class="hover:bg-primary-200 transition-colors"
        >
          <td class="text-content-muted px-6 py-4">{{ plant.id }}</td>
          <th scope="row" class="text-content px-6 py-4 font-semibold capitalize hover:underline">
            <RouterLink :to="{ name: 'plant-details', params: { id: plant.id } }">{{
              plant.label
            }}</RouterLink>
          </th>
          <td class="text-content-secondary px-6 py-4">{{ formatDate(plant.createdAt) }}</td>
          <td class="text-content-secondary px-6 py-4">
            {{ plant.updatedAt !== null ? formatDate(plant.updatedAt) : '-' }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped></style>
