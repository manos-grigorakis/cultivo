<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import { PLANT_SPECIES_API_URL, PLANT_VARIETIES_API_URL } from '@/constant/api.ts'
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import PlantVarietyDetailsSkeleton from '@/features/plant-varieties/components/PlantVarietyDetailsSkeleton.vue'
import PlantVarietyForm from '@/features/plant-varieties/components/PlantVarietyForm.vue'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
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
const varietyId = useRoute().params.id
const variety = ref<PlantVarietyResponse | null>(null)
const species = ref<PlantSpeciesResponse | null>(null)
const isLoading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const isEditFormOpen = ref<boolean>(false)
const isDeleteDialogOpen = ref<boolean>(false)
const isDeleting = ref<boolean>(false)
const isPlantSpeciesLoading = ref<boolean>(false)

// Fix skeleton flickering
const isPageLoading = ref<boolean>(true)
const showSkeleton = ref<boolean>(false)

const fetchPlantVariety = async () => {
  if (!varietyId) {
    errorMessage.value = `Plant variety with id ${varietyId} not found`
    return
  }

  isLoading.value = true
  errorMessage.value = null

  try {
    const { data } = await axios.get<ApiResponse<PlantVarietyResponse>>(
      `${PLANT_VARIETIES_API_URL}/${varietyId}`,
    )
    variety.value = data.data
  } catch (error) {
    if (error instanceof AxiosError && error.response?.status === 404) {
      errorMessage.value = `Plant variety with id ${varietyId} not found`
    } else {
      errorMessage.value = handleDefaultApiError(error)
    }
  } finally {
    isLoading.value = false
  }
}

const fetchPlantSpeciesById = async () => {
  if (!varietyId || !variety.value) {
    errorMessage.value = `Plant variety with id ${varietyId} not found`
    return
  }

  isPlantSpeciesLoading.value = true
  errorMessage.value = null

  try {
    const { data } = await axios.get<ApiResponse<PlantSpeciesResponse>>(
      `${PLANT_SPECIES_API_URL}/${variety.value.plantSpeciesId}`,
    )
    species.value = data.data
  } catch (error) {
    if (error instanceof AxiosError && error.response?.status === 404) {
      errorMessage.value = `Plant Species with id ${variety.value.plantSpeciesId} not found`
    } else {
      errorMessage.value = handleDefaultApiError(error)
    }
  } finally {
    isPlantSpeciesLoading.value = false
  }
}

const handleSuccessVarietyUpdate = async () => {
  isEditFormOpen.value = false
  await fetchPlantVariety()

  if (variety.value) await fetchPlantSpeciesById()
}

const onVarietyDeleteConfirmationClick = async () => {
  isDeleting.value = true
  errorMessage.value = null

  try {
    await axios.delete(`${PLANT_VARIETIES_API_URL}/${varietyId}`)
    await router.push({ name: 'plant-variety-list' })
  } catch (error) {
    if (axios.isAxiosError<ApiResponse<never>>(error)) {
      const status = error?.response?.status
      const errorCode = error?.response?.data?.error?.errorCode

      if (status === 404) {
        errorMessage.value = `Plant variety with id ${varietyId} not found`
        return
      }

      if (status === 409 && errorCode === 'PLANT_VARIETY_HAS_PLANTS') {
        errorMessage.value = `Cannot delete ${variety.value?.name} because it has plants assigned. Please delete plants and try again`
        isDeleteDialogOpen.value = false
        return
      }
    }
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isDeleting.value = false
  }
}

onMounted(async () => {
  let skeletonShowAt = 0

  const skeletonTimer = setTimeout(() => {
    showSkeleton.value = true
    skeletonShowAt = Date.now()
  }, SKELETON_DELAY_MS)

  try {
    await fetchPlantVariety()

    if (variety.value) await fetchPlantSpeciesById()
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
    v-if="isEditFormOpen && variety"
    mode="update"
    :varieties-id="variety.id"
    :data="variety"
    @close="isEditFormOpen = false"
    @success="handleSuccessVarietyUpdate"
  />

  <AppConfirmDialog
    v-if="isDeleteDialogOpen && variety"
    :text="`Are you sure you want to delete ${variety.name}`"
    :is-submitting="isDeleting"
    is-danger-operation
    @close="isDeleteDialogOpen = false"
    @confirm="onVarietyDeleteConfirmationClick"
  />

  <section class="mx-auto w-full max-w-5xl">
    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="mb-4" />

    <PlantVarietyDetailsSkeleton v-if="isPageLoading && showSkeleton" />

    <div v-else-if="!isPageLoading && variety && species">
      <div class="flex flex-wrap justify-between gap-2">
        <div>
          <h1 class="font-display text-2xl">{{ species.name }} - {{ variety.name }}</h1>
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
            <span>Created {{ formatDate(variety.createdAt) }}</span> -
            <span
              >Last Update
              {{ variety.updatedAt != null ? formatDate(variety.updatedAt) : '-' }}</span
            >
          </p>
        </div>

        <div class="mt-1">
          <p v-if="variety.description" class="text-content-muted">{{ variety.description }}</p>
          <p v-else class="text-content-secondary">No description provided</p>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped></style>
