<script setup lang="ts">
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppInput from '@/components/ui/AppInput.vue'
import { PLANT_API_URL } from '@/constant/api.ts'
import type { PlantEventRequest } from '@/types/plant-event-request.interface.ts'
import type { PlantEventResponse } from '@/types/plant-event-response.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { LucideX } from '@lucide/vue'
import axios, { AxiosError } from 'axios'
import { reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppButton from '../../ui/AppButton.vue'

const props = defineProps<{ mode: 'create' | 'update'; plantEvent?: PlantEventResponse }>()
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
  title: props.plantEvent?.title ?? '',
  type: props.plantEvent?.type ?? '',
  description: props.plantEvent?.description ?? null,
  date: props.plantEvent?.occurredAt?.split('T')[0] ?? '',
  time: props.plantEvent?.occurredAt?.split('T')[1]?.slice(0, 5) ?? '',
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
    description: form.description?.trim() || null,
  }

  try {
    if (props.mode === 'create') await createPlantEvent(plantId, payload)
    else if (props.mode === 'update') {
      if (props.plantEvent == null) errorMessage.value = 'Plant event not found'
      else await updatePlantEvent(plantId, props.plantEvent.id, payload)
    }
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error.response?.status
      const errorCode = error.response?.data?.error?.errorCode

      if (status === 404) {
        handle404Error()
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

const createPlantEvent = async (
  plantId: string | string[] | undefined,
  payload: PlantEventRequest,
) => {
  const response = await axios.post(`${PLANT_API_URL}/${plantId}/events`, payload)
  if (response.status === 201) emit('success')
}

const updatePlantEvent = async (
  plantId: string | string[] | undefined,
  eventId: number,
  payload: PlantEventRequest,
) => {
  const response = await axios.put(`${PLANT_API_URL}/${plantId}/events/${eventId}`, payload)
  if (response.status === 200) emit('success')
}

const handle404Error = () => {
  if (props.mode === 'create') errorMessage.value = 'Plant not found'
  else if (props.mode === 'update') errorMessage.value = 'Plant or Plant Event not found'
}

const onTypeChange = (type: string) => {
  if (!form.title.trim()) form.title = type
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
      class="rounded-card bg-surface-muted w-full max-w-2xl p-6 shadow-xl"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="font-display text-primary-800 text-2xl">
            {{ props.mode === 'create' ? 'Create' : 'Update' }} a plant event
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
        <div>
          <label for="type" class="text-sm font-semibold"
            ><span class="text-red-500">*</span>Type
          </label>
          <div class="mt-1 flex gap-2 overflow-y-auto">
            <div v-for="type in types" :key="type">
              <div>
                <input
                  :id="type"
                  v-model="form.type"
                  type="radio"
                  name="type"
                  :value="type"
                  required
                  class="peer sr-only"
                  @change="onTypeChange(type)"
                />
                <label
                  :for="type"
                  class="peer-focus-visible:ring-primary-600 rounded-card peer-checked:bg-primary-600 peer-hover:bg-primary-600 inline-block cursor-pointer bg-white px-4 py-2 transition-colors peer-checked:text-white peer-hover:text-white peer-focus-visible:ring-2"
                  >{{ type }}</label
                >
              </div>
            </div>
          </div>
        </div>

        <AppInput v-model="form.title" label="title" required />

        <div>
          <div class="flex gap-2">
            <AppInput
              v-model="form.date"
              label="date"
              type="date"
              :required="props.mode === 'update'"
              class="flex-1"
            />

            <AppInput
              v-model="form.time"
              label="time"
              type="time"
              :required="props.mode === 'update'"
              class="flex-1"
            />
          </div>

          <span v-if="props.mode === 'create'" class="text-content-muted text-sm"
            >Leave date and time blank to automatically use current date</span
          >
        </div>

        <div class="flex flex-col gap-1">
          <label for="description" class="text-sm font-semibold">Description</label>
          <textarea
            id="description"
            rows="5"
            v-model="form.description"
            class="border-border rounded-card focus:ring-primary-600 w-full resize-none border bg-white px-4 py-2 focus:ring-2 focus:outline-none"
          ></textarea>
        </div>

        <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

        <div>
          <AppButton
            :label="props.mode === 'create' ? 'Save' : 'Update'"
            type="submit"
            :disabled="isSubmitting"
          />
        </div>
      </form>
    </div>
  </section>
</template>
