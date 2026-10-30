<script setup lang="ts">
import PlantCategoryForm from '@/components/plant-categories/PlantCategoryForm.vue'
import PlantCategorySkeleton from '@/components/plant-categories/PlantCategorySkeleton.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppModal from '@/components/ui/AppModal.vue'
import { PLANT_CATEGORY_API_URL } from '@/constant/api.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { PlantCategoryResponse } from '@/types/plant-category-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { LucidePencil, LucideTrash } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const plantCategoryId = Number(route.params.id)
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const plantCategory = ref<PlantCategoryResponse | null>(null)
const isPlantCategoryFormOpen = ref<boolean>(false)
const isDeleting = ref<boolean>(false)
const isDeletePlantCategoryModalOpen = ref<boolean>(false)

const fetchPlantCategory = async () => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const res = await axios.get<ApiResponse<PlantCategoryResponse>>(
      `${PLANT_CATEGORY_API_URL}/${plantCategoryId}`,
    )
    if (res.status === 200) plantCategory.value = res.data.data
  } catch (error) {
    if (error instanceof AxiosError && error.status === 404) {
      errorMessage.value = `Plant category with id ${plantCategoryId} not found`
      return
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const onUpdatedPlantCategory = async () => {
  isPlantCategoryFormOpen.value = false
  await fetchPlantCategory()
}

const deletePlantCategory = async () => {
  isDeleting.value = true
  errorMessage.value = null

  try {
    const res = await axios.delete(`${PLANT_CATEGORY_API_URL}/${plantCategoryId}`)
    if (res.status === 204) await router.push({ name: 'plant-categories' })
  } catch (error) {
    if (error instanceof AxiosError) {
      if (error.status === 404) {
        errorMessage.value = `Plant category with id ${plantCategoryId} not found`
        return
      } else if (
        error.status === 409 &&
        error?.response?.data.error.errorCode === 'PLANT_CATEGORY_HAS_SPECIES'
      ) {
        errorMessage.value = `This plant category cannot be deleted because it still has plant species assigned to it`
        return
      }
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isDeleting.value = false
    isDeletePlantCategoryModalOpen.value = false
  }
}

onMounted(() => {
  fetchPlantCategory()
})
</script>

<template>
  <PlantCategoryForm
    v-if="isPlantCategoryFormOpen && plantCategory"
    mode="update"
    :plant-category-id="plantCategoryId"
    :data="plantCategory"
    @close="isPlantCategoryFormOpen = false"
    @success="onUpdatedPlantCategory"
  />

  <AppModal
    v-if="isDeletePlantCategoryModalOpen && plantCategory"
    :text="`Are you sure you want to delete ${plantCategory.name}? This action cannot be undone.`"
    confirmButtonText="Delete"
    :isSubmitting="isDeleting"
    isDangerOperation
    @close="isDeletePlantCategoryModalOpen = false"
    @confirm="deletePlantCategory"
  />

  <section>
    <PlantCategorySkeleton v-if="isLoading" />

    <template v-else>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

      <div v-if="plantCategory">
        <div class="mb-1 flex justify-between gap-2">
          <h1 class="font-display text-2xl capitalize">{{ plantCategory.name }}</h1>

          <div class="flex items-center gap-2">
            <AppButton
              :icon="LucidePencil"
              @on-click="isPlantCategoryFormOpen = true"
              variant="outline"
              :disabled="isLoading"
            />

            <AppButton
              :icon="LucideTrash"
              @on-click="isDeletePlantCategoryModalOpen = true"
              variant="danger"
              :disabled="isLoading"
            />
          </div>
        </div>

        <div class="mt-3">
          <p v-if="plantCategory.description" class="text-content">
            {{ plantCategory.description }}
          </p>
          <p v-else class="text-content-muted">No description provided</p>
        </div>

        <div class="border-content-muted/40 mt-3 flex flex-wrap gap-4 border-t pt-3">
          <div class="flex flex-col text-sm">
            <span class="text-content-muted">Created</span>
            <span> {{ formatDate(plantCategory.createdAt) }} </span>
          </div>

          <div class="flex flex-col text-sm">
            <span class="text-content-muted">Updated</span>
            <span>
              {{ plantCategory.updatedAt !== null ? formatDate(plantCategory.updatedAt) : '-' }}
            </span>
          </div>
        </div>
      </div>

      <div v-else>
        <p class="text-center">Plant Category not found</p>
      </div>
    </template>
  </section>
</template>

<style scoped></style>
