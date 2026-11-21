<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import SeedbedSowingTable from '@/features/seedbeds/components/SeedbedSowingTable.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import { onMounted } from 'vue'

const props = defineProps<{
  seedbed: SeedbedResponse
}>()

const { seedbedSowingsList, page, isLoading, errorMessage, fetchSeedbedSowingsBySeedbedId } =
  useSeedbeds()

onMounted(() => {
  fetchSeedbedSowingsBySeedbedId({ seedbedId: props.seedbed.id })
})
</script>

<template>
  <div>
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

    <div v-else>
      <SeedbedSowingTable :sowing-list="seedbedSowingsList" :is-loading="isLoading" show-actions />
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
