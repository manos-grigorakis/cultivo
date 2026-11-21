<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import SeedbedSowingForm from '@/features/seedbeds/components/SeedbedSowingForm.vue'
import SeedbedSowingTable from '@/features/seedbeds/components/SeedbedSowingTable.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import { useSeedbedSowings } from '@/features/seedbeds/composables/useSeedbedSowings.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
}>()

const {
  isDeleting,
  errorMessage: sowingErrorMessage,
  deleteSeedbedSowingById,
} = useSeedbedSowings()
const { seedbedSowingsList, page, isLoading, errorMessage, fetchSeedbedSowingsBySeedbedId } =
  useSeedbeds()

const isSowingFormOpen = ref<boolean>(false)
const selectedSowing = ref<SeedbedSowingResponse | null>(null)

const onSowingEditClick = (sowing: SeedbedSowingResponse) => {
  selectedSowing.value = sowing
  isSowingFormOpen.value = true
}

const onSowingUpdateSuccess = async () => {
  try {
    await fetchSeedbedSowingsBySeedbedId({ seedbedId: props.seedbed.id })
  } finally {
    isSowingFormOpen.value = false
  }
}

const onSowingDeleteClick = async (sowingId: number) => {
  try {
    const deleted = await deleteSeedbedSowingById(sowingId)
    if (deleted) await fetchSeedbedSowingsBySeedbedId({ seedbedId: props.seedbed.id })
  } finally {
    isSowingFormOpen.value = false
  }
}

const definedError = computed(() => errorMessage.value || sowingErrorMessage.value)

onMounted(() => {
  fetchSeedbedSowingsBySeedbedId({ seedbedId: props.seedbed.id })
})
</script>

<template>
  <SeedbedSowingForm
    v-if="isSowingFormOpen && selectedSowing"
    mode="update"
    :seedbed-id="seedbed.id"
    :data="selectedSowing"
    :slot-id="selectedSowing.seedbedSlot.id"
    @success="onSowingUpdateSuccess"
    @close="isSowingFormOpen = false"
  />

  <div>
    <AppErrorContainer v-if="definedError" :error-message="definedError" />

    <div v-else>
      <SeedbedSowingTable
        :sowing-list="seedbedSowingsList"
        :is-loading="isLoading"
        :is-deleting="isDeleting"
        show-actions
        @edit="onSowingEditClick($event)"
        @delete="onSowingDeleteClick($event)"
      />
      <AppPagination
        :data="page"
        @page-changed="
          (pageNumber) => fetchSeedbedSowingsBySeedbedId({ pageNumber, seedbedId: seedbed.id })
        "
      />
    </div>
  </div>
</template>

<style scoped></style>
