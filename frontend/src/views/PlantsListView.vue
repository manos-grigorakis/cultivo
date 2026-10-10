<script setup lang="ts">
import PlantForm from '@/components/plants/PlantForm.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import { PLANT_API_URL } from '@/constant/api'
import type { ApiResponse } from '@/types/api-response.interface'
import type { Pagination } from '@/types/pagination.interface'
import type { PlantResponse } from '@/types/plant-response.interface'
import { LucidePlus } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { onMounted, ref } from 'vue'

const plants = ref<PlantResponse[]>([])
const isCreatePlantFormOpen = ref<boolean>(false)
const showArchived = ref<boolean>(false)

const fetchPlants = async (archived: boolean = false) => {
  try {
    const response = await axios.get<ApiResponse<Pagination<PlantResponse>>>(PLANT_API_URL, {
      params: { archived },
    })

    plants.value = response.data.data.content
  } catch (error) {
    if (error instanceof AxiosError) {
      if (error.response?.status === 500) {
        window.alert('Server error. Please try again')
      }
    }
  }
}

const onPlantCreated = async () => {
  isCreatePlantFormOpen.value = false
  await fetchPlants(showArchived.value)
}

const toggleArchived = async () => {
  showArchived.value = !showArchived.value
  await fetchPlants(showArchived.value)
}

onMounted(() => {
  fetchPlants()
})
</script>

<template>
  <PlantForm
    v-if="isCreatePlantFormOpen"
    mode="create"
    @close="isCreatePlantFormOpen = false"
    @success="onPlantCreated"
  />

  <AppSectionWrapper title="Plants">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="block text-content-muted">{{ plants.length }} total</span>

        <AppButton :icon="LucidePlus" label="Add plant" @on-click="isCreatePlantFormOpen = true" />
      </div>

      <!-- Filters -->
      <div>
        <AppButton
          :label="showArchived ? 'Show Plants' : 'Show Archived'"
          @on-click="toggleArchived"
          variant="outline"
        />
      </div>

      <!-- Content -->
      <ul
        class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
      >
        <li v-for="plant of plants" :key="plant.id" class="bg-white rounded-card drop-shadow-sm">
          <RouterLink :to="{ name: 'plant-details', params: { id: plant.id } }">
            <div
              class="w-full aspect-video rounded-tl-card rounded-tr-card"
              :style="{
                background: `repeating-linear-gradient(135deg,#EFFBE5 0px,#EFFBE5 18px,#DDEECC 18px,#DDEECC 36px)`,
              }"
            />

            <div class="px-2 py-3">
              <h2 class="pl-1 text-lg truncate font-display">{{ plant.label }}</h2>
            </div>
          </RouterLink>
        </li>
      </ul>
    </div>
  </AppSectionWrapper>
</template>
