import { AddressPayload } from "./address";

export interface ProjectDTO {
  id: number;
  nome: string;
  objetivo: string;
  publicoAlvo: string;
  urlImagemDestaque: string;
}

export interface CreateProjectRequest {
  idOng: number;
  nome: string;
  descricaoDetalhada: string;
  objetivo: string;
  publicoAlvo: string;
  dataInicioPrevista: string | null;
  dataFimPrevista: string | null;
  endereco: AddressPayload;
  urlImagemDestaque?: string | null;
}
