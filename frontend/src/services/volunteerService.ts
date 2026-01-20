import api from "@/lib/api";
import axios from "axios";
import { InfoProfileResponse, UserInfoResponse, VolunteerSubscriptionResponse } from "@/types/volunteer";

export const volunteerService = {

  async getProfile(id: number): Promise<InfoProfileResponse> {
    try {
      const response = await api.get<InfoProfileResponse>(`/perfil/${id}`);
      return response.data;
    } catch (error) {
      throw new Error("Não foi possível carregar o perfil do voluntário");
    }
  },

  async getCurrentUser(): Promise<UserInfoResponse> {
    try {
      const response = await api.get<UserInfoResponse>("/perfil/users/me");
      return response.data;
    } catch (error) {
      throw new Error("Não foi possível carregar os dados do usuário");
    }
  },

  async getMySubscriptions(): Promise<VolunteerSubscriptionResponse[]> {
    try {
      const response = await api.get<VolunteerSubscriptionResponse[]>("/inscricao/me");
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const payload = error.response?.data;
        if (typeof payload === "string") {
          throw new Error(payload);
        }
        if (payload && typeof payload === "object" && "message" in payload) {
          throw new Error(String((payload as { message?: string }).message || "Não foi possível carregar as inscrições"));
        }
      }
      throw new Error("Não foi possível carregar as inscrições");
    }
  },
};
