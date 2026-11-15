<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppSelect from '@/components/ui/form/AppSelect.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { PLANT_CATEGORY_API_URL } from '@/constant/api.ts'
import type { PlantCategoryResponse } from '@/features/plant-categories/types/plant-category-response.interface.ts'
import { usePlantSpecies } from '@/features/plant-species/composables/usePlantSpecies.ts'
import { LifeCycle } from '@/features/plant-species/enums/life-cycle.enum.ts'
import { LightRequirement } from '@/features/plant-species/enums/light-requirement.enum.ts'
import { WaterRequirement } from '@/features/plant-species/enums/water-requirement.enum.ts'
import type { PlantSpeciesRequest } from '@/features/plant-species/types/plant-species-request.interface.ts'
import type {
  PlantCategorySummary,
  PlantSpeciesResponse,
} from '@/features/plant-species/types/plant-species-response.interface.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Pagination } from '@/types/pagination.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios from 'axios'
import { ref } from 'vue'

const props = defineProps<{
  mode: 'create' | 'update'
  speciesId?: number
  data?: PlantSpeciesResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlantSpecies, updatePlantSpeciesById } = usePlantSpecies()

const name = ref<string>(props?.data?.name ?? '')
const scientificName = ref<string | null>(props?.data?.scientificName ?? null)
const selectedCategory = ref<PlantCategorySummary | null>(props.data?.plantCategory ?? null)
const description = ref<string | null>(props?.data?.description ?? null)
const lifeCycle = ref<LifeCycle | null>((props?.data?.lifeCycle as LifeCycle) ?? null)
const lightRequirement = ref<LightRequirement | null>(
  (props?.data?.lightRequirement as LightRequirement) ?? null,
)
const waterRequirement = ref<WaterRequirement | null>(
  (props?.data?.waterRequirement as WaterRequirement) ?? null,
)

const onSubmit = async () => {
  if (selectedCategory.value === null) {
    errorMessage.value = 'Please select a plant category'
    return
  }

  const payload: PlantSpeciesRequest = {
    name: name.value,
    scientificName: scientificName.value,
    description: description.value !== null ? description.value.trim() : null,
    lifeCycle: lifeCycle.value !== null ? lifeCycle.value : null,
    lightRequirement: lightRequirement.value !== null ? lightRequirement.value : null,
    waterRequirement: waterRequirement.value !== null ? waterRequirement.value : null,
    plantCategoryId: selectedCategory.value.id,
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
}

const fetchPlantCategories = async (
  page: number,
  term: string,
): Promise<{ items: PlantCategorySummary[]; hasNext: boolean }> => {
  const { data } = await axios.get<ApiResponse<Pagination<PlantCategoryResponse>>>(
    PLANT_CATEGORY_API_URL,
    { params: { page, size: 10, name: term } },
  )

  return {
    items: data.data.content,
    hasNext: page + 1 < data.data.page.totalPages,
  }
}
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant species' : 'Edit plant species'"
    :container-size="'lg'"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <div class="flex flex-col gap-2 sm:flex-row">
        <AppInput v-model="name" label="name" placeholder="e.g. Basil" required />
        <AppInput
          v-model="scientificName"
          label="Scientific Name"
          placeholder="e.g. Ocimum basilicum"
        />
      </div>

      <AppAsyncSelect
        v-model="selectedCategory"
        :fetch-options="fetchPlantCategories"
        id="plant-category"
        label="plant category"
        option-label="name"
        placeholder="Please select plant category"
        required
        @error="(e) => (errorMessage = handleDefaultApiError(e))"
      />

      <div class="flex flex-col gap-2 sm:flex-row">
        <AppSelect
          v-model="lightRequirement"
          :options="
            Object.values(LightRequirement).map((value) => ({ label: value, value: value }))
          "
          id="light-requirement"
          label="light requirement"
          class="flex-1"
        />

        <AppSelect
          v-model="waterRequirement"
          :options="
            Object.values(WaterRequirement).map((value) => ({ label: value, value: value }))
          "
          id="water-requirement"
          label="water requirement"
          class="flex-1"
        />
      </div>

      <AppSelect
        v-model="lifeCycle"
        :options="Object.values(LifeCycle).map((value) => ({ label: value, value: value }))"
        id="life-cycle"
        label="life cycle"
      />

      <AppTextArea v-model="description" label="description" id="description" />

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
