import { SEEDBED_API_URL } from '@/constant/api.ts'
import type { SeedbedCreateRequest } from '@/features/seedbeds/types/seedbed-create-request.interface.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import type { SeedbedSlotSummaryResponse } from '@/features/seedbeds/types/seedbed-slot-summary-response.interface.ts'
import type { SeedbedUpdateRequest } from '@/features/seedbeds/types/seedbed-update-request.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

export function useSeedbeds() {
  // Seedbeds
  const seedbedsList = ref<SeedbedResponse[]>([])
  const seedbed = ref<SeedbedResponse | null>(null)

  // Seedbed Slots
  const seedbedSlotsList = ref<SeedbedSlotSummaryResponse[]>([])
  const seedbedSlot = ref<SeedbedSlotSummaryResponse | null>(null)

  // Misc
  const page = ref<Page>()
  const isLoading = ref<boolean>(false)
  const isSeedbedSlotsLoading = ref<boolean>(false)
  const isSubmitting = ref<boolean>(false)
  const isDeleting = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)

  async function fetchSeedbeds({ pageNumber = 0 }: { pageNumber?: number } = {}) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<SeedbedResponse>>>(SEEDBED_API_URL, {
        params: { pageNumber },
      })

      seedbedsList.value = data.data.content
      page.value = data.data.page
    } catch (error) {
      seedbedsList.value = []
      page.value = undefined
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isLoading.value = false
    }
  }

  async function fetchSeedbedById(id: number) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<SeedbedResponse>>(`${SEEDBED_API_URL}/${id}`)

      seedbed.value = data.data
    } catch (error) {
      seedbed.value = null
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError) errorMessage.value = 'Seedbed not found'
    } finally {
      isLoading.value = false
    }
  }

  async function createSeedbed(payload: SeedbedCreateRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.post(SEEDBED_API_URL, payload)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError && error.response?.status === 409) {
        errorMessage.value = 'Seedbed already exists by name'
      }

      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updateSeedbedById(id: number, payload: SeedbedUpdateRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.put(`${SEEDBED_API_URL}/${id}`, payload)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError) {
        const status = error.response?.status

        if (status === 404) errorMessage.value = 'Seedbed not found'
        else if (status === 409) errorMessage.value = 'Seedbed already exists by name'
      }

      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function deleteSeedbedById(id: number): Promise<boolean> {
    isDeleting.value = true
    errorMessage.value = null

    try {
      await axios.delete(`${SEEDBED_API_URL}/${id}`)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError && error.response?.status === 404) {
        errorMessage.value = 'Seedbed not found'
      }

      return false
    } finally {
      isDeleting.value = false
    }
  }

  async function fetchSeedbedSlotsBySeedbedId(seedbedId: number) {
    isSeedbedSlotsLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<SeedbedSlotSummaryResponse[]>>(
        `${SEEDBED_API_URL}/${seedbedId}/slots`,
      )

      seedbedSlotsList.value = data.data
    } catch (error) {
      seedbedSlotsList.value = []
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isSeedbedSlotsLoading.value = false
    }
  }

  async function fetchSeedbedSlotByIdAndSeedbedId(id: number, seedbedId: number) {
    isSeedbedSlotsLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<SeedbedSlotSummaryResponse>>(
        `${SEEDBED_API_URL}/${seedbedId}/slots/${id}`,
      )

      seedbedSlot.value = data.data
    } catch (error) {
      seedbedSlot.value = null
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isSeedbedSlotsLoading.value = false
    }
  }

  return {
    seedbedsList,
    seedbed,

    page,
    isLoading,
    isSeedbedSlotsLoading,
    isSubmitting,
    isDeleting,
    errorMessage,

    fetchSeedbeds,
    fetchSeedbedById,
    createSeedbed,
    updateSeedbedById,
    deleteSeedbedById,
    fetchSeedbedSlotsBySeedbedId,
    fetchSeedbedSlotByIdAndSeedbedId,
  }
}
