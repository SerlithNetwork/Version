<script setup lang="ts">
import type { SoftwareDataTokenless } from '~/types/software'

const backend = useBackend()
const props = defineProps<{ software: SoftwareDataTokenless }>()
const emit = defineEmits(['submit'])

function onSubmit() {
  backend.deleteSoftware(props.software.id)
    .then(() => {
      emit('submit')
    })
}
</script>

<template>
  <div class="flex flex-col items-center gap-4 w-full">
    <div class="flex flex-col items-center w-full">
      <span>You're about to delete [{{ props.software.name }}]</span>
      <span>Are you sure?</span>
    </div>
    <div class="flex flex-col items-end w-full">
      <UButton @click="onSubmit">
        Confirm
      </UButton>
    </div>
  </div>
</template>

<style scoped>

</style>
