"use client"

import { useEffect, useMemo, useState } from "react"
import Link from "next/link"
import Navbar from "@/components/Navbar"
import Footer from "@/components/Footer"
import { volunteerService } from "@/services/volunteerService"
import { ongService } from "@/services/ongService"
import { OngApprovalStatus, OngInfoWithProjects } from "@/types/ongInfo"
import { isAdminOng } from "@/lib/roles"

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

export default function PainelOngAdminPage() {
  const [loading, setLoading] = useState(true)
  const [isAdmin, setIsAdmin] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [ongInfo, setOngInfo] = useState<OngInfoWithProjects | null>(null)
  const [ongId, setOngId] = useState<number | null>(null)

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true)
        setError(null)
        const user = await volunteerService.getCurrentUser()
        const admin = isAdminOng(user.roles)
        setIsAdmin(admin)
        if (!admin) {
          return
        }
        if (!user.ongId) {
          setError("Nenhuma ONG vinculada ao usuario")
          return
        }
        setOngId(user.ongId)
        const data = await ongService.getOngWithProjects(user.ongId)
        setOngInfo(data)
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [])

  const projects = useMemo(() => ongInfo?.projectResponse ?? [], [ongInfo])

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        <Navbar />
        <main className="mx-auto w-full max-w-6xl flex-1 px-6 pb-16 pt-10">
          <div className="rounded-2xl border border-gray-200 bg-white p-10 text-center shadow-sm">
            <div className="mx-auto h-10 w-10 animate-spin rounded-full border-2 border-[#8F89FB] border-t-transparent" />
            <p className="mt-4 text-sm text-gray-500">Carregando painel da ONG</p>
          </div>
        </main>
        <Footer />
      </div>
    )
  }

  if (!isAdmin) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        <Navbar />
        <main className="mx-auto w-full max-w-4xl flex-1 px-6 pb-16 pt-10">
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
        </main>
        <Footer />
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        <Navbar />
        <main className="mx-auto w-full max-w-4xl flex-1 px-6 pb-16 pt-10">
          <div className="rounded-2xl border border-red-200 bg-red-50 p-8 text-center text-sm text-red-600">
            {error}
          </div>
        </main>
        <Footer />
      </div>
    )
  }

  const coverImage = ongInfo?.logoUrl?.trim() || "/logo_volunteer.png"
  const statusStyle = ongInfo?.status ? getStatusStyle(ongInfo.status) : "bg-gray-200 text-gray-700"
  const statusLabel = ongInfo?.status ? getStatusLabel(ongInfo.status) : "Status"
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <main className="mx-auto w-full max-w-6xl flex-1 px-6 pb-16 pt-10">
        <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6 md:p-8">
          <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
            <div className="flex flex-col gap-4 md:flex-row md:items-center">
              <div className="h-20 w-20 overflow-hidden rounded-2xl border border-[#E5E7F8] bg-[#F3F4FF] shadow-sm">
                <img
                  src={coverImage}
                  alt={ongInfo?.nomeOng || "Logo da ONG"}
                  className="h-full w-full object-cover"
                  loading="lazy"
                />
              </div>
              <div>
                <p className="text-xs font-semibold uppercase tracking-[0.3em] text-gray-400">
                  Painel da ONG
                </p>
                <h1 className="mt-2 text-2xl font-semibold text-[#2A2599]">
                  {ongInfo?.nomeOng || "ONG"}
                </h1>
                <span
                  className={`mt-3 inline-flex items-center rounded-md px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em] ${statusStyle}`}
                >
                  {statusLabel}
                </span>
                {ongInfo?.descricao && (
                  <p className="mt-4 text-sm leading-relaxed text-gray-600">
                    {ongInfo.descricao}
                  </p>
                )}
              </div>
            </div>

            {ongId && (
              <Link
                href={`/projetos/criar?ongId=${ongId}`}
                className="inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
              >
                Criar projeto
              </Link>
            )}
          </div>
        </section>

        <section className="mt-8 grid gap-6 lg:grid-cols-3">
          <div className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
            <h2 className="text-sm font-semibold uppercase tracking-[0.3em] text-[#2A2599]">
              Contato
            </h2>
            <div className="mt-4 space-y-2 text-sm text-gray-600">
              <p>
                <span className="font-semibold text-gray-900">Email: </span>
                {ongInfo?.emailContatoOng || "-"}
              </p>
              <p>
                <span className="font-semibold text-gray-900">Telefone: </span>
                {ongInfo?.telefoneOng || "-"}
              </p>
              <p>
                <span className="font-semibold text-gray-900">Site: </span>
                {ongInfo?.website || "-"}
              </p>
            </div>
          </div>

          <div className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
            <h2 className="text-sm font-semibold uppercase tracking-[0.3em] text-[#2A2599]">
              Endereco
            </h2>
            <div className="mt-4 space-y-2 text-sm text-gray-600">
              <p>{ongInfo?.endereco?.logradouro || "-"}</p>
              <p>{ongInfo?.endereco?.bairro || "-"}</p>
              <p>{ongInfo?.endereco?.cidade || "-"}</p>
              <p>{ongInfo?.endereco?.estado || "-"}</p>
              <p>{ongInfo?.endereco?.cep || "-"}</p>
            </div>
          </div>

          <div className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
            <h2 className="text-sm font-semibold uppercase tracking-[0.3em] text-[#2A2599]">
              Fundacao
            </h2>
            <div className="mt-4 space-y-2 text-sm text-gray-600">
              <p>
                <span className="font-semibold text-gray-900">Fundacao: </span>
                {formatDate(ongInfo?.dataFundacao)}
              </p>
              <p>
                <span className="font-semibold text-gray-900">Cadastro: </span>
                {formatDate(ongInfo?.dataCriacaoRegistro)}
              </p>
            </div>
          </div>
        </section>

        <section className="mt-10">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold uppercase tracking-[0.3em] text-[#2A2599]">
              Projetos
            </h2>
            <span className="text-xs text-gray-500">
              {projects.length} projeto(s)
            </span>
          </div>

          {projects.length === 0 ? (
            <div className="mt-6 rounded-2xl border border-gray-200 bg-white p-8 text-center text-sm text-gray-500">
              Nenhum projeto cadastrado ainda
            </div>
          ) : (
            <div className="mt-6 grid gap-6 md:grid-cols-2 xl:grid-cols-3">
              {projects.map((project) => {
                const projectImage = project.urlImagemDestaque?.trim() || "/logo_volunteer.png"
                return (
                  <article
                    key={project.id}
                    className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-4"
                  >
                    <div className="overflow-hidden rounded-xl">
                      <img
                        src={projectImage}
                        alt={project.nome}
                        className="h-36 w-full object-cover"
                        loading="lazy"
                      />
                    </div>
                    <div className="mt-4 space-y-3">
                      <div>
                        <h3 className="text-base font-semibold text-gray-900">
                          {project.nome}
                        </h3>
                        <p className="mt-2 text-sm text-gray-600">
                          {project.objetivo || project.publicoAlvo || "Descricao nao informada"}
                        </p>
                      </div>
                      <div className="flex flex-wrap gap-3 text-xs text-gray-500">
                        <span>
                          <span className="font-semibold text-gray-800">Publico: </span>
                          {project.publicoAlvo || "-"}
                        </span>
                      </div>
                      <div className="flex flex-wrap gap-3">
                        <Link
                          href={`/projetos/${project.id}`}
                          className="inline-flex items-center text-sm font-semibold text-[#8F89FB] hover:text-[#2A2599]"
                        >
                          Ver projeto →
                        </Link>
                        <Link
                          href={`/atividades/criar?projetoId=${project.id}`}
                          className="inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-4 py-2 text-xs font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
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
      </main>

      <Footer />
    </div>
  )
}
