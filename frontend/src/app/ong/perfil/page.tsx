"use client"

import type { ChangeEvent, FormEvent, ReactNode } from "react"
import { useEffect, useMemo, useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import Footer from "@/components/Footer"
import FormField from "@/components/FormField"
import PageContainer from "@/components/PageContainer"
import Navbar from "@/components/Navbar"
import { isAdminOng } from "@/lib/roles"
import { authService } from "@/services/authService"
import { ongService } from "@/services/ongService"
import { componentTokens, layoutTokens, typographyTokens } from "@/styles/tokens"
import type { UpdateOngRequest } from "@/types/ong"
import { OngApprovalStatus, OngInfoWithProjects } from "@/types/ongInfo"

type EditFormState = {
  nomeOng: string
  descricao: string
  emailContatoOng: string
  telefoneOng: string
  website: string
}

type EditFormErrors = Partial<Record<keyof EditFormState, string>>

const emptyEditForm: EditFormState = {
  nomeOng: "",
  descricao: "",
  emailContatoOng: "",
  telefoneOng: "",
  website: "",
}

const inputClass =
  "w-full rounded-lg border border-[#B8B5FF] bg-white px-4 py-2 text-sm text-gray-700 shadow-sm focus:border-[#8F89FB] focus:outline-none focus:ring-2 focus:ring-[#8F89FB]/30"
const textareaClass = `${inputClass} min-h-[120px] resize-none`

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
  if (responseData && typeof responseData === "object" && "message" in responseData) {
    return String((responseData as { message?: string }).message || "Erro ao carregar ONG")
  }
  return "Erro ao carregar ONG"
}

const isValidEmail = (value: string) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)

const isValidUrl = (value: string) => {
  try {
    new URL(value)
    return true
  } catch {
    return false
  }
}

export default function PainelOngAdminPage() {
  const router = useRouter()
  const [loading, setLoading] = useState(true)
  const [isAdmin, setIsAdmin] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [ongInfo, setOngInfo] = useState<OngInfoWithProjects | null>(null)
  const [missingOng, setMissingOng] = useState(false)
  const [isEditing, setIsEditing] = useState(false)
  const [formState, setFormState] = useState<EditFormState>(emptyEditForm)
  const [formErrors, setFormErrors] = useState<EditFormErrors>({})
  const [isSaving, setIsSaving] = useState(false)
  const [saveMessage, setSaveMessage] = useState<string | null>(null)
  const [saveError, setSaveError] = useState<string | null>(null)

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true)
        setError(null)
        setMissingOng(false)

        const user = await authService.getMe()
        const admin = isAdminOng(user.roles)
        setIsAdmin(admin)
        if (!admin) {
          return
        }

        try {
          const data = await ongService.getMyOng()
          setOngInfo(data)
        } catch (err) {
          const status = (err as { response?: { status?: number } })?.response?.status
          if (status === 401) {
            router.replace("/login?redirect=/ong/perfil")
            return
          }
          if (status === 403) {
            setError("Voce nao tem permissao para acessar esta ONG.")
            return
          }
          if (status === 404 || status === 204) {
            setMissingOng(true)
            return
          }
          setError(getErrorMessage(err))
        }
      } catch (err) {
        const status = (err as { response?: { status?: number } })?.response?.status
        if (status === 401) {
          router.replace("/login?redirect=/ong/perfil")
          return
        }
        setError(getErrorMessage(err))
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [router])

  const projects = useMemo(() => ongInfo?.projectResponse ?? [], [ongInfo])

  const openEdit = () => {
    if (!ongInfo) {
      return
    }
    setFormState({
      nomeOng: ongInfo.nomeOng ?? "",
      descricao: ongInfo.descricao ?? "",
      emailContatoOng: ongInfo.emailContatoOng ?? "",
      telefoneOng: ongInfo.telefoneOng ?? "",
      website: ongInfo.website ?? "",
    })
    setFormErrors({})
    setSaveError(null)
    setSaveMessage(null)
    setIsEditing(true)
  }

  const closeEdit = () => {
    setIsEditing(false)
    setFormErrors({})
    setSaveError(null)
  }

  const handleChange =
    (field: keyof EditFormState) =>
    (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setFormState((prev) => ({ ...prev, [field]: event.target.value }))
    }

  const validateForm = () => {
    const errors: EditFormErrors = {}
    if (!formState.nomeOng.trim()) {
      errors.nomeOng = "Informe o nome da ONG."
    }
    if (!formState.emailContatoOng.trim()) {
      errors.emailContatoOng = "Informe o e-mail de contato."
    } else if (!isValidEmail(formState.emailContatoOng)) {
      errors.emailContatoOng = "E-mail invalido."
    }
    if (formState.website.trim() && !isValidUrl(formState.website.trim())) {
      errors.website = "Informe uma URL valida."
    }
    setFormErrors(errors)
    return Object.keys(errors).length === 0
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!ongInfo) {
      setSaveError("Nao foi possivel identificar a ONG.")
      return
    }
    if (!validateForm()) {
      return
    }

    setIsSaving(true)
    setSaveError(null)

    const payload: UpdateOngRequest = {
      nomeOng: formState.nomeOng.trim(),
      descricao: formState.descricao.trim(),
      emailContatoOng: formState.emailContatoOng.trim(),
      telefoneOng: formState.telefoneOng.trim() || undefined,
      website: formState.website.trim() || undefined,
    }

    try {
      const updated = await ongService.updateMyOng(payload)
      setOngInfo(updated)
      const refreshed = await ongService.getMyOng()
      setOngInfo(refreshed)
      setSaveMessage("ONG atualizada com sucesso.")
      setIsEditing(false)
    } catch (err) {
      const apiError = err as { response?: { status?: number } }
      const status = apiError?.response?.status
      if (status === 401) {
        router.replace("/login?redirect=/ong/perfil")
        return
      }
      if (status === 403) {
        setSaveError("Voce nao tem permissao para editar esta ONG.")
        return
      }
      if (status === 404 || status === 204) {
        setMissingOng(true)
        setIsEditing(false)
        return
      }
      setSaveError("Nao foi possivel salvar as alteracoes.")
    } finally {
      setIsSaving(false)
    }
  }

  if (loading) {
    return (
      <PageShell>
        <PageContainer>
          <div className="rounded-2xl border border-gray-200 bg-white p-10 text-center shadow-sm">
            <div className="mx-auto h-10 w-10 animate-spin rounded-full border-2 border-[#8F89FB] border-t-transparent" />
            <p className="mt-4 text-sm text-gray-500">Carregando painel da ONG</p>
          </div>
        </PageContainer>
      </PageShell>
    )
  }

  if (!isAdmin) {
    return (
      <PageShell>
        <PageContainer>
          <div className="rounded-2xl border border-gray-200 bg-white p-8 text-center shadow-sm">
            <h1 className="text-xl font-semibold text-[#2A2599]">
              Painel exclusivo para administradores de ONG
            </h1>
            <p className="mt-3 text-sm text-gray-600">
              Voce nao possui permissao para acessar este painel.
            </p>
            <Link
              href="/perfil"
              className="mt-6 inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
            >
              Ir para meu perfil
            </Link>
          </div>
        </PageContainer>
      </PageShell>
    )
  }

  if (missingOng) {
    return (
      <PageShell>
        <PageContainer>
          <div className="rounded-2xl border border-gray-200 bg-white p-8 text-center shadow-sm">
            <h1 className="text-xl font-semibold text-[#2A2599]">
              Nenhuma ONG cadastrada
            </h1>
            <p className="mt-3 text-sm text-gray-600">
              Cadastre sua ONG para comecar a criar projetos e atividades.
            </p>
            <Link
              href="/ong/criar"
              className="mt-6 inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
            >
              Cadastrar ONG
            </Link>
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
  const statusLabel = ongInfo?.status ? getStatusLabel(ongInfo.status) : "Status"
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
            <div className="flex flex-wrap items-center justify-between gap-4">
              <h1 className={typographyTokens.pageTitle}>
                {ongInfo?.nomeOng || "ONG"}
              </h1>
              {isAdmin && (
                <div className="flex flex-wrap items-center gap-3">
                  {ongInfo && (
                    <Link
                      href="/projetos/criar"
                      className="rounded-md bg-[#2A2599] px-4 py-1.5 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
                    >
                      Criar projeto
                    </Link>
                  )}
                  <button
                    type="button"
                    onClick={openEdit}
                    className="text-sm font-semibold text-[#2A2599] hover:text-[#8F89FB]"
                  >
                    Editar ONG
                  </button>
                </div>
              )}
            </div>

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

            {saveMessage && (
              <p className="text-sm font-medium text-emerald-600">
                {saveMessage}
              </p>
            )}
            {!isEditing && saveError && (
              <p className="text-sm font-medium text-[#FF4B4B]">
                {saveError}
              </p>
            )}
          </div>
        </section>

        <div className={layoutTokens.divider} />

        <section>
          <div className="flex items-center justify-between">
            <h2 className={typographyTokens.sectionTitle}>Projetos</h2>
            <span className={typographyTokens.mutedText}>
              {projects.length} projeto(s)
            </span>
          </div>

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
                    <div className="mt-3 space-y-2">
                      <div className="space-y-1">
                        <h3 className="text-sm font-semibold text-[#2A2599]">
                          {project.nome}
                        </h3>
                        <p className={`${typographyTokens.detailText} leading-relaxed`}>
                          {project.objetivo || project.publicoAlvo || "Descricao nao informada"}
                        </p>
                      </div>
                      <div className="flex flex-wrap items-center gap-4">
                        <Link
                          href={`/projetos/${project.id}`}
                          className={typographyTokens.link}
                        >
                          Ver projeto →
                        </Link>
                        <Link
                          href={`/atividades/criar?projetoId=${project.id}`}
                          className="rounded-md bg-[#8F89FB] px-4 py-1.5 text-xs font-semibold text-white transition-colors hover:bg-[#2A2599]"
                        >
                          Criar atividade
                        </Link>
                      </div>
                    </div>
                  </article>
                )
              })}
            </div>
          )}
        </section>
      </PageContainer>

      {isEditing && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-2xl rounded-lg bg-white shadow-xl">
            <div className="flex items-center justify-between border-b border-gray-100 px-6 py-4">
              <h3 className="text-lg font-semibold text-gray-900">Editar ONG</h3>
              <button
                type="button"
                onClick={closeEdit}
                className="text-sm font-semibold text-gray-500 hover:text-[#2A2599]"
              >
                Fechar
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-6 px-6 py-6">
              <div className="grid gap-4 md:grid-cols-2">
                <FormField label="Nome da ONG" htmlFor="nomeOng">
                  <input
                    id="nomeOng"
                    type="text"
                    value={formState.nomeOng}
                    onChange={handleChange("nomeOng")}
                    className={inputClass}
                    required
                  />
                  {formErrors.nomeOng && (
                    <p className="text-xs text-red-500">{formErrors.nomeOng}</p>
                  )}
                </FormField>

                <FormField label="E-mail de contato" htmlFor="emailContatoOng">
                  <input
                    id="emailContatoOng"
                    type="email"
                    value={formState.emailContatoOng}
                    onChange={handleChange("emailContatoOng")}
                    className={inputClass}
                    required
                  />
                  {formErrors.emailContatoOng && (
                    <p className="text-xs text-red-500">
                      {formErrors.emailContatoOng}
                    </p>
                  )}
                </FormField>

                <FormField label="Telefone de contato" htmlFor="telefoneOng">
                  <input
                    id="telefoneOng"
                    type="tel"
                    value={formState.telefoneOng}
                    onChange={handleChange("telefoneOng")}
                    className={inputClass}
                  />
                </FormField>

                <FormField label="Site da ONG" htmlFor="website">
                  <input
                    id="website"
                    type="url"
                    value={formState.website}
                    onChange={handleChange("website")}
                    className={inputClass}
                  />
                  {formErrors.website && (
                    <p className="text-xs text-red-500">{formErrors.website}</p>
                  )}
                </FormField>

                <div className="md:col-span-2">
                  <FormField label="Descricao" htmlFor="descricao">
                    <textarea
                      id="descricao"
                      value={formState.descricao}
                      onChange={handleChange("descricao")}
                      className={textareaClass}
                    />
                  </FormField>
                </div>
              </div>

              {saveError && (
                <p className="text-sm font-medium text-[#FF4B4B]">
                  {saveError}
                </p>
              )}

              <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={closeEdit}
                  className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:border-gray-300"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isSaving}
                  className="rounded-lg bg-[#2A2599] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a] disabled:cursor-not-allowed disabled:opacity-70"
                >
                  {isSaving ? "Salvando..." : "Salvar alteracoes"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
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
