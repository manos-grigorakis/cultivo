<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import PlantVarietyTableRowSkeleton from '@/features/plant-varieties/components/PlantVarietyTableRowSkeleton.vue'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { LucidePencil, Trash2 } from '@lucide/vue'

withDefaults(
  defineProps<{
    plantVarieties: PlantVarietyResponse[]
    isLoading?: boolean
    showActions?: boolean
  }>(),
  { showActions: false },
)

const emit = defineEmits<{
  edit: [variety: PlantVarietyResponse]
  delete: [variety: PlantVarietyResponse]
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
          <th scope="col" class="px-6 py-3 font-medium">Name</th>
          <th scope="col" class="px-6 py-3 font-medium">Created</th>
          <th scope="col" class="px-6 py-3 font-medium">Last Update</th>
          <th scope="col" class="px-6 py-3 font-medium" v-if="showActions">Actions</th>
        </tr>
      </thead>
      <tbody class="divide-border divide-y">
        <template v-if="isLoading">
          <PlantVarietyTableRowSkeleton
            v-for="index in 10"
            :key="`skeleton-${index}`"
            :show-actions="showActions"
          />
        </template>

        <tr
          v-else
          v-for="variety in plantVarieties"
          :key="variety.id"
          class="hover:bg-primary-200 transition-colors"
        >
          <td class="text-content-muted px-6 py-4">{{ variety.id }}</td>
          <th scope="row" class="text-content px-6 py-4 font-semibold capitalize hover:underline">
            <RouterLink :to="{ name: 'plant-variety-details', params: { id: variety.id } }">{{
              variety.name
            }}</RouterLink>
          </th>
          <td class="text-content-secondary px-6 py-4">{{ formatDate(variety.createdAt) }}</td>
          <td class="text-content-secondary px-6 py-4">
            {{ variety.updatedAt !== null ? formatDate(variety.updatedAt) : '-' }}
          </td>
          <td class="flex flex-wrap gap-4 px-6 py-4" v-if="showActions">
            <AppButton :icon="LucidePencil" variant="outline" @on-click="emit('edit', variety)" />
            <AppButton :icon="Trash2" variant="danger" @on-click="emit('delete', variety)" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped></style>
