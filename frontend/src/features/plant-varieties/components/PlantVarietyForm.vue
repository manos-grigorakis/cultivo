<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { PLANT_SPECIES_API_URL } from '@/constant/api.ts'
import { usePlantSpecies } from '@/features/plant-species/composables/usePlantSpecies.ts'

import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import { usePlantVarieties } from '@/features/plant-varieties/composables/usePlantVarieties.ts'
import type { PlantVarietyRequest } from '@/features/plant-varieties/types/plant-variety-request.interface.ts'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios from 'axios'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  mode: 'create' | 'update'
  varietiesId?: number
  data?: PlantVarietyResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlantVariety, updatePlantVarietyById } =
  usePlantVarieties()
const {
  plantSpecies,
  isLoading: isSpeciesLoading,
  errorMessage: speciesError,
  fetchPlantSpeciesById,
} = usePlantSpecies()

// Form fields
const name = ref<string>(props?.data?.name || '')
const description = ref<string | null>(props?.data?.description || null)
const selectedPlantSpecies = ref<PlantSpeciesResponse | null>(null)

const onSubmit = async () => {
  if (selectedPlantSpecies.value === null) {
    errorMessage.value = 'Please select a plant species'
    return
  }

  const payload: PlantVarietyRequest = {
    name: name.value,
    description: description.value !== null ? description.value.trim() : null,
    plantSpeciesId: selectedPlantSpecies.value.id,
  }

  if (props.mode === 'create') {
    const created = await createPlantVariety(payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (!props.varietiesId) return
    const updated = await updatePlantVarietyById(props.varietiesId, payload)
    if (updated) emit('success')
  }
}

const fetchPlantSpecies = async (
  page: number,
  term: string,
): Promise<{ items: PlantSpeciesResponse[]; hasNext: boolean }> => {
  const { data } = await axios.get<ApiResponse<Pagination<PlantSpeciesResponse>>>(
    PLANT_SPECIES_API_URL,
    {
      params: { page, size: 10, name: term },
    },
  )

  return { items: data.data.content, hasNext: page + 1 < data.data.page.totalPages }
}

const displayError = computed(() => errorMessage.value || speciesError.value)

onMounted(async () => {
  if (props.mode === 'update' && props.data) {
    await fetchPlantSpeciesById(props.data.plantSpeciesId)
    selectedPlantSpecies.value = plantSpecies.value
  }
})
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant variety' : 'Edit plant variety'"
    :container-size="'md'"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
      <AppErrorContainer v-if="displayError" :error-message="displayError" />

      <AppInput v-model="name" label="name" placeholder="e.g. Curly" required />

      <AppAsyncSelect
        v-model="selectedPlantSpecies"
        :fetch-options="fetchPlantSpecies"
        :initial-loading="isSpeciesLoading"
        id="plant-species"
        label="plant species"
        option-label="name"
        placeholder="Please select plant species"
        required
        @error="(e) => (errorMessage = handleDefaultApiError(e))"
      />

      <AppTextArea v-model="description" label="description" id="description" />

      <div>
        <AppButton
          :disabled="isSubmitting || isSpeciesLoading"
          :label="mode === 'create' ? 'Save' : 'Update'"
          type="submit"
        />
      </div>
    </form>
  </AppFormModal>
</template>

<style scoped></style>
