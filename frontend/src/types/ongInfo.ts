import { AddressPayload } from "./address"
import { ProjectDTO } from "./project"

export type OngApprovalStatus = "PENDENTE" | "APROVADA" | "REJEITADA"

export type OngInfoWithProjects = {
  nomeOng: string
  descricao?: string | null
  emailContatoOng: string
  telefoneOng?: string | null
  website?: string | null
  logoUrl?: string | null
  dataFundacao?: string | null
  status: OngApprovalStatus
  dataCriacaoRegistro: string
  endereco?: AddressPayload | null
  projectResponse: ProjectDTO[]
}
