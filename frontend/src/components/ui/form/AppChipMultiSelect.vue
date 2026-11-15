<script setup lang="ts" generic="T extends string | number">
withDefaults(
  defineProps<{
    label: string
    idPrefix: string
    options: { label: string; value: T }[]
    required?: boolean
  }>(),
  { required: false },
)

const model = defineModel<T[]>({ default: () => [] })
</script>

<template>
  <div>
    <label class="text-sm font-semibold capitalize">
      {{ label }}<span v-if="required" class="ml-1 text-red-500">*</span>
    </label>

    <div class="mt-1 flex flex-wrap gap-2 pb-2">
      <div v-for="option in options" :key="option.value">
        <input
          :id="`${idPrefix}-${option.value}`"
          v-model="model"
          type="checkbox"
          :value="option.value"
          :required="required && model.length === 0"
          class="peer sr-only"
        />
        <label
          :for="`${idPrefix}-${option.value}`"
          class="peer-focus-visible:ring-primary-600 peer-hover:bg-primary-600 rounded-card peer-checked:bg-primary-600 inline-block cursor-pointer bg-white px-4 py-2 transition-colors peer-checked:text-white peer-hover:text-white peer-focus-visible:ring-2"
          >{{ option.label }}</label
        >
      </div>
    </div>
  </div>
</template>

<style scoped></style>
