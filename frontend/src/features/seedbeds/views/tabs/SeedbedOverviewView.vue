<script setup lang="ts">
import SeedbedDetailsFeaturesCard from '@/features/seedbeds/components/SeedbedDetailsFeaturesCard.vue'
import SeedbedDetailsRecordCard from '@/features/seedbeds/components/SeedbedDetailsRecordCard.vue'
import SeedbedDetailsSizeCard from '@/features/seedbeds/components/SeedbedDetailsSizeCard.vue'
import SeedbedDetailsSlotsTable from '@/features/seedbeds/components/SeedbedDetailsSlotsTable.vue'
import SeedbedSowingForm from '@/features/seedbeds/components/SeedbedSowingForm.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import type { SeedbedSlotSummaryResponse } from '@/features/seedbeds/types/seedbed-slot-summary-response.interface.ts'
import { ref } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
  seedbedSlotsList: SeedbedSlotSummaryResponse[]
}>()

const { seedbedsList, fetchSeedbedById } = useSeedbeds()

const isSowingFormOpen = ref<boolean>(false)
const selectedSlot = ref<number | null>(null)

const onSowingAddClick = async (slotId: number) => {
  selectedSlot.value = slotId
  isSowingFormOpen.value = true
}

const onSowingCreateSuccess = async () => {
  try {
    await fetchSeedbedById(props.seedbed.id)
  } finally {
    isSowingFormOpen.value = false
  }
}
</script>

<template>
  <SeedbedSowingForm
    v-if="isSowingFormOpen && selectedSlot !== null"
    mode="create"
    :seedbed-id="seedbed.id"
    :slot-id="selectedSlot"
    @success="onSowingCreateSuccess"
    @close="isSowingFormOpen = false"
  />

  <div>
    <!-- Size & Features -->
    <div class="grid grid-cols-1 gap-2 md:grid-cols-2">
      <SeedbedDetailsSizeCard :seedbed="seedbed" />
      <SeedbedDetailsFeaturesCard :seedbed="seedbed" />
    </div>

    <!-- Slots -->
    <SeedbedDetailsSlotsTable
      :seedbed="seedbed"
      :seedbed-slots-list="seedbedSlotsList"
      @add-sowing="onSowingAddClick($event)"
    />

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
