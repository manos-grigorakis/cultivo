<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppAsyncSelect from '@/components/ui/form/AppAsyncSelect.vue'
import AppChipMultiSelect from '@/components/ui/form/AppChipMultiSelect.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppSelect from '@/components/ui/form/AppSelect.vue'
import { usePlantVarieties } from '@/features/plant-varieties/composables/usePlantVarieties.ts'
import type { PlantReferenceResponse } from '@/features/plants/types/plant-reference-response.interface.ts'
import { useSeedPackets } from '@/features/seeds/seed-packets/composables/useSeedPackets.ts'
import { SeedPacketStatus } from '@/features/seeds/seed-packets/enums/seed-packet-status.enum.ts'
import type { SeedPacketCreateRequest } from '@/features/seeds/seed-packets/types/seed-packet-create-request.interface.ts'
import type { SeedPacketResponse } from '@/features/seeds/seed-packets/types/seed-packet-response.interface.ts'
import type { SeedPacketUpdateRequest } from '@/features/seeds/seed-packets/types/seed-packet-update-request.interface.ts'
import { handleDefaultApiError } from '@/utils/api-error.utils.ts'
import { useForm } from 'vee-validate'
import * as z from 'zod'

const props = defineProps<{
  mode: 'create' | 'update'
  seedPacketId?: number
  data?: SeedPacketResponse
  plantVariety?: PlantReferenceResponse
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createSeedPacket, updateSeedPacketById } = useSeedPackets()
const {
  plantVarieties,
  page: varietiesPage,
  errorMessage: varietiesError,
  fetchPlantVarieties,
} = usePlantVarieties()

const monthOptions = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec',
].map((label, idx) => ({ label, value: idx + 1 }))

const statusOptions = Object.values(SeedPacketStatus).map((value) => ({ label: value, value }))

const optionalNumber = z
  .union([z.string(), z.number(), z.null()])
  .transform((value) => {
    if (value === null || (typeof value === 'string' && !value.trim())) {
      return null
    }

    return Number(value)
  })
  .pipe(z.number().finite().nonnegative('Value cannot be negative').nullable())

const optionalInteger = optionalNumber.refine(
  (value) => value === null || Number.isInteger(value),
  { message: 'Value must be an integer' },
)

const formSchema = z
  .object({
    brand: z
      .string()
      .trim()
      .min(3, 'Name must be at least 3 characters')
      .max(150, 'Name must not exceed 150 characters'),
    supplier: z.string().trim().max(150, 'Name must not exceed 150 characters').nullable(),
    status: z.enum(SeedPacketStatus).nullable(),
    code: z.string().trim().max(100, 'Name must not exceed 100 characters').nullable(),
    selectedPlantVariety: z
      .object({
        id: z.number(),
        name: z.string(),
      })
      .nullable()
      .refine((value) => value !== null, {
        message: 'Please select a plant variety',
      }),
    heightCm: optionalNumber,
    weightG: optionalNumber,
    plantSpacingCm: optionalNumber,
    rowSpacingCm: optionalNumber,
    sowingDepthCm: optionalNumber,
    germinationDays: optionalInteger,
    startHarvestDays: optionalInteger,
    sowingOutdoorMonths: z.array(z.number().int().min(1).max(12)),
    sowingSeedbedMonths: z.array(z.number().int().min(1).max(12)),
    purchasedAt: z
      .string()
      .nullable()
      .transform((value) => value || null),
  })
  .superRefine((values, ctx) => {
    if (props.mode === 'update' && values.status === null) {
      ctx.addIssue({
        code: 'custom',
        path: ['status'],
        message: 'Please select a seed packet status',
      })
    }
  })

const { defineField, errors, handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    brand: props.data?.brand ?? '',
    supplier: props.data?.supplier ?? null,
    code: props.data?.code ?? null,
    status: props.data?.status ?? null,
    selectedPlantVariety: props.plantVariety ?? null,
    heightCm: props.data?.heightCm ?? null,
    weightG: props.data?.weightG ?? null,
    plantSpacingCm: props.data?.plantSpacingCm ?? null,
    rowSpacingCm: props.data?.rowSpacingCm ?? null,
    sowingDepthCm: props.data?.sowingDepthCm ?? null,
    germinationDays: props.data?.germinationDays ?? null,
    startHarvestDays: props.data?.startHarvestDays ?? null,
    sowingOutdoorMonths: [...(props.data?.sowingOutdoorMonths ?? [])],
    sowingSeedbedMonths: [...(props.data?.sowingSeedbedMonths ?? [])],
    purchasedAt: props.data?.purchasedAt?.slice(0, 10) ?? null,
  },
})

const [brand] = defineField('brand')
const [supplier] = defineField('supplier')
const [code] = defineField('code')
const [status] = defineField('status')
const [selectedPlantVariety] = defineField('selectedPlantVariety')
const [heightCm] = defineField('heightCm')
const [weightG] = defineField('weightG')
const [plantSpacingCm] = defineField('plantSpacingCm')
const [rowSpacingCm] = defineField('rowSpacingCm')
const [sowingDepthCm] = defineField('sowingDepthCm')
const [germinationDays] = defineField('germinationDays')
const [startHarvestDays] = defineField('startHarvestDays')
const [sowingOutdoorMonths] = defineField('sowingOutdoorMonths')
const [sowingSeedbedMonths] = defineField('sowingSeedbedMonths')
const [purchasedAt] = defineField('purchasedAt')

const onSubmit = handleSubmit(async (values) => {
  if (!values.selectedPlantVariety) return

  const commonPayload = {
    brand: values.brand,
    supplier: values.supplier,
    code: values.code,
    heightCm: values.heightCm,
    weightG: values.weightG,
    plantSpacingCm: values.plantSpacingCm,
    rowSpacingCm: values.rowSpacingCm,
    sowingDepthCm: values.sowingDepthCm,
    germinationDays: values.germinationDays,
    startHarvestDays: values.startHarvestDays,
    sowingOutdoorMonths: values.sowingOutdoorMonths.length ? values.sowingOutdoorMonths : null,
    sowingSeedbedMonths: values.sowingSeedbedMonths.length ? values.sowingSeedbedMonths : null,
    purchasedAt: values.purchasedAt ? `${values.purchasedAt}T00:00:00` : null,
    plantVarietyId: values.selectedPlantVariety.id,
  }

  if (props.mode === 'create') {
    const payload: SeedPacketCreateRequest = commonPayload

    const created = await createSeedPacket(payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (props.seedPacketId === undefined) {
      errorMessage.value = 'Seed packet ID is required'
      return
    }

    if (values.status === null) return

    const payload: SeedPacketUpdateRequest = {
      ...commonPayload,
      status: values.status,
    }

    const updated = await updateSeedPacketById(props.seedPacketId, payload)
    if (updated) emit('success')
  }
})

const loadPlantVarieties = async (
  pageNumber: number,
  term: string,
): Promise<{ items: PlantReferenceResponse[]; hasNext: boolean }> => {
  await fetchPlantVarieties({ pageNumber, size: 10, name: term })
  if (varietiesError.value) throw new Error(varietiesError.value)

  return {
    items: plantVarieties.value.map(({ id, name }) => ({ id, name })),
    hasNext: pageNumber + 1 < (varietiesPage.value?.totalPages ?? 0),
  }
}
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add seed packet' : 'Edit seed packet'"
    container-size="2xl"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <!-- General -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">General</h3>
        <AppAsyncSelect
          v-model="selectedPlantVariety"
          :fetch-options="loadPlantVarieties"
          :error-message="errors.selectedPlantVariety"
          id="plant-variety"
          label="plant variety"
          option-label="name"
          placeholder="Please select plant variety"
          required
          @error="(e) => (errorMessage = e instanceof Error ? e.message : handleDefaultApiError(e))"
        />
      </div>

      <div class="grid grid-cols-1 gap-2 md:grid-cols-3">
        <AppInput
          v-model="brand"
          label="brand"
          placeholder="e.g. Bayer"
          required
          :error-message="errors.brand"
        />
        <AppInput
          v-model="supplier"
          label="supplier"
          placeholder="e.g. Local Garden Center"
          :error-message="errors.supplier"
        />
        <AppInput
          v-model="code"
          label="code"
          placeholder="e.g. TOM-CHERRY-001"
          :error-message="errors.code"
        />
      </div>

      <div
        class="grid grid-cols-1 gap-2"
        :class="mode === 'update' ? 'md:grid-cols-2' : 'md:grid-cols-1'"
      >
        <AppSelect
          v-if="mode === 'update'"
          v-model="status"
          :options="statusOptions"
          :error-message="errors.status"
          id="status"
          label="status"
        />
        <AppInput
          v-model="purchasedAt"
          type="date"
          label="purchase date"
          :error-message="errors.purchasedAt"
        />
      </div>

      <!-- Spacing -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">Spacing</h3>
        <div class="grid grid-cols-1 gap-2 md:grid-cols-3">
          <AppInput
            v-model="plantSpacingCm"
            label="plant spacing (cm)"
            placeholder="e.g. 40"
            :error-message="errors.plantSpacingCm"
          />
          <AppInput
            v-model="rowSpacingCm"
            label="row spacing (cm)"
            placeholder="e.g. 70"
            :error-message="errors.rowSpacingCm"
          />
          <AppInput
            v-model="sowingDepthCm"
            label="sowing depth (cm)"
            placeholder="e.g. 1.5"
            :error-message="errors.sowingDepthCm"
          />
        </div>
      </div>

      <!-- Plant -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">Plant</h3>
        <div class="grid grid-cols-1 gap-2 md:grid-cols-4">
          <AppInput
            v-model="heightCm"
            label="height (cm)"
            placeholder="e.g. 150.5"
            :error-message="errors.heightCm"
          />
          <AppInput
            v-model="weightG"
            label="weight (g)"
            placeholder="e.g. 25.75"
            :error-message="errors.weightG"
          />
          <AppInput
            v-model="germinationDays"
            type="number"
            label="germination days"
            placeholder="e.g. 10"
            :error-message="errors.germinationDays"
          />

          <AppInput
            v-model="startHarvestDays"
            type="number"
            label="start harvest days"
            placeholder="e.g. 75"
            :error-message="errors.startHarvestDays"
          />
        </div>
      </div>

      <!-- Sowing -->
      <div>
        <h3 class="text-content-muted mb-1 text-xs font-semibold uppercase">Sowing months</h3>
        <div class="flex flex-col space-y-2">
          <AppChipMultiSelect
            v-model="sowingSeedbedMonths"
            label="Seedbed Months"
            id-prefix="seedbed"
            :options="monthOptions"
            :error-message="errors.sowingSeedbedMonths"
          />

          <AppChipMultiSelect
            v-model="sowingOutdoorMonths"
            label="Outdoor Months"
            id-prefix="outdoor"
            :options="monthOptions"
            :error-message="errors.sowingOutdoorMonths"
          />
        </div>
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
