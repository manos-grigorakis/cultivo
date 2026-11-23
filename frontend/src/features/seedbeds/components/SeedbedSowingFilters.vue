<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppSelect from '@/components/ui/form/AppSelect.vue'
import { SeedbedSowingStatus } from '@/features/seedbeds/enums/seedbed-sowing-status.enum.ts'
import { RefreshCcw, X } from '@lucide/vue'

defineProps<{ isRefreshing: boolean }>()
const emit = defineEmits<{ refresh: [] }>()
const selectedStatusForFilter = defineModel<SeedbedSowingStatus | null>({ required: true })
</script>

<template>
  <div class="flex items-end justify-between gap-2">
    <div class="flex items-end gap-3">
      <AppSelect
        v-model="selectedStatusForFilter"
        id="status"
        label="Status"
        :options="Object.values(SeedbedSowingStatus).map((value) => ({ label: value, value }))"
      />

      <button
        v-if="selectedStatusForFilter !== null"
        type="button"
        class="text-content-muted hover:text-primary-900 flex h-10 items-center gap-1 text-sm"
        @click="selectedStatusForFilter = null"
      >
        <X :size="16" />
        Clear filters
      </button>
    </div>

    <div class="flex h-10 items-center">
      <AppButton
        :icon="RefreshCcw"
        variant="outline"
        :disabled="isRefreshing"
        :icon-class="isRefreshing ? 'animate-spin' : ''"
        @click="emit('refresh')"
      />
    </div>
  </div>
</template>

<style scoped></style>
