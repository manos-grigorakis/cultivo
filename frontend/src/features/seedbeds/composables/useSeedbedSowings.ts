import { SEEDBED_SOWINGS_API_URL } from '@/constant/api.ts'
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

export function useSeedbedSowings() {
  const seedbedSowingList = ref<SeedbedSowingResponse[]>([])
  const seedbedSowing = ref<SeedbedSowingResponse | null>(null)

  // Misc
  const page = ref<Page>()
  const isLoading = ref<boolean>(false)
  const isDeleting = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)

  async function fetchSeedbedSowings({ pageNumber = 0 }: { pageNumber?: number } = {}) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<SeedbedSowingResponse>>>(
        SEEDBED_SOWINGS_API_URL,
        { params: { pageNumber } },
      )

      seedbedSowingList.value = data.data.content
      page.value = data.data.page
    } catch (error) {
      seedbedSowingList.value = []
      page.value = undefined
      errorMessage.value = handleDefaultApiError(error)
    } finally {
      isLoading.value = false
    }
  }

  async function fetchSeedbedSowingById(id: number) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<SeedbedSowingResponse>>(
        `${SEEDBED_SOWINGS_API_URL}/${id}`,
      )

      seedbedSowing.value = data.data
    } catch (error) {
      seedbedSowing.value = null
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError && error.response?.status === 404) {
        errorMessage.value = 'Sowing not found'
      }
    } finally {
      isLoading.value = false
    }
  }

  async function deleteSeedbedSowingById(id: number): Promise<boolean> {
    isDeleting.value = true
    errorMessage.value = null

    try {
      await axios.delete(`${SEEDBED_SOWINGS_API_URL}/${id}`)
      return true
    } catch (error) {
      errorMessage.value = handleDefaultApiError(error)

      if (error instanceof AxiosError) {
        const status = error.response?.status

        if (status === 404) {
          errorMessage.value = 'Sowing Not Found'
        } else if (status === 409) {
          errorMessage.value = 'Only sowings with SOWN status can be deleted'
        }
      }
      return false
    } finally {
      isDeleting.value = false
    }
  }

  return {
    seedbedSowingList,
    seedbedSowing,

    page,
    isLoading,
    isDeleting,
    errorMessage,

    fetchSeedbedSowings,
    fetchSeedbedSowingById,
    deleteSeedbedSowingById,
  }
}
