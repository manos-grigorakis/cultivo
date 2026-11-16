import { PLANT_SPECIES_API_URL } from '@/constant/api.ts'
import type { PlantSpeciesRequest } from '@/features/plant-species/types/plant-species-request.interface.ts'
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

export function usePlantSpecies() {
  const plantSpeciesList = ref<PlantSpeciesResponse[]>([])
  const plantSpecies = ref<PlantSpeciesResponse | null>(null)

  // Misc
  const page = ref<Page>()
  const isLoading = ref<boolean>(false)
  const isSubmitting = ref<boolean>(false)
  const isDeleting = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)

  async function fetchPlantSpecies({
    pageNumber = 0,
    size,
    name,
  }: { pageNumber?: number; size?: number; name?: string } = {}) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<PlantSpeciesResponse>>>(
        PLANT_SPECIES_API_URL,
        {
          params: { page: pageNumber, size: size, name: name },
        },
      )

      plantSpeciesList.value = data.data.content
      page.value = data.data.page
    } catch (error) {
      plantSpeciesList.value = []
      page.value = undefined
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isLoading.value = false
    }
  }

  async function fetchPlantSpeciesById(id: number) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<PlantSpeciesResponse>>(
        `${PLANT_SPECIES_API_URL}/${id}`,
      )

      plantSpecies.value = data.data
    } catch (error) {
      plantSpecies.value = null
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError && error.status === 404) {
        errorMessage.value = 'Plant species or category not found'
      }
    } finally {
      isLoading.value = false
    }
  }

  async function createPlantSpecies(payload: PlantSpeciesRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.post<PlantSpeciesRequest>(PLANT_SPECIES_API_URL, payload)
      return true
    } catch (error) {
      errorMessage.value =
        handleCommonCreateAndUpdateApiErrorHandling(error) ?? handleDefaultApiError(error)

      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updatePlantSpeciesById(
    id: number,
    payload: PlantSpeciesRequest,
  ): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.put(`${PLANT_SPECIES_API_URL}/${id}`, payload)
      return true
    } catch (error) {
      errorMessage.value =
        handleCommonCreateAndUpdateApiErrorHandling(error) ?? handleDefaultApiError(error)
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function deletePlantSpeciesById(id: number): Promise<boolean> {
    isDeleting.value = true
    errorMessage.value = null

    try {
      await axios.delete(`${PLANT_SPECIES_API_URL}/${id}`)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError) {
        const axiosError = error as AxiosError<ApiResponse<never>>
        const status = axiosError?.response?.status
        const errorCode = axiosError?.response?.data?.error?.errorCode

        if (status === 404) {
          errorMessage.value = 'Plant species not found'
        } else if (status === 409 && errorCode === 'PLANT_SPECIES_HAS_VARIETIES') {
          errorMessage.value = `Cannot delete plant species because it has varieties assigned. Please delete varieties and try again`
        } else if (status === 409 && errorCode === 'PLANT_SPECIES_HAS_PLANTS') {
          errorMessage.value = `Cannot delete plant species because it has plants assigned. Please delete plants and try again`
        }
      }

      return false
    } finally {
      isDeleting.value = false
    }
  }

  function handleCommonCreateAndUpdateApiErrorHandling(error: unknown): string | null {
    if (error instanceof AxiosError) {
      const status = error.response?.status

      if (status === 404) return 'Plant species or category not found'
      else if (status === 409) return 'Name or Scientific name already exists'
    }
    return null
  }

  return {
    plantSpeciesList,
    plantSpecies,

    page,
    isLoading,
    isSubmitting,
    isDeleting,
    errorMessage,

    fetchPlantSpecies,
    fetchPlantSpeciesById,
    createPlantSpecies,
    updatePlantSpeciesById,
    deletePlantSpeciesById,
  }
}
