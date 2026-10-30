<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import { PLANT_CATEGORY_API_URL } from '@/constant/api.ts'
import PlantCategoryCard from '@/features/plant-categories/components/PlantCategoryCard.vue'
import PlantCategoryCardSkeleton from '@/features/plant-categories/components/PlantCategoryCardSkeleton.vue'
import PlantCategoryForm from '@/features/plant-categories/components/PlantCategoryForm.vue'
import type { PlantCategoryResponse } from '@/features/plant-categories/types/plant-category-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucidePlus } from '@lucide/vue'
import axios from 'axios'
import { onMounted, ref } from 'vue'

const plantCategories = ref<PlantCategoryResponse[]>([])
const page = ref<Page>()
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

const isCreatePlantCategoryFormOpen = ref<boolean>(false)

const fetchPlantCategories = async (pageNumber: number = 0) => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const res = await axios.get<ApiResponse<Pagination<PlantCategoryResponse>>>(
      PLANT_CATEGORY_API_URL,
      { params: { page: pageNumber } },
    )

    plantCategories.value = res.data.data.content
    page.value = res.data.data.page
  } catch (error) {
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const onPlantCategoryCreated = async () => {
  isCreatePlantCategoryFormOpen.value = false
  await fetchPlantCategories()
}

onMounted(() => {
  fetchPlantCategories()
})
</script>

<template>
  <PlantCategoryForm
    v-if="isCreatePlantCategoryFormOpen"
    mode="create"
    @close="isCreatePlantCategoryFormOpen = false"
    @success="onPlantCategoryCreated"
  />

  <AppSectionWrapper title="Plant Categories">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <AppButton
          :icon="LucidePlus"
          label="Add plant category"
          @on-click="isCreatePlantCategoryFormOpen = true"
        />
      </div>
    </div>

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="mt-4" />

    <ul
      v-else-if="isLoading || plantCategories.length > 0"
      class="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
    >
      <template v-if="isLoading">
        <PlantCategoryCardSkeleton v-for="i in 10" :key="i" />
      </template>
      <template v-else>
        <PlantCategoryCard
          v-for="category in plantCategories"
          :key="category.id"
          :plantCategory="category"
        />
      </template>
    </ul>

    <p v-else class="text-center">There are no plant categories recorded yet</p>

    <AppPagination :data="page" @page-changed="fetchPlantCategories" />
  </AppSectionWrapper>
</template>

<style scoped></style>
