import api from "@/lib/api";
import { CreateActivityRequest } from "@/types/activity";

export const activityService = {
  async createActivity(
    payload: CreateActivityRequest
  ): Promise<{ message: string; id?: number }> {
    const response = await api.post<string>("/atividades", payload);
    const id = extractIdFromLocation(response.headers?.location);
    return { message: response.data, id };
  },
};

const extractIdFromLocation = (location?: string) => {
  if (!location) {
    return undefined;
  }
  const match = location.match(/\/(\d+)(?:\/)?$/);
  return match ? Number(match[1]) : undefined;
};
