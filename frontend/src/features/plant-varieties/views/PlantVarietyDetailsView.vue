<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import { PLANT_SPECIES_API_URL } from '@/constant/api.ts'
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import PlantVarietyDetailsSkeleton from '@/features/plant-varieties/components/PlantVarietyDetailsSkeleton.vue'
import PlantVarietyForm from '@/features/plant-varieties/components/PlantVarietyForm.vue'
import { usePlantVarieties } from '@/features/plant-varieties/composables/usePlantVarieties.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { formatDate } from '@/utils/format-date.utils.ts'
import { Pencil, Trash } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const SKELETON_DELAY_MS = 300
const SKELETON_MIN_VISIBLE_MS = 400

const router = useRouter()
const varietyId = Number(useRoute().params.id)

const { plantVariety, isDeleting, errorMessage, fetchPlantVarietyById, deletePlantVarietyById } =
  usePlantVarieties()

const species = ref<PlantSpeciesResponse | null>(null)

const isEditFormOpen = ref<boolean>(false)
const isDeleteDialogOpen = ref<boolean>(false)
const isPlantSpeciesLoading = ref<boolean>(false)

// Fix skeleton flickering
const isPageLoading = ref<boolean>(true)
const showSkeleton = ref<boolean>(false)

const fetchPlantSpeciesById = async () => {
  if (!varietyId || !plantVariety.value) {
    errorMessage.value = `Plant variety with id ${varietyId} not found`
    return
  }

  isPlantSpeciesLoading.value = true
  errorMessage.value = null

  try {
    const { data } = await axios.get<ApiResponse<PlantSpeciesResponse>>(
      `${PLANT_SPECIES_API_URL}/${plantVariety.value.plantSpeciesId}`,
    )
    species.value = data.data
  } catch (error) {
    if (error instanceof AxiosError && error.response?.status === 404) {
      errorMessage.value = `Plant Species with id ${plantVariety.value.plantSpeciesId} not found`
    } else {
      errorMessage.value = handleDefaultApiError(error)
    }
  } finally {
    isPlantSpeciesLoading.value = false
  }
}

const handleSuccessVarietyUpdate = async () => {
  isEditFormOpen.value = false
  await fetchPlantVarietyById(varietyId)

  if (plantVariety.value) await fetchPlantSpeciesById()
}

const onVarietyDeleteConfirmationClick = async () => {
  try {
    const deleted = await deletePlantVarietyById(varietyId)
    if (deleted) await router.push({ name: 'plant-variety-list' })
  } finally {
    isDeleteDialogOpen.value = false
  }
}

onMounted(async () => {
  let skeletonShowAt = 0

  const skeletonTimer = setTimeout(() => {
    showSkeleton.value = true
    skeletonShowAt = Date.now()
  }, SKELETON_DELAY_MS)

  try {
    await fetchPlantVarietyById(varietyId)

    if (plantVariety.value) await fetchPlantSpeciesById()
  } finally {
    clearTimeout(skeletonTimer)

    if (skeletonShowAt > 0) {
      const remaining = SKELETON_MIN_VISIBLE_MS - (Date.now() - skeletonShowAt)
      if (remaining > 0) await new Promise((resolve) => setTimeout(resolve, remaining))
    }

    isPageLoading.value = false
    showSkeleton.value = false
  }
})
</script>

<template>
  <PlantVarietyForm
    v-if="isEditFormOpen && plantVariety"
    mode="update"
    :varieties-id="plantVariety.id"
    :data="plantVariety"
    @close="isEditFormOpen = false"
    @success="handleSuccessVarietyUpdate"
  />

  <AppConfirmDialog
    v-if="isDeleteDialogOpen && plantVariety"
    :text="`Are you sure you want to delete ${plantVariety.name}`"
    :is-submitting="isDeleting"
    is-danger-operation
    @close="isDeleteDialogOpen = false"
    @confirm="onVarietyDeleteConfirmationClick"
  />

  <section class="mx-auto w-full max-w-5xl">
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="mb-4" />

    <PlantVarietyDetailsSkeleton v-if="isPageLoading && showSkeleton" />

    <div v-else-if="!isPageLoading && plantVariety && species">
      <div class="flex flex-wrap justify-between gap-2">
        <div>
          <h1 class="font-display text-2xl">{{ species.name }} - {{ plantVariety.name }}</h1>
        </div>

        <div class="flex flex-wrap gap-2">
          <AppButton :icon="Pencil" @click="isEditFormOpen = true" />
          <AppButton
            :icon="Trash"
            @click="isDeleteDialogOpen = true"
            variant="danger"
            :disabled="isDeleting || isDeleteDialogOpen"
          />
        </div>
      </div>

      <div>
        <div>
          <p class="text-content-muted mt-2 text-xs">
            <span>Created {{ formatDate(plantVariety.createdAt) }}</span> -
            <span
              >Last Update
              {{ plantVariety.updatedAt != null ? formatDate(plantVariety.updatedAt) : '-' }}</span
            >
          </p>
        </div>

        <div class="mt-1">
          <p v-if="plantVariety.description" class="text-content-muted">
            {{ plantVariety.description }}
          </p>
          <p v-else class="text-content-secondary">No description provided</p>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped></style>
