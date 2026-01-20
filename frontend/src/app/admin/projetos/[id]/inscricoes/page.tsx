"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { adminApiClient } from "@/services/adminApiClient";
import { Inscricao } from "@/types/admin";
import { OngProjectAndActivityInfo } from "@/types/projectInfo";
import { StatusInscricaoEnum } from "@/types/volunteer";
import { resolveAdminError } from "@/lib/adminErrors";
import PageHeader from "@/components/admin/PageHeader";
import ListPanel from "@/components/admin/ListPanel";
import ConfirmModal from "@/components/admin/ConfirmModal";
import AlertBanner from "@/components/admin/AlertBanner";

type ProjectSubscriptionsPageProps = {
  params: {
    id: string;
  };
};

const statusOptions: StatusInscricaoEnum[] = [
  "PENDENTE",
  "CONFIRMADA",
  "CANCELADA_PELO_VOLUNTARIO",
  "RECUSADA_PELA_ONG",
  "CONCLUIDA_PARTICIPACAO",
];

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

export default function ProjectSubscriptionsPage({ params }: ProjectSubscriptionsPageProps) {
  const router = useRouter();
  const pathname = usePathname();
  const projectId = Number(params.id);
  const [projectTitle, setProjectTitle] = useState("");
  const [subscriptions, setSubscriptions] = useState<Inscricao[]>([]);
  const [statusDrafts, setStatusDrafts] = useState<Record<number, StatusInscricaoEnum>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [actionId, setActionId] = useState<number | null>(null);
  const [actionType, setActionType] = useState<"update" | "delete" | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null);

  const isSubmitting = actionId !== null;

  const resetFeedback = () => {
    setErrorMessage(null);
    setSuccessMessage(null);
  };

  const filteredSubscriptions = useMemo(() => subscriptions, [subscriptions]);

  const handleError = (error: unknown) => {
    const message = resolveAdminError(error, router, pathname);
    if (message) {
      setErrorMessage(message);
    }
  };

  const loadData = async () => {
    try {
      setIsLoading(true);
      resetFeedback();
      const [projectInfo, list] = await Promise.all([
        adminApiClient.getProjeto(projectId),
        adminApiClient.listInscricoes(projectId),
      ]);

      const info = projectInfo as OngProjectAndActivityInfo;
      setProjectTitle(info.simpleInfoProjectResponse?.nome ?? "");
      setSubscriptions(list);

      const drafts: Record<number, StatusInscricaoEnum> = {};
      list.forEach((item) => {
        if (item.id && item.statusInscricaoEnum) {
          drafts[item.id] = item.statusInscricaoEnum;
        }
      });
      setStatusDrafts(drafts);
    } catch (error) {
      handleError(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!Number.isNaN(projectId)) {
      loadData();
    } else {
      setIsLoading(false);
      setErrorMessage("ID do projeto invalido");
    }
  }, [projectId]);

  const handleStatusChange = (id: number, value: StatusInscricaoEnum) => {
    setStatusDrafts((prev) => ({ ...prev, [id]: value }));
  };

  const handleUpdateStatus = async (id: number) => {
    const newStatus = statusDrafts[id];
    if (!newStatus) {
      return;
    }

    setActionId(id);
    setActionType("update");
    resetFeedback();

    try {
      await adminApiClient.updateInscricaoStatus(id, newStatus);
      setSuccessMessage("Status atualizado com sucesso");
      await loadData();
    } catch (error) {
      handleError(error);
    } finally {
      setActionId(null);
      setActionType(null);
    }
  };

  const handleDelete = async () => {
    if (confirmDeleteId === null) {
      return;
    }

    setActionId(confirmDeleteId);
    setActionType("delete");
    resetFeedback();

    try {
      await adminApiClient.deleteInscricao(confirmDeleteId);
      setSuccessMessage("Inscricao removida com sucesso");
      setConfirmDeleteId(null);
      await loadData();
    } catch (error) {
      handleError(error);
    } finally {
      setActionId(null);
      setActionType(null);
    }
  };

  return (
    <div className="space-y-8">
      <PageHeader
        title="Inscricoes do projeto"
        description={projectTitle || `Projeto ${params.id}`}
        actions={
          <Link
            href="/admin/projetos"
            className="inline-flex items-center text-sm font-semibold text-[#2A2599] hover:text-[#8F89FB]"
          >
            Voltar para projetos
          </Link>
        }
      />

      <AlertBanner variant="error" message={errorMessage} />
      <AlertBanner variant="success" message={successMessage} />

      <ListPanel title="Lista de inscricoes" count={filteredSubscriptions.length}>
        {isLoading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#8F89FB]"></div>
            <p className="text-gray-500 mt-4">Carregando inscricoes</p>
          </div>
        ) : filteredSubscriptions.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">Nenhuma inscricao encontrada</p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredSubscriptions.map((item) => {
              const id = item.id ?? 0;
              const status = statusDrafts[id] ?? item.statusInscricaoEnum ?? "PENDENTE";
              const isUpdating = actionId === id && actionType === "update";
              const isDeleting = actionId === id && actionType === "delete";

              return (
                <div
                  key={id || item.infoUserSubscription?.email}
                  className="rounded-lg border border-gray-100 px-4 py-4"
                >
                  <div className="grid grid-cols-1 gap-4 md:grid-cols-5 md:items-center">
                    <div>
                      <p className="text-xs text-gray-500">Nome</p>
                      <p className="text-sm font-semibold text-gray-900">
                        {item.infoUserSubscription?.nome ?? "Nao informado"}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Email</p>
                      <p className="text-sm text-gray-700">
                        {item.infoUserSubscription?.email ?? "Nao informado"}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Telefone</p>
                      <p className="text-sm text-gray-700">Nao informado</p>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Data</p>
                      <p className="text-sm text-gray-700">
                        {formatDate(item.dataInscricao)}
                      </p>
                    </div>
                    <div className="space-y-2">
                      <p className="text-xs text-gray-500">Status</p>
                      <select
                        value={status}
                        onChange={(event) =>
                          handleStatusChange(id, event.target.value as StatusInscricaoEnum)
                        }
                        className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20"
                      >
                        {statusOptions.map((option) => (
                          <option key={option} value={option}>
                            {option}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="mt-4 flex flex-wrap gap-3 text-sm">
                    <button
                      type="button"
                      onClick={() => handleUpdateStatus(id)}
                      disabled={isUpdating || isSubmitting}
                      className="rounded-lg bg-[#2A2599] px-4 py-2 font-semibold text-white transition-colors hover:bg-[#1f1b7a] disabled:cursor-not-allowed disabled:opacity-70"
                    >
                      {isUpdating ? "Salvando..." : "Salvar status"}
                    </button>
                    <button
                      type="button"
                      onClick={() => setConfirmDeleteId(id)}
                      disabled={isSubmitting}
                      className="rounded-lg border border-red-200 px-4 py-2 font-semibold text-red-600 transition-colors hover:border-red-300 hover:text-red-700 disabled:cursor-not-allowed disabled:opacity-70"
                    >
                      {isDeleting ? "Removendo..." : "Remover inscricao"}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </ListPanel>

      <ConfirmModal
        open={confirmDeleteId !== null}
        title="Confirmar remocao"
        description="Tem certeza que deseja remover esta inscricao? Esta acao nao pode ser desfeita."
        confirmLabel="Remover"
        loadingLabel="Removendo..."
        isLoading={isSubmitting}
        onCancel={() => setConfirmDeleteId(null)}
        onConfirm={handleDelete}
        variant="danger"
      />
    </div>
  );
}
