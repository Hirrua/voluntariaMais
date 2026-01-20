import api from "@/lib/api";
import { OngProjectAndActivityInfo } from "@/types/projectInfo";

export const projectInfoService = {
  async getProjectAndActivities(idProjeto: number): Promise<OngProjectAndActivityInfo> {
    const response = await api.get<OngProjectAndActivityInfo>(`/projetos/infos/${idProjeto}`)
    return response.data
  },
}
