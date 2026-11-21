<script setup lang="ts">
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'

defineProps<{ sowing: SeedbedSowingResponse }>()
</script>

<template>
  <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
    <h2 class="text-primary-900 font-display mb-2 text-xl">Counts</h2>

    <div class="grid grid-cols-2 gap-2 md:grid-cols-3">
      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">{{ sowing.seedCount }}</h3>
        </div>

        <h4 class="text-content-muted text-sm">Seeds sown</h4>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">
            {{ sowing.germinatedCount ?? '-' }}
          </h3>
        </div>

        <h4 class="text-content-muted text-sm">Germinated</h4>
      </div>

      <div class="bg-accent-100 rounded-card col-span-2 px-6 py-4 md:col-span-1">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">
            {{ sowing.transplantedCount ?? '-' }}
          </h3>
        </div>

        <h4 class="text-content-muted text-sm">Transplanted</h4>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
