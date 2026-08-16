<script setup lang="ts">
definePageMeta({
  layout: 'dashboard'
})
</script>

<template>
  <NuxtLayout>
    <UDashboardPanel>
      <template #body>
        <UPageCTA
          title="A minimalistic version manager"
          description="I don't have reason to have a proper downloads API like PaperMC/fill so I just made this"
          orientation="horizontal"
        >
          <img
            src="/assets/version_logo.svg"
            alt="Illustration"
            class="w-64 rounded-lg"
            loading="lazy"
          />
        </UPageCTA>
      </template>
    </UDashboardPanel>
  </NuxtLayout>
</template>

<style scoped>

</style>
