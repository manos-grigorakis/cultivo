import { SEED_PACKETS_API_URL } from '@/constant/api.ts'
import type { SeedPacketCreateRequest } from '@/features/seeds/seed-packets/types/seed-packet-create-request.interface.ts'
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'
import type { SeedPacketUpdateRequest } from '@/features/seeds/seed-packets/types/seed-packet-update-request.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

export function useSeedPackets() {
  const seedPackets = ref<SeedPacketResponse[]>([])
  const seedPacket = ref<SeedPacketResponse | null>(null)

  // Misc
  const page = ref<Page>()
  const isLoading = ref<boolean>(false)
  const isSubmitting = ref<boolean>(false)
  const isDeleting = ref<boolean>(false)
  const errorMessage = ref<string | null>(null)

  async function fetchSeedPackets({
    pageNumber = 0,
    size = 10,
    brand,
    code,
  }: { pageNumber?: number; size?: number; brand?: string; code?: string } = {}) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<Pagination<SeedPacketResponse>>>(
        SEED_PACKETS_API_URL,
        { params: { page: pageNumber, size, brand, code } },
      )

      seedPackets.value = data.data.content
      page.value = data.data.page
    } catch (err) {
      seedPackets.value = []
      page.value = undefined
      errorMessage.value = handleDefaultApiError(err)
    } finally {
      isLoading.value = false
    }
  }

  async function fetchSeedPacketById(id: number) {
    isLoading.value = true
    errorMessage.value = null

    try {
      const { data } = await axios.get<ApiResponse<SeedPacketResponse>>(
        `${SEED_PACKETS_API_URL}/${id}`,
      )

      seedPacket.value = data.data
    } catch (err) {
      seedPacket.value = null
      errorMessage.value = handleDefaultApiError(err)

      if (err instanceof AxiosError && err.response?.status === 404) {
        errorMessage.value = 'Seed packet or plant variety not found'
      }
    } finally {
      isLoading.value = false
    }
  }

  async function createSeedPacket(payload: SeedPacketCreateRequest): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.post(SEED_PACKETS_API_URL, payload)
      return true
    } catch (err) {
      errorMessage.value = handleDefaultApiError(err)

      if (err instanceof AxiosError && err.response?.status === 409) {
        errorMessage.value = 'Seed packet already exists with the same variety, brand and code'
      }
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function updateSeedPacketById(
    id: number,
    payload: SeedPacketUpdateRequest,
  ): Promise<boolean> {
    isSubmitting.value = true
    errorMessage.value = null

    try {
      await axios.put(`${SEED_PACKETS_API_URL}/${id}`, payload)
      return true
    } catch (err) {
      errorMessage.value = handleDefaultApiError(err)

      if (err instanceof AxiosError) {
        const status = err.response?.status
        if (status === 404) {
          errorMessage.value = 'Seed packet not found'
        } else if (status === 409) {
          errorMessage.value = 'Seed packet already exists with the same variety, brand and code'
        }
      }
      return false
    } finally {
      isSubmitting.value = false
    }
  }

  async function deleteSeedPacketById(id: number): Promise<boolean> {
    isDeleting.value = true
    errorMessage.value = null

    try {
      await axios.delete(`${SEED_PACKETS_API_URL}/${id}`)
      return true
    } catch (err) {
      errorMessage.value = handleDefaultApiError(err)

      if (err instanceof AxiosError && err.response?.status === 404) {
        errorMessage.value = 'Seed packet not found'
      }
      return false
    } finally {
      isDeleting.value = false
    }
  }

  return {
    seedPackets,
    seedPacket,

    page,
    isLoading,
    isSubmitting,
    isDeleting,
    errorMessage,

    fetchSeedPackets,
    fetchSeedPacketById,
    createSeedPacket,
    updateSeedPacketById,
    deleteSeedPacketById,
  }
}
