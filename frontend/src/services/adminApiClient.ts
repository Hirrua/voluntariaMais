import axios, { AxiosError, AxiosResponse } from "axios";
import { Ong, Projeto, ProjetoAdminDetalhe, Atividade, Inscricao } from "@/types/admin";
import { OngProjectAndActivityInfo } from "@/types/projectInfo";
import { StatusInscricaoEnum } from "@/types/volunteer";

export type AdminApiErrorCode = "UNAUTHORIZED" | "FORBIDDEN";

export class AdminApiError extends Error {
  readonly code: AdminApiErrorCode;
  readonly status: number;

  constructor(code: AdminApiErrorCode, status: number, message?: string) {
    super(message ?? code);
    this.code = code;
    this.status = status;
  }
}

const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "";

const adminApi = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

const createAdminApiError = (status?: number) => {
  if (status === 401) {
    return new AdminApiError("UNAUTHORIZED", status);
  }

  if (status === 403) {
    return new AdminApiError("FORBIDDEN", status);
  }

  return undefined;
};

const handleAxiosError = (error: unknown): never => {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;
    const mappedError = createAdminApiError(status);

    if (mappedError) {
      throw mappedError;
    }

    throw new Error(error.message);
  }

  if (error instanceof Error) {
    throw error;
  }

  throw new Error("Erro ao comunicar com o servidor");
};

const request = async <T>(promise: Promise<AxiosResponse<T>>): Promise<T> => {
  try {
    const response = await promise;
    return response.data;
  } catch (error) {
    return handleAxiosError(error);
  }
};

const requestWithLocation = async (
  promise: Promise<AxiosResponse<string>>
): Promise<{ message: string; id?: number }> => {
  try {
    const response = await promise;
    const id = extractIdFromLocation(response.headers?.location);
    return { message: response.data, id };
  } catch (error) {
    return handleAxiosError(error);
  }
};

export const adminFetch = async (
  path: string,
  options: RequestInit = {}
) => {
  const response = await fetch(`${baseURL}${path}`, {
    ...options,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  const mappedError = createAdminApiError(response.status);
  if (mappedError) {
    throw mappedError;
  }

  return response;
};

type PaginationParams = {
  page?: number;
  itens?: number;
};

export const adminApiClient = {
  listONGs: async (params?: PaginationParams): Promise<Ong[]> => {
    return request(adminApi.get<Ong[]>("/ong/info", { params }));
  },

  getONG: async (id: number | string): Promise<Ong> => {
    return request(adminApi.get<Ong>(`/ong/info/${id}`));
  },

  createONG: async (payload: Ong): Promise<{ message: string; id?: number }> => {
    return requestWithLocation(adminApi.post<string>("/ong", payload));
  },

  updateONG: async (id: number | string, payload: Ong): Promise<Ong> => {
    return request(adminApi.put<Ong>(`/ong/${id}`, payload));
  },

  deleteONG: async (id: number | string): Promise<void> => {
    await request(adminApi.delete<void>(`/ong/${id}`));
  },

  listProjetos: async (
    params?: PaginationParams
  ): Promise<{ content?: Projeto[]; totalPages?: number; totalElements?: number; number?: number; size?: number }> => {
    return request(
      adminApi.get<{
        content?: Projeto[];
        totalPages?: number;
        totalElements?: number;
        number?: number;
        size?: number;
      }>("/projetos/infos", { params })
    );
  },

  getProjeto: async (id: number | string): Promise<OngProjectAndActivityInfo> => {
    return request(adminApi.get<OngProjectAndActivityInfo>(`/projetos/infos/${id}`));
  },

  createProjeto: async (
    payload: Projeto
  ): Promise<{ message: string; id?: number }> => {
    return requestWithLocation(adminApi.post<string>("/me/projetos", payload));
  },

  updateProjeto: async (id: number | string, payload: Projeto): Promise<Projeto> => {
    return request(adminApi.put<Projeto>(`/projetos/${id}`, payload));
  },

  deleteProjeto: async (id: number | string): Promise<string> => {
    return request(adminApi.delete<string>(`/projetos/${id}`));
  },

  listMyProjects: async (): Promise<ProjetoAdminDetalhe[]> => {
    return request(adminApi.get<ProjetoAdminDetalhe[]>("/me/projects"));
  },

  getMyProject: async (id: number | string): Promise<ProjetoAdminDetalhe> => {
    return request(adminApi.get<ProjetoAdminDetalhe>(`/me/projects/${id}`));
  },

  updateMyProject: async (
    id: number | string,
    payload: Partial<Projeto>
  ): Promise<ProjetoAdminDetalhe> => {
    return request(adminApi.patch<ProjetoAdminDetalhe>(`/me/projects/${id}`, payload));
  },

  deleteMyProject: async (id: number | string): Promise<void> => {
    await request(adminApi.delete<void>(`/me/projects/${id}`));
  },

  listMyProjectActivities: async (projectId: number | string): Promise<Atividade[]> => {
    return request(adminApi.get<Atividade[]>(`/me/projects/${projectId}/activities`));
  },

  createMyProjectActivity: async (
    projectId: number | string,
    payload: Atividade
  ): Promise<{ message: string; id?: number }> => {
    return requestWithLocation(
      adminApi.post<string>(`/me/projects/${projectId}/activities`, payload)
    );
  },

  updateMyActivity: async (
    id: number | string,
    payload: Atividade
  ): Promise<Atividade> => {
    return request(adminApi.patch<Atividade>(`/me/activities/${id}`, payload));
  },

  deleteMyActivity: async (id: number | string): Promise<void> => {
    await request(adminApi.delete<void>(`/me/activities/${id}`));
  },

  listAtividades: async (projetoId: number | string): Promise<Atividade[]> => {
    const data = await request(
      adminApi.get<OngProjectAndActivityInfo>(`/projetos/infos/${projetoId}`)
    );
    return data.simpleInfoActivityResponse ?? [];
  },

  createAtividade: async (
    payload: Atividade
  ): Promise<{ message: string; id?: number }> => {
    return requestWithLocation(adminApi.post<string>("/atividades", payload));
  },

  updateAtividade: async (id: number | string, payload: Atividade): Promise<Atividade> => {
    return request(adminApi.put<Atividade>(`/atividades/${id}`, payload));
  },

  deleteAtividade: async (id: number | string): Promise<string> => {
    return request(adminApi.delete<string>(`/atividades/${id}`));
  },

  listInscricoes: async (projetoId: number | string): Promise<Inscricao[]> => {
    const activities = await adminApiClient.listAtividades(projetoId);
    const activityIds = activities
      .map((activity) => activity.id)
      .filter((id): id is number => typeof id === "number");

    const responses = await Promise.all(
      activityIds.map((idAtividade) =>
        request(adminApi.get<Inscricao[]>(`/inscricao/${idAtividade}`))
      )
    );

    return responses.flat();
  },

  updateInscricaoStatus: async (
    id: number | string,
    status: StatusInscricaoEnum
  ): Promise<Inscricao> => {
    return request(
      adminApi.put<Inscricao>(`/inscricao/${id}`, {
        statusInscricaoEnum: status,
      })
    );
  },

  deleteInscricao: async (id: number | string): Promise<void> => {
    await request(adminApi.delete<void>(`/inscricao/${id}`));
  },
};

const extractIdFromLocation = (location?: string) => {
  if (!location) {
    return undefined;
  }
  const match = location.match(/\/(\d+)(?:\/)?$/);
  return match ? Number(match[1]) : undefined;
};
