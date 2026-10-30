<script setup lang="ts">
import PlantSpeciesDetailsSkeleton from '@/components/plant-species/PlantSpeciesDetailsSkeleton.vue'
import PlantSpeciesForm from '@/components/plant-species/PlantSpeciesForm.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppModal from '@/components/ui/AppModal.vue'
import { PLANT_SPECIES_API_URL } from '@/constant/api.ts'
import type { PlantSpeciesResponse } from '@/types/plant-species-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { CalendarDays, Droplets, LucidePencil, LucideTrash, Sun } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { type Component, computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface GrowingRequirements {
  icon: Component
  title: string
  value: string | null
}

const router = useRouter()
const route = useRoute()
const speciesId = Number(route.params.id)
const species = ref<PlantSpeciesResponse | null>(null)
const isLoading = ref<boolean>(false)
const isDeleting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const isUpdatePlantSpeciesFormOpen = ref<boolean>(false)
const isPlantSpeciesDeleteModalOpen = ref<boolean>(false)

const growingRequirements = computed<GrowingRequirements[]>(() => [
  { icon: CalendarDays, title: 'life cycle', value: species.value?.lifeCycle ?? null },
  { icon: Sun, title: 'light', value: species.value?.lightRequirement ?? null },
  { icon: Droplets, title: 'water', value: species.value?.waterRequirement ?? null },
])

const fetchPlantSpeciesById = async () => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const response = await axios.get(`${PLANT_SPECIES_API_URL}/${speciesId}`)
    if (response.status === 200) species.value = response.data.data
  } catch (error) {
    if (error instanceof AxiosError && error.status === 404) {
      errorMessage.value = 'Plant species or category not found'
      return
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const onPlantSpeciesUpdate = async () => {
  isUpdatePlantSpeciesFormOpen.value = false
  await fetchPlantSpeciesById()
}

const onPlantSpeciesDelete = async () => {
  isDeleting.value = true
  errorMessage.value = null

  try {
    await axios.delete(`${PLANT_SPECIES_API_URL}/${speciesId}`)
    await router.push({ name: 'plant-species' })
  } catch (error) {
    if (error instanceof AxiosError && error.status === 404) {
      errorMessage.value = 'Plant species not found'
      return
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isDeleting.value = false
    isPlantSpeciesDeleteModalOpen.value = false
  }
}

onMounted(() => {
  fetchPlantSpeciesById()
})
</script>

<template>
  <section>
    <PlantSpeciesForm
      v-if="isUpdatePlantSpeciesFormOpen && species"
      mode="update"
      :species-id="speciesId"
      :data="species"
      @success="onPlantSpeciesUpdate"
      @close="isUpdatePlantSpeciesFormOpen = false"
    />

    <AppModal
      v-if="isPlantSpeciesDeleteModalOpen"
      :text="`Are you sure you want to delete ${species!.name}`"
      confirm-button-text="Delete"
      :isSubmitting="isDeleting"
      is-danger-operation
      @close="isPlantSpeciesDeleteModalOpen = false"
      @confirm="onPlantSpeciesDelete"
    />

    <PlantSpeciesDetailsSkeleton v-if="isLoading" />

    <template v-else>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <div v-if="species" class="mx-auto w-full max-w-5xl">
        <!-- Header -->
        <div class="flex items-start gap-6">
          <div class="rounded-card bg-plant-placeholder aspect-square w-32 shrink-0" />

          <div class="min-w-0 flex-1">
            <h1 class="font-display text-3xl capitalize">{{ species.name }}</h1>

            <p class="text-content-muted mt-1 text-sm italic">{{ species.scientificName }}</p>
            <p class="text-content-secondary mt-2 text-sm">{{ species.plantCategory.name }}</p>
          </div>

          <div class="flex shrink-0 items-center gap-2">
            <AppButton
              :icon="LucidePencil"
              variant="outline"
              :disabled="isLoading"
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

        <!-- Growing -->
        <div class="border-content-muted/40 my-8 border-y py-6">
          <h2 class="font-display text-lg">Growing Requirements</h2>

          <div class="mt-5 grid grid-cols-3 gap-4">
            <div
              v-for="item in growingRequirements"
              :key="item.title"
              class="flex flex-col items-center gap-2 text-center"
            >
              <div class="bg-primary-700 flex size-12 items-center justify-center rounded-full">
                <component :is="item.icon" class="text-primary-100 size-5" />
              </div>
              <span class="text-content-muted text-sm capitalize">{{ item.title }}</span>
              <p class="font-medium capitalize">
                {{ item.value?.toLocaleLowerCase().replaceAll('_', ' ') ?? '-' }}
              </p>
            </div>
          </div>
        </div>

        <!-- General -->
        <div>
          <h2 class="font-display mb-5 text-xl">General Information</h2>

          <div class="space-y-6">
            <!-- Description -->
            <div>
              <span class="text-content-muted mb-1 text-sm">Description</span>
              <p v-if="species.description" class="leading-relaxed">
                {{ species.description }}
              </p>
              <p v-else class="text-content-secondary text-sm italic">No description provided</p>
            </div>

            <!-- Timestamps -->
            <div class="grid max-w-lg grid-cols-2 gap-6">
              <div>
                <span class="text-content-muted mb-1 text-sm">Created</span>
                <p class="font-medium">{{ formatDate(species.createdAt) }}</p>
              </div>

              <div>
                <span class="text-content-muted mb-1 text-sm">Last Updated</span>
                <p class="font-medium">
                  {{ species.updatedAt !== null ? formatDate(species.updatedAt) : 'Never' }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-content-muted mt-2 text-center">
        <p>Plant Species not found</p>
      </div>
    </template>
  </section>
</template>

<style scoped></style>
