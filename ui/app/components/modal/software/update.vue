<script setup lang="ts">
import * as z from 'zod'
import type { FormSubmitEvent } from '@nuxt/ui'
import type { SoftwareDataTokenless } from '~/types/software'

const backend = useBackend()
const emit = defineEmits(['submit'])

const props = defineProps<{ software: SoftwareDataTokenless }>()

const schema = z.object({
  name: z.string().trim()
    .min(3, 'Must be at least 3 characters long'),
  displayName: z.string()
    .min(3, 'Must be at least 3 characters long'),
})
type Schema = z.output<typeof schema>

const state = reactive<Partial<Schema>>({
  name: props.software.name,
  displayName: props.software.display_name,
})

async function onSubmit(event: FormSubmitEvent<Schema>) {
  return backend.updateSoftware(props.software.id, {
    name: event.data.name,
    display_name: event.data.displayName
  }).then(() => {
    emit('submit')
  })
}
</script>

<template>
  <UForm
    :schema="schema"
    :state="state"
    @submit="onSubmit"
    class="flex flex-col items-center justify-center gap-4 w-full"
  >
    <div class="flex flex-col items-center justify-center gap-2 w-full">
      <UFormField label="Software Name" name="name">
        <UInput v-model="state.name" @keydown.space.prevent />
      </UFormField>
      <UFormField label="Display Name" name="name">
        <UInput v-model="state.name" />
      </UFormField>
    </div>
    <div class="flex flex-col items-end w-full">
      <UButton type="submit">
        Submit
      </UButton>
    </div>
  </UForm>
</template>

<style scoped>

</style>
