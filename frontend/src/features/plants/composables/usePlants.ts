import { PLANT_API_URL } from '@/constant/api.ts'
import type { PlantEventRequest } from '@/features/plants/events/types/plant-event-request.interface.ts'
import type { PlantEventResponse } from '@/features/plants/events/types/plant-event-response.interface.ts'
import type { PlantRequest } from '@/features/plants/types/plant-request.interface.ts'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

export function usePlants() {
  // Plants
  const plants = ref<PlantResponse[]>([])
  const totalPlantCounts = ref<number>(0)
  const plant = ref<PlantResponse | null>(null)

  // Plant Events
  const plantEvents = ref<PlantEventResponse[]>([])
  const totalPlantEventsCounts = ref<number>(0)

  // Misc
  const page = ref<Page>()
  const isLoading = ref<boolean>(false)
  const isSubmitting = ref<boolean>(false)
  const isUpdatingStatus = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)

  async function fetchPlants({
    pageNumber = 0,
    archived = false,
    plantSpeciesId,
  }: {
    pageNumber?: number
    archived?: boolean
    plantSpeciesId?: number
  }) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<PlantResponse>>>(PLANT_API_URL, {
        params: { archived, page: pageNumber, plantSpeciesId },
      })

      plants.value = data.data.content
      page.value = data.data.page
      totalPlantCounts.value = data.data.page.totalElements
    } catch (error) {
      plants.value = []
      page.value = undefined
      totalPlantEventsCounts.value = 0

      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isLoading.value = false
    }
  }

  async function fetchPlantById(id: number) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<PlantResponse>>(`${PLANT_API_URL}/${id}`)
      plant.value = data.data
    } catch (error) {
      if (error instanceof AxiosError && error?.response?.status === 404) {
        errorMessage.value = `Plant with id: ${id} not found`
      } else {
        errorMessage.value = handleDefaultApiError(error)
      }
    } finally {
      isLoading.value = false
    }
  }

  async function createPlant(payload: PlantRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.post(PLANT_API_URL, payload)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updatePlantById(id: number, payload: PlantRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.put(`${PLANT_API_URL}/${id}`, payload)
      return true
    } catch (error) {
      if (error instanceof AxiosError) {
        const axiosError = error as AxiosError<ApiResponse<never>>
        const status = axiosError?.response?.status
        const errorCode = axiosError?.response?.data?.error?.errorCode

        if (status === 404) {
          errorMessage.value = `Plant not found`
        } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
          errorMessage.value = 'Plant is archived and cannot be modified'
        }
      } else {
        errorMessage.value = handleDefaultApiError(error)
      }

      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updatePlantStatusById(
    id: number,
    newStatus: PlantResponse['status'],
  ): Promise<boolean> {
    if (!plant.value || isUpdatingStatus.value) return false
    if (plant.value.status === newStatus) return false

    isUpdatingStatus.value = true
    errorMessage.value = null

    try {
      await axios.patch(`${PLANT_API_URL}/${id}/status`, { status: newStatus })
      plant.value.status = newStatus
      return true
    } catch (error) {
      if (error instanceof AxiosError) {
        const axiosError = error as AxiosError<ApiResponse<never>>
        const status = axiosError?.response?.status
        const errorCode = axiosError?.response?.data?.error?.errorCode

        if (status === 404) {
          errorMessage.value = `Plant with id ${id} not found`
        } else if (status === 409 && errorCode === 'STATUS_VIOLATION') {
          errorMessage.value = 'Invalid plant status transition'
        } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
          errorMessage.value = 'Plant is archived and cannot be modified'
        }
      } else {
        errorMessage.value = handleDefaultApiError(error)
      }
      return false
    } finally {
      isUpdatingStatus.value = false
    }
  }

  async function archivePlantById(id: number): Promise<boolean> {
    isLoading.value = true
    errorMessage.value = null

    try {
      await axios.patch(`${PLANT_API_URL}/${id}/archive`)
      return true
    } catch (error) {
      if (error instanceof AxiosError) {
        const axiosError = error as AxiosError<ApiResponse<never>>
        const status = axiosError?.response?.status
        const errorCode = axiosError?.response?.data?.error?.errorCode

        if (status === 404) {
          errorMessage.value = `Plant with id ${id} not found`
        } else if (status === 409 && errorCode === 'ALREADY_ARCHIVED') {
          errorMessage.value = 'Plant is already archived'
        }
      } else {
        errorMessage.value = handleDefaultApiError(error)
      }
      return false
    } finally {
      isLoading.value = false
    }
  }

  async function fetchEventsByPlantId(id: number, pageNumber: number = 0) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<PlantEventResponse>>>(
        `${PLANT_API_URL}/${id}/events`,
        { params: { page: pageNumber } },
      )

      plantEvents.value = data.data.content
      page.value = data.data.page
      totalPlantEventsCounts.value = data.data.page.totalElements
    } catch (error) {
      if (error instanceof AxiosError && error?.response?.status === 404) {
        errorMessage.value = 'Plant not found'
      } else {
        errorMessage.value = handleDefaultApiError(error)
      }
    } finally {
      isLoading.value = false
    }
  }

  async function createPlantEvent(plantId: number, payload: PlantEventRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.post(`${PLANT_API_URL}/${plantId}/events`, payload)
      return true
    } catch (error) {
      if (error instanceof AxiosError) {
        const status = error.response?.status
        const errorCode = error.response?.data?.error?.errorCode

        if (status === 404) {
          errorMessage.value = 'Plant not found'
        } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
          errorMessage.value = 'Plant is archived and cannot be modified'
        }
      } else {
        errorMessage.value = handleDefaultApiError(error)
      }
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updatePlantEventById(
    plantId: number,
    eventId: number,
    payload: PlantEventRequest,
  ): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.put(`${PLANT_API_URL}/${plantId}/events/${eventId}`, payload)
      return true
    } catch (error) {
      if (error instanceof AxiosError) {
        const status = error.response?.status
        const errorCode = error.response?.data?.error?.errorCode

        if (status === 404) {
          errorMessage.value = 'Plant or Plant Event not found'
        } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
          errorMessage.value = 'Plant is archived and cannot be modified'
        }
      } else {
        errorMessage.value = handleDefaultApiError(error)
      }
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  return {
    plants,
    totalPlantCounts,
    plant,
    plantEvents,
    totalPlantEventsCounts,
    page,
    isLoading,
    isSubmitting,
    isUpdatingStatus,
    errorMessage,

    fetchPlants,
    fetchPlantById,
    createPlant,
    updatePlantById,
    updatePlantStatusById,
    archivePlantById,

    fetchEventsByPlantId,
    createPlantEvent,
    updatePlantEventById,
  }
}
