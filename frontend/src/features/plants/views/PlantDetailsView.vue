<script lang="ts" setup>
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import PlantDetailsHeader from '@/features/plants/components/PlantDetailsHeader.vue'
import PlantDetailsSkeleton from '@/features/plants/components/PlantDetailsSkeleton.vue'
import PlantDetailsTabs from '@/features/plants/components/PlantDetailsTabs.vue'
import PlantForm from '@/features/plants/components/PlantForm.vue'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import PlantEventForm from '@/features/plants/events/components/PlantEventForm.vue'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const plantId = useRoute().params.id

const {
  plant,
  isLoading,
  isUpdatingStatus,
  errorMessage,
  fetchPlantById,
  updatePlantStatusById,
  archivePlantById,
  fetchEventsByPlantId,
  totalPlantEventsCounts,
} = usePlants()

const isPlantUpdateFormOpen = ref<boolean>(false)
const isPlantEventFormOpen = ref<boolean>(false)
const journalRefreshKey = ref<number>(0)

const onPlantUpdated = async () => {
  isPlantUpdateFormOpen.value = false
  await fetchPlantById(Number(plantId))
}

const onPlantStatusUpdate = async (newStatus: PlantResponse['status']) => {
  await updatePlantStatusById(Number(plantId), newStatus)
}

const onClickArchivePlant = async () => {
  const archived = await archivePlantById(Number(plantId))

  if (archived) {
    alert('Plant archived successfully')
    await fetchPlantById(Number(plantId))
  }

  errorMessage.value = null
}

const onPlantEventCreation = async () => {
  isPlantEventFormOpen.value = false

  journalRefreshKey.value++
  await fetchEventsByPlantId(Number(plantId))
}

onMounted(() => {
  fetchPlantById(Number(plantId))
  fetchEventsByPlantId(Number(plantId))
})
</script>

<template>
  <PlantForm
    v-if="isPlantUpdateFormOpen && plant"
    :data="plant"
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

  <section class="mx-auto w-full max-w-5xl">
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

        <PlantDetailsTabs :events-count="totalPlantEventsCounts" :plant-id="plantId" />

        <RouterView v-slot="{ Component }">
          <component
            :is="Component"
            :plant="plant"
            :refresh-key="journalRefreshKey"
            @event-count-changed="totalPlantEventsCounts = $event"
          />
        </RouterView>
      </template>
    </template>
  </section>
</template>
