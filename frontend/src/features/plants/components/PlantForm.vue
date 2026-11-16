<script lang="ts" setup>
import AppButton from '@/components/ui/AppButton.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import { usePlantSpecies } from '@/features/plant-species/composables/usePlantSpecies.ts'
import { usePlantVarieties } from '@/features/plant-varieties/composables/usePlantVarieties.ts'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import type { PlantReferenceResponse } from '@/features/plants/types/plant-reference-response.interface.ts'
import type { PlantRequest } from '@/features/plants/types/plant-request.interface.ts'
import type { PlantResponse } from '@/features/plants/types/plant-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { useForm } from 'vee-validate'
import { watch } from 'vue'
import * as z from 'zod'
import AppErrorContainer from '../../../components/ui/AppErrorContainer.vue'

const props = defineProps<{
  mode: 'create' | 'update'
  data?: PlantResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlant, updatePlantById } = usePlants()
const {
  plantSpeciesList,
  page: speciesPage,
  errorMessage: speciesError,
  fetchPlantSpecies,
} = usePlantSpecies()
const {
  plantVarieties,
  page: varietiesPage,
  errorMessage: varietiesError,
  fetchPlantVarieties,
} = usePlantVarieties()

const formSchema = z.object({
  label: z
    .string()
    .trim()
    .min(3, 'Name must be at least 3 characters')
    .max(150, 'Name must not exceed 150 characters'),
  selectedSpecies: z
    .object({ id: z.number(), name: z.string() })
    .nullable()
    .refine((v) => v !== null, { message: 'Please select a plant species' }),
  selectedVariety: z.object({ id: z.number(), name: z.string() }).nullable(),
})

const { defineField, errors, handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    label: props.data?.label ?? '',
    selectedSpecies: props.data?.species ?? null,
    selectedVariety: props.data?.variety ?? null,
  },
})

const [label] = defineField('label')
const [selectedSpecies] = defineField('selectedSpecies')
const [selectedVariety] = defineField('selectedVariety')

const onSubmit = handleSubmit(async (values) => {
  if (!values.selectedSpecies) return

  const payload: PlantRequest = {
    label: values.label,
    plantSpeciesId: values.selectedSpecies.id,
    plantVarietyId: values.selectedVariety?.id ?? null,
  }

  if (props.mode == 'create') {
    const created = await createPlant(payload)
    if (created) emit('success')
  } else if (props.mode == 'update') {
    if (!props.data) {
      errorMessage.value = 'Plant ID is required'
      return
    }

    const updated = await updatePlantById(props.data.id, payload)
    if (updated) emit('success')
  }
})

const loadPlantSpecies = async (
  pageNumber: number,
  term: string,
): Promise<{ items: PlantReferenceResponse[]; hasNext: boolean }> => {
  await fetchPlantSpecies({ pageNumber, size: 10, name: term })
  if (speciesError.value) throw new Error(speciesError.value)

  return {
    items: plantSpeciesList.value.map(({ id, name }) => ({ id, name })),
    hasNext: pageNumber + 1 < (speciesPage.value?.totalPages ?? 0),
  }
}

const loadPlantVarieties = async (
  pageNumber: number,
  term: string,
): Promise<{ items: PlantReferenceResponse[]; hasNext: boolean }> => {
  if (!selectedSpecies.value) return { items: [], hasNext: false }

  await fetchPlantVarieties({
    pageNumber,
    size: 10,
    name: term,
    plantSpeciesId: selectedSpecies.value.id,
  })
  if (varietiesError.value) throw new Error(varietiesError.value)

  return {
    items: plantVarieties.value.map(({ id, name }) => ({ id, name })),
    hasNext: pageNumber + 1 < (varietiesPage.value?.totalPages ?? 0),
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
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <AppInput
        v-model="label"
        label="label"
        placeholder="e.g. Basil"
        required
        :error-message="errors.label"
      />

      <AppAsyncSelect
        v-model="selectedSpecies"
        :fetch-options="loadPlantSpecies"
        :error-message="errors.selectedSpecies"
        id="species"
        label="species"
        option-label="name"
        placeholder="Please select plant species"
        required
        @error="(e) => (errorMessage = e instanceof Error ? e.message : handleDefaultApiError(e))"
      />

      <AppAsyncSelect
        v-model="selectedVariety"
        :key="selectedSpecies?.id ?? 'no-species'"
        :fetch-options="loadPlantVarieties"
        :error-message="errors.selectedVariety"
        :disabled="!selectedSpecies"
        id="variety"
        label="variety"
        option-label="name"
        placeholder="Please select plant variety"
        @error="(e) => (errorMessage = e instanceof Error ? e.message : handleDefaultApiError(e))"
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
