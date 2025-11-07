import api from "@/lib/api";

interface GetProjectsResponse {
    content: any[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
}

export const projectService = {
    async getProjects({ page, itens }: { page: number; itens: number; }): Promise<GetProjectsResponse> {
        const response = await api.get<GetProjectsResponse>("/projetos/infos", {
            params: {
                page,
                itens,
            },
        });
        return response.data;
    }
}