<script setup lang="ts">
import type { PlantEventResponse } from '@/types/plant-event-response.interface'

defineProps<{
  event: PlantEventResponse
}>()

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
  <div class="px-6 py-4 bg-white rounded-card">
    <div class="flex items-center gap-2">
      <span class="px-2 py-1 text-sm rounded-card" :class="applyEventTypeBadgeColor(event.type)">{{
        event.type
      }}</span>
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
    <h3 class="mt-2 text-lg font-semibold">{{ event.title }}</h3>
  </div>
</template>
