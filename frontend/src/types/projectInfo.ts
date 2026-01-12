export type SimpleInfoProjectResponse = {
  id: number | null,
  nome: string,
  objetivo: string,
  publicoAlvo: string,
  urlImagemDestaque: string | null,
}

export type OngContextResponse = {
  id: number,
  nomeOng: string,
  emailContatoOng: string,
  telefoneOng: string,
  dataFundacao: string,
}

export type SubscriptionStatus =
  | "PENDENTE"
  | "CONFIRMADA"
  | "CANCELADA_PELO_VOLUNTARIO"
  | "RECUSADA_PELA_ONG"
  | "CONCLUIDA_PARTICIPACAO"

export type SimpleInfoActivityResponse = {
  id: number,
  nomeAtividade: string,
  descricaoAtividade: string,
  dataHoraInicioAtividade: string,
  dataHoraFimAtividade: string,
  localAtividade?: string | null,
  vagasTotais: number,
  vagasPreenchidasAtividade: number,
  dataCriacao: string,
  idInscricao?: number | null,
  statusInscricao?: SubscriptionStatus | null,
}

export type OngProjectAndActivityInfo = {
  simpleInfoProjectResponse: SimpleInfoProjectResponse,
  ongContextResponse: OngContextResponse,
  simpleInfoActivityResponse: SimpleInfoActivityResponse[],
}
