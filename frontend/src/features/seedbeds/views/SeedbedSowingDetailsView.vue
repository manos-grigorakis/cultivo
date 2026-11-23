<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import SeedbedSowingDetails from '@/features/seedbeds/components/SeedbedSowingDetails.vue'
import SeedbedSowingDetailsCount from '@/features/seedbeds/components/SeedbedSowingDetailsCount.vue'
import SeedbedSowingDetailsStatus from '@/features/seedbeds/components/SeedbedSowingDetailsStatus.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import { useSeedbedSowings } from '@/features/seedbeds/composables/useSeedbedSowings.ts'
import { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'
import { applySeedbedSowingStatusBadgeColorUtils } from '@/features/seedbeds/utils/apply-seedbed-sowing-status-badge-color.utils.ts'
import { LucidePencil, LucideTrash } from '@lucide/vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const sowingId = Number(useRoute().params.id)
const {
  seedbedSowing: sowing,
  isDeleting,
  isLoading,
  errorMessage,
  fetchSeedbedSowingById,
  deleteSeedbedSowingById,
} = useSeedbedSowings()
const {
  isSubmitting,
  errorMessage: seedbedsErrorMessage,
  updateSeedbedSowingStatusByIdAndSeedbedIdAndSlotId,
} = useSeedbeds()

const isSowingEditFormOpen = ref<boolean>(false)
const isSowingDeleteDialogOpen = ref<boolean>(false)

const onStatusUpdate = async (status: SeedbedSowingStatus) => {
  if (!sowing.value) return

  const updated = await updateSeedbedSowingStatusByIdAndSeedbedIdAndSlotId(
    sowingId,
    sowing.value.seedbedSlot.id,
    sowing.value.seedbed.id,
    status,
  )

  if (updated) await fetchSeedbedSowingById(sowingId)
}

const onSowingDeleteClick = async () => {
  try {
    const deleted = await deleteSeedbedSowingById(sowingId)
    if (deleted) router.back()
  } finally {
    isSowingDeleteDialogOpen.value = false
  }
}

const definedError = computed(() => errorMessage.value || seedbedsErrorMessage.value)

onMounted(() => {
  fetchSeedbedSowingById(sowingId)
})
</script>

<template>
  <AppConfirmDialog
    v-if="isSowingDeleteDialogOpen"
    :is-submitting="isDeleting"
    is-danger-operation
    text="Are you sure you want to delete this sowing? This action cannot be undone"
    confirm-button-text="Yes, delete"
    @confirm="onSowingDeleteClick"
    @close="isSowingDeleteDialogOpen = false"
  />

  <section class="mx-auto w-full max-w-5xl">
    <template v-if="isLoading"><p>Loading...</p></template>

    <template v-else>
      <AppErrorContainer v-if="definedError" :error-message="definedError" class="mb-2" />

      <template v-if="sowing">
        <!-- Header -->
        <div class="mb-1 flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div class="min-w-0">
            <h1 class="font-display truncate text-4xl">Sowing #{{ sowing.id }}</h1>

            <div class="mt-2 flex flex-wrap items-center gap-2">
              <span
                class="rounded-card px-4 py-1 text-sm"
                :class="applySeedbedSowingStatusBadgeColorUtils(sowing.status)"
                >{{ sowing.status }}</span
              >

              <RouterLink
                :to="{ name: 'seed-packet-details', params: { id: sowing.seedPacket.id } }"
                class="text-content-muted text-sm hover:underline"
              >
                {{ sowing.seedPacket.brand }} - {{ sowing.seedPacket.code ?? '-' }}
              </RouterLink>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex shrink-0 items-center gap-2 self-end sm:self-auto">
            <AppButton
              :icon="LucidePencil"
              @on-click="isSowingEditFormOpen = true"
              variant="outline"
              :disabled="isLoading"
            />

            <AppButton
              :icon="LucideTrash"
              @on-click="isSowingDeleteDialogOpen = true"
              variant="danger"
              :disabled="isLoading || sowing.status !== SeedbedSowingStatus.SOWN"
            />
          </div>
        </div>

        <SeedbedSowingDetailsStatus
          :sowing="sowing"
          :is-submitting="isSubmitting"
          @update="onStatusUpdate"
        />
        <SeedbedSowingDetailsCount :sowing="sowing" />
        <SeedbedSowingDetails :sowing="sowing" />
      </template>
    </template>
  </section>
</template>

<style scoped></style>
