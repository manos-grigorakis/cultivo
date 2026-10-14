<script setup lang="ts">
import type { PlantEventResponse } from '@/types/plant-event-response.interface.ts'
import { LucidePencil } from '@lucide/vue'

defineProps<{
  event: PlantEventResponse
  isPlantArchived: boolean
}>()

const emit = defineEmits<{ onEditPlantEventClick: [id: number] }>()

const applyEventTypeBadgeColor = (type: string): string => {
  switch (type) {
    case 'WATERING':
      return 'bg-cyan-100 text-cyan-800'
    case 'REPOTTING':
      return 'bg-amber-100 text-amber-800'
    case 'OBSERVATION':
      return 'bg-violet-100 text-violet-800'
    case 'HARVEST':
      return 'bg-green-100 text-green-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}
</script>

<template>
  <div class="rounded-card bg-white px-6 py-4 shadow-sm">
    <div class="flex items-center justify-between gap-2">
      <div class="flex items-center gap-2">
        <span
          class="rounded-card px-2 py-1 text-sm"
          :class="applyEventTypeBadgeColor(event.type)"
          >{{ event.type }}</span
        >
        <span
          >{{
            new Date(event.occurredAt).toLocaleString('en-GB', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit',
              hour12: false,
            })
          }}
        </span>
      </div>

      <button
        type="button"
        @click="emit('onEditPlantEventClick', event.id)"
        class="hover:text-primary-500-200 transition-colors hover:cursor-pointer disabled:cursor-not-allowed disabled:opacity-40"
        :disabled="isPlantArchived"
      >
        <LucidePencil :size="18" />
      </button>
    </div>
    <h3 class="mt-2 text-lg font-semibold">{{ event.title }}</h3>
    <p class="text-content-muted mt-1 w-2/3 truncate">{{ event.description }}</p>
  </div>
</template>
