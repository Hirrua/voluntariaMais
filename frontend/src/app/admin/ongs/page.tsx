"use client";

import { useEffect, useMemo, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { adminApiClient } from "@/services/adminApiClient";
import { Ong } from "@/types/admin";
import { OngApprovalStatus } from "@/types/ongInfo";
import { resolveAdminError } from "@/lib/adminErrors";
import PageHeader from "@/components/admin/PageHeader";
import ListPanel from "@/components/admin/ListPanel";
import ConfirmModal from "@/components/admin/ConfirmModal";
import AlertBanner from "@/components/admin/AlertBanner";

type FormMode = "create" | "edit";

type FormState = {
  nomeOng: string;
  emailContatoOng: string;
  telefoneOng: string;
  website: string;
  descricao: string;
  status: OngApprovalStatus | "";
};

type FormErrors = Partial<Record<keyof FormState, string>>;

const initialFormState: FormState = {
  nomeOng: "",
  emailContatoOng: "",
  telefoneOng: "",
  website: "",
  descricao: "",
  status: "PENDENTE",
};

const statusOptions: OngApprovalStatus[] = ["PENDENTE", "APROVADA", "REJEITADA"];

export default function AdminOngsPage() {
  const router = useRouter();
  const pathname = usePathname();
  const [ongs, setOngs] = useState<Ong[]>([]);
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

  const filteredOngs = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();
    if (!term) {
      return ongs;
    }
    return ongs.filter((ong) =>
      (ong.nomeOng ?? "").toLowerCase().includes(term)
    );
  }, [ongs, searchTerm]);

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

  const loadOngs = async () => {
    try {
      setIsLoading(true);
      resetFeedback();
      const response = await adminApiClient.listONGs({ page: 0, itens: 50 });
      setOngs(Array.isArray(response) ? response : []);
    } catch (error) {
      handleError(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadOngs();
  }, []);

  const openCreateForm = () => {
    resetFeedback();
    setFormMode("create");
    setEditingId(null);
    setFormState(initialFormState);
    setFormErrors({});
    setIsFormOpen(true);
  };

  const openEditForm = (ong: Ong) => {
    resetFeedback();
    setFormMode("edit");
    setEditingId(ong.id ?? null);
    setFormState({
      nomeOng: ong.nomeOng ?? "",
      emailContatoOng: ong.emailContatoOng ?? "",
      telefoneOng: ong.telefoneOng ?? "",
      website: ong.website ?? "",
      descricao: ong.descricao ?? "",
      status: ong.status ?? "PENDENTE",
    });
    setFormErrors({});
    setIsFormOpen(true);
  };

  const validateForm = () => {
    const errors: FormErrors = {};

    if (!formState.nomeOng.trim()) {
      errors.nomeOng = "Informe o nome";
    }

    if (!formState.emailContatoOng.trim()) {
      errors.emailContatoOng = "Informe o email";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formState.emailContatoOng)) {
      errors.emailContatoOng = "Email invalido";
    }

    if (!formState.status) {
      errors.status = "Selecione um status";
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

    const payload: Ong = {
      nomeOng: formState.nomeOng.trim(),
      emailContatoOng: formState.emailContatoOng.trim(),
      telefoneOng: formState.telefoneOng.trim() || undefined,
      website: formState.website.trim() || undefined,
      descricao: formState.descricao.trim() || undefined,
      status: formState.status || undefined,
    };

    try {
      if (formMode === "create") {
        await adminApiClient.createONG(payload);
        setSuccessMessage("ONG criada com sucesso");
      } else if (editingId !== null) {
        await adminApiClient.updateONG(editingId, payload);
        setSuccessMessage("ONG atualizada com sucesso");
      }

      setIsFormOpen(false);
      await loadOngs();
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
      await adminApiClient.deleteONG(confirmDeleteId);
      setSuccessMessage("ONG excluida com sucesso");
      setConfirmDeleteId(null);
      await loadOngs();
    } catch (error) {
      handleError(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-8">
      <PageHeader
        title="ONGs cadastradas"
        description="Visao geral das ONGs e status de aprovacao."
        actions={
          <button
            type="button"
            onClick={openCreateForm}
            className="inline-flex items-center justify-center rounded-lg bg-[#8F89FB] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#2A2599]"
          >
            Nova ONG
          </button>
        }
      />

      <AlertBanner variant="error" message={errorMessage} />
      <AlertBanner variant="success" message={successMessage} />

      <ListPanel
        title="Lista de ONGs"
        count={filteredOngs.length}
        actions={
          <input
            type="text"
            placeholder="Buscar por nome"
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20 transition-all"
          />
        }
      >
        {isLoading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#8F89FB]"></div>
            <p className="text-gray-500 mt-4">Carregando ONGs</p>
          </div>
        ) : filteredOngs.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">Nenhuma ONG encontrada</p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredOngs.map((ong) => (
              <div
                key={ong.id ?? ong.nomeOng}
                className="flex flex-col gap-4 rounded-lg border border-gray-100 px-4 py-4 md:flex-row md:items-center md:justify-between"
              >
                <div>
                  <p className="text-sm font-semibold text-gray-900">
                    {ong.nomeOng ?? "Sem nome"}
                  </p>
                  <div className="mt-1 flex flex-wrap gap-2 text-xs text-gray-500">
                    <span>{ong.emailContatoOng ?? "Email nao informado"}</span>
                    {ong.telefoneOng && <span>- {ong.telefoneOng}</span>}
                  </div>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                  <span className="inline-flex items-center rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold text-gray-700">
                    {ong.status ?? "PENDENTE"}
                  </span>
                  <button
                    type="button"
                    onClick={() => openEditForm(ong)}
                    className="text-sm font-semibold text-[#2A2599] hover:text-[#8F89FB]"
                  >
                    Editar
                  </button>
                  <button
                    type="button"
                    onClick={() => setConfirmDeleteId(ong.id ?? null)}
                    className="text-sm font-semibold text-red-600 hover:text-red-700"
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
                {formMode === "create" ? "Nova ONG" : "Editar ONG"}
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
                    value={formState.nomeOng}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        nomeOng: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                  {formErrors.nomeOng && (
                    <p className="text-xs text-red-500">{formErrors.nomeOng}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Email
                  </label>
                  <input
                    type="email"
                    value={formState.emailContatoOng}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        emailContatoOng: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                  {formErrors.emailContatoOng && (
                    <p className="text-xs text-red-500">
                      {formErrors.emailContatoOng}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Telefone
                  </label>
                  <input
                    type="text"
                    value={formState.telefoneOng}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        telefoneOng: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Site
                  </label>
                  <input
                    type="url"
                    value={formState.website}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        website: event.target.value,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  />
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Status
                  </label>
                  <select
                    value={formState.status}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        status: event.target.value as OngApprovalStatus,
                      }))
                    }
                    className="w-full px-3 py-2 rounded-lg border border-gray-300 bg-white focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                  >
                    <option value="" disabled>
                      Selecione
                    </option>
                    {statusOptions.map((status) => (
                      <option key={status} value={status}>
                        {status}
                      </option>
                    ))}
                  </select>
                  {formErrors.status && (
                    <p className="text-xs text-red-500">{formErrors.status}</p>
                  )}
                </div>

                <div className="space-y-2 md:col-span-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Descricao
                  </label>
                  <textarea
                    value={formState.descricao}
                    onChange={(event) =>
                      setFormState((prev) => ({
                        ...prev,
                        descricao: event.target.value,
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
        description="Tem certeza que deseja excluir esta ONG? Esta acao nao pode ser desfeita."
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
