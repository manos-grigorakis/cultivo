<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import PlantTable from '@/features/plants/components/PlantTable.vue'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'

const speciesId = useRoute().params.id
const { plants, isLoading, errorMessage, page, fetchPlants } = usePlants()

onMounted(() => fetchPlants({ plantSpeciesId: Number(speciesId) }))
</script>

<template>
  <section>
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

    <PlantTable v-if="isLoading || plants.length > 0" :plants="plants" :is-loading="isLoading" />
    <p class="text-content-muted text-center text-sm" v-else>
      There are no recorded plants with this species
    </p>

    <AppPagination
      :data="page"
      @page-changed="(pageNumber) => fetchPlants({ pageNumber, plantSpeciesId: Number(speciesId) })"
    />
  </section>
</template>

<style scoped></style>
