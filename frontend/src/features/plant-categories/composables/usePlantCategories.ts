import { PLANT_CATEGORY_API_URL } from '@/constant/api.ts'
import type { PlantCategoryRequest } from '@/features/plant-categories/types/plant-category-request.interface.ts'
import type { PlantCategoryResponse } from '@/features/plant-categories/types/plant-category-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

export function usePlantCategories() {
  // Categories
  const plantCategories = ref<PlantCategoryResponse[]>([])
  const plantCategory = ref<PlantCategoryResponse | null>(null)

  // Misc
  const isLoading = ref<boolean>(false)
  const isSubmitting = ref<boolean>(false)
  const isDeleting = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)
  const page = ref<Page>()

  async function fetchPlantCategories({ pageNumber = 0 }: { pageNumber?: number } = {}) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<PlantCategoryResponse>>>(
        PLANT_CATEGORY_API_URL,
        {
          params: { page: pageNumber },
        },
      )

      plantCategories.value = data.data.content
      page.value = data.data.page
    } catch (error) {
      plantCategories.value = []
      page.value = undefined
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isLoading.value = false
    }
  }

  async function fetchPlantCategoryById(id: number) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<PlantCategoryResponse>>(
        `${PLANT_CATEGORY_API_URL}/${id}`,
      )

      plantCategory.value = data.data
    } catch (error) {
      plantCategory.value = null
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError && error.status === 404) {
        errorMessage.value = `Plant category with id ${id} not found`
        return
      }
    } finally {
      isLoading.value = false
    }
  }

  async function createPlantCategory(payload: PlantCategoryRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.post(PLANT_CATEGORY_API_URL, payload)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError && error.response?.status === 409) {
        errorMessage.value = `Plant category with name: ${payload.name} already exists`
      }

      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updatePlantCategoryById(
    id: number,
    payload: PlantCategoryRequest,
  ): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.put(`${PLANT_CATEGORY_API_URL}/${id}`, payload)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError) {
        const status = error.response?.status

        if (status === 404) {
          errorMessage.value = 'Plant category not found'
        } else if (status === 409) {
          errorMessage.value = `Plant category with name: ${payload.name} already exists`
        }
      }
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function deletePlantCategoryById(id: number): Promise<boolean> {
    isDeleting.value = true
    errorMessage.value = null

    try {
      await axios.delete(`${PLANT_CATEGORY_API_URL}/${id}`)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError) {
        if (error.status === 404) {
          errorMessage.value = `Plant category with id ${id} not found`
        } else if (
          error.status === 409 &&
          error.response?.data.error.errorCode === 'PLANT_CATEGORY_HAS_SPECIES'
        ) {
          errorMessage.value = `This plant category cannot be deleted because it still has plant species assigned to it`
        }
      }

      return false
    } finally {
      isDeleting.value = false
    }
  }

  return {
    plantCategories,
    plantCategory,

    isLoading,
    isSubmitting,
    isDeleting,
    errorMessage,
    page,

    fetchPlantCategories,
    fetchPlantCategoryById,
    createPlantCategory,
    updatePlantCategoryById,
    deletePlantCategoryById,
  }
}
