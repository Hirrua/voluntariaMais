import { AddressPayload } from "./address";

export interface OngDTO {
  nomeOng: string;
  descricao: string;
  emailContatoOng: string;
  website: string;
  logoUrl: string;
  dataFundacao: string;
}

export interface UpdateOngRequest {
  nomeOng: string;
  descricao: string;
  emailContatoOng: string;
  telefoneOng?: string;
  website?: string;
  logoUrl?: string;
}

export interface CreateOngRequest {
  idUsuarioResponsavel: number;
  nomeOng: string;
  cnpj: string;
  descricao: string;
  emailContatoOng: string;
  telefoneOng: string;
  website: string;
  dataFundacao: string | null;
  endereco: AddressPayload;
}

export interface OngApiResponse {
  content: OngDTO[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}
