<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import type { SeedbedSlotSummaryResponse } from '@/features/seedbeds/types/seedbed-slot-summary-response.interface.ts'
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { Check, Plus } from '@lucide/vue'
import { computed, ref } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
  seedbedSlotsList: SeedbedSlotSummaryResponse[]
  seedbedSowingsList: SeedbedSowingResponse[]
}>()

const emit = defineEmits<{ addSowing: [slotId: number] }>()

const selectedSlot = ref<number | null>(null)

/**
 * Find a slot by its row and column (UI 1-based index, backend 0-based index)
 *
 * @param row Row of slot
 * @param col Column of slot
 */
const getSlot = (row: number, col: number) => {
  return props.seedbedSlotsList.find(
    (slot) => slot.rowIndex === row - 1 && slot.columnIndex === col - 1,
  )
}

/**
 * Find the active sowing for a slot, excluding terminal statuses
 *
 * @param slotId The slot ID to search for
 */
const getSowingForSlot = (slotId: number) => {
  return props.seedbedSowingsList.find(
    (sowing) =>
      sowing.seedbedSlot.id === slotId &&
      ![SeedbedSowingStatus.FAILED, SeedbedSowingStatus.TRANSPLANTED].includes(sowing.status),
  )
}

const getSlotClasses = (slotId: number | null, status: SeedbedSowingStatus | null) => [
  getSowingStatusClasses(status),
  selectedSlot.value === slotId && slotId !== null ? 'ring-primary-600 ring-2 ring-offset-2' : '',
]

const getSowingStatusClasses = (status: SeedbedSowingStatus | null) => {
  switch (status) {
    case SeedbedSowingStatus.SOWN:
      return 'border-accent-500 bg-accent-500'
    case SeedbedSowingStatus.GERMINATING:
      return 'border-primary-500 bg-primary-500'
    case SeedbedSowingStatus.GERMINATED:
      return 'border-primary-800 bg-primary-800 text-white'
    default:
      return 'border-primary-200 bg-gray-100'
  }
}

/**
 * Builds a reactive 2D grid containing each slot's ID and active sowing status
 */
const slotsGrid = computed(() =>
  Array.from({ length: props.seedbed.rowsCount }, (_, row) =>
    Array.from({ length: props.seedbed.columnsCount }, (_, col) => {
      const slot = getSlot(row + 1, col + 1)

      return {
        id: slot?.id ?? null,
        status: slot ? (getSowingForSlot(slot.id)?.status ?? null) : null,
      }
    }),
  ),
)

const selectedSlotIsEmpty = computed(() => selectedSlot.value !== null && !selectedSowing.value)

const selectedSowing = computed(() =>
  selectedSlot.value !== null ? getSowingForSlot(selectedSlot.value) : undefined,
)

const selectedSlotDetails = computed(() =>
  props.seedbedSlotsList.find((slot) => slot.id === selectedSlot.value),
)
</script>

<template>
  <div class="rounded-card mt-4 bg-white p-4">
    <!--  Header  -->
    <div class="mb-3 flex flex-wrap items-center justify-between gap-x-1">
      <h2 class="font-display text-xl">Slots</h2>
      <p class="text-content-muted text-xs">
        {{ seedbed.rowsCount }} rows x {{ seedbed.columnsCount }} columns =
        {{ seedbed.rowsCount * seedbed.columnsCount }} slots
      </p>
    </div>

    <!-- Table   -->
    <div class="rounded-card bg-surface relative mt-4 overflow-x-auto shadow-xs">
      <table class="w-full text-sm">
        <thead class="bg-primary-200 text-primary-900">
          <tr>
            <th scope="col" class="w-16 min-w-16 px-3 py-3 font-medium">Row</th>
            <th
              v-for="col in seedbed.columnsCount"
              :key="col"
              scope="col"
              class="min-w-16 px-2 py-3 font-medium"
            >
              {{ col }}
            </th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="(slots, rowIndex) in slotsGrid" :key="rowIndex">
            <th scope="row" class="bg-primary-200 text-primary-900 px-3 py-2 font-medium">
              {{ rowIndex + 1 }}
            </th>

            <td v-for="(slot, colIndex) in slots" :key="colIndex" class="p-2">
              <button
                type="button"
                class="mx-auto flex size-12 cursor-pointer items-center justify-center rounded-full border-2 transition-colors"
                :class="getSlotClasses(slot.id, slot.status)"
                :aria-label="`Row ${rowIndex + 1}, Column ${colIndex + 1}`"
                @click="selectedSlot = slot.id"
              >
                <span v-if="slot.status === SeedbedSowingStatus.SOWN">S</span>
                <span v-else-if="slot.status === SeedbedSowingStatus.GERMINATING">G</span>
                <Check v-else-if="slot.status === SeedbedSowingStatus.GERMINATED" :size="18" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Slot Status Legend   -->
    <div class="mt-4 mb-6 ml-4 flex items-center gap-2">
      <div class="flex items-center gap-2">
        <div class="border-primary-200 size-6 rounded-full border-2 bg-gray-100"></div>
        <span>Empty</span>
      </div>

      <div class="flex items-center gap-1.5">
        <div
          class="border-accent-500 bg-accent-500 flex size-6 items-center justify-center rounded-full border-2"
        >
          S
        </div>
        <span>Sown</span>
      </div>

      <div class="flex items-center gap-1.5">
        <div
          class="border-primary-500 bg-primary-500 flex size-6 items-center justify-center rounded-full border-2"
        >
          G
        </div>
        <span>Germinating</span>
      </div>

      <div class="flex items-center gap-1.5">
        <div
          class="border-primary-800 bg-primary-800 flex size-6 items-center justify-center rounded-full border-2 text-white"
        >
          <Check :size="16" />
        </div>
        <span>Germinated</span>
      </div>
    </div>

    <!--  Selected Slot Details  -->
    <div v-if="selectedSlot !== null" class="bg-accent-100 rounded-card mt-3 p-4">
      <div class="flex flex-wrap items-center justify-between">
        <div>
          <h3 class="text-primary-900 font-semibold">Selected Slot</h3>
          <div class="flex items-center gap-2">
            <p class="text-content-muted text-sm">Slot ID: {{ selectedSlot }}</p>
            <p v-if="selectedSlotDetails" class="text-content-muted text-sm">
              R{{ selectedSlotDetails.rowIndex + 1 }}, C{{ selectedSlotDetails.columnIndex + 1 }}
            </p>
          </div>
        </div>

        <button
          type="button"
          class="text-content-muted hover:text-primary-900"
          @click="selectedSlot = null"
        >
          Clear
        </button>
      </div>

      <div class="mt-4">
        <AppButton
          v-if="selectedSlotIsEmpty"
          label="Add sowing"
          :icon="Plus"
          @click="emit('addSowing', selectedSlot)"
        />

        <div v-else-if="selectedSowing" class="space-y-3">
          <div class="grid grid-cols-[110px_1fr] items-center gap-x-3 gap-y-2 text-sm">
            <span class="text-content-muted">Seed packet</span>
            <RouterLink
              class="text-primary-900 font-medium hover:underline"
              :to="{ name: 'seed-packet-details', params: { id: selectedSowing.seedPacket.id } }"
            >
              {{ selectedSowing.seedPacket.brand }} - {{ selectedSowing.seedPacket.code ?? '-' }}
            </RouterLink>

            <span class="text-content-muted">Seeds sown</span>
            <span>{{ selectedSowing.seedCount }}</span>

            <span class="text-content-muted">Germinated</span>
            <span>{{ selectedSowing.germinatedCount ?? '-' }}</span>

            <span class="text-content-muted">Sown at</span>
            <span>{{ formatDate(selectedSowing.sownAt) }}</span>
          </div>

          <div>
            <RouterLink
              class="font-display rounded-card hover:bg-primary-100 inline-flex w-fit items-center gap-2 bg-white px-4 py-2 text-sm text-black"
              :to="{ name: 'seedbed-sowing-details', params: { id: selectedSowing.id } }"
            >
              Open sowing #{{ selectedSowing.id }}
            </RouterLink>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="bg-accent-100 rounded-card mt-3 p-4">
      <p class="text-content-muted text-sm">Select a slot to see its details or to add a sowing</p>
    </div>
  </div>
</template>

<style scoped></style>
