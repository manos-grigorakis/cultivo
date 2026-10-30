<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppSelect from '@/components/ui/form/AppSelect.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { PLANT_CATEGORY_API_URL, PLANT_SPECIES_API_URL } from '@/constant/api.ts'
import { LifeCycle } from '@/enums/life-cycle.enum.ts'
import { LightRequirement } from '@/enums/light-requirement.enum.ts'
import { WaterRequirement } from '@/enums/water-requirement.enum.ts'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import type { Pagination } from '@/types/pagination.interface.ts'
import type { PlantCategoryResponse } from '@/types/plant-category-response.interface.ts'
import type { PlantSpeciesRequest } from '@/types/plant-species-request.interface.ts'
import type {
  PlantCategorySummary,
  PlantSpeciesResponse,
} from '@/types/plant-species-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucideX } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

const props = defineProps<{
  mode: 'create' | 'update'
  speciesId?: number
  data?: PlantSpeciesResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const isSubmitting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

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

  isSubmitting.value = true
  errorMessage.value = null

  const payload: PlantSpeciesRequest = {
    name: name.value,
    scientificName: scientificName.value,
    description: description.value !== null ? description.value.trim() : null,
    lifeCycle: lifeCycle.value !== null ? lifeCycle.value : null,
    lightRequirement: lightRequirement.value !== null ? lightRequirement.value : null,
    waterRequirement: waterRequirement.value !== null ? waterRequirement.value : null,
    plantCategoryId: selectedCategory.value.id,
  }

  try {
    if (props.mode === 'create') await createPlantSpecies(payload)
    else if (props.mode === 'update') {
      if (!props.speciesId) return

      await updatePlantSpeciesById(props.speciesId, payload)
    }
  } catch (error) {
    if (error instanceof AxiosError) {
      switch (error?.response?.status) {
        case 404:
          errorMessage.value = 'Plant species or category not found'
          break
        case 409:
          errorMessage.value = 'Name or Scientific name already exists'
          break
        default:
          errorMessage.value = handleDefaultApiError(error)
      }
    }
  } finally {
    isSubmitting.value = false
  }
}

const createPlantSpecies = async (payload: PlantSpeciesRequest) => {
  const response = await axios.post<PlantSpeciesRequest>(PLANT_SPECIES_API_URL, payload)
  if (response.status === 201) emit('success')
}

const updatePlantSpeciesById = async (speciesId: number, payload: PlantSpeciesRequest) => {
  const response = await axios.put(`${PLANT_SPECIES_API_URL}/${speciesId}`, payload)
  if (response.status === 200) emit('success')
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
  <section
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    @click.self="emit('close')"
  >
    <div
      aria-modal="true"
      class="rounded-card bg-surface-muted w-full max-w-lg p-6 shadow-xl"
      role="dialog"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="font-display text-primary-800 text-2xl">
            {{ mode === 'create' ? 'Add plant species' : 'Edit plant species' }}
          </h2>
          <span class="text-content-muted text-sm">Fields marked with * are required</span>
        </div>

        <button
          aria-label="Close"
          class="hover:text-primary-700 hover:bg-surface rounded-full p-1 hover:cursor-pointer"
          type="button"
          @click="emit('close')"
        >
          <LucideX :size="20" />
        </button>
      </div>

      <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
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

      <div class="mt-6">
        <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />
      </div>
    </div>
  </section>
</template>

<style scoped></style>
