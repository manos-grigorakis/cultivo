<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import PlantSpeciesCard from '@/features/plant-species/components/PlantSpeciesCard.vue'
import PlantSpeciesCardSkeleton from '@/features/plant-species/components/PlantSpeciesCardSkeleton.vue'
import PlantSpeciesForm from '@/features/plant-species/components/PlantSpeciesForm.vue'
import { usePlantSpecies } from '@/features/plant-species/composables/usePlantSpecies.ts'
import { LucidePlus, RefreshCcw } from '@lucide/vue'
import { onMounted, ref } from 'vue'

const { plantSpeciesList, page, isLoading, errorMessage, fetchPlantSpecies } = usePlantSpecies()

const isRefreshing = ref<boolean>(false)
const isPlantSpeciesFormOpen = ref<boolean>(false)

const onCreatePlantSpecies = async () => {
  isPlantSpeciesFormOpen.value = false
  await fetchPlantSpecies()
}

const refreshSpecies = async () => {
  if (isRefreshing.value) return

  isRefreshing.value = true

  try {
    await fetchPlantSpecies(page.value?.number ?? 0)
  } finally {
    isRefreshing.value = false
  }
}

onMounted(() => fetchPlantSpecies())
</script>

<template>
  <PlantSpeciesForm
    v-if="isPlantSpeciesFormOpen"
    mode="create"
    @success="onCreatePlantSpecies"
    @close="isPlantSpeciesFormOpen = false"
  />

  <AppSectionWrapper title="Plant Species">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <div class="flex items-center gap-2">
          <AppButton
            :icon="RefreshCcw"
            variant="outline"
            @click="refreshSpecies()"
            :disabled="isRefreshing"
            :icon-class="isRefreshing ? 'animate-spin' : ''"
          />

          <AppButton
            :icon="LucidePlus"
            label="Add plant species"
            @on-click="isPlantSpeciesFormOpen = true"
            class="mt-2 sm:mt-0"
          />
        </div>
      </div>
    </div>

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

    <ul
      v-else-if="isLoading || plantSpeciesList.length > 0"
      class="mt-6 grid grid-cols-2 gap-4 lg:grid-cols-3 xl:grid-cols-4 [&>li]:min-w-0"
    >
      <template v-if="isLoading">
        <PlantSpeciesCardSkeleton v-for="i in 10" :key="i" />
      </template>
      <template v-else>
        <PlantSpeciesCard
          v-for="species in plantSpeciesList"
          :key="species.id"
          :species="species"
        />
      </template>
    </ul>

    <p v-else class="mt-4 text-center">There are no plant species recorded yet</p>

    <AppPagination :data="page" @page-changed="fetchPlantSpecies" />
  </AppSectionWrapper>
</template>

<style scoped></style>
