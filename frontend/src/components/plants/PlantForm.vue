<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '../ui/AppErrorContainer.vue'
import type { PlantRequest } from '@/types/plant-request.interface'
import type { ApiResponse } from '@/types/api-response.interface.ts'
import { LucideX } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { ref } from 'vue'
import { PLANT_API_URL } from '@/constant/api'
import AppInput from '@/components/ui/AppInput.vue'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'

const props = defineProps<{
  mode: 'create' | 'update'
  plantId?: number
  initialLabel?: string
}>()

const emit = defineEmits<{
  close: []
  success: []
}>()

const label = ref<string>(props.initialLabel ?? '')
const isSubmitting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

const onSubmit = async () => {
  const payload: PlantRequest = { label: label.value.trim() }
  isSubmitting.value = true
  errorMessage.value = null

  if (props.mode == 'create') {
    await createPlant(payload)
  } else if (props.mode == 'update') {
    if (props.plantId === undefined) {
      errorMessage.value = 'Plant ID is required'
      isSubmitting.value = false
      return
    }

    await updatePlant(props.plantId, payload)
  }
}

const createPlant = async (payload: PlantRequest) => {
  try {
    const response = await axios.post(PLANT_API_URL, payload)

    if (response.status === 201) emit('success')
  } catch (error) {
    errorMessage.value = handleDefaultApiError(error)
  } finally {
    isSubmitting.value = false
  }
}

const updatePlant = async (plantId: number, payload: PlantRequest) => {
  try {
    const response = await axios.put(`${PLANT_API_URL}/${plantId}`, payload)
    if (response.status === 200) emit('success')
  } catch (error) {
    if (error instanceof AxiosError) {
      const axiosError = error as AxiosError<ApiResponse<never>>
      const status = axiosError?.response?.status
      const errorCode = axiosError?.response?.data?.error?.errorCode

      if (status === 404) {
        errorMessage.value = `Plant not found`
        return
      } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
        errorMessage.value = 'Plant is archived and cannot be modified'
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
      role="dialog"
      aria-modal="true"
      class="rounded-card bg-surface-muted w-full max-w-md p-6 shadow-xl"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="font-display text-primary-800 text-2xl">
            {{ mode === 'create' ? 'Add plant' : 'Edit plant' }}
          </h2>
          <span class="text-content-muted text-sm">Fields marked with * are required</span>
        </div>

        <button
          type="button"
          aria-label="Close"
          class="hover:text-primary-700 hover:bg-surface rounded-full p-1 hover:cursor-pointer"
          @click="emit('close')"
        >
          <LucideX :size="20" />
        </button>
      </div>

      <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
        <AppInput v-model="label" label="label" placeholder="e.g. Basil" required />

        <div>
          <AppButton
            :label="mode === 'create' ? 'Save' : 'Update'"
            type="submit"
            :disabled="isSubmitting"
          />
        </div>
      </form>

      <div class="mt-6">
        <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />
      </div>
    </div>
  </section>
</template>
