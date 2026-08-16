import type { PrivilegedUserDetails, ExpirableToken } from '~/types/authentication'

export const useAuthenticationStore = defineStore('authentication', () => {
  // State
  const user = ref<PrivilegedUserDetails | undefined>(undefined)

  // Cookies for token storage (SSR-compatible)
  const accessToken = useCookie<ExpirableToken | undefined>('cobalt_access_token', {
    maxAge: 45 * 60, // 45 minutes
    sameSite: 'lax',
    secure: true
  })

  const refreshToken = useCookie<ExpirableToken | undefined>('cobalt_refresh_token', {
    maxAge: 24 * 60 * 60, // 1 day
    sameSite: 'lax',
    secure: true
  })

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value)

  // Actions
  function setUser(u: PrivilegedUserDetails) {
    user.value = u
  }

  function setAccessToken(token: string, expiration: number) {
    accessToken.value = {
      token,
      expiration
    }
  }

  function setRefreshToken(token: string, expiration: number) {
    refreshToken.value = {
      token,
      expiration
    }
  }

  function invalidateTokens() {
    accessToken.value = undefined
    refreshToken.value = undefined
  }

  return {
    // State
    user,
    accessToken,
    refreshToken,

    // Getters
    isAuthenticated,

    // Actions
    setUser,
    setAccessToken,
    setRefreshToken,
    invalidateTokens
  }
})
