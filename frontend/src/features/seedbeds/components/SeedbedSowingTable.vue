<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import { applySeedbedSowingStatusBadgeColorUtils } from '@/features/seedbeds/utils/apply-seedbed-sowing-status-badge-color.utils.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { LucidePencil, Trash2 } from '@lucide/vue'

withDefaults(
  defineProps<{
    sowingList: SeedbedSowingResponse[]
    isLoading: boolean
    isDeleting?: boolean
    showActions?: boolean
  }>(),
  { showActions: false },
)

const emit = defineEmits<{
  edit: [sowing: SeedbedSowingResponse]
  delete: [sowingId: number]
}>()
</script>

<template>
  <div
    class="rounded-card border-content-muted/40 bg-surface relative mt-4 overflow-x-auto border shadow-xs"
  >
    <table class="w-full text-left text-sm">
      <thead class="bg-primary-200 text-primary-900">
        <tr>
          <th scope="col" class="w-[5%] p-3 font-medium">#</th>
          <th scope="col" class="w-[23%] p-3 font-medium">Seed Packet</th>
          <th scope="col" class="w-[7%] px-2 py-3 font-medium">Slot</th>
          <th scope="col" class="w-[12%] p-3 font-medium">Status</th>
          <th scope="col" class="w-[7%] px-2 py-3 font-medium">Seeds</th>
          <th scope="col" class="w-[10%] px-2 py-3 font-medium">Germinated</th>
          <th scope="col" class="w-[11%] px-2 py-3 font-medium">Transplanted</th>
          <th scope="col" class="w-[15%] p-3 font-medium">Sown Date</th>
          <th v-if="showActions" scope="col" class="w-[10%] px-2 py-3 font-medium">Actions</th>
        </tr>
      </thead>

      <tbody class="divide-border divide-y">
        <template v-if="isLoading">Loading...</template>

        <template v-else>
          <tr
            v-for="sowing in sowingList"
            :key="sowing.id"
            class="hover:bg-primary-200 transition-colors"
          >
            <td class="text-content-muted px-2 py-4">
              <RouterLink
                :to="{ name: 'seedbed-sowing-details', params: { id: sowing.id } }"
                class="hover:underline"
              >
                {{ sowing.id }}
              </RouterLink>
            </td>
            <td class="px-2 py-4">
              <RouterLink
                :to="{ name: 'seed-packet-details', params: { id: sowing.seedPacket.id } }"
                class="font-semibold capitalize hover:underline"
                >{{ sowing.seedPacket.brand }} - {{ sowing.seedPacket.code ?? '-' }}</RouterLink
              >
            </td>
            <td class="text-content-muted px-2 py-4">
              R{{ sowing.seedbedSlot.rowIndex + 1 }}, C{{ sowing.seedbedSlot.columnIndex + 1 }}
            </td>
            <td class="px-2 py-4">
              <span
                class="rounded-card px-4 py-2"
                :class="applySeedbedSowingStatusBadgeColorUtils(sowing.status)"
              >
                {{ sowing.status }}
              </span>
            </td>
            <td class="text-content-muted px-2 py-4">{{ sowing.seedCount }}</td>
            <td class="text-content-muted px-2 py-4">{{ sowing.germinatedCount ?? '-' }}</td>
            <td class="text-content-muted px-2 py-4">{{ sowing.transplantedCount ?? '-' }}</td>

            <td class="text-content-muted px-2 py-4">{{ formatDate(sowing.sownAt) }}</td>

            <td class="px-3 py-4" v-if="showActions">
              <div class="flex gap-2">
                <AppButton
                  :icon="LucidePencil"
                  :disabled="sowing.status === 'FAILED' || sowing.status === 'TRANSPLANTED'"
                  variant="outline"
                  @on-click="emit('edit', sowing)"
                />
                <AppButton
                  :icon="Trash2"
                  :disabled="sowing.status !== 'SOWN' || isDeleting"
                  variant="danger"
                  @on-click="emit('delete', sowing.id)"
                />
              </div>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
</template>

<style scoped></style>
