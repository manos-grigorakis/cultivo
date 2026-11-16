<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'
import AppErrorContainer from '@/components/ui/AppErrorContainer.vue'
import AppFormModal from '@/components/ui/form/AppFormModal.vue'
import AppInput from '@/components/ui/form/AppInput.vue'
import AppTextArea from '@/components/ui/form/AppTextArea.vue'
import { usePlantCategories } from '@/features/plant-categories/composables/usePlantCategories.ts'
import type { PlantCategoryRequest } from '@/features/plant-categories/types/plant-category-request.interface.ts'
import { useForm } from 'vee-validate'
import * as z from 'zod'

const props = defineProps<{
  mode: 'create' | 'update'
  plantCategoryId?: number
  data?: PlantCategoryRequest
}>()

const emit = defineEmits<{ close: []; success: [] }>()

const { isSubmitting, errorMessage, createPlantCategory, updatePlantCategoryById } =
  usePlantCategories()

const formSchema = z.object({
  name: z
    .string()
    .trim()
    .min(3, 'Name must be at least 3 characters')
    .max(100, 'Name must not exceed 100 characters'),
  description: z.string().trim().nullable(),
})

const { defineField, errors, handleSubmit } = useForm({
  validationSchema: formSchema,
  initialValues: {
    name: props.data?.name ?? '',
    description: props.data?.description ?? null,
  },
})

const [name] = defineField('name')
const [description] = defineField('description')

const onSubmit = handleSubmit(async (values) => {
  const payload: PlantCategoryRequest = values

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
})
</script>

<template>
  <AppFormModal
    :title="mode === 'create' ? 'Add plant category' : 'Edit plant category'"
    container-size="md"
    @close="emit('close')"
  >
    <form class="mt-6 flex flex-col gap-6" @submit.prevent="onSubmit" novalidate>
      <AppErrorContainer v-if="errorMessage" :error-message="errorMessage" />

      <AppInput
        v-model="name"
        label="name"
        placeholder="e.g. Flowers"
        required
        :error-message="errors.name"
      />

      <AppTextArea
        v-model="description"
        label="description"
        id="description"
        :error-message="errors.description"
      />

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
