<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import { PLANT_VARIETIES_API_URL } from '@/constant/api.ts'
import PlantVarietyTable from '@/features/plant-varieties/components/PlantVarietyTable.vue'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios from 'axios'
import { onMounted, ref } from 'vue'

const props = defineProps<{
  speciesId: number
}>()

const varieties = ref<PlantVarietyResponse[]>([])
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const page = ref<Page>()

const fetchSpeciesPlantVarieties = async (pageNumber: number = 0) => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const { data } = await axios.get<ApiResponse<Pagination<PlantVarietyResponse>>>(
      PLANT_VARIETIES_API_URL,
      {
        params: { page: pageNumber, plantSpeciesId: props.speciesId },
      },
    )
    varieties.value = data.data.content
    page.value = data.data.page
  } catch (error) {
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchSpeciesPlantVarieties()
})
</script>

<template>
  <section>
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

    <template v-else>
      <PlantVarietyTable :plant-varieties="varieties" :is-loading="isLoading" />
      <AppPagination :data="page" @page-changed="fetchSpeciesPlantVarieties" />
    </template>
  </section>
</template>

<style scoped></style>
