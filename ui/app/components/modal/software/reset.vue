<script setup lang="ts">
import type {SoftwareDataTokenized, SoftwareDataTokenless} from '~/types/software'

type Emits = {
  submit: [SoftwareDataTokenized]
}


const backend = useBackend()
const props = defineProps<{ software: SoftwareDataTokenless }>()
const emit = defineEmits<Emits>()

function onSubmit() {
  backend.resetSoftwareToken(props.software.id)
    .then((result) => {
      emit('submit', result)
    })
}
</script>

<template>
  <div class="flex flex-col items-center gap-4 w-full">
    <div class="flex flex-col items-center w-full">
      <span>You're about to reset the API token for [{{ props.software.name }}]</span>
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
