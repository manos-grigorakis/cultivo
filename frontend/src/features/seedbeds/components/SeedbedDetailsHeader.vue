<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import { SeedbedLocationType } from '@/features/seedbeds/enums/seedbed-location-type.enum.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import { House, LucideGrid3x3, LucidePencil, Sun, Trash2 } from '@lucide/vue'
import { computed } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
}>()

const emit = defineEmits<{ edit: []; delete: [] }>()

const locationIcon = computed(() =>
  props.seedbed.locationType === SeedbedLocationType.INDOOR ? House : Sun,
)
</script>

<template>
  <div class="flex flex-wrap items-start justify-between gap-4">
    <div>
      <h1 class="font-display truncate text-4xl">{{ seedbed.name }}</h1>

      <div class="mt-2 flex items-center gap-x-2">
        <div class="flex items-center gap-x-1.5">
          <component :is="locationIcon" :size="18" class="text-content-muted" />
          <span class="text-content-muted text-sm capitalize">{{
            seedbed.locationType.toLowerCase()
          }}</span>
        </div>

        <div>
          <p class="text-content-muted text-sm">
            {{ seedbed.rowsCount }} rows x {{ seedbed.columnsCount }} columns
          </p>
        </div>

        <div class="flex items-center gap-x-1.5">
          <LucideGrid3x3 :size="18" class="text-content-muted shrink-0" />
          <span class="text-content-muted text-sm">
            {{ seedbed.columnsCount * seedbed.rowsCount }} slots
          </span>
        </div>
      </div>
    </div>

    <!-- Actions -->
    <div class="flex items-center gap-2">
      <AppButton :icon="LucidePencil" variant="outline" @on-click="emit('edit')" />
      <AppButton :icon="Trash2" variant="danger" @on-click="emit('delete')" />
    </div>
  </div>
</template>

<style scoped></style>
