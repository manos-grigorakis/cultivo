<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppSelect from '@/components/ui/form/AppSelect.vue'
import AppSwitch from '@/components/ui/form/AppSwitch.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import { SeedbedLocationType } from '@/features/seedbeds/enums/seedbed-location-type.enum.ts'
import type { SeedbedResponse } from '@/features/seedbeds/types/seedbed-response.interface.ts'
import { useForm } from 'vee-validate'
import * as z from 'zod'

type SeedbedFormValues = z.input<typeof createSchema>

const props = defineProps<{
  mode: 'create' | 'update'
  data?: SeedbedResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createSeedbed, updateSeedbedById } = useSeedbeds()

const optionalNumber = z
  .union([z.string(), z.number(), z.null()])
  .transform((value) => {
    if (value === null || (typeof value === 'string' && !value.trim())) {
      return null
    }

    return Number(value)
  })
  .pipe(z.number().finite().nonnegative('Value cannot be negative').nullable())

const createSchema = z.object({
  name: z
    .string()
    .trim()
    .min(3, 'Name must be at least 3 characters')
    .max(100, 'Name must not exceed 100 characters'),
  locationType: z.enum(SeedbedLocationType),
  rowsCount: z
    .number({
      error: 'Rows count must be a number',
    })
    .int()
    .positive(),

  columnsCount: z
    .number({
      error: 'Columns count must be a number',
    })
    .int()
    .positive(),
  lengthCm: optionalNumber,
  widthCm: optionalNumber,
  soilDepthCm: optionalNumber,
  hasEnclosure: z.boolean(),
  hasHumidityControl: z.boolean(),
  hasAutomaticWatering: z.boolean(),
  notes: z
    .string()
    .trim()
    .nullable()
    .transform((value) => value || null),
})

const updateSchema = createSchema.omit({
  rowsCount: true,
  columnsCount: true,
})

const { defineField, errors, handleSubmit } = useForm<SeedbedFormValues>({
  validationSchema: props.mode === 'create' ? createSchema : updateSchema,
  initialValues: {
    name: props.data?.name ?? '',
    locationType: props.data?.locationType,
    rowsCount: props.data?.rowsCount,
    columnsCount: props.data?.columnsCount,
    lengthCm: props.data?.lengthCm ?? null,
    widthCm: props.data?.widthCm ?? null,
    soilDepthCm: props.data?.soilDepthCm ?? null,
    hasEnclosure: props.data?.hasEnclosure ?? false,
    hasHumidityControl: props.data?.hasHumidityControl ?? false,
    hasAutomaticWatering: props.data?.hasAutomaticWatering ?? false,
    notes: props.data?.notes ?? null,
  },
})

const [name] = defineField('name')
const [locationType] = defineField('locationType')
const [rowsCount] = defineField('rowsCount')
const [columnsCount] = defineField('columnsCount')
const [lengthCm] = defineField('lengthCm')
const [widthCm] = defineField('widthCm')
const [soilDepthCm] = defineField('soilDepthCm')
const [hasEnclosure] = defineField('hasEnclosure')
const [hasHumidityControl] = defineField('hasHumidityControl')
const [hasAutomaticWatering] = defineField('hasAutomaticWatering')
const [notes] = defineField('notes')

const onSubmit = handleSubmit(async (values) => {
  if (props.mode === 'create') {
    const payload = createSchema.parse(values)

    const created = await createSeedbed(payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (props.data?.id == null) {
      errorMessage.value = 'Seedbed ID is required'
      return
    }

    const payload = updateSchema.parse(values)
    const updated = await updateSeedbedById(props.data.id, payload)
    if (updated) emit('success')
  }
})
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add seedbed' : 'Edit seedbed'"
    container-size="xl"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <!-- General -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">General</h3>
        <div class="grid grid-cols-1 gap-2 md:grid-cols-2">
          <AppInput
            v-model="name"
            label="name"
            placeholder="e.g. Seedbed #1"
            required
            :error-message="errors.name"
          />

          <AppSelect
            v-model="locationType"
            :options="
              Object.values(SeedbedLocationType).map((value) => ({ label: value, value: value }))
            "
            :error-message="errors.locationType"
            id="location-type"
            label="location type"
          />
        </div>
      </div>

      <!-- Size -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">Size</h3>

        <div class="flex flex-col gap-4">
          <div class="grid grid-cols-1 gap-2 md:grid-cols-2">
            <AppInput
              v-model.number="rowsCount"
              type="number"
              label="rows count"
              placeholder="e.g. 3"
              required
              :disabled="mode === 'update'"
              :error-message="errors.rowsCount"
            />

            <AppInput
              v-model.number="columnsCount"
              type="number"
              label="columns count"
              placeholder="e.g. 2"
              required
              :disabled="mode === 'update'"
              :error-message="errors.columnsCount"
            />
          </div>

          <div class="grid grid-cols-1 gap-2 md:grid-cols-3">
            <AppInput
              v-model="lengthCm"
              label="length (cm)"
              placeholder="e.g. 20.00"
              :error-message="errors.lengthCm"
            />

            <AppInput
              v-model="widthCm"
              label="width (cm)"
              placeholder="e.g. 5.00"
              :error-message="errors.widthCm"
            />

            <AppInput
              v-model="soilDepthCm"
              label="soil depth (cm)"
              placeholder="e.g. 0.1"
              :error-message="errors.soilDepthCm"
            />
          </div>
        </div>
      </div>

      <!-- Features -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">Features</h3>

        <div class="grid grid-cols-1 gap-3">
          <AppSwitch
            v-model="hasEnclosure"
            label="Enclosure"
            description="Protective enclosure for the seedbed"
            :error-message="errors.hasEnclosure"
          />

          <AppSwitch
            v-model="hasHumidityControl"
            label="Humidity control"
            description="Humidity regulation capability"
            :error-message="errors.hasHumidityControl"
          />

          <AppSwitch
            v-model="hasAutomaticWatering"
            label="Automatic watering"
            description="Automated watering system"
            :error-message="errors.hasAutomaticWatering"
          />
        </div>
      </div>

      <div class="flex flex-col gap-1">
        <AppTextArea
          v-model="notes"
          id="notes"
          label="notes"
          :rows="5"
          :error-message="errors.notes"
        />
      </div>

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
