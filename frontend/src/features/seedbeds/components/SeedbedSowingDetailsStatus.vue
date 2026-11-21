<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppSelect from '@/components/ui/form/AppSelect.vue'
import { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import { ref, watch } from 'vue'

const props = defineProps<{ sowing: SeedbedSowingResponse; isSubmitting: boolean }>()
const emit = defineEmits<{
  update: [status: SeedbedSowingStatus]
}>()

const selectedSowingStatus = ref<SeedbedSowingStatus | null>(null)

const sowingStatusTypes: { label: string; statuses: SeedbedSowingStatus[] }[] = [
  {
    label: 'Sown',
    statuses: [SeedbedSowingStatus.SOWN],
  },
  {
    label: 'Germinating',
    statuses: [SeedbedSowingStatus.GERMINATING],
  },
  {
    label: 'Germinated',
    statuses: [SeedbedSowingStatus.GERMINATED],
  },
  {
    label: 'Failed or Transplanted',
    statuses: [SeedbedSowingStatus.FAILED, SeedbedSowingStatus.TRANSPLANTED],
  },
]

const applyStatusChipColor = (statuses: SeedbedSowingStatus[]): string => {
  return statuses.includes(props.sowing.status)
    ? 'bg-primary-600 text-white font-semibold'
    : 'bg-accent-100 text-content-muted'
}

const onStatusUpdate = () => {
  if (selectedSowingStatus.value === null) return
  emit('update', selectedSowingStatus.value)
}

watch(
  () => props.sowing.status,
  () => (selectedSowingStatus.value = null),
)
</script>

<template>
  <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
    <h2 class="text-primary-900 font-display mb-2 text-xl">Status</h2>

    <div class="-mx-4 overflow-x-auto px-4 pb-2">
      <div class="flex w-max min-w-full items-center gap-2">
        <span
          v-for="(step, index) in sowingStatusTypes"
          :key="step.label"
          class="rounded-card px-4 py-2"
          :class="applyStatusChipColor(step.statuses)"
          >{{ index + 1 }}. {{ step.label }}</span
        >
      </div>
    </div>

    <form
      class="mt-4 flex flex-col gap-3 sm:flex-row sm:items-end sm:gap-2"
      @submit.prevent="onStatusUpdate"
    >
      <div class="min-w-0 flex-1">
        <AppSelect
          v-model="selectedSowingStatus"
          id="status"
          label="Move to"
          :options="
            Object.values(SeedbedSowingStatus)
              .filter((status) => status !== sowing?.status)
              .map((status) => ({
                label: status,
                value: status,
              }))
          "
        />
      </div>

      <div class="shrink-0">
        <AppButton
          label="Update status"
          variant="primary"
          type="submit"
          :disabled="isSubmitting || selectedSowingStatus === null"
        />
      </div>
    </form>
    <p class="text-content-muted mt-2 text-xs sm:text-sm">
      Only sowings in the SOWN status can be deleted
    </p>
  </div>
</template>

<style scoped></style>
