<script lang="ts" setup>
import PlantEventCard from '@/components/plants/events/PlantEventCard.vue'
import PlantEventCardSkeleton from '@/components/plants/events/PlantEventCardSkeleton.vue'
import PlantEventForm from '@/components/plants/events/PlantEventForm.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import { PLANT_API_URL } from '@/constant/api.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import type { PlantEventResponse } from '@/types/plant-event-response.interface.ts'
import type { PlantResponse } from '@/types/plant-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps<{ refreshKey: number; plant: PlantResponse }>()

const route = useRoute()
const plantId = ref<string | string[] | undefined>(undefined)
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const plantEvents = ref<PlantEventResponse[]>([])
const page = ref<Page>()

const selectedPlantEvent = ref<PlantEventResponse | null>(null)
const isEditPlantEventOpen = ref<boolean>(false)

plantId.value = route.params.id

const fetchJournalsByPlant = async (pageNumber: number = 0) => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const response = await axios.get<ApiResponse<Pagination<PlantEventResponse>>>(
      `${PLANT_API_URL}/${plantId.value}/events`,
      { params: { page: pageNumber } },
    )

    if (response.status === 200) {
      plantEvents.value = response.data.data.content
      page.value = response.data.data.page
    }
  } catch (error) {
    if (error instanceof AxiosError && error?.response?.status === 404) {
      errorMessage.value = 'Plant not found'
      return
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const openEditPlantEventForm = (event: PlantEventResponse) => {
  selectedPlantEvent.value = event
  isEditPlantEventOpen.value = true
}

const handleEditSuccess = async () => {
  isEditPlantEventOpen.value = false
  selectedPlantEvent.value = null
  await fetchJournalsByPlant()
}

onMounted(() => fetchJournalsByPlant())

watch(
  () => props.refreshKey,
  () => fetchJournalsByPlant(0),
)
</script>

<template>
  <section class="flex flex-col gap-4">
    <PlantEventForm
      v-if="isEditPlantEventOpen && selectedPlantEvent !== null"
      :plant-event="selectedPlantEvent"
      mode="update"
      @close="isEditPlantEventOpen = false"
      @success="handleEditSuccess"
    />

    <div v-if="isLoading">
      <PlantEventCardSkeleton v-for="i in 3" :key="i" />
    </div>

    <AppErrorContainer v-else-if="errorMessage" :error-message="errorMessage" />

    <template v-else>
      <p v-if="plantEvents.length === 0" class="text-center">There are no events recorded yet</p>

      <PlantEventCard
        v-for="event in plantEvents"
        :key="event.id"
        :event="event"
        :is-plant-archived="props.plant.archivedAt !== null"
        @on-edit-plant-event-click="openEditPlantEventForm(event)"
      />

      <AppPagination :data="page" @page-changed="fetchJournalsByPlant($event)" />
    </template>
  </section>
</template>
