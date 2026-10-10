<script setup lang="ts">
import PlantDetailsCard from '@/components/plants/PlantDetailsCard.vue'
import PlantForm from '@/components/plants/PlantForm.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import type { ApiResponse } from '@/types/api-response.interface'
import type { PlantResponse } from '@/types/plant-response.interface'
import { LucideArchive, LucidePencil, LucidePlus } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const plantId = route.params.id
const plant = ref<PlantResponse | null>(null)
const errorMessage = ref<string | null>(null)
const isPlantUpdateFormOpen = ref<boolean>(false)
const currentPlantStatus = ref<string>(plant.value?.status ?? 'ACTIVE')

const fetchPlantDetailsById = async () => {
  try {
    const response = await axios.get<ApiResponse<PlantResponse>>(
      `${import.meta.env.VITE_API_BASE_URL}/v1/plants/${plantId}`,
    )

    plant.value = response.data.data
    currentPlantStatus.value = plant.value.status
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error?.response?.status

      if (status === 404) {
        errorMessage.value = `Plant with id: ${plantId} does not exist`
      } else {
        errorMessage.value = 'Server error. Please try again'
      }
    }
  }
}

const applyPlantStatusBadgeColor = (status: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-primary-200 text-primary-900'
    case 'DEAD':
      return 'bg-red-200 text-red-900'
    default:
      return 'bg-surface-muted text-content'
  }
}

const onAddEventClick = () => {
  // TODO handle event creation event
}

const onPlantUpdated = async () => {
  isPlantUpdateFormOpen.value = false
  await fetchPlantDetailsById()
}

const onPlantStatusUpdate = async () => {
  errorMessage.value = null

  try {
    const response = await axios.patch(
      `${import.meta.env.VITE_API_BASE_URL}/v1/plants/${plantId}/status`,
      {
        status: currentPlantStatus.value,
      },
    )

    if (response.status === 204 && plant.value) plant.value.status = currentPlantStatus.value
  } catch (error) {
    if (error instanceof AxiosError) {
      const axiosError = error as AxiosError<ApiResponse<never>>
      const status = axiosError?.response?.status
      const errorCode = axiosError?.response?.data?.error?.errorCode

      if (status === 404) {
        errorMessage.value = `Plant with id ${plantId} does not exist`
      } else if (status === 409 && errorCode === 'STATUS_VIOLATION') {
        errorMessage.value = 'Invalid plant status transition'
      } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
        errorMessage.value = 'Plant is archived and cannot modified'
      } else {
        errorMessage.value = 'Server error. Please try again'
      }
    }

    // Restore to previous status if update fails
    currentPlantStatus.value = plant.value?.status ?? 'ACTIVE'
  }
}

const onClickArchivePlant = async () => {
  try {
    const response = await axios.patch(
      `${import.meta.env.VITE_API_BASE_URL}/v1/plants/${plantId}/archive`,
    )
    if (response.status === 204) {
      alert('Plant archived successfully')
      fetchPlantDetailsById()
    }
  } catch (error) {
    const axiosError = error as AxiosError<ApiResponse<never>>
    const status = axiosError?.response?.status
    const errorCode = axiosError?.response?.data?.error?.errorCode

    if (status === 404) {
      errorMessage.value = `Plant with id ${plantId} does not exist`
    } else if (status === 409 && errorCode === 'ALREADY_ARCHIVED') {
      errorMessage.value = 'Plant is already archived'
    } else {
      errorMessage.value = 'Server error. Please try again'
    }
  }
}

onMounted(() => {
  fetchPlantDetailsById()
})
</script>

<template>
  <PlantForm
    v-if="isPlantUpdateFormOpen && plant"
    mode="update"
    :plant-id="plant.id"
    :initial-label="plant.label"
    @close="isPlantUpdateFormOpen = false"
    @success="onPlantUpdated"
  />

  <section>
    <div v-if="plant !== null">
      <div
        class="w-50 h-50 rounded-card max-w-150"
        :style="{
          background: `repeating-linear-gradient(135deg,#EFFBE5 0px,#EFFBE5 18px,#DDEECC 18px,#DDEECC 36px)`,
        }"
      />

      <h2 class="mt-4 text-4xl truncate font-display">{{ plant.label }}</h2>

      <div class="flex flex-wrap items-center justify-between mt-2">
        <select
          v-model="currentPlantStatus"
          @change="onPlantStatusUpdate"
          class="px-4 py-2 text-xs rounded-card disabled:cursor-not-allowed"
          :disabled="plant.archivedAt !== null"
          :class="applyPlantStatusBadgeColor(plant.status)"
        >
          <option value="ACTIVE">ACTIVE</option>
          <option value="DEAD">DEAD</option>
        </select>

        <div class="flex items-center gap-2">
          <AppButton
            :icon="LucidePlus"
            label="Add Event"
            @on-click="onAddEventClick"
            :disabled="plant.archivedAt != null"
          />
          <AppButton
            :icon="LucidePencil"
            label="Edit Plant"
            @on-click="isPlantUpdateFormOpen = true"
            variant="outline"
            :disabled="plant.archivedAt != null"
          />

          <AppButton
            :icon="LucideArchive"
            @on-click="onClickArchivePlant"
            :variant="'outline'"
            :disabled="plant.archivedAt != null"
          />
        </div>
      </div>

      <PlantDetailsCard
        :status="plant.status"
        :created-at="plant.createdAt"
        :archived-at="plant.archivedAt"
      />
    </div>
    <div v-else class="text-center">Loading...</div>
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />
  </section>
</template>
