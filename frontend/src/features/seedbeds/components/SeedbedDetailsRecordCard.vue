<script setup lang="ts">
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import { formatDate } from '@/utils/format-date.utils.ts'

defineProps<{
  seedbed: SeedbedResponse
}>()
</script>

<template>
  <div class="rounded-card mt-4 bg-white p-4">
    <h2 class="font-display mb-3 text-xl">Record</h2>

    <ul class="flex flex-col space-y-1">
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Location type</p>
        <p class="text-primary-900 capitalize">
          {{ seedbed.locationType.toLowerCase() }}
        </p>
      </li>

      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Created at</p>
        <p class="text-primary-900">
          {{ formatDate(seedbed.createdAt) }}
        </p>
      </li>

      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Last update</p>
        <p class="text-primary-900m capitalize">
          {{ seedbed.updatedAt != null ? formatDate(seedbed.updatedAt) : '-' }}
        </p>
      </li>
    </ul>
  </div>
</template>

<style scoped></style>
