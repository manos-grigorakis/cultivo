<script setup lang="ts">
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'
import { formatDate } from '@/utils/format-date.utils.ts'

defineProps<{ seedPacket: SeedPacketResponse }>()
</script>

<template>
  <div class="rounded-card mt-6 bg-white px-4 py-3 drop-shadow-sm">
    <h3 class="text-primary-900 font-display mb-2 text-xl">Details</h3>

    <ul class="flex flex-col space-y-2">
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Variety</p>
        <RouterLink
          :to="{ name: 'plant-variety-details', params: { id: seedPacket.plantVariety.id } }"
          class="text-primary-900 underline"
          >{{ seedPacket.plantVariety.name }}</RouterLink
        >
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Brand</p>
        <p class="text-primary-900">
          {{ seedPacket.brand }}
        </p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Supplier</p>
        <p class="text-primary-900">
          {{ seedPacket.supplier !== null ? seedPacket.supplier : '-' }}
        </p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Code</p>
        <p class="text-primary-900">
          {{ seedPacket.code !== null ? seedPacket.code : '-' }}
        </p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Purchased</p>
        <p class="text-primary-900">
          {{ seedPacket.purchasedAt !== null ? formatDate(seedPacket.purchasedAt) : '-' }}
        </p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Created</p>
        <p class="text-primary-900">
          {{ formatDate(seedPacket.createdAt) }}
        </p>
      </li>
      <li class="flex justify-between gap-2">
        <p class="text-content-muted">Last update</p>
        <p class="text-primary-900">
          {{ seedPacket.updatedAt !== null ? formatDate(seedPacket.updatedAt) : '-' }}
        </p>
      </li>
    </ul>
  </div>
</template>

<style scoped></style>
