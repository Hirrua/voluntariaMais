import api from "@/lib/api";
import { CreateOngRequest, OngDTO } from "@/types/ong";
import { OngInfoWithProjects } from "@/types/ongInfo";

interface GetOngsParams {
  page: number;
  itens: number;
}

interface GetOngsResponse {
  content: OngDTO[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

export const ongService = {
  /**
   * Busca informações das ONGs com paginação
   * @param page - Número da página (começando em 1)
   * @param itens - Quantidade de itens por página
   * @returns Promise com a lista de ONGs
   */
  async getOngs({ page, itens }: GetOngsParams): Promise<GetOngsResponse> {
    const response = await api.get<GetOngsResponse>("/ong/info", {
      params: {
        page,
        itens,
      },
    });
    return response.data;
  },

  /**
   * Busca informações de uma ONG específica
   * @param id - ID da ONG
   * @returns Promise com os dados da ONG
   */
  async getOngById(id: string): Promise<OngDTO> {
    const response = await api.get<OngDTO>(`/ong/info/${id}`);
    return response.data;
  },

  async getOngWithProjects(id: number): Promise<OngInfoWithProjects> {
    const response = await api.get<OngInfoWithProjects>(`/ong/info/about/${id}`)
    return response.data
  },

  async createOng(payload: CreateOngRequest): Promise<{ message: string; id?: number }> {
    const response = await api.post<string>("/ong", payload);
    const id = extractIdFromLocation(response.headers?.location);
    return { message: response.data, id };
  },

  async uploadOngLogo(ongId: number, file: File): Promise<string> {
    const formData = new FormData();
    formData.append("file", file);
    const response = await api.post<string>(`/ong/${ongId}/photo`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },
};

const extractIdFromLocation = (location?: string) => {
  if (!location) {
    return undefined;
  }
  const match = location.match(/\/(\d+)(?:\/)?$/);
  return match ? Number(match[1]) : undefined;
};
