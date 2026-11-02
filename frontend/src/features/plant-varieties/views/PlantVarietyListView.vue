<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppConfirmDialog from '@/components/ui/AppConfirmDialog.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppSectionWrapper from '@/components/ui/AppSectionWrapper.vue'
import { PLANT_VARIETIES_API_URL } from '@/constant/api.ts'
import PlantVarietyForm from '@/features/plant-varieties/components/PlantVarietyForm.vue'
import PlantVarietyTable from '@/features/plant-varieties/components/PlantVarietyTable.vue'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Page, Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucidePlus, RefreshCcw } from '@lucide/vue'
import axios from 'axios'
import { onMounted, ref } from 'vue'

const plantVarieties = ref<PlantVarietyResponse[]>([])
const isRefreshing = ref<boolean>(false)
const isLoading = ref<boolean>(false)
const isDeleting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)
const page = ref<Page>()
const selectedVariety = ref<PlantVarietyResponse | null>(null)
const isDeleteDialogOpen = ref<boolean>(false)
const formMode = ref<'create' | 'update' | null>(null) // null === closed form

const fetchPlantVarieties = async (pageNumber: number = 0) => {
  isLoading.value = true
  errorMessage.value = null

  try {
    const response = await axios.get<ApiResponse<Pagination<PlantVarietyResponse>>>(
      PLANT_VARIETIES_API_URL,
      { params: { page: pageNumber } },
    )
    plantVarieties.value = response.data.data.content
    page.value = response.data.data.page
  } catch (error) {
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isLoading.value = false
  }
}

const refreshPlantVarieties = async () => {
  if (isRefreshing.value) return

  isRefreshing.value = true

  try {
    await fetchPlantVarieties(page.value?.number ?? 0)
  } finally {
    isRefreshing.value = false
  }
}

const openDeleteDialog = (variety: PlantVarietyResponse) => {
  selectedVariety.value = variety
  isDeleteDialogOpen.value = true
}

const closeDeleteDialog = () => {
  isDeleteDialogOpen.value = false
  selectedVariety.value = null
}

const onPlantVarietyDelete = async () => {
  if (!selectedVariety.value || isDeleting.value) return

  isDeleting.value = true
  errorMessage.value = null

  try {
    await axios.delete(`${PLANT_VARIETIES_API_URL}/${selectedVariety.value.id}`)
    closeDeleteDialog()
    await fetchPlantVarieties(page.value?.number ?? 0)
  } catch (error) {
    if (axios.isAxiosError<ApiResponse<never>>(error)) {
      const status = error?.response?.status
      const errorCode = error?.response?.data?.error?.errorCode

      if (status === 404) {
        errorMessage.value = 'Plant variety not found'
        return
      }

      if (status === 409 && errorCode === 'PLANT_VARIETY_HAS_PLANTS') {
        errorMessage.value = `Cannot delete ${selectedVariety.value?.name} because it has plants assigned. Please delete plants and try again`
        return
      }
    }
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isDeleting.value = false
    closeDeleteDialog()
  }
}

const openCreateForm = () => {
  selectedVariety.value = null
  formMode.value = 'create'
}

const openEditForm = (variety: PlantVarietyResponse) => {
  selectedVariety.value = variety
  formMode.value = 'update'
}

const closeForm = () => {
  formMode.value = null
  selectedVariety.value = null
}

const onFormSuccess = async () => {
  closeForm()
  await fetchPlantVarieties(page.value?.number ?? 0)
}

onMounted(() => {
  fetchPlantVarieties()
})
</script>

<template>
  <AppConfirmDialog
    v-if="isDeleteDialogOpen"
    :text="`Are you sure that you want to delete '${selectedVariety?.name}'`"
    confirm-button-text="Yes, delete"
    is-danger-operation
    :is-submitting="isDeleting"
    @close="closeDeleteDialog"
    @confirm="onPlantVarietyDelete"
  />

  <PlantVarietyForm
    v-if="formMode"
    :mode="formMode"
    :varieties-id="selectedVariety?.id"
    :data="selectedVariety ?? undefined"
    @success="onFormSuccess"
    @close="closeForm"
  />

  <AppSectionWrapper title="Plant Varieties">
    <div class="flex flex-col gap-4">
      <div class="flex items-center justify-between">
        <span class="text-content-muted block">{{ page?.totalElements ?? 0 }} total</span>

        <div class="flex items-center gap-2">
          <AppButton
            :icon="RefreshCcw"
            variant="outline"
            @click="refreshPlantVarieties()"
            :disabled="isRefreshing"
            :icon-class="isRefreshing ? 'animate-spin' : ''"
          />
          <AppButton :icon="LucidePlus" label="Add plant variety" @click="openCreateForm" />
        </div>
      </div>
    </div>

    <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" class="my-4" />

    <PlantVarietyTable
      v-if="isLoading || plantVarieties.length > 0"
      :plant-varieties="plantVarieties"
      :is-loading="isLoading"
      :show-actions="true"
      @edit="openEditForm($event)"
      @delete="openDeleteDialog($event)"
    />

    <p v-else class="mt-4 text-center">There are no plant varieties recorded yet</p>

    <AppPagination :data="page" @page-changed="fetchPlantVarieties" />
  </AppSectionWrapper>
</template>

<style scoped></style>
