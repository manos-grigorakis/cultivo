<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import SeedbedSowingTable from '@/features/seedbeds/components/SeedbedSowingTable.vue'
import { useSeedbedSowings } from '@/features/seedbeds/composables/useSeedbedSowings.ts'
import { RefreshCcw } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const { seedbedSowingList, page, isLoading, errorMessage, fetchSeedbedSowings } =
  useSeedbedSowings()

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

onMounted(() => fetchSeedbedSowings())
</script>

<template>
  <AppSectionWrapper title="Sowings">
    <div class="flex items-center justify-between">
      <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

      <AppButton
        :icon="RefreshCcw"
        variant="outline"
        @click="onRefreshClick"
        :disabled="isRefreshing"
        :icon-class="isRefreshing ? 'animate-spin' : ''"
      />
    </div>

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

    <div v-if="seedbedSowingList.length > 0">
      <SeedbedSowingTable :sowing-list="seedbedSowingList" :is-loading="isLoading" />
      <AppPagination
        :data="page"
        @page-changed="(pageNumber) => fetchSeedbedSowings({ pageNumber })"
      />
    </div>
    <div v-else>
      <p class="text-center">There are no sowings recorded yet</p>
    </div>
  </AppSectionWrapper>
</template>

<style scoped></style>
