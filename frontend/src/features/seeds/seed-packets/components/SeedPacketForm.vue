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
import { ref } from 'vue'

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

// Form fields
const brand = ref<string>(props.data?.brand ?? '')
const supplier = ref<string | null>(props.data?.supplier ?? null)
const status = ref<SeedPacketStatus | null>(props.data?.status ?? null)
const code = ref<string | null>(props.data?.code ?? null)

const heightCm = ref<number | null>(props.data?.heightCm ?? null)
const weightG = ref<number | null>(props.data?.weightG ?? null)
const plantSpacingCm = ref<number | null>(props.data?.plantSpacingCm ?? null)
const rowSpacingCm = ref<number | null>(props.data?.rowSpacingCm ?? null)
const sowingDepthCm = ref<number | null>(props.data?.sowingDepthCm ?? null)
const germinationDays = ref<number | null>(props.data?.germinationDays ?? null)
const startHarvestDays = ref<number | null>(props.data?.startHarvestDays ?? null)

const sowingOutdoorMonths = ref<number[]>(
  props.data?.sowingOutdoorMonths ? [...props.data.sowingOutdoorMonths] : [],
)
const sowingSeedbedMonths = ref<number[]>(
  props.data?.sowingSeedbedMonths ? [...props.data.sowingSeedbedMonths] : [],
)

const purchasedAt = ref<string | null>(props.data?.purchasedAt?.slice(0, 10) ?? null)
const selectedPlantVariety = ref<PlantReferenceResponse | null>(props.plantVariety ?? null)

const onSubmit = async () => {
  const plantVarietyId = selectedPlantVariety.value?.id

  const commonPayload = {
    brand: brand.value.trim(),
    supplier: supplier.value,
    code: code.value,
    heightCm: heightCm.value,
    weightG: weightG.value,
    plantSpacingCm: plantSpacingCm.value,
    rowSpacingCm: rowSpacingCm.value,
    sowingDepthCm: sowingDepthCm.value,
    germinationDays: germinationDays.value,
    startHarvestDays: startHarvestDays.value,
    sowingOutdoorMonths: sowingOutdoorMonths.value.length ? sowingOutdoorMonths.value : null,
    sowingSeedbedMonths: sowingSeedbedMonths.value.length ? sowingSeedbedMonths.value : null,
    purchasedAt: purchasedAt.value ? `${purchasedAt.value}T00:00:00` : null,
  }

  if (!plantVarietyId) {
    errorMessage.value = 'Plant variety ID is required'
    return
  }

  if (props.mode === 'create') {
    const payload: SeedPacketCreateRequest = {
      ...commonPayload,
      plantVarietyId: plantVarietyId,
    }

    const created = await createSeedPacket(payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (props.seedPacketId === undefined) {
      errorMessage.value = 'Seed packet ID is required'
      return
    }

    if (status.value === null) {
      errorMessage.value = 'Seed packet status is required'
      return
    }

    const payload: SeedPacketUpdateRequest = {
      ...commonPayload,
      status: status.value,
      plantVarietyId: plantVarietyId,
    }
    const updated = await updateSeedPacketById(props.seedPacketId, payload)
    if (updated) emit('success')
  }
}

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
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <!-- General -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">General</h3>
        <AppAsyncSelect
          v-model="selectedPlantVariety"
          :fetch-options="loadPlantVarieties"
          id="plant-variety"
          label="plant variety"
          option-label="name"
          placeholder="Please select plant variety"
          required
          @error="(e) => (errorMessage = e instanceof Error ? e.message : handleDefaultApiError(e))"
        />
      </div>

      <div class="grid grid-cols-1 gap-2 md:grid-cols-3">
        <AppInput v-model="brand" label="brand" placeholder="e.g. Bayer" required />
        <AppInput v-model="supplier" label="supplier" placeholder="e.g. Local Garden Center" />
        <AppInput v-model="code" label="code" placeholder="e.g. TOM-CHERRY-001" />
      </div>

      <div
        class="grid grid-cols-1 gap-2"
        :class="mode === 'update' ? 'md:grid-cols-2' : 'md:grid-cols-1'"
      >
        <AppSelect
          v-if="mode === 'update'"
          v-model="status"
          id="status"
          label="status"
          :options="statusOptions"
        />
        <AppInput v-model="purchasedAt" type="date" label="purchase date" />
      </div>

      <!-- Spacing -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">Spacing</h3>
        <div class="grid grid-cols-1 gap-2 md:grid-cols-3">
          <AppInput v-model="plantSpacingCm" label="plant spacing (cm)" placeholder="e.g. 40" />
          <AppInput v-model="rowSpacingCm" label="row spacing (cm)" placeholder="e.g. 70" />
          <AppInput v-model="sowingDepthCm" label="sowing depth (cm)" placeholder="e.g. 1.5" />
        </div>
      </div>

      <!-- Plant -->
      <div>
        <h3 class="text-content-muted mb-2 text-xs font-semibold uppercase">Plant</h3>
        <div class="grid grid-cols-1 gap-2 md:grid-cols-4">
          <AppInput v-model="heightCm" label="height (cm)" placeholder="e.g. 150.5" />
          <AppInput v-model="weightG" label="weight (g)" placeholder="e.g. 25.75" />
          <AppInput
            v-model="germinationDays"
            type="number"
            label="germination days"
            placeholder="e.g. 10"
          />

          <AppInput
            v-model="startHarvestDays"
            type="number"
            label="start harvest days"
            placeholder="e.g. 75"
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
          />

          <AppChipMultiSelect
            v-model="sowingOutdoorMonths"
            label="Outdoor Months"
            id-prefix="outdoor"
            :options="monthOptions"
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
