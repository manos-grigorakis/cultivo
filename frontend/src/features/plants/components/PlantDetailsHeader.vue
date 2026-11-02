<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
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
  <div class="flex flex-col items-start gap-4 lg:flex-row lg:gap-6">
    <!-- Image placeholder -->
    <div class="rounded-card bg-plant-placeholder aspect-square w-32 shrink-0" />

    <div class="min-w-0 flex-1">
      <h1 class="font-display truncate text-4xl">{{ plant.label }}</h1>

      <p v-if="plant.species" class="text-content-muted mt-1 text-sm">
        {{ plant?.category?.name }} - {{ plant.species.name }} {{ plant.variety?.name }}
      </p>

      <div class="mt-2">
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
      </div>
    </div>

    <!-- Actions -->
    <div class="flex w-full shrink-0 items-center justify-end gap-2 lg:w-auto">
      <AppButton
        :icon="LucidePlus"
        label="Add Event"
        @on-click="emit('add-event')"
        :disabled="plant.archivedAt != null"
      />

      <AppButton
        :icon="LucidePencil"
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
