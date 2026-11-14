<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import SeedPacketTable from '@/features/seeds/seed-packets/components/SeedPacketTable.vue'
import { useSeedPackets } from '@/features/seeds/seed-packets/composables/useSeedPackets.ts'
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'
import { LucidePlus, RefreshCcw } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const {
  seedPackets,
  page,
  isLoading,
  isDeleting,
  errorMessage,
  fetchSeedPackets,
  deleteSeedPacketById,
} = useSeedPackets()
const isRefreshing = ref<boolean>(false)
const isDeleteDialogOpen = ref<boolean>(false)
const selectedSeedPacket = ref<SeedPacketResponse | null>(null)

const refreshSeedPackets = async () => {
  if (isRefreshing.value) return
  isRefreshing.value = true

  try {
    await fetchSeedPackets(page.value?.number ?? 0)
  } finally {
    isRefreshing.value = false
  }
}

const onOpenDeleteDialog = (packet: SeedPacketResponse) => {
  isDeleteDialogOpen.value = true
  selectedSeedPacket.value = packet
}

const onCloseDeleteDialog = () => {
  isDeleteDialogOpen.value = false
  selectedSeedPacket.value = null
}

const onSeedPacketDelete = async () => {
  if (!selectedSeedPacket.value || isDeleting.value) return

  const deleted = await deleteSeedPacketById(selectedSeedPacket.value.id)

  if (!deleted) return

  onCloseDeleteDialog()

  // If the selected item is the item in the current page, fetch the previous page,
  // Otherwise the current page
  const currentPage = page.value?.number ?? 0
  const isLastItemOnPage = seedPackets.value.length === 1
  await fetchSeedPackets(isLastItemOnPage && currentPage > 0 ? currentPage - 1 : currentPage)
}

onMounted(() => fetchSeedPackets())
</script>

<template>
  <AppConfirmDialog
    v-if="isDeleteDialogOpen && selectedSeedPacket"
    :text="`Are you sure that you want to delete '${selectedSeedPacket.brand} - ${selectedSeedPacket.code}'`"
    confirm-button-text="Yes, delete"
    :is-submitting="isDeleting"
    is-danger-operation
    @close="onCloseDeleteDialog"
    @confirm="onSeedPacketDelete"
  />

  <AppSectionWrapper title="Seed Packets">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <div class="flex items-center gap-2">
          <AppButton
            :icon="RefreshCcw"
            variant="outline"
            @click="refreshSeedPackets()"
            :disabled="isRefreshing"
            :icon-class="isRefreshing ? 'animate-spin' : ''"
          />
          <AppButton :icon="LucidePlus" label="Add seed packet" />
        </div>
      </div>
    </div>

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

    <SeedPacketTable
      v-if="isLoading || seedPackets.length > 0"
      :packets="seedPackets"
      :is-loading="isLoading"
      :show-actions="true"
      @delete="onOpenDeleteDialog($event)"
    />

    <p v-else class="mt-4 text-center">There are no seed packets recorded yet.</p>

    <AppPagination :data="page" @page-changed="fetchSeedPackets" />
  </AppSectionWrapper>
</template>

<style scoped></style>
