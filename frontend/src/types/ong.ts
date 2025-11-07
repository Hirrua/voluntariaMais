export interface OngDTO {
  nomeOng: string;
  descricao: string;
  emailContatoOng: string;
  website: string;
  logoUrl: string;
  dataFundacao: string;
}

export interface OngApiResponse {
  content: OngDTO[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}
