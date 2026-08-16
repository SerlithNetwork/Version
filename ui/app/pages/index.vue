<script setup lang="ts">
</script>

<template>
  <NuxtLayout>
    <div class="flex flex-col items-center justify-center h-[80vh]">
      <UPageHero
        title="Version Management"
        description="Simple software version management panel."
        :links="[{
          label: 'Get started',
          to: '/login',
          trailingIcon: 'i-lucide-arrow-right',
          size: 'xl'
        }]"
        orientation="vertical"
      />
    </div>
  </NuxtLayout>
</template>
