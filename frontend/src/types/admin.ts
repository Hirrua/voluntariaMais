import { AddressPayload } from "@/types/address";
import { OngApprovalStatus } from "@/types/ongInfo";
import { InfoUserSubscription, StatusInscricaoEnum } from "@/types/volunteer";

export interface Ong {
  id?: number;
  idUsuarioResponsavel?: number;
  nomeOng?: string;
  cnpj?: string;
  descricao?: string;
  emailContatoOng?: string;
  telefoneOng?: string;
  website?: string;
  logoUrl?: string;
  dataFundacao?: string | null;
  status?: OngApprovalStatus;
  dataCriacaoRegistro?: string;
  endereco?: AddressPayload | null;
}

export interface Projeto {
  id?: number;
  idOng?: number;
  nome?: string;
  objetivo?: string;
  descricaoDetalhada?: string;
  publicoAlvo?: string;
  urlImagemDestaque?: string | null;
  dataInicioPrevista?: string | null;
  dataFimPrevista?: string | null;
  dataAtualizacao?: string;
  endereco?: AddressPayload | null;
}

export interface ProjetoAdminDetalhe {
  id: number;
  nome: string;
  status?: string | null;
  objetivo?: string | null;
  descricaoDetalhada?: string | null;
  publicoAlvo?: string | null;
  dataInicioPrevista?: string | null;
  dataFimPrevista?: string | null;
  endereco?: AddressPayload | null;
  urlImagemDestaque?: string | null;
  dataCriacao?: string | null;
  dataAtualizacao?: string | null;
}

export interface Atividade {
  id?: number;
  idProjeto?: number;
  nomeAtividade?: string;
  descricaoAtividade?: string;
  dataHoraInicioAtividade?: string;
  dataHoraFimAtividade?: string;
  localAtividade?: string | null;
  vagasTotais?: number;
  vagasPreenchidasAtividade?: number;
  dataCriacao?: string;
  idInscricao?: number | null;
  statusInscricao?: StatusInscricaoEnum | null;
}

export interface Inscricao {
  id?: number;
  dataInscricao?: string;
  statusInscricaoEnum?: StatusInscricaoEnum;
  infoUserSubscription?: InfoUserSubscription;
}
