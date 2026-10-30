<script lang="ts" setup>
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import { PLANT_API_URL } from '@/constant/api.ts'
import PlantCard from '@/features/plants/components/PlantCard.vue'
import PlantCardSkeleton from '@/features/plants/components/PlantCardSkeleton.vue'
import PlantForm from '@/features/plants/components/PlantForm.vue'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import { type Page, type Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucidePlus } from '@lucide/vue'
import axios from 'axios'
import { onMounted, ref } from 'vue'

const plants = ref<PlantResponse[]>([])
const isCreatePlantFormOpen = ref<boolean>(false)
const showArchived = ref<boolean>(false)
const page = ref<Page>()

const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

const fetchPlants = async (archived: boolean = false, pageNumber: number = 0) => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const response = await axios.get<ApiResponse<Pagination<PlantResponse>>>(PLANT_API_URL, {
      params: { archived, page: pageNumber },
    })

    plants.value = response.data.data.content
    page.value = response.data.data.page
  } catch (error) {
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const onPlantCreated = async () => {
  isCreatePlantFormOpen.value = false
  await fetchPlants(showArchived.value)
}

const toggleArchived = async () => {
  showArchived.value = !showArchived.value
  await fetchPlants(showArchived.value)
}

onMounted(() => {
  fetchPlants()
})
</script>

<template>
  <PlantForm
    v-if="isCreatePlantFormOpen"
    mode="create"
    @close="isCreatePlantFormOpen = false"
    @success="onPlantCreated"
  />

  <AppSectionWrapper title="Plants">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <AppButton :icon="LucidePlus" label="Add plant" @on-click="isCreatePlantFormOpen = true" />
      </div>

      <!-- Filters -->
      <div>
        <AppButton
          :label="showArchived ? 'Show Plants' : 'Show Archived'"
          variant="outline"
          @on-click="toggleArchived"
        />
      </div>

      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <ul
        v-else-if="isLoading || plants.length > 0"
        class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
      >
        <template v-if="isLoading">
          <PlantCardSkeleton v-for="i in 10" :key="i" />
        </template>

        <template v-else>
          <PlantCard v-for="plant of plants" :key="plant.id" :plant="plant" />
        </template>
      </ul>

      <p v-else class="text-center">There are no plants recorded yet</p>

      <AppPagination :data="page" @page-changed="fetchPlants(showArchived, $event)" />
    </div>
  </AppSectionWrapper>
</template>
