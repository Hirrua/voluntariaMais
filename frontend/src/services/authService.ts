import api from "@/lib/api";
import { LoginRequest, LoginResponse } from "@/types/auth";
import { UserInfoResponse } from "@/types/volunteer";

export const authService = {

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>("/auth/login", credentials);
    return response.data;
  },

  async logout(): Promise<void> {
    await api.post("/logout")
  },

  async getMe(): Promise<UserInfoResponse> {
    const response = await api.get<UserInfoResponse>("/perfil/users/me");
    return response.data;
  },

  async checkAuth(): Promise<boolean> {
    try {
      await api.get("/perfil/users/me");
      return true;
    } catch (error) {
      return false;
    }
  },
};
