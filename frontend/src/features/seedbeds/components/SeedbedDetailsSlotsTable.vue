<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import type { SeedbedSlotSummaryResponse } from '@/features/seedbeds/types/seedbed-slot-summary-response.interface.ts'
import { Plus } from '@lucide/vue'
import { ref } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
  seedbedSlotsList: SeedbedSlotSummaryResponse[]
}>()

const emit = defineEmits<{
  addSowing: [slotId: number]
}>()

const selectedSlot = ref<number | null>(null)

const getSlot = (row: number, col: number) => {
  return props.seedbedSlotsList.find(
    (slot) => slot.rowIndex === row - 1 && slot.columnIndex === col - 1,
  )
}
</script>

<template>
  <div class="rounded-card mt-4 bg-white p-4">
    <div class="mb-3 flex flex-wrap items-center justify-between gap-x-1">
      <h2 class="font-display text-xl">Slots</h2>
      <p class="text-content-muted text-xs">
        {{ seedbed.rowsCount }} rows x {{ seedbed.columnsCount }} columns =
        {{ seedbed.rowsCount * seedbed.columnsCount }} slots
      </p>
    </div>

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
          <tr v-for="row in seedbed.rowsCount" :key="row">
            <th scope="row" class="bg-primary-200 text-primary-900 px-3 py-2 font-medium">
              {{ row }}
            </th>

            <td v-for="col in seedbed.columnsCount" :key="col" class="px-2 py-2">
              <button
                type="button"
                class="border-primary-200 hover:bg-primary-100 mx-auto block size-12 cursor-pointer rounded-full border-2 bg-gray-100 transition-colors"
                :class="{
                  'border-primary-500 ring-primary-500/20 ring-2':
                    selectedSlot === getSlot(row, col)?.id,
                }"
                :aria-label="`Row ${row}, Column ${col}`"
                @click="selectedSlot = getSlot(row, col)?.id ?? null"
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="selectedSlot !== null" class="bg-accent-100 rounded-card mt-3 p-4">
      <div class="flex flex-wrap items-center justify-between">
        <div>
          <h3 class="text-primary-900 font-semibold">Selected Slot</h3>
          <p class="text-content-muted text-sm">Slot ID: {{ selectedSlot }}</p>
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
        <AppButton label="Add sowing" :icon="Plus" @click="emit('addSowing', selectedSlot)" />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
