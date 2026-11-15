<script lang="ts" setup>
import AppButton from '@/components/ui/AppButton.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import { PLANT_SPECIES_API_URL, PLANT_VARIETIES_API_URL } from '@/constant/api.ts'
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import type { PlantReferenceResponse } from '@/features/plants/types/plant-reference-response.interface.ts'
import type { PlantRequest } from '@/features/plants/types/plant-request.interface.ts'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios from 'axios'
import { ref, watch } from 'vue'
import AppErrorContainer from '../../../components/ui/AppErrorContainer.vue'

const props = defineProps<{
  mode: 'create' | 'update'
  data?: PlantResponse
}>()

const emit = defineEmits<{
  close: []
  success: []
}>()

const { isSubmitting, errorMessage, createPlant, updatePlantById } = usePlants()
const label = ref<string>(props.data?.label ?? '')
const selectedSpecies = ref<PlantReferenceResponse | null>(props.data?.species ?? null)
const selectedVariety = ref<PlantReferenceResponse | null>(props.data?.variety ?? null)

const onSubmit = async () => {
  if (!label.value.trim() || !selectedSpecies.value) {
    errorMessage.value = 'Please fill the required fields'
    return
  }

  const payload: PlantRequest = {
    label: label.value.trim(),
    plantSpeciesId: selectedSpecies.value.id,
    plantVarietyId: selectedVariety.value?.id ?? null,
  }

  isSubmitting.value = true
  errorMessage.value = null

  if (props.mode == 'create') {
    const created = await createPlant(payload)
    if (created) emit('success')
  } else if (props.mode == 'update') {
    if (!props.data) {
      errorMessage.value = 'Plant ID is required'
      isSubmitting.value = false
      return
    }

    const updated = await updatePlantById(props.data.id, payload)
    if (updated) emit('success')
  }
}

const fetchPlantSpecies = async (
  page: number,
  term: string,
): Promise<{ items: PlantReferenceResponse[]; hasNext: boolean }> => {
  const { data } = await axios.get<ApiResponse<Pagination<PlantSpeciesResponse>>>(
    PLANT_SPECIES_API_URL,
    {
      params: { page, size: 10, name: term },
    },
  )

  return {
    items: data.data.content.map(({ id, name }) => ({ id, name })),
    hasNext: page + 1 < data.data.page.totalPages,
  }
}

const fetchPlantVarieties = async (
  page: number,
  term: string,
): Promise<{ items: PlantReferenceResponse[]; hasNext: boolean }> => {
  if (!selectedSpecies.value) return { items: [], hasNext: false }

  const { data } = await axios.get<ApiResponse<Pagination<PlantVarietyResponse>>>(
    PLANT_VARIETIES_API_URL,
    {
      params: { page, size: 10, name: term, plantSpeciesId: selectedSpecies.value.id },
    },
  )

  return {
    items: data.data.content.map(({ id, name }) => ({ id, name })),
    hasNext: page + 1 < data.data.page.totalPages,
  }
}

watch(selectedSpecies, (newSpecies, oldSpecies) => {
  if (newSpecies?.id !== oldSpecies?.id) selectedVariety.value = null
})
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant' : 'Edit plant'"
    container-size="md"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <AppInput v-model="label" label="label" placeholder="e.g. Basil" required />

      <AppAsyncSelect
        v-model="selectedSpecies"
        :fetch-options="fetchPlantSpecies"
        id="species"
        label="species"
        option-label="name"
        placeholder="Please select plant species"
        required
        @error="(e) => (errorMessage = handleDefaultApiError(e))"
      />

      <AppAsyncSelect
        :key="selectedSpecies?.id ?? 'no-species'"
        v-model="selectedVariety"
        :fetch-options="fetchPlantVarieties"
        :disabled="!selectedSpecies"
        id="variety"
        label="variety"
        option-label="name"
        placeholder="Please select plant variety"
        @error="(e) => (errorMessage = handleDefaultApiError(e))"
      />

      <div>
        <AppButton
          :disabled="isSubmitting"
          :label="mode === 'create' ? 'Save' : 'Update'"
          type="submit"
        />
      </div>
    </form>
  </AppFormModal>
</template>
