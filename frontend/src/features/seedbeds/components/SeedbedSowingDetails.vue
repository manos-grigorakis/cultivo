<script setup lang="ts">
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import { formatDate } from '@/utils/format-date.utils.ts'

defineProps<{ sowing: SeedbedSowingResponse }>()
</script>

<template>
  <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
    <h2 class="text-primary-900 font-display mb-2 text-xl">Details</h2>

    <ul class="flex flex-col space-y-2">
      <li class="flex flex-col gap-1 sm:flex-row sm:justify-between sm:gap-4">
        <p class="text-content-muted">Seed packet</p>
        <RouterLink
          :to="{ name: 'seed-packet-details', params: { id: sowing.seedPacket.id } }"
          class="text-primary-600 hover:underline"
        >
          {{ sowing.seedPacket.brand }} - {{ sowing.seedPacket.code ?? '-' }}
        </RouterLink>
      </li>
      <li class="flex flex-col gap-1 sm:flex-row sm:justify-between sm:gap-4">
        <p class="text-content-muted">Seedbed</p>
        <RouterLink
          :to="{ name: 'seedbed-details', params: { id: sowing.seedbed.id } }"
          class="text-primary-600 hover:underline"
        >
          {{ sowing.seedbed.name }}
        </RouterLink>
      </li>
      <li class="flex flex-col gap-1 sm:flex-row sm:justify-between sm:gap-4">
        <p class="text-content-muted">Slot</p>
        <p class="text-primary-900">
          Row {{ sowing.seedbedSlot.rowIndex }}, Column {{ sowing.seedbedSlot.columnIndex }}
        </p>
      </li>
      <li class="flex flex-col gap-1 sm:flex-row sm:justify-between sm:gap-4">
        <p class="text-content-muted">Sown At</p>
        <p class="text-primary-900">{{ formatDate(sowing.sownAt) }}</p>
      </li>
      <li class="flex flex-col gap-1 sm:flex-row sm:justify-between sm:gap-4">
        <p class="text-content-muted">Completed At</p>
        <p v-if="sowing.completedAt != null" class="text-primary-900">
          {{ formatDate(sowing.completedAt) }}
        </p>
        <p v-else class="text-content-muted">Not completed</p>
      </li>
      <li class="flex flex-col gap-1 sm:flex-row sm:justify-between sm:gap-4">
        <p class="text-content-muted">Created</p>
        <p class="text-primary-900">
          {{ formatDate(sowing.createdAt) }}
        </p>
      </li>
      <li class="flex flex-col gap-1 sm:flex-row sm:justify-between sm:gap-4">
        <p class="text-content-muted">Last update</p>
        <p class="text-primary-900">
          {{ sowing.updatedAt != null ? formatDate(sowing.updatedAt) : 'Never' }}
        </p>
      </li>
    </ul>
  </div>
</template>

<style scoped></style>
