<script setup lang="ts">
import type { PlantEventRequest } from '@/types/plant-event-request.interface.ts'
import { LucideX } from '@lucide/vue'
import { PLANT_API_URL } from '@/constant/api.ts'
import { reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import axios, { AxiosError } from 'axios'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppButton from '../ui/AppButton.vue'

const emit = defineEmits<{ close: []; success: [] }>()

const route = useRoute()
const plantId = route.params.id

const form = reactive<{
  title: string
  type: string
  description: string | null
  date: string
  time: string
}>({
  title: '',
  type: '',
  description: null,
  date: '',
  time: '',
})

const types = ['WATERING', 'HARVEST', 'REPOTTING', 'OBSERVATION', 'NOTE']

const isSubmitting = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

const onSubmit = async () => {
  isSubmitting.value = true
  errorMessage.value = null

  const payload: PlantEventRequest = {
    title: form.title,
    type: form.type,
    occurredAt: form.date ? `${form.date}T${form.time || '00:00'}:00` : null,
    description: form.description,
  }

  try {
    const response = await axios.post(`${PLANT_API_URL}/${plantId}/events`, payload)

    if (response.status === 201) emit('success')
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error.response?.status
      const errorCode = error.response?.data?.error?.errorCode

      if (error.status === 404) {
        errorMessage.value = 'Plant does not exist'
      } else if (status === 409 && errorCode === 'PLANT_ARCHIVED') {
        errorMessage.value = 'Plant is archived and cannot be modified'
      } else {
        errorMessage.value = 'Server error. Please try again'
      }
    }
  } finally {
    isSubmitting.value = false
  }
}

const onTypeChange = (type: string) => {
  if (!form.title.trim()) form.title = type
}
</script>

<template>
  <section
    class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40"
    @click.self="emit('close')"
  >
    <div
      role="dialog"
      aria-modal="true"
      class="w-full max-w-2xl p-6 shadow-xl rounded-card bg-surface-muted"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="text-2xl font-display text-primary-800">Create a plant event</h2>
          <span class="text-sm text-content-muted">Fields marked with * are required</span>
        </div>

        <button
          type="button"
          aria-label="Close"
          class="p-1 rounded-full hover:cursor-pointer hover:text-primary-700 hover:bg-surface"
          @click="emit('close')"
        >
          <LucideX :size="20" />
        </button>
      </div>

      <form class="flex flex-col gap-6 mt-6" @submit.prevent="onSubmit">
        <div>
          <label for="type" class="text-sm font-semibold"
            ><span class="text-red-500">*</span>Type
          </label>
          <div class="flex gap-2 mt-1 overflow-y-auto">
            <div v-for="type in types" :key="type">
              <div>
                <input
                  :id="type"
                  v-model="form.type"
                  type="radio"
                  name="type"
                  :value="type"
                  required
                  class="sr-only peer"
                  @change="onTypeChange(type)"
                />
                <label
                  :for="type"
                  class="inline-block px-4 py-2 transition-colors bg-white cursor-pointer peer-focus-visible:ring-2 peer-focus-visible:ring-primary-600 rounded-card peer-checked:bg-primary-600 peer-hover:bg-primary-600 peer-hover:text-white peer-checked:text-white"
                  >{{ type }}</label
                >
              </div>
            </div>
          </div>
        </div>

        <div class="flex flex-col gap-1">
          <label for="title" class="text-sm font-semibold"
            ><span class="text-red-500">*</span>Title
          </label>
          <input
            type="text"
            id="title"
            required
            class="w-full px-4 py-2 bg-white border border-border rounded-card focus:outline-none focus:ring-2 focus:ring-primary-600"
            v-model="form.title"
          />
        </div>

        <div>
          <div class="flex gap-2">
            <div class="flex-1">
              <label for="date" class="text-sm font-semibold">Date </label>
              <input
                type="date"
                id="date"
                v-model="form.date"
                class="w-full px-4 py-2 bg-white border border-border rounded-card focus:outline-none focus:ring-2 focus:ring-primary-600"
              />
            </div>

            <div class="flex-1">
              <label for="time" class="text-sm font-semibold">Time</label>
              <input
                type="time"
                id="time"
                v-model="form.time"
                class="w-full px-4 py-2 bg-white border border-border rounded-card focus:outline-none focus:ring-2 focus:ring-primary-600"
              />
            </div>
          </div>

          <span class="text-sm text-content-muted"
            >Leave date and time blank to automatically use current date</span
          >
        </div>

        <div class="flex flex-col gap-1">
          <label for="description" class="text-sm font-semibold">Description</label>
          <textarea
            id="description"
            rows="5"
            v-model="form.description"
            class="w-full px-4 py-2 bg-white border resize-none border-border rounded-card focus:outline-none focus:ring-2 focus:ring-primary-600"
          ></textarea>
        </div>

        <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

        <div>
          <AppButton label="Save" type="submit" :disabled="isSubmitting" />
        </div>
      </form>
    </div>
  </section>
</template>
