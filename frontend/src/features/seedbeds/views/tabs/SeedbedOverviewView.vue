<script setup lang="ts">
import SeedbedDetailsFeaturesCard from '@/features/seedbeds/components/SeedbedDetailsFeaturesCard.vue'
import SeedbedDetailsRecordCard from '@/features/seedbeds/components/SeedbedDetailsRecordCard.vue'
import SeedbedDetailsSizeCard from '@/features/seedbeds/components/SeedbedDetailsSizeCard.vue'
import SeedbedDetailsSlotsTable from '@/features/seedbeds/components/SeedbedDetailsSlotsTable.vue'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import type { SeedbedSlotSummaryResponse } from '@/features/seedbeds/types/seedbed-slot-summary-response.interface.ts'

defineProps<{
  seedbed: SeedbedResponse
  seedbedSlotsList: SeedbedSlotSummaryResponse[]
}>()
</script>

<template>
  <div>
    <!-- Size & Features -->
    <div class="grid grid-cols-1 gap-2 md:grid-cols-2">
      <SeedbedDetailsSizeCard :seedbed="seedbed" />
      <SeedbedDetailsFeaturesCard :seedbed="seedbed" />
    </div>

    <!-- Slots -->
    <SeedbedDetailsSlotsTable :seedbed="seedbed" :seedbed-slots-list="seedbedSlotsList" />

    <!-- Notes -->
    <div class="rounded-card mt-4 bg-white p-4">
      <h2 class="font-display mb-3 text-xl">Notes</h2>
      <p class="text-sm" :class="seedbed.notes ? 'text-content' : 'text-content-muted'">
        {{ seedbed.notes ?? 'No notes yet' }}
      </p>
    </div>

    <!-- Record -->
    <SeedbedDetailsRecordCard :seedbed="seedbed" />
  </div>
</template>

<style scoped></style>
