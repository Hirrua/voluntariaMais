import api from "@/lib/api";
import { InfoProfileResponse, UserInfoResponse } from "@/types/volunteer";

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
};
