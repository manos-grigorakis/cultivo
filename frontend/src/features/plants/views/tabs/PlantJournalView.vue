<script lang="ts" setup>
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import PlantEventCard from '@/features/plants/events/components/PlantEventCard.vue'
import PlantEventCardSkeleton from '@/features/plants/events/components/PlantEventCardSkeleton.vue'
import PlantEventForm from '@/features/plants/events/components/PlantEventForm.vue'
import type { PlantEventResponse } from '@/features/plants/events/types/plant-event-response.interface.ts'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps<{ refreshKey: number; plant: PlantResponse }>()

const plantId = Number(useRoute().params.id)
const { isLoading, errorMessage, plantEvents, page, fetchEventsByPlantId } = usePlants()
const selectedPlantEvent = ref<PlantEventResponse | null>(null)
const isEditPlantEventOpen = ref<boolean>(false)

const openEditPlantEventForm = (event: PlantEventResponse) => {
  selectedPlantEvent.value = event
  isEditPlantEventOpen.value = true
}

const handleEditSuccess = async () => {
  isEditPlantEventOpen.value = false
  selectedPlantEvent.value = null
  await fetchEventsByPlantId(plantId)
}

onMounted(() => fetchEventsByPlantId(plantId))

watch(
  () => props.refreshKey,
  () => fetchEventsByPlantId(plantId, 0),
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

      <AppPagination :data="page" @page-changed="fetchEventsByPlantId(plantId, $event)" />
    </template>
  </section>
</template>
