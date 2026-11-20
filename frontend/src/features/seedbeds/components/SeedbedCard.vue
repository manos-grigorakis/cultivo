<script setup lang="ts">
import { SeedbedLocationType } from '@/features/seedbeds/enums/seedbed-location-type.enum.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import { Droplets, House, LucideGrid3x3, Ruler, ShieldCheck, Sun } from '@lucide/vue'
import { computed } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
}>()

const locationIcon = computed(() =>
  props.seedbed.locationType === SeedbedLocationType.INDOOR ? House : Sun,
)
</script>

<template>
  <li class="rounded-card bg-white p-4 drop-shadow-sm">
    <RouterLink
      :to="{ name: 'seedbed-details', params: { id: seedbed.id } }"
      class="flex h-full flex-col gap-4"
    >
      <!-- Header -->
      <div class="flex items-center justify-between">
        <component :is="locationIcon" :size="18" />
        <span class="text-content-muted text-xs capitalize">{{
          seedbed.locationType.toLowerCase()
        }}</span>
      </div>

      <div class="space-y-1">
        <h2 class="text-lg font-semibold">{{ seedbed.name }}</h2>
        <p class="text-content-muted text-sm">
          {{ seedbed.rowsCount }} rows x {{ seedbed.columnsCount }} columns
        </p>
      </div>

      <div class="text-content-muted flex flex-wrap items-center gap-x-4 gap-y-2 text-sm">
        <div class="flex items-center gap-x-1.5">
          <LucideGrid3x3 :size="16" class="shrink-0" />
          <span>{{ seedbed.columnsCount * seedbed.rowsCount }} slots</span>
        </div>

        <div
          v-if="seedbed.lengthCm != null && seedbed.widthCm != null"
          class="flex items-center gap-x-1.5"
        >
          <Ruler :size="16" class="shrink-0" />
          <span>{{ seedbed.lengthCm }} x {{ seedbed.widthCm }} cm</span>
        </div>
      </div>

      <div
        class="text-content-muted mt-auto flex flex-wrap items-center justify-between gap-x-4 gap-y-2 border-t border-gray-100 pt-3 text-sm"
      >
        <div class="flex items-center gap-x-1.5">
          <Droplets
            :size="16"
            class="shrink-0"
            :class="{ 'text-blue-500': seedbed.hasAutomaticWatering }"
          />
          <span>{{ seedbed.hasAutomaticWatering ? 'Auto' : 'Manual' }} watering</span>
        </div>

        <div class="flex items-center gap-x-1.5">
          <ShieldCheck
            :size="16"
            class="shrink-0"
            :class="{ 'text-green-500': seedbed.hasEnclosure }"
          />
          <span>{{ seedbed.hasEnclosure ? 'Enclosed' : 'Open' }}</span>
        </div>
      </div>
    </RouterLink>
  </li>
</template>

<style scoped></style>
