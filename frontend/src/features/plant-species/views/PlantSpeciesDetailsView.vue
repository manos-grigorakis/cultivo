<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import { PLANT_SPECIES_API_URL, PLANT_VARIETIES_API_URL } from '@/constant/api.ts'
import PlantSpeciesDetailsSkeleton from '@/features/plant-species/components/PlantSpeciesDetailsSkeleton.vue'
import PlantSpeciesDetailsTabs from '@/features/plant-species/components/PlantSpeciesDetailsTabs.vue'
import PlantSpeciesForm from '@/features/plant-species/components/PlantSpeciesForm.vue'
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucidePencil, LucideTrash } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const speciesId = Number(route.params.id)
const species = ref<PlantSpeciesResponse | null>(null)
const isLoading = ref<boolean>(false)
const isDeleting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const isUpdatePlantSpeciesFormOpen = ref<boolean>(false)
const isPlantSpeciesDeleteModalOpen = ref<boolean>(false)
const varietiesCount = ref<number>(0)

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
  if (!species.value) {
    errorMessage.value = 'Plant species not found'
    return
  }

  isDeleting.value = true
  errorMessage.value = null

  try {
    await axios.delete(`${PLANT_SPECIES_API_URL}/${speciesId}`)
    await router.push({ name: 'plant-species' })
  } catch (error) {
    if (error instanceof AxiosError) {
      const axiosError = error as AxiosError<ApiResponse<never>>
      const status = axiosError?.response?.status
      const errorCode = axiosError?.response?.data?.error?.errorCode

      if (status === 404) {
        errorMessage.value = 'Plant species not found'
      } else if (status === 409 && errorCode === 'PLANT_SPECIES_HAS_VARIETIES') {
        errorMessage.value = `Cannot delete ${species.value.name} because it has varieties assigned. Please delete varieties and try again`
      }
    } else {
      errorMessage.value = handleDefaultApiError(error)
    }
  } finally {
    isDeleting.value = false
    isPlantSpeciesDeleteModalOpen.value = false
  }
}

const fetchPlantVarietiesSize = async () => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const { data } = await axios.get<ApiResponse<Pagination<PlantVarietyResponse>>>(
      PLANT_VARIETIES_API_URL,
      {
        params: { plantSpeciesId: speciesId },
      },
    )

    varietiesCount.value = data.data.page.totalElements
  } catch (error) {
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchPlantSpeciesById()
  fetchPlantVarietiesSize()
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

    <AppConfirmDialog
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
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="mb-2" />

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

        <PlantSpeciesDetailsTabs :species-id="species.id" :varieties-count="varietiesCount" />

        <RouterView v-slot="{ Component }">
          <component :is="Component" :species-id="species.id" :species="species" />
        </RouterView>
      </div>

      <div v-else class="text-content-muted mt-2 text-center">
        <p>Plant Species not found</p>
      </div>
    </template>
  </section>
</template>

<style scoped></style>
