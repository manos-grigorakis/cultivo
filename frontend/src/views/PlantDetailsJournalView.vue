<script setup lang="ts">
import PlantEventCard from '@/components/plants/PlantEventCard.vue'
import PlantEventCardSkeleton from '@/components/skeleton/PlantEventCardSkeleton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import { PLANT_API_URL } from '@/constant/api'
import type { ApiResponse } from '@/types/api-response.interface'
import type { Page } from '@/types/pagination.interface'
import type { Pagination } from '@/types/pagination.interface'
import type { PlantEventResponse } from '@/types/plant-event-response.interface'
import axios, { AxiosError } from 'axios'
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps<{ refreshKey: number }>()

const route = useRoute()
const plantId = ref<string | string[] | undefined>(undefined)
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const plantEvents = ref<PlantEventResponse[]>([])
const page = ref<Page>()

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
      errorMessage.value = 'Plant does not exist'
    } else {
      errorMessage.value = 'Server error. Please try again'
    }
  } finally {
    isLoading.value = false
  }
}

onMounted(() => fetchJournalsByPlant())

watch(
  () => props.refreshKey,
  () => fetchJournalsByPlant(0),
)
</script>

<template>
  <section class="flex flex-col gap-4">
    <div v-if="isLoading">
      <PlantEventCardSkeleton v-for="i in 3" :key="i" />
    </div>

    <AppErrorContainer v-else-if="errorMessage" :error-message="errorMessage" />

    <template v-else>
      <p v-if="plantEvents.length === 0" class="text-center">There are no events recorded yet</p>

      <PlantEventCard v-for="event in plantEvents" :key="event.id" :event="event" />

      <AppPagination :data="page" @page-changed="fetchJournalsByPlant($event)" />
    </template>
  </section>
</template>
