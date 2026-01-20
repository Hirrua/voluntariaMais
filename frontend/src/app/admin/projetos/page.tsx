"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { adminApiClient } from "@/services/adminApiClient";
import { Projeto } from "@/types/admin";
import { OngProjectAndActivityInfo } from "@/types/projectInfo";
import { resolveAdminError } from "@/lib/adminErrors";
import PageHeader from "@/components/admin/PageHeader";
import ListPanel from "@/components/admin/ListPanel";
import ConfirmModal from "@/components/admin/ConfirmModal";
import AlertBanner from "@/components/admin/AlertBanner";

type FormMode = "create" | "edit";

type FormState = {
  nome: string;
  descricaoDetalhada: string;
  publicoAlvo: string;
};

type FormErrors = Partial<Record<keyof FormState, string>>;

type ProjectRow = Projeto & {
  ongNome?: string | null;
};

const initialFormState: FormState = {
  nome: "",
  descricaoDetalhada: "",
  publicoAlvo: "",
};

export default function AdminProjetosPage() {
  const router = useRouter();
  const pathname = usePathname();
  const [projects, setProjects] = useState<ProjectRow[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
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
  const [editingOngLabel, setEditingOngLabel] = useState("");

  const filteredProjects = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();
    if (!term) {
      return projects;
    }
    return projects.filter((project) =>
      (project.nome ?? "").toLowerCase().includes(term)
    );
  }, [projects, searchTerm]);

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

  const loadProjects = async () => {
    try {
      setIsLoading(true);
      resetFeedback();
      const response = await adminApiClient.listProjetos({ page: 0, itens: 50 });
      const list = response.content ?? [];

      const enriched = await Promise.all(
        list.map(async (project) => {
          if (!project.id) {
            return project;
          }

          try {
            const info: OngProjectAndActivityInfo = await adminApiClient.getProjeto(project.id);
            const simple = info.simpleInfoProjectResponse;
            return {
              ...project,
              nome: simple?.nome ?? project.nome,
              publicoAlvo: simple?.publicoAlvo ?? project.publicoAlvo,
              ongNome: info.ongContextResponse?.nomeOng ?? null,
            };
          } catch (error) {
            handleError(error);
            return project;
          }
        })
      );

      setProjects(enriched);
    } catch (error) {
      handleError(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  const openCreateForm = () => {
    resetFeedback();
    setFormMode("create");
    setEditingId(null);
    setEditingOngLabel("");
    setFormState(initialFormState);
    setFormErrors({});
    setIsFormOpen(true);
  };

  const openEditForm = (project: ProjectRow) => {
    resetFeedback();
    setFormMode("edit");
    setEditingId(project.id ?? null);
    setEditingOngLabel(project.ongNome ?? "");
    setFormState({
      nome: project.nome ?? "",
      descricaoDetalhada: "",
      publicoAlvo: project.publicoAlvo ?? "",
    });
    setFormErrors({});
    setIsFormOpen(true);
  };

  const validateForm = () => {
    const errors: FormErrors = {};

    if (!formState.nome.trim()) {
      errors.nome = "Informe o titulo";
    }

    if (!formState.publicoAlvo.trim()) {
      errors.publicoAlvo = "Informe o publico alvo";
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

    const payload: Projeto = {
      nome: formState.nome.trim(),
      publicoAlvo: formState.publicoAlvo.trim(),
      descricaoDetalhada: formState.descricaoDetalhada.trim() || undefined,
    };

    try {
      if (formMode === "create") {
        await adminApiClient.createProjeto(payload);
        setSuccessMessage("Projeto criado com sucesso");
      } else if (editingId !== null) {
        await adminApiClient.updateProjeto(editingId, payload);
        setSuccessMessage("Projeto atualizado com sucesso");
      }

      setIsFormOpen(false);
      await loadProjects();
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
      await adminApiClient.deleteProjeto(confirmDeleteId);
      setSuccessMessage("Projeto excluido com sucesso");
      setConfirmDeleteId(null);
      await loadProjects();
    } catch (error) {
      handleError(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-8">
      <PageHeader
        title="Projetos"
        description="Acompanhe os projetos e acesse atividades e inscricoes."
        actions={
          <button
            type="button"
            onClick={openCreateForm}
            className="inline-flex items-center justify-center rounded-lg bg-[#8F89FB] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#2A2599]"
          >
            Novo projeto
          </button>
        }
      />

      <AlertBanner variant="error" message={errorMessage} />
      <AlertBanner variant="success" message={successMessage} />

      <ListPanel
        title="Lista de projetos"
        count={filteredProjects.length}
        actions={
          <input
            type="text"
            placeholder="Buscar por titulo"
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20 transition-all"
          />
        }
      >
        {isLoading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#8F89FB]"></div>
            <p className="text-gray-500 mt-4">Carregando projetos</p>
          </div>
        ) : filteredProjects.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">Nenhum projeto encontrado</p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredProjects.map((project) => (
              <div
                key={project.id ?? project.nome}
                className="flex flex-col gap-4 rounded-lg border border-gray-100 px-4 py-4 md:flex-row md:items-center md:justify-between"
              >
                <div>
                  <p className="text-sm font-semibold text-gray-900">
                    {project.nome ?? "Sem titulo"}
                  </p>
                  <div className="mt-1 flex flex-wrap gap-2 text-xs text-gray-500">
                    <span>{project.publicoAlvo ?? "Publico alvo nao informado"}</span>
                    {project.ongNome && <span>- {project.ongNome}</span>}
                  </div>
                </div>

                <div className="flex flex-col gap-2 md:flex-row md:items-center">
                  <div className="flex flex-wrap gap-3 text-sm">
                    {project.id && (
                      <>
                        <Link
                          href={`/admin/projetos/${project.id}`}
                          className="font-semibold text-[#2A2599] hover:text-[#8F89FB]"
                        >
                          Painel
                        </Link>
                        <Link
                          href={`/admin/projetos/${project.id}/atividades`}
                          className="font-semibold text-[#2A2599] hover:text-[#8F89FB]"
                        >
                          Atividades
                        </Link>
                        <Link
                          href={`/admin/projetos/${project.id}/inscricoes`}
                          className="font-semibold text-[#2A2599] hover:text-[#8F89FB]"
                        >
                          Inscricoes
                        </Link>
                      </>
                    )}
                    <button
                      type="button"
                      onClick={() => openEditForm(project)}
                      className="font-semibold text-[#2A2599] hover:text-[#8F89FB]"
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      onClick={() => setConfirmDeleteId(project.id ?? null)}
                      className="font-semibold text-red-600 hover:text-red-700"
                    >
                      Excluir
                    </button>
                  </div>
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
                {formMode === "create" ? "Novo projeto" : "Editar projeto"}
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
                    Titulo
                  </label>
                  <input
                    type="text"
                    value={formState.nome}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        nome: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                  {formErrors.nome && (
                    <p className="text-xs text-red-500">{formErrors.nome}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Publico alvo
                  </label>
                  <input
                    type="text"
                    value={formState.publicoAlvo}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        publicoAlvo: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                  {formErrors.publicoAlvo && (
                    <p className="text-xs text-red-500">
                      {formErrors.publicoAlvo}
                    </p>
                  )}
                </div>

                <div className="space-y-2 md:col-span-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Descricao
                  </label>
                  <textarea
                    value={formState.descricaoDetalhada}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        descricaoDetalhada: event.target.value,
                      }))
                    }
                    rows={4}
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                </div>

                <div className="space-y-2 md:col-span-2">
                  <label className="text-sm font-semibold text-gray-700">
                    ONG vinculada
                  </label>
                  {formMode === "create" ? (
                    <p className="text-sm text-gray-500">
                      A ONG sera definida automaticamente pelo usuario logado.
                    </p>
                  ) : (
                    <input
                      type="text"
                      value={editingOngLabel || "Nao informado"}
                      disabled
                      className="w-full px-3 py-2 rounded-lg border border-gray-200 bg-gray-50 text-gray-500"
                    />
                  )}
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
        description="Tem certeza que deseja excluir este projeto? Esta acao nao pode ser desfeita."
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
