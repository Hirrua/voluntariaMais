"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useEffect, useMemo, useState } from "react";
import { useParams, usePathname, useRouter } from "next/navigation";
import FormField from "@/components/FormField";
import PageContainer from "@/components/PageContainer";
import { adminApiClient } from "@/services/adminApiClient";
import { resolveAdminError } from "@/lib/adminErrors";
import { componentTokens, layoutTokens, typographyTokens } from "@/styles/tokens";
import type { Atividade, Projeto, ProjetoAdminDetalhe } from "@/types/admin";

type ProjectFormState = {
  nome: string;
  objetivo: string;
  descricaoDetalhada: string;
  publicoAlvo: string;
  dataInicioPrevista: string;
  dataFimPrevista: string;
};

type ActivityFormState = {
  nomeAtividade: string;
  descricaoAtividade: string;
  dataHoraInicioAtividade: string;
  dataHoraFimAtividade: string;
  localAtividade: string;
  vagasTotais: string;
};

const emptyProjectForm: ProjectFormState = {
  nome: "",
  objetivo: "",
  descricaoDetalhada: "",
  publicoAlvo: "",
  dataInicioPrevista: "",
  dataFimPrevista: "",
};

const emptyActivityForm: ActivityFormState = {
  nomeAtividade: "",
  descricaoAtividade: "",
  dataHoraInicioAtividade: "",
  dataHoraFimAtividade: "",
  localAtividade: "",
  vagasTotais: "",
};

const inputClass =
  "w-full rounded-lg border border-[#B8B5FF] bg-white px-4 py-2 text-sm text-gray-700 shadow-sm focus:border-[#8F89FB] focus:outline-none focus:ring-2 focus:ring-[#8F89FB]/30";
const textareaClass = `${inputClass} min-h-[140px] resize-none`;

const formatDate = (value?: string | null) => {
  if (!value) {
    return "-";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "-";
  }
  return date.toLocaleDateString("pt-BR");
};

const formatDateTime = (value?: string | null) => {
  if (!value) {
    return "-";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "-";
  }
  return date.toLocaleString("pt-BR");
};

const toInputDate = (value?: string | null) => {
  if (!value) {
    return "";
  }
  return value.slice(0, 10);
};

const toInputDateTime = (value?: string | null) => {
  if (!value) {
    return "";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "";
  }
  return date.toISOString().slice(0, 16);
};

const toOffsetDateTime = (value: string) => {
  if (!value) {
    return "";
  }
  const date = new Date(value);
  const offsetMinutes = date.getTimezoneOffset();
  const sign = offsetMinutes > 0 ? "-" : "+";
  const absoluteMinutes = Math.abs(offsetMinutes);
  const offsetHours = String(Math.floor(absoluteMinutes / 60)).padStart(2, "0");
  const offsetRest = String(absoluteMinutes % 60).padStart(2, "0");
  return `${value}:00${sign}${offsetHours}:${offsetRest}`;
};

export default function AdminProjetoPainelPage() {
  const router = useRouter();
  const pathname = usePathname();
  const params = useParams();
  const idParam = params?.id;
  const projectIdRaw = Array.isArray(idParam) ? idParam[0] : idParam;
  const projectId = projectIdRaw ? Number(projectIdRaw) : Number.NaN;

  const [project, setProject] = useState<ProjetoAdminDetalhe | null>(null);
  const [activities, setActivities] = useState<Atividade[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isEditingProject, setIsEditingProject] = useState(false);
  const [isDeletingProject, setIsDeletingProject] = useState(false);
  const [projectForm, setProjectForm] = useState<ProjectFormState>(emptyProjectForm);
  const [activityForm, setActivityForm] = useState<ActivityFormState>(emptyActivityForm);
  const [isActivityModalOpen, setIsActivityModalOpen] = useState(false);
  const [editingActivityId, setEditingActivityId] = useState<number | null>(null);
  const [confirmDeleteActivityId, setConfirmDeleteActivityId] = useState<number | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const coverImage = project?.urlImagemDestaque?.trim() || "/logo_volunteer.png";

  const resetFeedback = () => {
    setErrorMessage(null);
    setSuccessMessage(null);
  };

  const handleError = (error: unknown) => {
    const message = resolveAdminError(error, router, pathname);
    if (message) {
      setErrorMessage(message);
    }
  };

  const loadProjectData = async () => {
    if (!Number.isFinite(projectId)) {
      setErrorMessage("Projeto invalido.");
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      resetFeedback();
      const [projectResponse, activityResponse] = await Promise.all([
        adminApiClient.getMyProject(projectId),
        adminApiClient.listMyProjectActivities(projectId),
      ]);
      setProject(projectResponse);
      setActivities(activityResponse);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjectData();
  }, [projectId]);

  const openProjectEdit = () => {
    if (!project) {
      return;
    }
    setProjectForm({
      nome: project.nome ?? "",
      objetivo: project.objetivo ?? "",
      descricaoDetalhada: project.descricaoDetalhada ?? "",
      publicoAlvo: project.publicoAlvo ?? "",
      dataInicioPrevista: toInputDate(project.dataInicioPrevista),
      dataFimPrevista: toInputDate(project.dataFimPrevista),
    });
    resetFeedback();
    setIsEditingProject(true);
  };

  const submitProjectEdit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!project) {
      return;
    }
    setIsSaving(true);
    resetFeedback();

    const payload: Partial<Projeto> = {
      nome: projectForm.nome.trim(),
      objetivo: projectForm.objetivo.trim(),
      descricaoDetalhada: projectForm.descricaoDetalhada.trim(),
      publicoAlvo: projectForm.publicoAlvo.trim(),
      dataInicioPrevista: projectForm.dataInicioPrevista || undefined,
      dataFimPrevista: projectForm.dataFimPrevista || undefined,
    };

    try {
      const updated = await adminApiClient.updateMyProject(project.id, payload);
      setProject(updated);
      await loadProjectData();
      setSuccessMessage("Projeto atualizado com sucesso.");
      setIsEditingProject(false);
    } catch (error) {
      handleError(error);
    } finally {
      setIsSaving(false);
    }
  };

  const confirmProjectDelete = async () => {
    if (!project) {
      return;
    }
    setIsSaving(true);
    resetFeedback();
    try {
      await adminApiClient.deleteMyProject(project.id);
      router.push("/admin/projetos");
    } catch (error) {
      handleError(error);
    } finally {
      setIsSaving(false);
      setIsDeletingProject(false);
    }
  };

  const openActivityCreate = () => {
    setActivityForm(emptyActivityForm);
    setEditingActivityId(null);
    resetFeedback();
    setIsActivityModalOpen(true);
  };

  const openActivityEdit = (activity: Atividade) => {
    setActivityForm({
      nomeAtividade: activity.nomeAtividade ?? "",
      descricaoAtividade: activity.descricaoAtividade ?? "",
      dataHoraInicioAtividade: toInputDateTime(activity.dataHoraInicioAtividade),
      dataHoraFimAtividade: toInputDateTime(activity.dataHoraFimAtividade),
      localAtividade: activity.localAtividade ?? "",
      vagasTotais: activity.vagasTotais ? String(activity.vagasTotais) : "",
    });
    setEditingActivityId(activity.id ?? null);
    resetFeedback();
    setIsActivityModalOpen(true);
  };

  const submitActivity = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!project) {
      return;
    }
    setIsSaving(true);
    resetFeedback();

    const vagas = Number(activityForm.vagasTotais);
    if (!Number.isFinite(vagas) || vagas <= 0) {
      setErrorMessage("Informe um numero de vagas valido.");
      setIsSaving(false);
      return;
    }

    const payload: Atividade = {
      nomeAtividade: activityForm.nomeAtividade.trim(),
      descricaoAtividade: activityForm.descricaoAtividade.trim(),
      dataHoraInicioAtividade: toOffsetDateTime(activityForm.dataHoraInicioAtividade),
      dataHoraFimAtividade: toOffsetDateTime(activityForm.dataHoraFimAtividade),
      localAtividade: activityForm.localAtividade.trim(),
      vagasTotais: vagas,
    };

    try {
      if (editingActivityId) {
        await adminApiClient.updateMyActivity(editingActivityId, payload);
        setSuccessMessage("Atividade atualizada com sucesso.");
      } else {
        await adminApiClient.createMyProjectActivity(project.id, payload);
        setSuccessMessage("Atividade criada com sucesso.");
      }
      setIsActivityModalOpen(false);
      await loadProjectData();
    } catch (error) {
      handleError(error);
    } finally {
      setIsSaving(false);
    }
  };

  const confirmDeleteActivity = async () => {
    if (!confirmDeleteActivityId) {
      return;
    }
    setIsSaving(true);
    resetFeedback();
    try {
      await adminApiClient.deleteMyActivity(confirmDeleteActivityId);
      setSuccessMessage("Atividade excluida com sucesso.");
      setConfirmDeleteActivityId(null);
      await loadProjectData();
    } catch (error) {
      handleError(error);
    } finally {
      setIsSaving(false);
    }
  };

  const addressLabel = useMemo(() => {
    const address = project?.endereco;
    if (!address) {
      return "-";
    }
    const parts = [
      address.logradouro,
      address.bairro,
      address.cidade,
      address.estado,
    ].filter(Boolean);
    return parts.length ? parts.join(", ") : "-";
  }, [project]);

  if (loading) {
    return (
      <PageContainer>
        <div className="rounded-2xl border border-gray-200 bg-white p-10 text-center shadow-sm">
          <div className="mx-auto h-10 w-10 animate-spin rounded-full border-2 border-[#8F89FB] border-t-transparent" />
          <p className="mt-4 text-sm text-gray-500">Carregando projeto</p>
        </div>
      </PageContainer>
    );
  }

  if (errorMessage) {
    return (
      <PageContainer>
        <div className="rounded-2xl border border-red-200 bg-red-50 p-8 text-sm text-red-600 shadow-sm">
          {errorMessage}
        </div>
      </PageContainer>
    );
  }

  return (
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
          <div className="flex flex-wrap items-center justify-between gap-4">
            <h1 className={typographyTokens.pageTitle}>
              {project?.nome || "Projeto"}
            </h1>
            <div className="flex flex-wrap gap-3">
              <button
                type="button"
                onClick={openProjectEdit}
                className="rounded-md bg-[#2A2599] px-4 py-1.5 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
              >
                Editar projeto
              </button>
              <button
                type="button"
                onClick={() => setIsDeletingProject(true)}
                className="rounded-md bg-[#FF4B4B] px-4 py-1.5 text-sm font-semibold text-white transition-colors hover:bg-[#E63E3E]"
              >
                Excluir projeto
              </button>
            </div>
          </div>

          <div className="space-y-1">
            <p className={typographyTokens.detailText}>
              <span className={typographyTokens.labelText}>Status: </span>
              {project?.status || "-"}
            </p>
            <p className={typographyTokens.detailText}>
              <span className={typographyTokens.labelText}>Inicio: </span>
              {formatDate(project?.dataInicioPrevista)}
            </p>
            <p className={typographyTokens.detailText}>
              <span className={typographyTokens.labelText}>Fim: </span>
              {formatDate(project?.dataFimPrevista)}
            </p>
            <p className={typographyTokens.detailText}>
              <span className={typographyTokens.labelText}>Endereco: </span>
              {addressLabel}
            </p>
            <p className={typographyTokens.detailText}>
              <span className={typographyTokens.labelText}>Publico alvo: </span>
              {project?.publicoAlvo || "-"}
            </p>
          </div>

          <p className={`${typographyTokens.detailText} leading-relaxed`}>
            {project?.descricaoDetalhada || "Descricao nao informada"}
          </p>

          {successMessage && (
            <p className="text-sm font-medium text-emerald-600">
              {successMessage}
            </p>
          )}
          {errorMessage && (
            <p className="text-sm font-medium text-[#FF4B4B]">
              {errorMessage}
            </p>
          )}
        </div>
      </section>

      <div className={layoutTokens.divider} />

      <section className="space-y-6">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <h2 className={typographyTokens.sectionTitle}>Atividades</h2>
          <button
            type="button"
            onClick={openActivityCreate}
            className="rounded-md bg-[#8F89FB] px-4 py-1.5 text-sm font-semibold text-white transition-colors hover:bg-[#2A2599]"
          >
            Nova atividade
          </button>
        </div>

        {activities.length === 0 ? (
          <div className={componentTokens.emptyState}>
            Nenhuma atividade cadastrada para este projeto
          </div>
        ) : (
          activities.map((activity) => (
            <article
              key={activity.id ?? activity.nomeAtividade}
              className="border-b border-[#D9DAF3] pb-6 last:border-b-0"
            >
              <div className="space-y-3">
                <h3 className={typographyTokens.activityTitle}>
                  {activity.nomeAtividade || "-"}
                </h3>

                <div className="space-y-1 text-sm text-gray-600">
                  <p>Inicio: {formatDateTime(activity.dataHoraInicioAtividade)}</p>
                  <p>Fim: {formatDateTime(activity.dataHoraFimAtividade)}</p>
                  <p>Endereco: {activity.localAtividade || "-"}</p>
                </div>

                <p className={`${typographyTokens.detailText} leading-relaxed`}>
                  {activity.descricaoAtividade || "Descricao nao informada"}
                </p>

                <p className="text-sm font-semibold text-gray-900">
                  Numero total de vagas: {activity.vagasPreenchidasAtividade ?? 0}/
                  {activity.vagasTotais ?? 0}
                </p>
              </div>

              <div className="mt-4 flex flex-wrap items-center gap-3">
                <button
                  type="button"
                  onClick={() => openActivityEdit(activity)}
                  className="rounded-md bg-[#2A2599] px-4 py-1.5 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
                >
                  Editar
                </button>
                <button
                  type="button"
                  onClick={() => setConfirmDeleteActivityId(activity.id ?? null)}
                  className="rounded-md bg-[#FF4B4B] px-4 py-1.5 text-sm font-semibold text-white transition-colors hover:bg-[#E63E3E]"
                >
                  Excluir
                </button>
              </div>
            </article>
          ))
        )}
      </section>

      {isEditingProject && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-2xl rounded-lg bg-white shadow-xl">
            <div className="flex items-center justify-between border-b border-gray-100 px-6 py-4">
              <h3 className="text-lg font-semibold text-gray-900">Editar projeto</h3>
              <button
                type="button"
                onClick={() => setIsEditingProject(false)}
                className="text-sm font-semibold text-gray-500 hover:text-[#2A2599]"
              >
                Fechar
              </button>
            </div>

            <form onSubmit={submitProjectEdit} className="space-y-6 px-6 py-6">
              <div className="grid gap-4 md:grid-cols-2">
                <FormField label="Nome do projeto" htmlFor="nomeProjeto">
                  <input
                    id="nomeProjeto"
                    type="text"
                    value={projectForm.nome}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setProjectForm((prev) => ({ ...prev, nome: event.target.value }))
                    }
                    className={inputClass}
                    required
                  />
                </FormField>

                <FormField label="Publico alvo" htmlFor="publicoAlvo">
                  <input
                    id="publicoAlvo"
                    type="text"
                    value={projectForm.publicoAlvo}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setProjectForm((prev) => ({ ...prev, publicoAlvo: event.target.value }))
                    }
                    className={inputClass}
                  />
                </FormField>

                <FormField label="Data de inicio" htmlFor="dataInicioPrevista">
                  <input
                    id="dataInicioPrevista"
                    type="date"
                    value={projectForm.dataInicioPrevista}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setProjectForm((prev) => ({ ...prev, dataInicioPrevista: event.target.value }))
                    }
                    className={inputClass}
                  />
                </FormField>

                <FormField label="Data de fim" htmlFor="dataFimPrevista">
                  <input
                    id="dataFimPrevista"
                    type="date"
                    value={projectForm.dataFimPrevista}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setProjectForm((prev) => ({ ...prev, dataFimPrevista: event.target.value }))
                    }
                    className={inputClass}
                  />
                </FormField>

                <div className="md:col-span-2">
                  <FormField label="Objetivo" htmlFor="objetivo">
                    <textarea
                      id="objetivo"
                      value={projectForm.objetivo}
                      onChange={(event: ChangeEvent<HTMLTextAreaElement>) =>
                        setProjectForm((prev) => ({ ...prev, objetivo: event.target.value }))
                      }
                      className={textareaClass}
                    />
                  </FormField>
                </div>

                <div className="md:col-span-2">
                  <FormField label="Descricao detalhada" htmlFor="descricaoDetalhada">
                    <textarea
                      id="descricaoDetalhada"
                      value={projectForm.descricaoDetalhada}
                      onChange={(event: ChangeEvent<HTMLTextAreaElement>) =>
                        setProjectForm((prev) => ({
                          ...prev,
                          descricaoDetalhada: event.target.value,
                        }))
                      }
                      className={textareaClass}
                    />
                  </FormField>
                </div>
              </div>

              <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={() => setIsEditingProject(false)}
                  className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:border-gray-300"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isSaving}
                  className="rounded-lg bg-[#2A2599] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a] disabled:cursor-not-allowed disabled:opacity-70"
                >
                  {isSaving ? "Salvando..." : "Salvar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isDeletingProject && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-md rounded-lg bg-white shadow-xl">
            <div className="space-y-4 px-6 py-6">
              <h3 className="text-lg font-semibold text-gray-900">
                Tem certeza? Esta acao nao pode ser desfeita.
              </h3>
              <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={() => setIsDeletingProject(false)}
                  className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:border-gray-300"
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  disabled={isSaving}
                  onClick={confirmProjectDelete}
                  className="rounded-lg bg-[#FF4B4B] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#E63E3E] disabled:cursor-not-allowed disabled:opacity-70"
                >
                  {isSaving ? "Excluindo..." : "Excluir"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {isActivityModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-2xl rounded-lg bg-white shadow-xl">
            <div className="flex items-center justify-between border-b border-gray-100 px-6 py-4">
              <h3 className="text-lg font-semibold text-gray-900">
                {editingActivityId ? "Editar atividade" : "Nova atividade"}
              </h3>
              <button
                type="button"
                onClick={() => setIsActivityModalOpen(false)}
                className="text-sm font-semibold text-gray-500 hover:text-[#2A2599]"
              >
                Fechar
              </button>
            </div>

            <form onSubmit={submitActivity} className="space-y-6 px-6 py-6">
              <div className="grid gap-4 md:grid-cols-2">
                <FormField label="Nome" htmlFor="nomeAtividade">
                  <input
                    id="nomeAtividade"
                    type="text"
                    value={activityForm.nomeAtividade}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setActivityForm((prev) => ({ ...prev, nomeAtividade: event.target.value }))
                    }
                    className={inputClass}
                    required
                  />
                </FormField>

                <FormField label="Local" htmlFor="localAtividade">
                  <input
                    id="localAtividade"
                    type="text"
                    value={activityForm.localAtividade}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setActivityForm((prev) => ({ ...prev, localAtividade: event.target.value }))
                    }
                    className={inputClass}
                  />
                </FormField>

                <FormField label="Inicio" htmlFor="dataHoraInicioAtividade">
                  <input
                    id="dataHoraInicioAtividade"
                    type="datetime-local"
                    value={activityForm.dataHoraInicioAtividade}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setActivityForm((prev) => ({
                        ...prev,
                        dataHoraInicioAtividade: event.target.value,
                      }))
                    }
                    className={inputClass}
                    required
                  />
                </FormField>

                <FormField label="Fim" htmlFor="dataHoraFimAtividade">
                  <input
                    id="dataHoraFimAtividade"
                    type="datetime-local"
                    value={activityForm.dataHoraFimAtividade}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setActivityForm((prev) => ({
                        ...prev,
                        dataHoraFimAtividade: event.target.value,
                      }))
                    }
                    className={inputClass}
                    required
                  />
                </FormField>

                <FormField label="Vagas" htmlFor="vagasTotais">
                  <input
                    id="vagasTotais"
                    type="number"
                    value={activityForm.vagasTotais}
                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                      setActivityForm((prev) => ({ ...prev, vagasTotais: event.target.value }))
                    }
                    className={inputClass}
                    min={1}
                    required
                  />
                </FormField>

                <div className="md:col-span-2">
                  <FormField label="Descricao" htmlFor="descricaoAtividade">
                    <textarea
                      id="descricaoAtividade"
                      value={activityForm.descricaoAtividade}
                      onChange={(event: ChangeEvent<HTMLTextAreaElement>) =>
                        setActivityForm((prev) => ({
                          ...prev,
                          descricaoAtividade: event.target.value,
                        }))
                      }
                      className={textareaClass}
                    />
                  </FormField>
                </div>
              </div>

              <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={() => setIsActivityModalOpen(false)}
                  className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:border-gray-300"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isSaving}
                  className="rounded-lg bg-[#2A2599] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a] disabled:cursor-not-allowed disabled:opacity-70"
                >
                  {isSaving ? "Salvando..." : "Salvar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {confirmDeleteActivityId !== null && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-md rounded-lg bg-white shadow-xl">
            <div className="space-y-4 px-6 py-6">
              <h3 className="text-lg font-semibold text-gray-900">
                Tem certeza? Esta acao nao pode ser desfeita.
              </h3>
              <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={() => setConfirmDeleteActivityId(null)}
                  className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:border-gray-300"
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  disabled={isSaving}
                  onClick={confirmDeleteActivity}
                  className="rounded-lg bg-[#FF4B4B] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#E63E3E] disabled:cursor-not-allowed disabled:opacity-70"
                >
                  {isSaving ? "Excluindo..." : "Excluir"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </PageContainer>
  );
}
