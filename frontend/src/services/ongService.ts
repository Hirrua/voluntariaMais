import api from "@/lib/api";
import { OngDTO } from "@/types/ong";

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
};
