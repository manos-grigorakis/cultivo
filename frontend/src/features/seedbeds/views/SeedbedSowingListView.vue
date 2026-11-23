<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import SeedbedSowingFilters from '@/features/seedbeds/components/SeedbedSowingFilters.vue'
import SeedbedSowingTable from '@/features/seedbeds/components/SeedbedSowingTable.vue'
import { useSeedbedSowings } from '@/features/seedbeds/composables/useSeedbedSowings.ts'
import { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'
import { onMounted, ref, watch } from 'vue'

const { seedbedSowingList, page, isLoading, errorMessage, fetchSeedbedSowings } =
  useSeedbedSowings()

const selectedStatusForFilter = ref<SeedbedSowingStatus | null>(null)
const isRefreshing = ref<boolean>(false)

const onRefreshClick = async () => {
  if (isRefreshing.value) return

  isRefreshing.value = true

  try {
    await fetchSeedbedSowings({ pageNumber: page.value?.number ?? 0 })
  } finally {
    isRefreshing.value = false
  }
}

watch(selectedStatusForFilter, async (status) => {
  await fetchSeedbedSowings({
    pageNumber: 0,
    status: status ?? undefined,
  })
})

onMounted(() => fetchSeedbedSowings())
</script>

<template>
  <AppSectionWrapper title="Sowings">
    <span class="text-content-muted mb-4 block">{{ page?.totalElements ?? 0 }} total</span>

    <SeedbedSowingFilters
      v-model="selectedStatusForFilter"
      :is-refreshing="isRefreshing"
      @refresh="onRefreshClick"
    />

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-2" />

    <div v-if="seedbedSowingList.length > 0">
      <SeedbedSowingTable :sowing-list="seedbedSowingList" :is-loading="isLoading" />
      <AppPagination
        :data="page"
        @page-changed="(pageNumber) => fetchSeedbedSowings({ pageNumber })"
      />
    </div>
    <div v-else-if="!isLoading && !errorMessage">
      <p class="text-center">There are no sowings recorded yet</p>
    </div>
  </AppSectionWrapper>
</template>

<style scoped></style>
