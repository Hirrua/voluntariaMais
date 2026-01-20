export type StatusInscricaoEnum =
  | 'PENDENTE'
  | 'CONFIRMADA'
  | 'CANCELADA_PELO_VOLUNTARIO'
  | 'RECUSADA_PELA_ONG'
  | 'CONCLUIDA_PARTICIPACAO';

export interface InfoActivitySubscription {
  id: number;
  nomeAtividade: string;
}

export interface InfoUserSubscription {
  id: number;
  nome: string;
  email: string;
}

export interface SubscriptionResponse {
  id: number;
  dataInscricao: string;
  statusInscricaoEnum: StatusInscricaoEnum;
  infoUserSubscription: InfoUserSubscription;
}

export interface VolunteerSubscriptionResponse {
  id: number;
  dataInscricao: string;
  statusInscricaoEnum: StatusInscricaoEnum;
  infoActivitySubscription: InfoActivitySubscription;
}

export interface EnderecoEntity {
  logradouro: string;
  bairro: string;
  complemento: string;
  cidade: string;
  estado: string;
  cep: string;
}

export interface InfoProfileResponse {
  id: number;
  nome: string;
  sobrenome: string;
  email: string;
  bio: string;
  disponibilidade: string;
  dataNascimento: string;
  telefoneContato: string;
  fotoPerfilUrl?: string | null;
  endereco: EnderecoEntity;
}

export interface UserInfoResponse {
  id: number;
  nome: string;
  email: string;
  roles: string[];
  ongId?: number | null;
}
