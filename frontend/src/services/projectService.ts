import api from "@/lib/api";
import { CreateProjectRequest } from "@/types/project";

interface GetProjectsResponse {
    content: any[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
}

export const projectService = {
  async getProjects({
    page,
    itens,
  }: {
    page: number;
    itens: number;
  }): Promise<GetProjectsResponse> {
    const response = await api.get<GetProjectsResponse>("/projetos/infos", {
      params: {
        page,
        itens,
      },
    });
    return response.data;
  },

  async createProject(
    payload: CreateProjectRequest
  ): Promise<{ message: string; id?: number }> {
    const response = await api.post<string>("/me/projetos", payload);
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
