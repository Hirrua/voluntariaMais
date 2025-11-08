import api from "@/lib/api";
import { InfoProfileResponse } from "@/types/volunteer";

export const volunteerService = {

  async getProfile(id: number): Promise<InfoProfileResponse> {
    try {
      const response = await api.get<InfoProfileResponse>(`/perfil/${id}`);
      return response.data;
    } catch (error) {
      throw new Error("Não foi possível carregar o perfil do voluntário");
    }
  },

  async getCurrentUser(): Promise<{ id: number; nome: string; email: string }> {
    try {
      const response = await api.get("/perfil/users/me");
      return response.data;
    } catch (error) {
      throw new Error("Não foi possível carregar os dados do usuário");
    }
  },
};