<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import SeedPacketTableRowSkeleton from '@/features/seeds/seed-packets/components/SeedPacketTableRowSkeleton.vue'
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'
import { applySeedPacketStatusBadgeColor } from '@/features/seeds/seed-packets/utils/apply-seed-packet-status-badge-color.utils.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { LucidePencil, Trash2 } from '@lucide/vue'

withDefaults(
  defineProps<{
    packets: SeedPacketResponse[]
    isLoading?: boolean
    showActions?: boolean
  }>(),
  { showActions: false },
)

const emit = defineEmits<{
  edit: [packet: SeedPacketResponse]
  delete: [packet: SeedPacketResponse]
}>()
</script>

<template>
  <div
    class="rounded-card border-content-muted/40 bg-surface relative mt-4 overflow-x-auto border shadow-xs"
  >
    <table class="w-full text-left text-sm">
      <thead class="bg-primary-200 text-primary-900">
        <tr>
          <th scope="col" class="px-6 py-3 font-medium">#</th>
          <th scope="col" class="px-6 py-3 font-medium">Variety</th>
          <th scope="col" class="px-6 py-3 font-medium">Brand</th>
          <th scope="col" class="px-6 py-3 font-medium">Supplier</th>
          <th scope="col" class="px-6 py-3 font-medium">Status</th>
          <th scope="col" class="px-6 py-3 font-medium">Code</th>
          <th scope="col" class="px-6 py-3 font-medium">Created</th>
          <th scope="col" class="px-6 py-3 font-medium">Last Update</th>
          <th scope="col" class="px-6 py-3 font-medium" v-if="showActions">Actions</th>
        </tr>
      </thead>
      <tbody class="divide-border divide-y">
        <template v-if="isLoading">
          <SeedPacketTableRowSkeleton v-for="i in 10" :key="i" :show-actions="true" />
        </template>

        <tr
          v-else
          v-for="packet in packets"
          :key="packet.id"
          class="hover:bg-primary-200 transition-colors"
        >
          <td class="text-content-muted px-6 py-4">{{ packet.id }}</td>
          <td class="px-6 py-4">
            <RouterLink
              :to="{ name: 'plant-variety-details', params: { id: packet.plantVariety.id } }"
              class="font-semibold capitalize hover:underline"
              >{{ packet.plantVariety.name }}</RouterLink
            >
          </td>
          <th scope="row" class="px-6 py-4">
            <RouterLink
              :to="{ name: 'seed-packet-details', params: { id: packet.id } }"
              class="font-semibold capitalize hover:underline"
              >{{ packet.brand }}</RouterLink
            >
          </th>
          <td class="text-content-secondary px-6 py-4">{{ packet.supplier }}</td>
          <td class="px-6 py-4">
            <span
              class="rounded-card px-4 py-2"
              :class="applySeedPacketStatusBadgeColor(packet.status)"
            >
              {{ packet.status.replace('_', ' ') }}
            </span>
          </td>
          <td class="text-content-secondary px-6 py-4">{{ packet.code }}</td>
          <td class="text-content-secondary px-6 py-4">{{ formatDate(packet.createdAt) }}</td>
          <td class="text-content-secondary px-6 py-4">
            {{ packet.updatedAt !== null ? formatDate(packet.updatedAt) : '-' }}
          </td>
          <td class="px-6 py-4" v-if="showActions">
            <div class="flex gap-2">
              <AppButton :icon="LucidePencil" variant="outline" @on-click="emit('edit', packet)" />
              <AppButton :icon="Trash2" variant="danger" @on-click="emit('delete', packet)" />
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped></style>
