import { PLANT_VARIETIES_API_URL } from '@/constant/api.ts'
import type { PlantVarietyRequest } from '@/features/plant-varieties/types/plant-variety-request.interface.ts'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios from 'axios'
import { ref } from 'vue'

export function usePlantVarieties() {
  const plantVarieties = ref<PlantVarietyResponse[]>([])
  const plantVariety = ref<PlantVarietyResponse | null>(null)
  const plantVarietyTotalCount = ref<number>(0)

  // Misc
  const page = ref<Page>()
  const isLoading = ref<boolean>(false)
  const isSubmitting = ref<boolean>(false)
  const isDeleting = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)

  async function fetchPlantVarieties({
    pageNumber = 0,
    plantSpeciesId,
    size,
    name,
  }: { pageNumber?: number; size?: number; name?: string; plantSpeciesId?: number } = {}) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<PlantVarietyResponse>>>(
        PLANT_VARIETIES_API_URL,
        { params: { page: pageNumber, size, name, plantSpeciesId } },
      )

      plantVarieties.value = data.data.content
      page.value = data.data.page
      plantVarietyTotalCount.value = data.data.page.totalElements
    } catch (error) {
      plantVarieties.value = []
      page.value = undefined
      plantVarietyTotalCount.value = 0
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isLoading.value = false
    }
  }

  async function fetchPlantVarietyById(id: number) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<PlantVarietyResponse>>(
        `${PLANT_VARIETIES_API_URL}/${id}`,
      )

      plantVariety.value = data.data
    } catch (error) {
      plantVariety.value = null
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isLoading.value = false
    }
  }

  async function createPlantVariety(payload: PlantVarietyRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.post(PLANT_VARIETIES_API_URL, payload)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updatePlantVarietyById(id: number, payload: PlantVarietyRequest) {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.put(`${PLANT_VARIETIES_API_URL}/${id}`, payload)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function deletePlantVarietyById(id: number): Promise<boolean> {
    isDeleting.value = true
    errorMessage.value = null

    try {
      await axios.delete(`${PLANT_VARIETIES_API_URL}/${id}`)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (axios.isAxiosError<ApiResponse<never>>(error)) {
        const status = error.response?.status
        const errorCode = error.response?.data?.error?.errorCode

        if (status === 404) {
          errorMessage.value = 'Plant variety not found'
        } else if (status === 409 && errorCode === 'PLANT_VARIETY_HAS_PLANTS') {
          errorMessage.value = `Cannot delete plant variety because it has plants assigned. Please delete plants and try again`
        } else if (status === 409 && errorCode === 'PLANT_VARIETY_HAS_SEED_PACKETS') {
          errorMessage.value = `Cannot delete plant variety because it has seed packets assigned. Please delete seed packets and try again`
        }
      }

      return false
    } finally {
      isDeleting.value = false
    }
  }

  return {
    plantVarieties,
    plantVariety,
    plantVarietyTotalCount,

    page,
    isLoading,
    isSubmitting,
    isDeleting,
    errorMessage,

    fetchPlantVarieties,
    fetchPlantVarietyById,
    createPlantVariety,
    updatePlantVarietyById,
    deletePlantVarietyById,
  }
}
