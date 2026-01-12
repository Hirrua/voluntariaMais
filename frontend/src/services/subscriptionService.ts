import api from "@/lib/api";

type SubscriptionResponse = {
  message: string,
  id?: number,
}

export const subscriptionService = {
  async create(idAtividade: number): Promise<SubscriptionResponse> {
    const response = await api.post<string>(`/inscricao/${idAtividade}`)
    const id = extractIdFromLocation(response.headers?.location)
    return { message: response.data, id }
  },

  async revoke(idSubscription: number): Promise<string> {
    const response = await api.delete<string>(`/inscricao/revoke/${idSubscription}`)
    return response.data
  },
}

const extractIdFromLocation = (location?: string) => {
  if (!location) {
    return undefined
  }
  const match = location.match(/\/(\d+)(?:\/)?$/)
  return match ? Number(match[1]) : undefined
}
