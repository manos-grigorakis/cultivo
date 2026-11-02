<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import { PLANT_API_URL } from '@/constant/api.ts'
import PlantTable from '@/features/plants/components/PlantTable.vue'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const speciesId = useRoute().params.id
const plants = ref<PlantResponse[]>([])
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const page = ref<Page>()

const fetchPlantBySpeciesId = async (pageNumber: number = 0) => {
  if (!speciesId) {
    errorMessage.value = 'No species found.'
    return
  }

  isLoading.value = true
  errorMessage.value = null

  try {
    const { data } = await axios.get<ApiResponse<Pagination<PlantResponse>>>(PLANT_API_URL, {
      params: { page: pageNumber, plantSpeciesId: speciesId },
    })
    plants.value = data.data.content
    page.value = data.data.page
  } catch (error) {
    plants.value = []
    page.value = undefined
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchPlantBySpeciesId()
})
</script>

<template>
  <section>
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

    <PlantTable v-if="isLoading || plants.length > 0" :plants="plants" :is-loading="isLoading" />
    <p class="text-content-muted text-center text-sm" v-else>
      There are no recorded plants with this species
    </p>

    <AppPagination :data="page" @page-changed="fetchPlantBySpeciesId" />
  </section>
</template>

<style scoped></style>
