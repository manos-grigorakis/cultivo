<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '../ui/AppErrorContainer.vue'
import type { PlantRequest } from '@/types/plant-request.interface'
import { LucideX } from '@lucide/vue'
import axios from 'axios'
import { ref } from 'vue'

const emit = defineEmits<{
  closePlantFormClick: []
  plantCreated: []
}>()

const label = ref<string>('')
const isSubmitting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

const onSubmit = async () => {
  const payload: PlantRequest = { label: label.value.trim() }
  isSubmitting.value = true
  errorMessage.value = null

  try {
    const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/v1/plants`, payload)

    if (response.status === 201) emit('plantCreated')
  } catch (error) {
    console.error(error)
    errorMessage.value = 'Error while creating plant. Please try again'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <section
    class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40"
    @click.self="emit('closePlantFormClick')"
  >
    <div
      role="dialog"
      aria-modal="true"
      class="w-full max-w-md p-6 shadow-xl rounded-card bg-surface-muted"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="text-2xl font-display text-primary-800">Add plant</h2>
          <span class="text-sm text-content-muted">Fields marked with * are required</span>
        </div>

        <button
          type="button"
          aria-label="Close"
          class="p-1 rounded-full hover:cursor-pointer hover:text-primary-700 hover:bg-surface"
          @click="emit('closePlantFormClick')"
        >
          <LucideX :size="20" />
        </button>
      </div>

      <form class="flex flex-col gap-6 mt-6" @submit.prevent="onSubmit">
        <div class="flex flex-col gap-1">
          <label for="label" class="text-sm font-semibold"
            ><span class="text-red-500">*</span>Label
          </label>
          <input
            type="text"
            id="label"
            required
            class="w-full px-4 py-2 bg-white border border-border rounded-card focus:outline-none focus:ring-2 focus:ring-primary-600"
            placeholder="e.g. Basil"
            v-model="label"
          />
        </div>

        <div>
          <AppButton label="Save" type="submit" :disabled="isSubmitting" />
        </div>
      </form>

      <div class="mt-6">
        <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />
      </div>
    </div>
  </section>
</template>
