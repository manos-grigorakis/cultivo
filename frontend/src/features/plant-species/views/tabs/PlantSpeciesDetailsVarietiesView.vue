<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import PlantVarietyTable from '@/features/plant-varieties/components/PlantVarietyTable.vue'
import { usePlantVarieties } from '@/features/plant-varieties/composables/usePlantVarieties.ts'
import { onMounted } from 'vue'

const props = defineProps<{
  speciesId: number
}>()

const { plantVarieties, page, isLoading, errorMessage, fetchPlantVarieties } = usePlantVarieties()

onMounted(() => {
  fetchPlantVarieties({ plantSpeciesId: props.speciesId })
})
</script>

<template>
  <section>
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

    <template v-else>
      <PlantVarietyTable :plant-varieties="plantVarieties" :is-loading="isLoading" />
      <AppPagination
        :data="page"
        @page-changed="
          (pageNumber) => fetchPlantVarieties({ pageNumber, plantSpeciesId: props.speciesId })
        "
      />
    </template>
  </section>
</template>

<style scoped></style>
