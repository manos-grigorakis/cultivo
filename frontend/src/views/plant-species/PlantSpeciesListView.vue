<script setup lang="ts">
import PlantSpeciesCard from '@/components/plant-species/PlantSpeciesCard.vue'
import PlantSpeciesCardSkeleton from '@/components/plant-species/PlantSpeciesCardSkeleton.vue'
import PlantSpeciesForm from '@/components/plant-species/PlantSpeciesForm.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import { PLANT_SPECIES_API_URL } from '@/constant/api.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import type { PlantSpeciesResponse } from '@/types/plant-species-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucidePlus } from '@lucide/vue'
import axios from 'axios'
import { onMounted, ref } from 'vue'

const plantSpecies = ref<PlantSpeciesResponse[]>([])
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const page = ref<Page>()

const isPlantSpeciesFormOpen = ref<boolean>(false)

const fetchPlantSpecies = async (pageNumber: number = 0) => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const res = await axios.get<ApiResponse<Pagination<PlantSpeciesResponse>>>(
      PLANT_SPECIES_API_URL,
      {
        params: { page: pageNumber },
      },
    )
    if (res.status === 200) {
      plantSpecies.value = res.data.data.content
      page.value = res.data.data.page
    }
  } catch (error) {
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const onCreatePlantSpecies = async () => {
  isPlantSpeciesFormOpen.value = false
  await fetchPlantSpecies()
}

onMounted(() => {
  fetchPlantSpecies()
})
</script>

<template>
  <PlantSpeciesForm
    v-if="isPlantSpeciesFormOpen"
    mode="create"
    @success="onCreatePlantSpecies"
    @close="isPlantSpeciesFormOpen = false"
  />

  <AppSectionWrapper title="Plant Species">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <AppButton
          :icon="LucidePlus"
          label="Add plant species"
          @on-click="isPlantSpeciesFormOpen = true"
        />
      </div>
    </div>

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

    <ul
      v-else-if="isLoading || plantSpecies.length > 0"
      class="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
    >
      <template v-if="isLoading">
        <PlantSpeciesCardSkeleton v-for="i in 10" :key="i" />
      </template>
      <template v-else>
        <PlantSpeciesCard v-for="species in plantSpecies" :key="species.id" :species="species" />
      </template>
    </ul>

    <p v-else class="mt-4 text-center">There are no plant species recorded yet</p>

    <AppPagination :data="page" @page-changed="fetchPlantSpecies" />
  </AppSectionWrapper>
</template>

<style scoped></style>
