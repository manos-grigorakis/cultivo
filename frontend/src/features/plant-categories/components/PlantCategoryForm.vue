<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { usePlantCategories } from '@/features/plant-categories/composables/usePlantCategories.ts'
import type { PlantCategoryRequest } from '@/features/plant-categories/types/plant-category-request.interface.ts'
import { ref } from 'vue'

const props = defineProps<{
  mode: 'create' | 'update'
  plantCategoryId?: number
  data?: PlantCategoryRequest
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlantCategory, updatePlantCategoryById } =
  usePlantCategories()

const name = ref<string>(props.data?.name ?? '')
const description = ref<string | null>(props.data?.description ?? null)

const onSubmit = async () => {
  const payload: PlantCategoryRequest = {
    name: name.value.trim(),
    description: description.value?.trim() || null,
  }

  if (props.mode === 'create') {
    const created = await createPlantCategory(payload)
    if (created) emit('success')
  } else if (props.mode === 'update') {
    if (props.plantCategoryId === undefined) {
      errorMessage.value = 'Plant category ID is required'
      return
    }

    const updated = await updatePlantCategoryById(props.plantCategoryId, payload)
    if (updated) emit('success')
  }
}
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant category' : 'Edit plant category'"
    container-size="md"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit">
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <AppInput v-model="name" label="name" placeholder="e.g. Flowers" required />
      <AppTextArea v-model="description" label="description" id="description" />

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
