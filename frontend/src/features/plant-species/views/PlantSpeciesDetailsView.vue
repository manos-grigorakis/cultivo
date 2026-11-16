<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import PlantSpeciesDetailsSkeleton from '@/features/plant-species/components/PlantSpeciesDetailsSkeleton.vue'
import PlantSpeciesDetailsTabs from '@/features/plant-species/components/PlantSpeciesDetailsTabs.vue'
import PlantSpeciesForm from '@/features/plant-species/components/PlantSpeciesForm.vue'
import { usePlantSpecies } from '@/features/plant-species/composables/usePlantSpecies.ts'
import { usePlantVarieties } from '@/features/plant-varieties/composables/usePlantVarieties.ts'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import { LucidePencil, LucideTrash } from '@lucide/vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const speciesId = Number(useRoute().params.id)

const {
  plantSpecies,
  isLoading,
  isDeleting,
  errorMessage,
  fetchPlantSpeciesById,
  deletePlantSpeciesById,
} = usePlantSpecies()
const { errorMessage: plantsError, totalPlantCounts, fetchPlants } = usePlants()
const {
  errorMessage: varietiesError,
  plantVarietyTotalCount,
  fetchPlantVarieties,
} = usePlantVarieties()

const isUpdatePlantSpeciesFormOpen = ref<boolean>(false)
const isPlantSpeciesDeleteModalOpen = ref<boolean>(false)

const onPlantSpeciesUpdate = async () => {
  isUpdatePlantSpeciesFormOpen.value = false
  await fetchPlantSpeciesById(speciesId)
}

const onPlantSpeciesDelete = async () => {
  if (!plantSpecies.value) {
    errorMessage.value = 'Plant species not found'
    return
  }

  try {
    const deleted = await deletePlantSpeciesById(speciesId)
    if (deleted) await router.push({ name: 'plant-species' })
  } finally {
    isPlantSpeciesDeleteModalOpen.value = false
  }
}

const displayedError = computed(
  () => errorMessage.value || plantsError.value || varietiesError.value,
)

onMounted(async () => {
  isLoading.value = true

  try {
    await Promise.all([
      fetchPlantSpeciesById(speciesId),
      fetchPlantVarieties({ plantSpeciesId: speciesId }),
      fetchPlants({ plantSpeciesId: speciesId }),
    ])
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <section>
    <PlantSpeciesForm
      v-if="isUpdatePlantSpeciesFormOpen && plantSpecies"
      mode="update"
      :species-id="speciesId"
      :data="plantSpecies"
      @success="onPlantSpeciesUpdate"
      @close="isUpdatePlantSpeciesFormOpen = false"
    />

    <AppConfirmDialog
      v-if="isPlantSpeciesDeleteModalOpen"
      :text="`Are you sure you want to delete ${plantSpecies!.name}`"
      confirm-button-text="Delete"
      :isSubmitting="isDeleting"
      is-danger-operation
      @close="isPlantSpeciesDeleteModalOpen = false"
      @confirm="onPlantSpeciesDelete"
    />

    <PlantSpeciesDetailsSkeleton v-if="isLoading" />

    <template v-else>
      <div class="mx-auto w-full max-w-5xl">
        <AppErrorContainer v-if="displayedError" :error-message="displayedError" class="mb-2" />

        <template v-if="plantSpecies">
          <!-- Header -->
          <div class="flex items-start gap-6">
            <div class="rounded-card bg-plant-placeholder aspect-square w-32 shrink-0" />

            <div class="min-w-0 flex-1">
              <h1 class="font-display text-3xl capitalize">{{ plantSpecies.name }}</h1>

              <p class="text-content-muted mt-1 text-sm italic">
                {{ plantSpecies.scientificName }}
              </p>
              <p class="text-content-secondary mt-2 text-sm">
                {{ plantSpecies.plantCategory.name }}
              </p>
            </div>

            <div class="flex shrink-0 items-center gap-2">
              <AppButton
                :icon="LucidePencil"
                variant="outline"
                @on-click="isUpdatePlantSpeciesFormOpen = true"
              />
              <AppButton
                :icon="LucideTrash"
                variant="danger"
                :disabled="isLoading"
                @on-click="isPlantSpeciesDeleteModalOpen = true"
              />
            </div>
          </div>

          <PlantSpeciesDetailsTabs
            :species-id="plantSpecies.id"
            :varieties-count="plantVarietyTotalCount"
            :plants-count="totalPlantCounts"
          />

          <RouterView v-slot="{ Component }">
            <component :is="Component" :species-id="plantSpecies.id" :species="plantSpecies" />
          </RouterView>
        </template>

        <div v-else class="text-content-muted mt-2 text-center">
          <p>Plant Species not found</p>
        </div>
      </div>
    </template>
  </section>
</template>

<style scoped></style>
