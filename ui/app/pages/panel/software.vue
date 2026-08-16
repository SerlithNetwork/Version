<script setup lang="ts">
import type { TableColumn } from '#ui/components/Table.vue'
import type {SoftwareDataTokenized, SoftwareDataTokenless} from '~/types/software'

const backend = useBackend()
const { data: servers, status, refresh } = await backend.fetchAllSoftware({
  key: 'table-servers',
  transform(data: SoftwareDataTokenless[]) {
    return data || []
  },
  lazy: true
})

const filter = ref('')
const columns: TableColumn<SoftwareDataTokenless>[] = [
  {
    accessorKey: 'name',
    header: 'Key'
  },
  {
    accessorKey: 'display',
    header: 'Software Name'
  },
  {
    id: 'token',
    header: 'Token',
    cell: ({ row }) => {
      return `${row.original.id.toString(16).padStart(8, '0')}.******`
    }
  },
  {
    accessorKey: 'created_at',
    header: 'Creation Date',
    cell: ({ row }) => {
      return new Date(row.original.created_at).toLocaleString('en-US', {
        day: 'numeric',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      })
    }
  },
  {
    id: 'actions',
    header: 'Actions'
  }
]

const isCreateModalShown = ref(false)
const isUpdateModalShown = ref(false)
const isDeleteModalShown = ref(false)
const isResetModalShown = ref(false)
const selectedSoftware = ref<SoftwareDataTokenless>()

const isSecretModalShown = ref(false)
const secretSoftware = ref<SoftwareDataTokenized>()

function triggerUpdateModal(software: SoftwareDataTokenless) {
  selectedSoftware.value = software
  isUpdateModalShown.value = true
}

function triggerDeleteModal(software: SoftwareDataTokenless) {
  selectedSoftware.value = software
  isDeleteModalShown.value = true
}

function triggerResetModal(software: SoftwareDataTokenless) {
  selectedSoftware.value = software
  isResetModalShown.value = true
}

function onSubmitDisplaySecret(software: SoftwareDataTokenized) {
  onSubmitGeneric()
  secretSoftware.value = software
  isSecretModalShown.value = true
}

function onSubmitGeneric() {
  isCreateModalShown.value = false
  isUpdateModalShown.value = false
  isDeleteModalShown.value = false
  isResetModalShown.value = false
  refresh()
}

definePageMeta({
  layout: 'dashboard'
})
</script>

<template>
  <NuxtLayout>
    <div class="flex flex-col w-full h-full">
      <div class="flex justify-between px-4 py-3.5 border-b border-accented">
        <UInput
          v-model="filter"
          placeholder="Filter..."
          class="max-w-sm"
        />
        <UModal
          v-model:open="isCreateModalShown"
          title="Register Software"
        >
          <UButton icon="i-lucide-plus" />
          <template #body>
            <div class="flex items-center justify-center">
              <ModalSoftwareCreate @submit="onSubmitGeneric" />
            </div>
          </template>
        </UModal>
      </div>
      <UTable
        v-model:global-filter="filter"
        :data="servers || []"
        :columns="columns"
        :loading="status === 'pending'"
        class="flex-1"
      >
        <template #actions-cell="{ row }">
          <UButton icon="i-lucide-rotate-ccw-key" color="neutral" variant="ghost" class="ml-auto" @click="triggerResetModal(row.original)" />
          <UButton icon="i-lucide-pencil-line" color="neutral" variant="ghost" class="ml-auto" @click="triggerUpdateModal(row.original)" />
          <UButton icon="i-lucide-trash-2" color="neutral" variant="ghost" class="ml-auto" @click="triggerDeleteModal(row.original)" />
        </template>
      </UTable>
    </div>
    <UModal title="Update Software Details" v-model:open="isUpdateModalShown">
      <template #body>
        <div class="flex items-center justify-center">
          <ModalSoftwareUpdate :software="selectedSoftware!" @submit="onSubmitGeneric" />
        </div>
      </template>
    </UModal>
    <UModal title="Delete Software" v-model:open="isDeleteModalShown">
      <template #body>
        <div class="flex items-center justify-center">
          <ModalSoftwareDelete :software="selectedSoftware!" @submit="onSubmitGeneric" />
        </div>
      </template>
    </UModal>
    <UModal title="Reset Downloader User Credentials" v-model:open="isResetModalShown">
      <template #body>
        <div class="flex items-center justify-center">
          <ModalSoftwareReset :software="selectedSoftware!" @submit="onSubmitDisplaySecret" />
        </div>
      </template>
    </UModal>
    <UModal title="Secret" v-model:open="isSecretModalShown" :ui="{ content: 'md:max-w-3xl' }">
      <template #body>
        <div class="flex items-center justify-center">
          <ModalSecretDisplay :target="secretSoftware!" name-field="name" secret-field="token" />
        </div>
      </template>
    </UModal>
  </NuxtLayout>
</template>

<style scoped>

</style>
