<script lang="ts" setup>
import PlantEventForm from '@/components/plants/events/PlantEventForm.vue'
import PlantDetailsHeader from '@/components/plants/PlantDetailsHeader.vue'
import PlantDetailsSkeleton from '@/components/plants/PlantDetailsSkeleton.vue'
import PlantDetailsTabs from '@/components/plants/PlantDetailsTabs.vue'
import PlantForm from '@/components/plants/PlantForm.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import { PLANT_API_URL } from '@/constant/api.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Pagination } from '@/types/pagination.interface.ts'
import type { PlantEventResponse } from '@/types/plant-event-response.interface.ts'
import type { PlantResponse } from '@/types/plant-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const plantId = route.params.id
const plant = ref<PlantResponse | null>(null)
const errorMessage = ref<string | null>(null)
const isPlantUpdateFormOpen = ref<boolean>(false)
const isPlantEventFormOpen = ref<boolean>(false)
const eventsCount = ref<number>(0)
const journalRefreshKey = ref<number>(0)
const isLoading = ref<boolean>(false)
const isUpdatingStatus = ref<boolean>(false)

const fetchPlantDetailsById = async () => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const response = await axios.get<ApiResponse<PlantResponse>>(`${PLANT_API_URL}/${plantId}`)
    plant.value = response.data.data
  } catch (error) {
    if (error instanceof AxiosError && error?.response?.status === 404) {
      errorMessage.value = `Plant with id: ${plantId} not found`
      return
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const onPlantUpdated = async () => {
  isPlantUpdateFormOpen.value = false
  await fetchPlantDetailsById()
}

const onPlantStatusUpdate = async (newStatus: PlantResponse['status']) => {
  if (!plant.value || isUpdatingStatus.value) return

  const previousStatus = plant.value.status

  if (newStatus === previousStatus) return

  isUpdatingStatus.value = true
  errorMessage.value = null

  try {
    const response = await axios.patch(`${PLANT_API_URL}/${plantId}/status`, { status: newStatus })

    if (response.status === 204) plant.value.status = newStatus
  } catch (error) {
    if (error instanceof AxiosError) {
      const axiosError = error as AxiosError<ApiResponse<never>>
      const status = axiosError?.response?.status
      const errorCode = axiosError?.response?.data?.error?.errorCode

      if (status === 404) {
        errorMessage.value = `Plant with id ${plantId} not found`
        return
      } else if (status === 409 && errorCode === 'STATUS_VIOLATION') {
        errorMessage.value = 'Invalid plant status transition'
        return
      } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
        errorMessage.value = 'Plant is archived and cannot be modified'
        return
      }
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isUpdatingStatus.value = false
  }
}

const onClickArchivePlant = async () => {
  errorMessage.value = null

  try {
    const response = await axios.patch(`${PLANT_API_URL}/${plantId}/archive`)
    if (response.status === 204) {
      alert('Plant archived successfully')
      await fetchPlantDetailsById()
    }
  } catch (error) {
    if (error instanceof AxiosError) {
      const axiosError = error as AxiosError<ApiResponse<never>>
      const status = axiosError?.response?.status
      const errorCode = axiosError?.response?.data?.error?.errorCode

      if (status === 404) {
        errorMessage.value = `Plant with id ${plantId} not found`
        return
      } else if (status === 409 && errorCode === 'ALREADY_ARCHIVED') {
        errorMessage.value = 'Plant is already archived'
        return
      }
    }

    errorMessage.value = handleDefaultApiError(error)
  }
}

const fetchEventsCount = async () => {
  try {
    const response = await axios.get<ApiResponse<Pagination<PlantEventResponse>>>(
      `${PLANT_API_URL}/${plantId}/events`,
      { params: { page: 0, size: 1 } },
    )

    eventsCount.value = response.data.data.page.totalElements
  } catch (error) {
    console.error(error)
  }
}

const onPlantEventCreation = async () => {
  isPlantEventFormOpen.value = false

  journalRefreshKey.value++
  await fetchEventsCount()
}

onMounted(() => {
  fetchPlantDetailsById()
  fetchEventsCount()
})
</script>

<template>
  <PlantForm
    v-if="isPlantUpdateFormOpen && plant"
    :initial-label="plant.label"
    :plant-id="plant.id"
    mode="update"
    @close="isPlantUpdateFormOpen = false"
    @success="onPlantUpdated"
  />

  <PlantEventForm
    v-if="isPlantEventFormOpen"
    mode="create"
    @close="isPlantEventFormOpen = false"
    @success="onPlantEventCreation()"
  />

  <section>
    <PlantDetailsSkeleton v-if="isLoading" />

    <template v-else>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

      <template v-if="plant">
        <PlantDetailsHeader
          :is-updating-status="isUpdatingStatus"
          :plant="plant"
          @status-change="onPlantStatusUpdate"
          @add-event="isPlantEventFormOpen = true"
          @edit-plant="isPlantUpdateFormOpen = true"
          @archive-plant="onClickArchivePlant"
        />

        <PlantDetailsTabs :events-count="eventsCount" :plant-id="plantId" />

        <RouterView v-slot="{ Component }">
          <component
            :is="Component"
            :plant="plant"
            :refresh-key="journalRefreshKey"
            @event-count-changed="eventsCount = $event"
          />
        </RouterView>
      </template>
    </template>
  </section>
</template>
