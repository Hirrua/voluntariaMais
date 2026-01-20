"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { adminApiClient } from "@/services/adminApiClient";
import { Atividade } from "@/types/admin";
import { OngProjectAndActivityInfo } from "@/types/projectInfo";
import { resolveAdminError } from "@/lib/adminErrors";
import PageHeader from "@/components/admin/PageHeader";
import ListPanel from "@/components/admin/ListPanel";
import ConfirmModal from "@/components/admin/ConfirmModal";
import AlertBanner from "@/components/admin/AlertBanner";

type ProjectActivitiesPageProps = {
  params: {
    id: string;
  };
};

type FormMode = "create" | "edit";

type FormState = {
  nomeAtividade: string;
  descricaoAtividade: string;
  data: string;
  vagasTotais: string;
  localAtividade: string;
};

type FormErrors = Partial<Record<keyof FormState, string>>;

const initialFormState: FormState = {
  nomeAtividade: "",
  descricaoAtividade: "",
  data: "",
  vagasTotais: "",
  localAtividade: "",
};

const toIsoDateTime = (value: string) => {
  if (!value) {
    return undefined;
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toISOString();
};

const toInputDateTime = (value?: string | null) => {
  if (!value) {
    return "";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${year}-${month}-${day}T${hours}:${minutes}`;
};

const formatDate = (value?: string | null) => {
  if (!value) {
    return "Nao informado";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString("pt-BR");
};

export default function ProjectActivitiesPage({ params }: ProjectActivitiesPageProps) {
  const router = useRouter();
  const pathname = usePathname();
  const projectId = Number(params.id);
  const [projectTitle, setProjectTitle] = useState<string>("");
  const [activities, setActivities] = useState<Atividade[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [formMode, setFormMode] = useState<FormMode>("create");
  const [formState, setFormState] = useState<FormState>(initialFormState);
  const [formErrors, setFormErrors] = useState<FormErrors>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null);

  const filteredActivities = useMemo(() => activities, [activities]);

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

  const loadProjectInfo = async () => {
    try {
      setIsLoading(true);
      resetFeedback();
      const response: OngProjectAndActivityInfo = await adminApiClient.getProjeto(projectId);
      setProjectTitle(response.simpleInfoProjectResponse?.nome ?? "");
      setActivities(response.simpleInfoActivityResponse ?? []);
    } catch (error) {
      handleError(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!Number.isNaN(projectId)) {
      loadProjectInfo();
    } else {
      setIsLoading(false);
      setErrorMessage("ID do projeto invalido");
    }
  }, [projectId]);

  const openCreateForm = () => {
    resetFeedback();
    setFormMode("create");
    setEditingId(null);
    setFormState(initialFormState);
    setFormErrors({});
    setIsFormOpen(true);
  };

  const openEditForm = (activity: Atividade) => {
    resetFeedback();
    setFormMode("edit");
    setEditingId(activity.id ?? null);
    setFormState({
      nomeAtividade: activity.nomeAtividade ?? "",
      descricaoAtividade: activity.descricaoAtividade ?? "",
      data: toInputDateTime(activity.dataHoraInicioAtividade),
      vagasTotais: activity.vagasTotais?.toString() ?? "",
      localAtividade: activity.localAtividade ?? "",
    });
    setFormErrors({});
    setIsFormOpen(true);
  };

  const validateForm = () => {
    const errors: FormErrors = {};
    if (formState.data) {
      const parsed = new Date(formState.data);
      if (Number.isNaN(parsed.getTime())) {
        errors.data = "Data invalida";
      }
    }
    if (formState.vagasTotais) {
      const value = Number(formState.vagasTotais);
      if (!Number.isFinite(value) || value < 0) {
        errors.vagasTotais = "Vagas invalidas";
      }
    }
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    resetFeedback();

    const isoDate = toIsoDateTime(formState.data);
    const payload: Atividade = {
      idProjeto: projectId,
      nomeAtividade: formState.nomeAtividade.trim() || undefined,
      descricaoAtividade: formState.descricaoAtividade.trim() || undefined,
      dataHoraInicioAtividade: isoDate,
      dataHoraFimAtividade: isoDate,
      localAtividade: formState.localAtividade.trim() || undefined,
      vagasTotais: formState.vagasTotais ? Number(formState.vagasTotais) : undefined,
    };

    try {
      if (formMode === "create") {
        await adminApiClient.createAtividade(payload);
        setSuccessMessage("Atividade criada com sucesso");
      } else if (editingId !== null) {
        await adminApiClient.updateAtividade(editingId, payload);
        setSuccessMessage("Atividade atualizada com sucesso");
      }

      setIsFormOpen(false);
      await loadProjectInfo();
    } catch (error) {
      handleError(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (confirmDeleteId === null) {
      return;
    }
    setIsSubmitting(true);
    resetFeedback();
    try {
      await adminApiClient.deleteAtividade(confirmDeleteId);
      setSuccessMessage("Atividade excluida com sucesso");
      setConfirmDeleteId(null);
      await loadProjectInfo();
    } catch (error) {
      handleError(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-8">
      <PageHeader
        title="Atividades do projeto"
        description={projectTitle || `Projeto ${params.id}`}
        actions={
          <>
            <Link
              href="/admin/projetos"
              className="inline-flex items-center text-sm font-semibold text-[#2A2599] hover:text-[#8F89FB]"
            >
              Voltar para projetos
            </Link>
            <button
              type="button"
              onClick={openCreateForm}
              className="inline-flex items-center justify-center rounded-lg bg-[#8F89FB] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#2A2599]"
            >
              Nova atividade
            </button>
          </>
        }
      />

      <AlertBanner variant="error" message={errorMessage} />
      <AlertBanner variant="success" message={successMessage} />

      <ListPanel title="Lista de atividades" count={filteredActivities.length}>
        {isLoading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#8F89FB]"></div>
            <p className="text-gray-500 mt-4">Carregando atividades</p>
          </div>
        ) : filteredActivities.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">Nenhuma atividade encontrada</p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredActivities.map((activity) => (
              <div
                key={activity.id ?? activity.nomeAtividade}
                className="flex flex-col gap-4 rounded-lg border border-gray-100 px-4 py-4 md:flex-row md:items-center md:justify-between"
              >
                <div className="space-y-1">
                  <p className="text-sm font-semibold text-gray-900">
                    {activity.nomeAtividade ?? "Sem nome"}
                  </p>
                  <div className="flex flex-wrap gap-2 text-xs text-gray-500">
                    <span>{formatDate(activity.dataHoraInicioAtividade)}</span>
                    {activity.localAtividade && <span>- {activity.localAtividade}</span>}
                    {typeof activity.vagasTotais === "number" && (
                      <span>- {activity.vagasTotais} vagas</span>
                    )}
                  </div>
                </div>

                <div className="flex flex-wrap gap-3 text-sm">
                  <button
                    type="button"
                    onClick={() => openEditForm(activity)}
                    className="font-semibold text-[#2A2599] hover:text-[#8F89FB]"
                  >
                    Editar
                  </button>
                  <button
                    type="button"
                    onClick={() => setConfirmDeleteId(activity.id ?? null)}
                    className="font-semibold text-red-600 hover:text-red-700"
                  >
                    Excluir
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </ListPanel>

      {isFormOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-2xl rounded-lg bg-white shadow-xl">
            <div className="flex items-center justify-between border-b border-gray-100 px-6 py-4">
              <h3 className="text-lg font-semibold text-gray-900">
                {formMode === "create" ? "Nova atividade" : "Editar atividade"}
              </h3>
              <button
                type="button"
                onClick={() => setIsFormOpen(false)}
                className="text-sm font-semibold text-gray-500 hover:text-[#2A2599]"
              >
                Fechar
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-6 px-6 py-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Nome
                  </label>
                  <input
                    type="text"
                    value={formState.nomeAtividade}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        nomeAtividade: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Data
                  </label>
                  <input
                    type="datetime-local"
                    value={formState.data}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        data: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                  {formErrors.data && (
                    <p className="text-xs text-red-500">{formErrors.data}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Vagas
                  </label>
                  <input
                    type="number"
                    value={formState.vagasTotais}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        vagasTotais: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                  {formErrors.vagasTotais && (
                    <p className="text-xs text-red-500">
                      {formErrors.vagasTotais}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Local
                  </label>
                  <input
                    type="text"
                    value={formState.localAtividade}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        localAtividade: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                </div>

                <div className="space-y-2 md:col-span-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Descricao
                  </label>
                  <textarea
                    value={formState.descricaoAtividade}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        descricaoAtividade: event.target.value,
                      }))
                    }
                    rows={4}
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                </div>
              </div>

              <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={() => setIsFormOpen(false)}
                  className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:border-gray-300"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="rounded-lg bg-[#2A2599] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a] disabled:cursor-not-allowed disabled:opacity-70"
                >
                  {isSubmitting ? "Salvando..." : "Salvar"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmModal
        open={confirmDeleteId !== null}
        title="Confirmar exclusao"
        description="Tem certeza que deseja excluir esta atividade? Esta acao nao pode ser desfeita."
        confirmLabel="Excluir"
        loadingLabel="Excluindo..."
        isLoading={isSubmitting}
        onCancel={() => setConfirmDeleteId(null)}
        onConfirm={handleDelete}
        variant="danger"
      />
    </div>
  );
}
