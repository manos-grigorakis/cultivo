<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppSelect from '@/components/ui/form/AppSelect.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { usePlantCategories } from '@/features/plant-categories/composables/usePlantCategories.ts'
import { usePlantSpecies } from '@/features/plant-species/composables/usePlantSpecies.ts'
import { LifeCycle } from '@/features/plant-species/enums/life-cycle.enum.ts'
import { LightRequirement } from '@/features/plant-species/enums/light-requirement.enum.ts'
import { WaterRequirement } from '@/features/plant-species/enums/water-requirement.enum.ts'
import type { PlantSpeciesRequest } from '@/features/plant-species/types/plant-species-request.interface.ts'
import type {
  PlantCategorySummary,
  PlantSpeciesResponse,
} from '@/features/plant-species/types/plant-species-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { useForm } from 'vee-validate'
import * as z from 'zod'

const props = defineProps<{
  mode: 'create' | 'update'
  speciesId?: number
  data?: PlantSpeciesResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlantSpecies, updatePlantSpeciesById } = usePlantSpecies()
const {
  plantCategories,
  page: plantCategoriesPage,
  errorMessage: plantCategoriesError,
  fetchPlantCategories,
} = usePlantCategories()

const formSchema = z.object({
  name: z
    .string()
    .trim()
    .min(3, 'Name must be at least 3 characters')
    .max(100, 'Name must not exceed 100 characters'),
  scientificName: z
    .string()
    .trim()
    .max(255, 'Name must not exceed 255 characters')
    .nullable()
    .transform((value) => value || null),
  selectedCategory: z
    .object({ id: z.number(), name: z.string() })
    .nullable()
    .refine((v) => v !== null, { message: 'Please select a plant category' }),
  description: z
    .string()
    .trim()
    .nullable()
    .transform((value) => value || null),
  lifeCycle: z.enum(LifeCycle).nullable(),
  lightRequirement: z.enum(LightRequirement).nullable(),
  waterRequirement: z.enum(WaterRequirement).nullable(),
})

const { defineField, errors, handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    name: props?.data?.name ?? '',
    scientificName: props?.data?.scientificName ?? null,
    selectedCategory: props.data?.plantCategory ?? null,
    description: props.data?.description ?? null,
    lifeCycle: (props.data?.lifeCycle as LifeCycle) ?? null,
    lightRequirement: (props.data?.lightRequirement as LightRequirement) ?? null,
    waterRequirement: (props.data?.waterRequirement as WaterRequirement) ?? null,
  },
})

const [name] = defineField('name')
const [scientificName] = defineField('scientificName')
const [selectedCategory] = defineField('selectedCategory')
const [description] = defineField('description')
const [lifeCycle] = defineField('lifeCycle')
const [lightRequirement] = defineField('lightRequirement')
const [waterRequirement] = defineField('waterRequirement')

const onSubmit = handleSubmit(async (values) => {
  if (!values.selectedCategory) return

  const payload: PlantSpeciesRequest = {
    name: values.name,
    scientificName: values.scientificName,
    description: values.description,
    plantCategoryId: values.selectedCategory.id,
    lifeCycle: values.lifeCycle,
    lightRequirement: values.lightRequirement,
    waterRequirement: values.waterRequirement,
  }

  if (props.mode === 'create') {
    const created = await createPlantSpecies(payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (!props.speciesId) {
      errorMessage.value = 'Plant species ID is required'
      return
    }

    const updated = await updatePlantSpeciesById(props.speciesId, payload)
    if (updated) emit('success')
  }
})

const loadPlantCategories = async (
  pageNumber: number,
  term: string,
): Promise<{ items: PlantCategorySummary[]; hasNext: boolean }> => {
  await fetchPlantCategories({ pageNumber, size: 10, name: term })

  if (plantCategoriesError.value) throw new Error(plantCategoriesError.value)

  return {
    items: plantCategories.value,
    hasNext: pageNumber + 1 < (plantCategoriesPage.value?.totalPages ?? 0),
  }
}
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant species' : 'Edit plant species'"
    :container-size="'lg'"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <div class="flex flex-col gap-2 sm:flex-row">
        <AppInput
          v-model="name"
          label="name"
          placeholder="e.g. Basil"
          required
          :error-message="errors.name"
        />
        <AppInput
          v-model="scientificName"
          label="Scientific Name"
          placeholder="e.g. Ocimum basilicum"
          :error-message="errors.scientificName"
        />
      </div>

      <AppAsyncSelect
        v-model="selectedCategory"
        :fetch-options="loadPlantCategories"
        :error-message="errors.selectedCategory"
        id="plant-category"
        label="plant category"
        option-label="name"
        placeholder="Please select plant category"
        required
        @error="(e) => (errorMessage = e instanceof Error ? e.message : handleDefaultApiError(e))"
      />

      <div class="flex flex-col gap-2 sm:flex-row">
        <AppSelect
          v-model="lightRequirement"
          :options="
            Object.values(LightRequirement).map((value) => ({ label: value, value: value }))
          "
          :error-message="errors.lightRequirement"
          id="light-requirement"
          label="light requirement"
          class="flex-1"
        />

        <AppSelect
          v-model="waterRequirement"
          :options="
            Object.values(WaterRequirement).map((value) => ({ label: value, value: value }))
          "
          :error-message="errors.waterRequirement"
          id="water-requirement"
          label="water requirement"
          class="flex-1"
        />
      </div>

      <AppSelect
        v-model="lifeCycle"
        :options="Object.values(LifeCycle).map((value) => ({ label: value, value: value }))"
        :error-message="errors.lifeCycle"
        id="life-cycle"
        label="life cycle"
      />

      <AppTextArea
        v-model="description"
        label="description"
        id="description"
        :error-message="errors.description"
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

<style scoped></style>
