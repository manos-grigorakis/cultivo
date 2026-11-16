<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { usePlantSpecies } from '@/features/plant-species/composables/usePlantSpecies.ts'
import type { PlantSpeciesResponse } from '@/features/plant-species/types/plant-species-response.interface.ts'
import { usePlantVarieties } from '@/features/plant-varieties/composables/usePlantVarieties.ts'
import type { PlantVarietyRequest } from '@/features/plant-varieties/types/plant-variety-request.interface.ts'
import type { PlantVarietyResponse } from '@/features/plant-varieties/types/plant-variety-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { useForm } from 'vee-validate'
import { computed, onMounted } from 'vue'
import * as z from 'zod'

const props = defineProps<{
  mode: 'create' | 'update'
  varietiesId?: number
  data?: PlantVarietyResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlantVariety, updatePlantVarietyById } =
  usePlantVarieties()
const {
  plantSpeciesList,
  plantSpecies,
  page: plantSpeciesPage,
  isLoading: isSpeciesLoading,
  errorMessage: speciesError,
  fetchPlantSpeciesById,
  fetchPlantSpecies,
} = usePlantSpecies()

// Form fields
const formSchema = z.object({
  name: z
    .string()
    .trim()
    .min(3, 'Name must be at least 3 characters')
    .max(150, 'Name must not exceed 150 characters'),
  description: z
    .string()
    .trim()
    .nullable()
    .transform((value) => value || null),
  selectedPlantSpecies: z
    .object({
      id: z.number(),
      name: z.string(),
    })
    .nullable()
    .refine((value) => value !== null, {
      message: 'Please select a plant species',
    }),
})

const { defineField, errors, handleSubmit, setFieldValue } = useForm({
  validationSchema: formSchema,
  initialValues: {
    name: props?.data?.name || '',
    description: props?.data?.description || null,
    selectedPlantSpecies: null as PlantSpeciesResponse | null,
  },
})

const [name] = defineField('name')
const [description] = defineField('description')
const [selectedPlantSpecies] = defineField('selectedPlantSpecies')

const onSubmit = handleSubmit(async (values) => {
  if (!values.selectedPlantSpecies) return

  const payload: PlantVarietyRequest = {
    name: values.name,
    description: values.description,
    plantSpeciesId: values.selectedPlantSpecies.id,
  }

  if (props.mode === 'create') {
    const created = await createPlantVariety(payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (!props.varietiesId) {
      errorMessage.value = 'Plant variety ID is required'
      return
    }
    const updated = await updatePlantVarietyById(props.varietiesId, payload)
    if (updated) emit('success')
  }
})

const loadPlantSpecies = async (
  pageNumber: number,
  term: string,
): Promise<{ items: PlantSpeciesResponse[]; hasNext: boolean }> => {
  await fetchPlantSpecies({ pageNumber, size: 10, name: term })

  if (speciesError.value) throw new Error(speciesError.value)

  return {
    items: plantSpeciesList.value,
    hasNext: pageNumber + 1 < (plantSpeciesPage.value?.totalPages ?? 0),
  }
}

const displayError = computed(() => errorMessage.value || speciesError.value)

onMounted(async () => {
  if (props.mode === 'update' && props.data) {
    await fetchPlantSpeciesById(props.data.plantSpeciesId)
    if (plantSpecies.value) setFieldValue('selectedPlantSpecies', plantSpecies.value)
  }
})
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant variety' : 'Edit plant variety'"
    :container-size="'md'"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="displayError" :error-message="displayError" />

      <AppInput
        v-model="name"
        label="name"
        placeholder="e.g. Curly"
        required
        :error-message="errors.name"
      />

      <AppAsyncSelect
        v-model="selectedPlantSpecies"
        :fetch-options="loadPlantSpecies"
        :initial-loading="isSpeciesLoading"
        :error-message="errors.selectedPlantSpecies"
        id="plant-species"
        label="plant species"
        option-label="name"
        placeholder="Please select plant species"
        required
        @error="(e) => (errorMessage = e instanceof Error ? e.message : handleDefaultApiError(e))"
      />

      <AppTextArea
        v-model="description"
        label="description"
        id="description"
        :error-message="errors.description"
      />

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
