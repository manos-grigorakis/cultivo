<script setup lang="ts">
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'

defineProps<{ seedPacket: SeedPacketResponse }>()
</script>

<template>
  <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
    <h3 class="text-primary-900 font-display mb-2 text-xl">Growing Specs</h3>

    <div class="grid grid-cols-2 gap-2 md:grid-cols-3">
      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h4 class="text-primary-900 text-xl font-bold">
            {{ seedPacket.heightCm }}
          </h4>
          <span class="text-content-muted text-xs">cm</span>
        </div>

        <h5 class="text-content-muted text-sm">Height</h5>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h4 class="text-primary-900 text-xl font-bold">
            {{ seedPacket.weightG }}
          </h4>
          <span class="text-content-muted text-xs">g</span>
        </div>

        <h5 class="text-content-muted text-sm">Packet Weight</h5>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h4 class="text-primary-900 text-xl font-bold">
            {{ seedPacket.plantSpacingCm }}
          </h4>
          <span class="text-content-muted text-xs">cm</span>
        </div>

        <h5 class="text-content-muted text-sm">Plant Spacing</h5>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h4 class="text-primary-900 text-xl font-bold">
            {{ seedPacket.rowSpacingCm }}
          </h4>
          <span class="text-content-muted text-xs">cm</span>
        </div>

        <h5 class="text-content-muted text-sm">Row Spacing</h5>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h4 class="text-primary-900 text-xl font-bold">
            {{ seedPacket.sowingDepthCm }}
          </h4>
          <span class="text-content-muted text-xs">cm</span>
        </div>

        <h5 class="text-content-muted text-sm">Sowing depth</h5>
      </div>

      <div class="bg-accent-100 rounded-card px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h4 class="text-primary-900 text-xl font-bold">
            {{ seedPacket.germinationDays }}
          </h4>
          <span class="text-content-muted text-xs">days</span>
        </div>

        <h5 class="text-content-muted text-sm">Germination</h5>
      </div>

      <div class="bg-accent-100 rounded-card col-span-2 px-6 py-4">
        <div class="flex items-baseline gap-1">
          <h4 class="text-primary-900 text-xl font-bold">
            {{ seedPacket.startHarvestDays }}
          </h4>
          <span class="text-content-muted text-xs">days</span>
        </div>

        <h5 class="text-content-muted text-sm">Days to first harvest</h5>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
