import type {PrivilegedUserDetails} from "~/types/authentication";
import type {UseFetchOptions} from "nuxt/app";
import type {SoftwareDataTokenized, SoftwareDataTokenless, SoftwareUpdateRequest} from "~/types/software";
import {useAuthenticationStore} from "~/store/auth-store.ts";

export default function () {
  const runtime = useRuntimeConfig()
  const authStore = useAuthenticationStore()
  const messages = useToastMessages()
  const toast = useToast()

  const managementApi = `${runtime.public.apiBackendUrl}/management`

  const headers: HeadersInit | undefined = authStore.accessToken
    ? {
      Authorization: `Bearer ${authStore.accessToken.token}`
    }
    : undefined

  function fetchSelfUser(opts: UseFetchOptions<any> | undefined = undefined) {
    return useFetch<PrivilegedUserDetails>(`${managementApi}/privileged/self`, {
      headers,
      ...opts
    })
  }

  function fetchAllSoftware(opts: UseFetchOptions<any> | undefined = undefined) {
    return useFetch<SoftwareDataTokenless[]>(`${managementApi}/software`, {
      headers,
      ...opts
    })
  }

  function createSoftware(software: SoftwareUpdateRequest) {
    return $fetch<SoftwareDataTokenized>(`${managementApi}/software`, {
      headers,
      method: 'POST',
      body: software,
      ...messages,
      onResponse(event) {
        if (!event.error) {
          toast.add({
            title: "Done!",
            description: "Software updated successfully",
            icon: "uil:check-circle",
            color: "success",
          })
        }
      }
    })
  }

  function updateSoftware(id: number, software: SoftwareUpdateRequest) {
    return $fetch<SoftwareDataTokenless>(`${managementApi}/software/${id}`, {
      headers,
      method: 'PUT',
      body: software,
      ...messages,
      onResponse(event) {
        if (!event.error) {
          toast.add({
            title: "Done!",
            description: "Software updated successfully",
            icon: "uil:check-circle",
            color: "success",
          })
        }
      }
    })
  }

  function resetSoftwareToken(id: number) {
    return $fetch<SoftwareDataTokenized>(`${managementApi}/software/${id}/reset`, {
      headers,
      method: 'POST',
      ...messages,
      onResponse(event) {
        if (!event.error) {
          toast.add({
            title: "Done!",
            description: "Software secrets reset successfully",
            icon: "uil:check-circle",
            color: "success",
          })
        }
      }
    })
  }

  function deleteSoftware(id: number) {
    return $fetch(`${managementApi}/software/${id}`, {
      headers,
      method: 'DELETE',
      ...messages,
      onResponse(event) {
        if (!event.error) {
          toast.add({
            title: "Done!",
            description: "Software deleted successfully",
            icon: "uil:check-circle",
            color: "success",
          })
        }
      }
    })
  }

  return {
    fetchSelfUser,
    fetchAllSoftware,
    createSoftware,
    updateSoftware,
    resetSoftwareToken,
    deleteSoftware,
  }
}
