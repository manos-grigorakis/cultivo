<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { useSeedbeds } from '@/features/seedbeds/composables/useSeedbeds.ts'
import type { SeedbedSowingRequest } from '@/features/seedbeds/types/seedbed-sowing-request.interface.ts'
import type { SeedbedSowingResponse } from '@/features/seedbeds/types/seedbed-sowing-response.interface.ts'
import { useSeedPackets } from '@/features/seeds/seed-packets/composables/useSeedPackets.ts'
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'
import { useForm } from 'vee-validate'
import { useRouter } from 'vue-router'
import * as z from 'zod'

const props = defineProps<{
  mode: 'create' | 'update'
  data?: SeedbedSowingResponse
  seedbedId: number
  slotId: number
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const router = useRouter()
const {
  isSubmitting,
  errorMessage,
  createSeedbedSowingBySlotIdAndSeedbedId,
  updateSeedbedSowingByIdAndBySlotIdAndSeedbedId,
} = useSeedbeds()

const {
  seedPackets,
  page,
  isLoading: isPacketsLoading,
  errorMessage: seedPacketsError,
  fetchSeedPackets,
} = useSeedPackets()

const formSchema = z
  .object({
    seedCount: z.number().int().positive('Seed count must be greater than 0'),
    germinatedCount: z.number().int().nonnegative('Germinated count cannot be negative').nullable(),
    transplantedCount: z
      .number()
      .int()
      .nonnegative('Transplanted count cannot be negative')
      .nullable(),
    selectedSeedPacket: z
      .object({
        id: z.number(),
        brand: z.string(),
        code: z.string().nullable(),
      })
      .nullable()
      .refine((value) => value !== null, 'Seed packet is required')
      .transform((value) => value!.id),
    sownAt: z
      .string()
      .min(1, 'Sown date is required')
      .refine((value) => {
        const date = new Date(value)
        return !Number.isNaN(date.getTime()) && date <= new Date()
      }, 'Sown date cannot be in the future'),
    notes: z
      .string()
      .trim()
      .nullable()
      .transform((value) => value || null),
  })
  .superRefine((data, ctx) => {
    if (data.germinatedCount !== null && data.germinatedCount > data.seedCount) {
      ctx.addIssue({
        code: 'custom',
        path: ['germinatedCount'],
        message: 'Germinated count cannot exceed seed count',
      })
    }

    if (data.transplantedCount !== null) {
      if (data.germinatedCount === null) {
        ctx.addIssue({
          code: 'custom',
          path: ['transplantedCount'],
          message: 'Germinated count is required when transplanted count is provided',
        })
      } else if (data.transplantedCount > data.germinatedCount) {
        ctx.addIssue({
          code: 'custom',
          path: ['transplantedCount'],
          message: 'Transplanted count cannot exceed germinated count',
        })
      }
    }
  })

const { defineField, errors, handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    seedCount: props.data?.seedCount ?? 0,
    germinatedCount: props.data?.germinatedCount ?? null,
    transplantedCount: props.data?.transplantedCount ?? null,
    selectedSeedPacket: props.data?.seedPacket ?? null,
    sownAt: props.data?.sownAt?.slice(0, 16) ?? '',
    notes: props.data?.notes ?? null,
  },
})

const [seedCount] = defineField('seedCount')
const [germinatedCount] = defineField('germinatedCount')
const [transplantedCount] = defineField('transplantedCount')
const [selectedSeedPacket] = defineField('selectedSeedPacket')
const [sownAt] = defineField('sownAt')
const [notes] = defineField('notes')

const onSubmit = handleSubmit(async (values) => {
  const { selectedSeedPacket, ...rest } = values

  const payload: SeedbedSowingRequest = {
    ...rest,
    seedPacketId: selectedSeedPacket,
  }

  if (props.mode === 'create') {
    const created = await createSeedbedSowingBySlotIdAndSeedbedId(
      props.seedbedId,
      props.slotId,
      payload,
    )

    if (created) {
      emit('success')
      await router.push({ name: 'seedbed-details', params: { id: props.seedbedId } })
    }
  } else if (props.mode === 'update' && props.data) {
    const updated = await updateSeedbedSowingByIdAndBySlotIdAndSeedbedId(
      props.data.id,
      props.seedbedId,
      props.slotId,
      payload,
    )

    if (updated) emit('success')
  }
})

const loadSeedPackets = async (
  pageNumber: number,
  term: string,
): Promise<{ items: SeedPacketResponse[]; hasNext: boolean }> => {
  await fetchSeedPackets({ pageNumber, brand: term, code: term, size: 20 })

  if (seedPacketsError.value) throw new Error(seedPacketsError.value)

  return {
    items: seedPackets.value,
    hasNext: pageNumber + 1 < (page.value?.totalPages ?? 0),
  }
}
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add sowing' : 'Edit sowing'"
    container-size="xl"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <AppAsyncSelect
        v-model="selectedSeedPacket"
        id="seed-packet"
        label="Seed packet"
        option-label="brand"
        :fetch-options="loadSeedPackets"
        :error-message="errors.selectedSeedPacket"
        placeholder="Please select a seed packet"
        required
      >
        <template #option="{ option }"> {{ option.brand }} - {{ option.code ?? '-' }} </template>

        <template #selected-option="{ option }">
          {{ option.brand }} - {{ option.code ?? '-' }}
        </template>
      </AppAsyncSelect>

      <AppInput
        v-model="sownAt"
        label="date"
        type="datetime-local"
        required
        :error-message="errors.sownAt"
      />

      <AppInput
        v-model.number="seedCount"
        label="seed count"
        placeholder="e.g. 3"
        required
        :error-message="errors.seedCount"
      />

      <div class="grid grid-cols-1 gap-2 sm:grid-cols-2">
        <AppInput
          v-model.number="germinatedCount"
          label="germinated count"
          placeholder="e.g. 2"
          :error-message="errors.germinatedCount"
        />

        <AppInput
          v-model.number="transplantedCount"
          label="transplanted count"
          placeholder="e.g. 1"
          :error-message="errors.transplantedCount"
        />
      </div>

      <AppTextArea
        v-model="notes"
        id="notes"
        label="notes"
        :rows="5"
        :error-message="errors.notes"
      />

      <div>
        <AppButton
          :disabled="isSubmitting || isPacketsLoading"
          :label="mode === 'create' ? 'Save' : 'Update'"
          type="submit"
        />
      </div>
    </form>
  </AppFormModal>
</template>

<style scoped></style>
