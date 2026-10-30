<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { PLANT_CATEGORY_API_URL } from '@/constant/api.ts'
import type { PlantCategoryRequest } from '@/features/plant-categories/types/plant-category-request.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'

const props = defineProps<{
  mode: 'create' | 'update'
  plantCategoryId?: number
  data?: PlantCategoryRequest
}>()

const emit = defineEmits<{
  close: []
  success: []
}>()

const name = ref<string>(props.data?.name ?? '')
const description = ref<string | null>(props.data?.description ?? null)
const isSubmitting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

const onSubmit = async () => {
  const payload: PlantCategoryRequest = {
    name: name.value.trim(),
    description: description.value?.trim() || null,
  }
  isSubmitting.value = true
  errorMessage.value = null

  if (props.mode === 'create') {
    await createPlantCategory(payload)
  } else if (props.mode === 'update') {
    if (props.plantCategoryId === undefined) {
      errorMessage.value = 'Plant category ID is required'
      isSubmitting.value = false
      return
    }

    await updatePlantCategory(props.plantCategoryId, payload)
  }
}

const createPlantCategory = async (payload: PlantCategoryRequest) => {
  try {
    const res = await axios.post(PLANT_CATEGORY_API_URL, payload)
    if (res.status === 201) emit('success')
  } catch (error) {
    if (error instanceof AxiosError) {
      if (error.response?.status === 409) {
        errorMessage.value = `Plant category with name: ${payload.name} already exists`
        return
      }
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isSubmitting.value = false
  }
}

const updatePlantCategory = async (plantCategoryId: number, payload: PlantCategoryRequest) => {
  try {
    const res = await axios.put(`${PLANT_CATEGORY_API_URL}/${plantCategoryId}`, payload)
    if (res.status === 200) emit('success')
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error?.response?.status

      if (status === 404) {
        errorMessage.value = 'Plant category not found'
        return
      } else if (status === 409) {
        errorMessage.value = `Plant category with name: ${payload.name} already exists`
        return
      }
    }

    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant category' : 'Edit plant category'"
    container-size="md"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <AppInput v-model="name" label="name" placeholder="e.g. Flowers" required />

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
