<template>
  <slot />

  <USeparator icon="i-simple-icons-nuxtdotjs" />

  <UFooter>
    <template #left>
      <p class="text-sm text-muted">
        Built with ❤️ • © {{ new Date().getFullYear() }}
      </p>
    </template>
  </UFooter>
</template>
