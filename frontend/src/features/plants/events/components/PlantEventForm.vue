<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppFormError from '@/components/ui/form/AppFormError.vue'
import AppFormLabel from '@/components/ui/form/AppFormLabel.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { usePlants } from '@/features/plants/composables/usePlants.ts'
import type { PlantEventRequest } from '@/features/plants/events/types/plant-event-request.interface.ts'
import type { PlantEventResponse } from '@/features/plants/events/types/plant-event-response.interface.ts'
import { useForm } from 'vee-validate'
import { useRoute } from 'vue-router'
import * as z from 'zod'

const props = defineProps<{ mode: 'create' | 'update'; plantEvent?: PlantEventResponse }>()
const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlantEvent, updatePlantEventById } = usePlants()
const plantId = Number(useRoute().params.id)

const eventTypes = ['WATERING', 'HARVEST', 'REPOTTING', 'OBSERVATION', 'NOTE'] as const

const formSchema = z
  .object({
    title: z
      .string()
      .trim()
      .min(3, 'Title must be at least 3 characters')
      .max(200, 'Title must not exceed 200 characters'),
    type: z.enum(eventTypes, { error: 'Please select an event type' }),
    description: z
      .string()
      .trim()
      .nullable()
      .transform((value) => value || null),
    date: z.string(),
    time: z.string(),
  })
  .superRefine((values, ctx) => {
    if (props.mode === 'update' || values.date || values.time) {
      if (!/^\d{4}-\d{2}-\d{2}$/.test(values.date)) {
        ctx.addIssue({
          code: 'custom',
          path: ['date'],
          message: 'Please select a valid date',
        })
      }

      if (!/^\d{2}:\d{2}$/.test(values.time)) {
        ctx.addIssue({
          code: 'custom',
          path: ['time'],
          message: 'Please select a valid time',
        })
      }
    }
  })

const { defineField, errors, handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    title: props.plantEvent?.title ?? '',
    type: props.plantEvent?.type ?? '',
    description: props.plantEvent?.description ?? null,
    date: props.plantEvent?.occurredAt?.split('T')[0] ?? '',
    time: props.plantEvent?.occurredAt?.split('T')[1]?.slice(0, 5) ?? '',
  },
})

const [title] = defineField('title')
const [type] = defineField('type')
const [description] = defineField('description')
const [date] = defineField('date')
const [time] = defineField('time')

const onSubmit = handleSubmit(async (values) => {
  const payload: PlantEventRequest = {
    title: values.title,
    type: values.type,
    description: values.description,
    occurredAt: values.date && values.time ? `${values.date}T${values.time}:00` : null,
  }

  if (props.mode === 'create') {
    const created = await createPlantEvent(plantId, payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (props.plantEvent == null) errorMessage.value = 'Plant event not found'
    else {
      const updated = await updatePlantEventById(plantId, props.plantEvent.id, payload)
      if (updated) emit('success')
    }
  }
})

const onTypeChange = (selectedType: string) => {
  if (!title.value?.trim()) title.value = selectedType
}
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Create a plant event' : 'Update a plant event'"
    container-size="2xl"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <div>
        <div id="event-type-label">
          <AppFormLabel id="type" label="type" required />
        </div>

        <div
          class="mt-1 flex gap-2 overflow-y-auto"
          :aria-invalid="!!errors.type"
          aria-labelledby="event-type-label"
          role="radiogroup"
        >
          <div v-for="eventType in eventTypes" :key="eventType">
            <input
              v-model="type"
              :id="`event-${eventType}`"
              type="radio"
              name="type"
              :value="eventType"
              required
              class="peer sr-only"
              @change="onTypeChange(eventType)"
            />
            <label
              :for="`event-${eventType}`"
              class="peer-focus-visible:ring-primary-600 rounded-card peer-checked:bg-primary-600 peer-hover:bg-primary-600 inline-block cursor-pointer bg-white px-4 py-2 transition-colors peer-checked:text-white peer-hover:text-white peer-focus-visible:ring-2"
            >
              {{ eventType }}
            </label>
          </div>
        </div>
        <AppFormError id="type-error" :message="errors.type" />
      </div>

      <AppInput v-model="title" label="title" required :error-message="errors.title" />

      <div>
        <div class="flex flex-col gap-2 sm:flex-row">
          <AppInput
            v-model="date"
            :required="props.mode === 'update'"
            :error-message="errors.date"
            label="date"
            type="date"
            class="flex-1"
          />

          <AppInput
            v-model="time"
            :required="props.mode === 'update'"
            :error-message="errors.time"
            label="time"
            type="time"
            class="flex-1"
          />
        </div>

        <span v-if="props.mode === 'create'" class="text-content-muted text-sm"
          >Leave date and time blank to automatically use current date</span
        >
      </div>

      <div class="flex flex-col gap-1">
        <AppTextArea
          v-model="description"
          id="description"
          label="description"
          :rows="5"
          :error-message="errors.description"
        />
      </div>

      <div>
        <AppButton
          :label="props.mode === 'create' ? 'Save' : 'Update'"
          type="submit"
          :disabled="isSubmitting"
        />
      </div>
    </form>
  </AppFormModal>
</template>
