<script setup lang="ts">
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { CalendarDays, Droplets, Sun } from '@lucide/vue'
import { type Component, computed } from 'vue'

interface GrowingRequirements {
  icon: Component
  title: string
  value: string | null
}

const props = defineProps<{ species: PlantSpeciesResponse }>()

const growingRequirements = computed<GrowingRequirements[]>(() => [
  { icon: CalendarDays, title: 'life cycle', value: props.species.lifeCycle ?? null },
  { icon: Sun, title: 'light', value: props.species.lightRequirement ?? null },
  { icon: Droplets, title: 'water', value: props.species.waterRequirement ?? null },
])
</script>

<template>
  <section>
    <!-- Growing -->
    <div class="border-content-muted/40 my-8 border-y py-6">
      <h2 class="font-display text-lg">Growing Requirements</h2>

      <div class="mt-5 grid grid-cols-3 gap-4">
        <div
          v-for="item in growingRequirements"
          :key="item.title"
          class="flex flex-col items-center gap-2 text-center"
        >
          <div class="bg-primary-700 flex size-12 items-center justify-center rounded-full">
            <component :is="item.icon" class="text-primary-100 size-5" />
          </div>
          <span class="text-content-muted text-sm capitalize">{{ item.title }}</span>
          <p class="font-medium capitalize">
            {{ item.value?.toLocaleLowerCase().replaceAll('_', ' ') ?? '-' }}
          </p>
        </div>
      </div>
    </div>

    <!-- General -->
    <div>
      <h2 class="font-display mb-5 text-xl">General Information</h2>

      <div class="space-y-6">
        <!-- Description -->
        <div>
          <span class="text-content-muted mb-1 text-sm">Description</span>
          <p v-if="species.description" class="leading-relaxed">
            {{ species.description }}
          </p>
          <p v-else class="text-content-secondary text-sm italic">No description provided</p>
        </div>

        <!-- Timestamps -->
        <div class="grid max-w-lg grid-cols-2 gap-6">
          <div>
            <span class="text-content-muted mb-1 text-sm">Created</span>
            <p class="font-medium">{{ formatDate(species.createdAt) }}</p>
          </div>

          <div>
            <span class="text-content-muted mb-1 text-sm">Last Updated</span>
            <p class="font-medium">
              {{ species.updatedAt !== null ? formatDate(species.updatedAt) : 'Never' }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped></style>
