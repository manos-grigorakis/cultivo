<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import SeedbedSowingFilters from '@/features/seedbeds/components/SeedbedSowingFilters.vue'
import SeedbedSowingForm from '@/features/seedbeds/components/SeedbedSowingForm.vue'
import SeedbedSowingTable from '@/features/seedbeds/components/SeedbedSowingTable.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import { useSeedbedSowings } from '@/features/seedbeds/composables/useSeedbedSowings.ts'
import { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import { computed, onMounted, ref, watch } from 'vue'

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

const selectedStatusForFilter = ref<SeedbedSowingStatus | null>(null)
const isRefreshing = ref<boolean>(false)
const isSowingFormOpen = ref<boolean>(false)
const selectedSowing = ref<SeedbedSowingResponse | null>(null)

const onRefreshClick = async () => {
  if (isRefreshing.value) return

  isRefreshing.value = true

  try {
    await fetchSeedbedSowingsBySeedbedId({
      pageNumber: page.value?.number ?? 0,
      seedbedId: props.seedbed.id,
    })
  } finally {
    isRefreshing.value = false
  }
}

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

watch(selectedStatusForFilter, async (status) => {
  await fetchSeedbedSowingsBySeedbedId({
    seedbedId: props.seedbed.id,
    pageNumber: 0,
    status: status ?? undefined,
  })
})

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
    <SeedbedSowingFilters
      v-model="selectedStatusForFilter"
      :is-refreshing="isRefreshing"
      @refresh="onRefreshClick"
    />

    <AppErrorContainer v-if="definedError" :error-message="definedError" class="my-2" />

    <div v-if="seedbedSowingsList.length > 0">
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

    <div v-else-if="!definedError && !isLoading">
      <p class="text-center">There are no sowings recorded yet</p>
    </div>
  </div>
</template>

<style scoped></style>
