<script setup lang="ts">
import type { PlantResponse } from '@/types/plant-response.interface.ts'
import AppButton from '@/components/ui/AppButton.vue'
import { LucideArchive, LucidePencil, LucidePlus } from '@lucide/vue'

const props = defineProps<{
  plant: PlantResponse
  isUpdatingStatus: boolean
}>()

const emit = defineEmits<{
  'status-change': [status: PlantResponse['status']]
  'add-event': []
  'edit-plant': []
  'archive-plant': []
}>()

const applyPlantStatusBadgeColor = (status: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-primary-200 text-primary-900'
    case 'DEAD':
      return 'bg-red-200 text-red-900'
    default:
      return 'bg-surface-muted text-content'
  }
}

const onStatusChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  emit('status-change', target.value as PlantResponse['status'])

  // Display the confirmed status until the API responds
  target.value = props.plant.status
}
</script>

<template>
  <div
    class="rounded-card h-50 w-50 max-w-150"
    :style="{
      background: `repeating-linear-gradient(135deg,#EFFBE5 0px,#EFFBE5 18px,#DDEECC 18px,#DDEECC 36px)`,
    }"
  />

  <h2 class="font-display mt-4 truncate text-4xl">{{ plant.label }}</h2>

  <div class="mt-2 flex flex-wrap items-center justify-between">
    <select
      :value="plant.status"
      @change="onStatusChange"
      class="rounded-card px-4 py-2 text-xs disabled:cursor-not-allowed"
      :disabled="plant.archivedAt !== null || isUpdatingStatus"
      :class="applyPlantStatusBadgeColor(plant.status)"
    >
      <option value="ACTIVE">ACTIVE</option>
      <option value="DEAD">DEAD</option>
    </select>

    <div class="flex items-center gap-2">
      <AppButton
        :icon="LucidePlus"
        label="Add Event"
        @on-click="emit('add-event')"
        :disabled="plant.archivedAt != null"
      />
      <AppButton
        :icon="LucidePencil"
        label="Edit Plant"
        @on-click="emit('edit-plant')"
        variant="outline"
        :disabled="plant.archivedAt != null"
      />

      <AppButton
        :icon="LucideArchive"
        @on-click="emit('archive-plant')"
        variant="outline"
        :disabled="plant.archivedAt != null"
      />
    </div>
  </div>
</template>

<style scoped></style>
