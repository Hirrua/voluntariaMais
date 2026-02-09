"use client"

import type { ReactNode } from "react"
import { useEffect, useMemo, useState } from "react"
import Link from "next/link"
import { useParams } from "next/navigation"
import Footer from "@/components/Footer"
import Navbar from "@/components/Navbar"
import PageContainer from "@/components/PageContainer"
import { authService } from "@/services/authService"
import { ongService } from "@/services/ongService"
import { projectInfoService } from "@/services/projectInfoService"
import { subscriptionService } from "@/services/subscriptionService"
import { componentTokens, layoutTokens, typographyTokens } from "@/styles/tokens"
import {
  OngProjectAndActivityInfo,
  SimpleInfoActivityResponse,
  SubscriptionStatus,
} from "@/types/projectInfo"

type ActivityMap = Record<number, string>
type LoadingMap = Record<number, boolean>
type SubscriptionMap = Record<number, number>

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
      (responseData as { message?: string }).message ||
        "Erro ao processar a solicitacao"
    )
  }
  return "Erro ao processar a solicitacao"
}

const getActivityCapacity = (activity: SimpleInfoActivityResponse) => {
  const total = activity.vagasTotais ?? 0
  const filled = activity.vagasPreenchidasAtividade ?? 0
  return { total, filled, isFull: total > 0 && filled >= total }
}

const isActiveSubscription = (status?: SubscriptionStatus | null) =>
  status === "PENDENTE" || status === "CONFIRMADA"

export default function PainelAtividadePage() {
  const params = useParams()
  const idParam = params?.id
  const projectIdRaw = Array.isArray(idParam) ? idParam[0] : idParam
  const projectId = projectIdRaw ? Number(projectIdRaw) : Number.NaN

  const [projectInfo, setProjectInfo] =
    useState<OngProjectAndActivityInfo | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [subscriptionIds, setSubscriptionIds] = useState<SubscriptionMap>({})
  const [actionLoading, setActionLoading] = useState<LoadingMap>({})
  const [actionErrors, setActionErrors] = useState<ActivityMap>({})
  const [actionMessages, setActionMessages] = useState<ActivityMap>({})
  const [canSubscribe, setCanSubscribe] = useState(false)
  const [authHint, setAuthHint] = useState<string | null>(null)

  const activities = useMemo(
    () => projectInfo?.simpleInfoActivityResponse ?? [],
    [projectInfo]
  )

  useEffect(() => {
    const fetchProjectInfo = async () => {
      if (!Number.isFinite(projectId)) {
        setError("Projeto inválido")
        setLoading(false)
        return
      }
      try {
        setLoading(true)
        setError(null)
        const data = await projectInfoService.getProjectAndActivities(projectId)
        const ongId = data.ongContextResponse?.id
        if (ongId) {
          const ongData = await ongService.getOngWithProjects(ongId)
          if (ongData.status !== "APROVADA") {
            setProjectInfo(null)
            setError("Projeto indisponivel.")
            return
          }
        }
        setProjectInfo(data)
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setLoading(false)
      }
    }

    fetchProjectInfo()
  }, [projectId])

  useEffect(() => {
    const loadUser = async () => {
      try {
        const user = await authService.getMe()
        const isVolunteer = user.roles?.includes("ROLE_VOLUNTARIO")
        setCanSubscribe(Boolean(isVolunteer))
        setAuthHint(
          isVolunteer ? null : "Apenas voluntarios podem se inscrever."
        )
      } catch {
        setCanSubscribe(false)
        setAuthHint("Faca login para se inscrever.")
      }
    }

    loadUser()
  }, [])

  useEffect(() => {
    if (!projectInfo) {
      return
    }
    const initialSubscriptions: SubscriptionMap = {}
    projectInfo.simpleInfoActivityResponse.forEach((activity) => {
      if (activity.idInscricao && isActiveSubscription(activity.statusInscricao)) {
        initialSubscriptions[activity.id] = activity.idInscricao
      }
    })
    setSubscriptionIds(initialSubscriptions)
  }, [projectInfo])

  const handleSubscribe = async (activityId: number) => {
    if (!canSubscribe) {
      setActionErrors((prev) => ({
        ...prev,
        [activityId]: authHint || "Apenas voluntarios podem se inscrever.",
      }))
      return
    }
    setActionLoading((prev) => ({ ...prev, [activityId]: true }))
    setActionErrors((prev) => ({ ...prev, [activityId]: "" }))
    setActionMessages((prev) => ({ ...prev, [activityId]: "" }))

    try {
      const response = await subscriptionService.create(activityId)
      const createdSubscriptionId = response.id
      if (typeof createdSubscriptionId === "number") {
        setSubscriptionIds((prev) => ({
          ...prev,
          [activityId]: createdSubscriptionId,
        }))
      }
      setActionMessages((prev) => ({
        ...prev,
        [activityId]:
          response.message || "Inscricao enviada para confirmacao",
      }))
    } catch (err) {
      setActionErrors((prev) => ({ ...prev, [activityId]: getErrorMessage(err) }))
    } finally {
      setActionLoading((prev) => ({ ...prev, [activityId]: false }))
    }
  }

  const handleRevoke = async (activityId: number) => {
    const subscriptionId = subscriptionIds[activityId]
    if (!subscriptionId) {
      setActionErrors((prev) => ({
        ...prev,
        [activityId]: "Nao foi possivel cancelar a inscricao",
      }))
      return
    }

    setActionLoading((prev) => ({ ...prev, [activityId]: true }))
    setActionErrors((prev) => ({ ...prev, [activityId]: "" }))
    setActionMessages((prev) => ({ ...prev, [activityId]: "" }))

    try {
      const message = await subscriptionService.revoke(subscriptionId)
      setSubscriptionIds((prev) => {
        const next = { ...prev }
        delete next[activityId]
        return next
      })
      setActionMessages((prev) => ({
        ...prev,
        [activityId]: message || "Inscricao cancelada",
      }))
    } catch (err) {
      setActionErrors((prev) => ({ ...prev, [activityId]: getErrorMessage(err) }))
    } finally {
      setActionLoading((prev) => ({ ...prev, [activityId]: false }))
    }
  }

  if (loading) {
    return (
      <PageShell>
        <PageContainer>
          <div className="rounded-2xl border border-gray-200 bg-white p-10 text-center shadow-sm">
            <div className="mx-auto h-10 w-10 animate-spin rounded-full border-2 border-[#8F89FB] border-t-transparent" />
            <p className="mt-4 text-sm text-gray-500">Carregando projeto</p>
          </div>
        </PageContainer>
      </PageShell>
    )
  }

  if (error) {
    return (
      <PageShell>
        <PageContainer>
          <div className="rounded-2xl border border-red-200 bg-red-50 p-8 text-center text-sm text-red-600">
            {error}
          </div>
        </PageContainer>
      </PageShell>
    )
  }

  const project = projectInfo?.simpleInfoProjectResponse
  const ong = projectInfo?.ongContextResponse
  const coverImage = project?.urlImagemDestaque?.trim() || "/logo_volunteer.png"
  const ongLink = ong?.id ? `/ong/${ong.id}` : null

  return (
    <PageShell>
      <PageContainer className={layoutTokens.sectionSpacing}>
        <section>
          <div className={componentTokens.coverImageWrapper}>
            <img
              src={coverImage}
              alt={project?.nome || "Imagem do projeto"}
              className={componentTokens.coverImage}
              loading="lazy"
            />
          </div>

          <div className="mt-5 space-y-3">
            <h1 className={typographyTokens.pageTitle}>
              {project?.nome || "Projeto"}
            </h1>

            <div className="space-y-1">
              <p className={typographyTokens.detailText}>
                <span className={typographyTokens.labelText}>Publico alvo: </span>
                {project?.publicoAlvo || "-"}
              </p>
              <p className={typographyTokens.detailText}>
                <span className={typographyTokens.labelText}>ONG: </span>
                {ongLink ? (
                  <Link
                    href={ongLink}
                    className={typographyTokens.link}
                  >
                    {ong?.nomeOng}
                  </Link>
                ) : (
                  <span className="text-gray-900">{ong?.nomeOng || "-"}</span>
                )}
              </p>
            </div>

            <p className={`${typographyTokens.detailText} leading-relaxed`}>
              {project?.objetivo || "Objetivo nao informado"}
            </p>
          </div>
        </section>

        <div className={layoutTokens.divider} />

        <section className="space-y-6">
          <h2 className="sr-only">Atividades</h2>

          {authHint && (
            <p className="text-sm text-gray-500">{authHint}</p>
          )}

          {activities.length === 0 && (
            <div className={componentTokens.emptyState}>
              Nenhuma atividade cadastrada para este projeto
            </div>
          )}

          {activities.map((activity) => {
            const { total, filled, isFull } = getActivityCapacity(activity)
            const isSubscribed = Boolean(subscriptionIds[activity.id])
            const isBusy = Boolean(actionLoading[activity.id])
            const buttonLabel = isSubscribed
              ? "Cancelar inscricao"
              : "Realizar inscricao"
            const buttonStyle = isSubscribed
              ? "bg-[#FF4B4B] hover:bg-[#E63E3E]"
              : "bg-[#8F89FB] hover:bg-[#2A2599]"
            const location = activity.localAtividade?.trim() || "Nao informado"

            return (
              <article
                key={activity.id}
                className="border-b border-[#D9DAF3] pb-6 last:border-b-0"
              >
                <div className="space-y-3">
                  <h3 className={typographyTokens.activityTitle}>
                    {activity.nomeAtividade}
                  </h3>

                  <div className="space-y-1 text-sm text-gray-600">
                    <p>
                      Data de inicio: {formatDate(activity.dataHoraInicioAtividade)}
                    </p>
                    <p>
                      Data de termino: {formatDate(activity.dataHoraFimAtividade)}
                    </p>
                    <p>Endereco: {location}</p>
                  </div>

                  <p className={`${typographyTokens.detailText} leading-relaxed`}>
                    {activity.descricaoAtividade || "Descricao nao informada"}
                  </p>

                  <p className="text-sm font-semibold text-gray-900">
                    Numero total de vagas: {filled}/{total}
                  </p>

                  {isFull && !isSubscribed && (
                    <p className="text-sm font-semibold text-[#FF4B4B]">
                      Vagas esgotadas
                    </p>
                  )}
                </div>

                <div className="mt-4 flex flex-wrap items-center gap-4">
                  <button
                    type="button"
                    onClick={() =>
                      isSubscribed
                        ? handleRevoke(activity.id)
                        : handleSubscribe(activity.id)
                    }
                    disabled={isBusy || (!isSubscribed && isFull) || (!isSubscribed && !canSubscribe)}
                    className={`rounded-md px-5 py-1.5 text-sm font-semibold text-white transition-colors disabled:cursor-not-allowed disabled:opacity-60 ${buttonStyle}`}
                  >
                    {isBusy ? "Processando" : buttonLabel}
                  </button>

                  {actionMessages[activity.id] && (
                    <p className="text-sm text-emerald-600">
                      {actionMessages[activity.id]}
                    </p>
                  )}
                  {actionErrors[activity.id] && (
                    <p className="text-sm text-[#FF4B4B]">
                      {actionErrors[activity.id]}
                    </p>
                  )}
                </div>
              </article>
            )
          })}
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
