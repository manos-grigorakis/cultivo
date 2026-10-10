<script setup lang="ts">
import PlantDetailsCard from '@/components/plants/PlantDetailsCard.vue'
import PlantForm from '@/components/plants/PlantForm.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import type { ApiResponse } from '@/types/api-response.interface'
import type { PlantResponse } from '@/types/plant-response.interface'
import { LucidePencil, LucidePlus } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const plantId = route.params.id
const plant = ref<PlantResponse | null>(null)
const errorMessage = ref<string | null>(null)
const isPlantUpdateFormOpen = ref<boolean>(false)

const fetchPlantDetailsById = async () => {
  try {
    const response = await axios.get<ApiResponse<PlantResponse>>(
      `${import.meta.env.VITE_API_BASE_URL}/v1/plants/${plantId}`,
    )

    plant.value = response.data.data
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
        <span
          class="block px-4 py-2 text-xs rounded-card"
          :class="applyPlantStatusBadgeColor(plant.status)"
          >{{ plant.status }}</span
        >

        <div class="flex items-center gap-2">
          <AppButton :icon="LucidePlus" label="Add Event" @on-click="onAddEventClick" />
          <AppButton
            :icon="LucidePencil"
            label="Edit Plant"
            @on-click="isPlantUpdateFormOpen = true"
            variant="outline"
          />
        </div>
      </div>

      <PlantDetailsCard :status="plant.status" :created-at="plant.createdAt" />
    </div>
    <AppErrorContainer v-else-if="errorMessage" :error-message="errorMessage" />
    <div v-else class="text-center">Loading...</div>
  </section>
</template>
