export interface CreateActivityRequest {
  idProjeto: number;
  nomeAtividade: string;
  descricaoAtividade: string;
  dataHoraInicioAtividade: string;
  dataHoraFimAtividade: string;
  localAtividade: string;
  vagasTotais: number;
}
