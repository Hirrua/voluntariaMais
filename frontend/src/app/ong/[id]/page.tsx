"use client"

import type { ReactNode } from "react"
import { useEffect, useMemo, useState } from "react"
import { useParams } from "next/navigation"
import Footer from "@/components/Footer"
import Navbar from "@/components/Navbar"
import PageContainer from "@/components/PageContainer"
import { ongService } from "@/services/ongService"
import { componentTokens, layoutTokens, typographyTokens } from "@/styles/tokens"
import { OngApprovalStatus, OngInfoWithProjects } from "@/types/ongInfo"

const formatDate = (value?: string | null) => {
  if (!value) {
    return "-"
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return "-"
  }
  return date.toLocaleDateString("pt-BR")
}

const getStatusStyle = (status: OngApprovalStatus) => {
  switch (status) {
    case "APROVADA":
      return "bg-[#8F89FB] text-white"
    case "REJEITADA":
      return "bg-red-500 text-white"
    default:
      return "bg-amber-400 text-white"
  }
}

const getStatusLabel = (status: OngApprovalStatus) => {
  switch (status) {
    case "APROVADA":
      return "Aprovada"
    case "REJEITADA":
      return "Rejeitada"
    default:
      return "Pendente"
  }
}

const getErrorMessage = (err: unknown) => {
  if (typeof err === "string") {
    return err
  }
  if (err instanceof Error) {
    return err.message
  }
  const apiError = err as { response?: { data?: unknown } }
  const responseData = apiError?.response?.data
  if (typeof responseData === "string") {
    return responseData
  }
  if (
    responseData &&
    typeof responseData === "object" &&
    "message" in responseData
  ) {
    return String(
      (responseData as { message?: string }).message || "Erro ao carregar ONG"
    )
  }
  return "Erro ao carregar ONG"
}

export default function PainelOngPage() {
  const params = useParams()
  const idParam = params?.id
  const ongIdRaw = Array.isArray(idParam) ? idParam[0] : idParam
  const ongId = ongIdRaw ? Number(ongIdRaw) : Number.NaN

  const [ongInfo, setOngInfo] = useState<OngInfoWithProjects | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchOngInfo = async () => {
      if (!Number.isFinite(ongId)) {
        setError("ONG inválida")
        setLoading(false)
        return
      }
      try {
        setLoading(true)
        setError(null)
        const data = await ongService.getOngWithProjects(ongId)
        setOngInfo(data)
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setLoading(false)
      }
    }

    fetchOngInfo()
  }, [ongId])

  const projects = useMemo(() => {
    if (!ongInfo || ongInfo.status !== "APROVADA") {
      return []
    }
    return ongInfo.projectResponse ?? []
  }, [ongInfo])

  if (loading) {
    return (
      <PageShell>
        <PageContainer>
          <div className="rounded-2xl border border-gray-200 bg-white p-10 text-center shadow-sm">
            <div className="mx-auto h-10 w-10 animate-spin rounded-full border-2 border-[#8F89FB] border-t-transparent" />
            <p className="mt-4 text-sm text-gray-500">Carregando ONG</p>
          </div>
        </PageContainer>
      </PageShell>
    )
  }

  if (error) {
    return (
      <PageShell>
        <PageContainer>
          <div className="rounded-2xl border border-red-200 bg-red-50 p-8 text-sm text-red-600 shadow-sm">
            {error}
          </div>
        </PageContainer>
      </PageShell>
    )
  }

  const coverImage = ongInfo?.logoUrl?.trim() || "/logo_volunteer.png"
  const statusStyle = ongInfo?.status
    ? getStatusStyle(ongInfo.status)
    : "bg-gray-200 text-gray-700"
  const statusLabel = ongInfo?.status
    ? getStatusLabel(ongInfo.status)
    : "Status"
  const phone = ongInfo?.telefoneOng?.trim() || "-"
  const website = ongInfo?.website?.trim() || "-"

  return (
    <PageShell>
      <PageContainer className={layoutTokens.sectionSpacing}>
        <section>
          <div className={componentTokens.coverImageWrapper}>
            <img
              src={coverImage}
              alt={ongInfo?.nomeOng || "Imagem da ONG"}
              className={componentTokens.coverImage}
              loading="lazy"
            />
          </div>

          <div className="mt-5 space-y-3">
            <h1 className={typographyTokens.pageTitle}>
              {ongInfo?.nomeOng || "ONG"}
            </h1>

            <div className="space-y-1">
              <p className={typographyTokens.detailText}>
                <span className={typographyTokens.labelText}>Data de fundacao: </span>
                {formatDate(ongInfo?.dataFundacao)}
              </p>
              <p className={typographyTokens.detailText}>
                <span className={typographyTokens.labelText}>Data de cadastro: </span>
                {formatDate(ongInfo?.dataCriacaoRegistro)}
              </p>
            </div>

            <span
              className={`inline-flex w-fit items-center rounded-md px-3 py-1 text-xs font-semibold ${statusStyle}`}
            >
              {statusLabel}
            </span>

            <div className="flex flex-wrap gap-6">
              <span className={typographyTokens.detailText}>
                <span className={typographyTokens.labelText}>E-mail: </span>
                {ongInfo?.emailContatoOng || "-"}
              </span>
              <span className={typographyTokens.detailText}>
                <span className={typographyTokens.labelText}>Telefone: </span>
                {phone}
              </span>
              <span className={typographyTokens.detailText}>
                <span className={typographyTokens.labelText}>WWW: </span>
                {website}
              </span>
            </div>

            <p className={`${typographyTokens.detailText} leading-relaxed`}>
              {ongInfo?.descricao || "Descricao nao informada"}
            </p>
          </div>
        </section>

        <div className={layoutTokens.divider} />

        <section>
          <h2 className={typographyTokens.sectionTitle}>Projetos</h2>

          {projects.length === 0 ? (
            <div className={componentTokens.emptyState}>
              Nenhum projeto cadastrado para esta ONG
            </div>
          ) : (
            <div className="mt-4 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {projects.map((project) => {
                const projectImage =
                  project.urlImagemDestaque?.trim() || "/logo_volunteer.png"
                return (
                  <article key={project.id}>
                    <div className="overflow-hidden rounded-lg">
                      <img
                        src={projectImage}
                        alt={project.nome}
                        className="h-20 w-full object-cover"
                        loading="lazy"
                      />
                    </div>
                    <div className="mt-3 space-y-1">
                      <h3 className="text-sm font-semibold text-[#2A2599]">
                        {project.nome}
                      </h3>
                      <p className={`${typographyTokens.detailText} leading-relaxed`}>
                        {project.objetivo || project.publicoAlvo || "Descricao nao informada"}
                      </p>
                    </div>
                  </article>
                )
              })}
            </div>
          )}
        </section>
      </PageContainer>
    </PageShell>
  )
}

function PageShell({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <main className={layoutTokens.pageShellMain}>{children}</main>
      <Footer />
    </div>
  )
}
