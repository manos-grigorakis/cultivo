<script setup lang="ts">
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import { CheckCircle2, CircleMinus, Droplets, ShieldCheck, Waves } from '@lucide/vue'
import { computed } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
}>()

const features = computed(() => [
  {
    label: 'Enclosure',
    icon: ShieldCheck,
    enabled: props.seedbed.hasEnclosure,
  },
  {
    label: 'Automatic watering',
    icon: Droplets,
    enabled: props.seedbed.hasAutomaticWatering,
  },
  {
    label: 'Humidity control',
    icon: Waves,
    enabled: props.seedbed.hasHumidityControl,
  },
])
</script>

<template>
  <div class="rounded-card mt-4 bg-white p-4">
    <h2 class="font-display mb-3 text-xl">Features</h2>

    <div class="grid grid-cols-1 gap-2">
      <div
        v-for="feature in features"
        :key="feature.label"
        class="rounded-card flex items-center gap-3 p-4"
        :class="feature.enabled ? 'bg-accent-100' : 'bg-gray-100'"
      >
        <component
          :is="feature.icon"
          :size="20"
          class="shrink-0"
          :class="feature.enabled ? 'text-green-600' : 'text-gray-400'"
        />

        <div class="min-w-0 flex-1">
          <h3
            class="text-sm font-semibold"
            :class="feature.enabled ? 'text-primary-900' : 'text-content-muted'"
          >
            {{ feature.label }}
          </h3>
          <p class="text-content-muted mt-0.5 text-xs">
            {{ feature.enabled ? 'Available' : 'Not available' }}
          </p>
        </div>

        <CheckCircle2 v-if="feature.enabled" :size="18" class="shrink-0 text-green-600" />
        <CircleMinus v-else :size="18" class="shrink-0 text-gray-400" />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
