import { SEEDBED_SOWINGS_API_URL } from '@/constant/api.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

export function useSeedbedSowings() {
  // Misc
  const isDeleting = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)

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
    isDeleting,
    errorMessage,

    deleteSeedbedSowingById,
  }
}
