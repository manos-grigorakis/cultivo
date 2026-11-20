<script setup lang="ts">
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'

defineProps<{
  seedbed: SeedbedResponse
}>()
</script>

<template>
  <div class="rounded-card mt-4 bg-white p-4">
    <h2 class="font-display mb-3 text-xl">Size</h2>

    <div class="grid grid-cols-2 gap-2 md:grid-cols-3">
      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">
            {{ seedbed.lengthCm ?? '-' }}
          </h3>
          <span class="text-content-muted text-xs">cm</span>
        </div>

        <h4 class="text-content-muted text-sm">Length</h4>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">
            {{ seedbed.widthCm ?? '-' }}
          </h3>
          <span class="text-content-muted text-xs">cm</span>
        </div>

        <h4 class="text-content-muted text-sm">Width</h4>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">
            {{ seedbed.soilDepthCm ?? '-' }}
          </h3>
          <span class="text-content-muted text-xs">cm</span>
        </div>

        <h4 class="text-content-muted text-sm">Soil depth</h4>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">{{ seedbed.rowsCount }}</h3>
        </div>

        <h4 class="text-content-muted text-sm">Rows</h4>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">{{ seedbed.columnsCount }}</h3>
        </div>

        <h4 class="text-content-muted text-sm">Columns</h4>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h3 class="text-primary-900 text-xl font-bold">
            {{ seedbed.columnsCount * seedbed.rowsCount }}
          </h3>
        </div>

        <h4 class="text-content-muted text-sm">Slots</h4>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
