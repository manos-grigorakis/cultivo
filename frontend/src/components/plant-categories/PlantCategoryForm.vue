<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextArea from '@/components/ui/AppTextArea.vue'
import { PLANT_CATEGORY_API_URL } from '@/constant/api.ts'
import type { PlantCategoryRequest } from '@/types/plant-category-request.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucideX } from '@lucide/vue'
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
  <section
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    @click.self="emit('close')"
  >
    <div
      aria-modal="true"
      class="rounded-card bg-surface-muted w-full max-w-md p-6 shadow-xl"
      role="dialog"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="font-display text-primary-800 text-2xl">
            {{ mode === 'create' ? 'Add plant category' : 'Edit plant category' }}
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

      <div class="mt-6">
        <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />
      </div>
    </div>
  </section>
</template>

<style scoped></style>
