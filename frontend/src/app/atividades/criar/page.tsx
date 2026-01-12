"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";
import FormField from "@/components/FormField";
import SimpleNavbar from "@/components/SimpleNavbar";
import { activityService } from "@/services/activityService";
import { LAST_PROJECT_ID_KEY } from "@/lib/createFlowStorage";

type ActivityDraft = {
  idProjeto: string;
  nomeAtividade: string;
  dataHoraInicioAtividade: string;
  dataHoraFimAtividade: string;
  localAtividade: string;
  vagasTotais: string;
  descricaoAtividade: string;
};

const emptyDraft: ActivityDraft = {
  idProjeto: "",
  nomeAtividade: "",
  dataHoraInicioAtividade: "",
  dataHoraFimAtividade: "",
  localAtividade: "",
  vagasTotais: "",
  descricaoAtividade: "",
};

const inputClass =
  "w-full rounded-lg border border-[#B8B5FF] bg-white px-4 py-2 text-sm text-gray-700 shadow-sm focus:border-[#8F89FB] focus:outline-none focus:ring-2 focus:ring-[#8F89FB]/30";
const textareaClass = `${inputClass} min-h-[140px] resize-none`;

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

export default function CriarAtividadePage() {
  const searchParams = useSearchParams();
  const [formData, setFormData] = useState<ActivityDraft>(emptyDraft);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    const projetoIdParam = searchParams.get("projetoId");
    if (projetoIdParam) {
      setFormData((prev) => ({ ...prev, idProjeto: projetoIdParam }));
      return;
    }

    const lastProjectId = sessionStorage.getItem(LAST_PROJECT_ID_KEY);
    if (lastProjectId) {
      setFormData((prev) => ({ ...prev, idProjeto: lastProjectId }));
    }
  }, [searchParams]);

  const handleChange =
    (field: keyof ActivityDraft) =>
    (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setFormData((prev) => ({ ...prev, [field]: event.target.value }));
    };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setSuccess(null);

    if (!formData.idProjeto.trim()) {
      setError("Informe o ID do projeto.");
      return;
    }
    if (!formData.nomeAtividade.trim()) {
      setError("Informe o nome da atividade.");
      return;
    }
    if (!formData.dataHoraInicioAtividade || !formData.dataHoraFimAtividade) {
      setError("Informe as datas de início e término.");
      return;
    }
    if (!formData.vagasTotais.trim()) {
      setError("Informe o número de vagas.");
      return;
    }

    const startDate = new Date(formData.dataHoraInicioAtividade);
    const endDate = new Date(formData.dataHoraFimAtividade);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (startDate <= today) {
      setError("A data de início deve ser a partir de amanhã.");
      return;
    }
    if (endDate <= startDate) {
      setError("A data de término deve ser após a data de início.");
      return;
    }

    const vagas = Number(formData.vagasTotais);
    if (!Number.isFinite(vagas) || vagas <= 0) {
      setError("Informe um número de vagas válido.");
      return;
    }

    try {
      setLoading(true);
      const response = await activityService.createActivity({
        idProjeto: Number(formData.idProjeto),
        nomeAtividade: formData.nomeAtividade,
        descricaoAtividade: formData.descricaoAtividade,
        dataHoraInicioAtividade: toOffsetDateTime(
          formData.dataHoraInicioAtividade
        ),
        dataHoraFimAtividade: toOffsetDateTime(
          formData.dataHoraFimAtividade
        ),
        localAtividade: formData.localAtividade,
        vagasTotais: vagas,
      });
      setSuccess(response.message || "Atividade criada com sucesso.");
      setFormData((prev) => ({
        ...emptyDraft,
        idProjeto: prev.idProjeto,
      }));
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          err?.response?.data ||
          "Não foi possível criar a atividade."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-white">
      <SimpleNavbar />

      <main className="mx-auto max-w-6xl px-6 pb-20 pt-10">
        <h1 className="mb-8 text-xl font-semibold text-[#2A2599]">
          Informações sobre a atividade
        </h1>

        <form onSubmit={handleSubmit} className="space-y-8">
          <div className="grid gap-12 md:grid-cols-2">
            <div className="space-y-6">
              <FormField label="Nome da atividade" htmlFor="nomeAtividade">
                <input
                  id="nomeAtividade"
                  type="text"
                  value={formData.nomeAtividade}
                  onChange={handleChange("nomeAtividade")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField
                label="Data de início da atividade"
                htmlFor="dataHoraInicioAtividade"
              >
                <input
                  id="dataHoraInicioAtividade"
                  type="datetime-local"
                  value={formData.dataHoraInicioAtividade}
                  onChange={handleChange("dataHoraInicioAtividade")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField
                label="Data de término da atividade"
                htmlFor="dataHoraFimAtividade"
              >
                <input
                  id="dataHoraFimAtividade"
                  type="datetime-local"
                  value={formData.dataHoraFimAtividade}
                  onChange={handleChange("dataHoraFimAtividade")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField label="Local da atividade" htmlFor="localAtividade">
                <input
                  id="localAtividade"
                  type="text"
                  value={formData.localAtividade}
                  onChange={handleChange("localAtividade")}
                  className={inputClass}
                />
              </FormField>

              <button
                type="submit"
                className="mt-4 w-full rounded-lg bg-[#2A2599] py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-[#1f1b7a] disabled:cursor-not-allowed disabled:opacity-70"
                disabled={loading}
              >
                {loading ? "Salvando..." : "Prosseguir"}
              </button>
            </div>

            <div className="space-y-6 md:border-l md:border-[#B8B5FF] md:pl-12">
              <FormField label="ID do projeto" htmlFor="idProjeto">
                <input
                  id="idProjeto"
                  type="number"
                  value={formData.idProjeto}
                  onChange={handleChange("idProjeto")}
                  className={inputClass}
                  min={1}
                  required
                />
              </FormField>

              <FormField label="Número de vagas" htmlFor="vagasTotais">
                <input
                  id="vagasTotais"
                  type="number"
                  value={formData.vagasTotais}
                  onChange={handleChange("vagasTotais")}
                  className={inputClass}
                  min={1}
                  required
                />
              </FormField>

              <FormField label="Descrição da atividade" htmlFor="descricaoAtividade">
                <textarea
                  id="descricaoAtividade"
                  value={formData.descricaoAtividade}
                  onChange={handleChange("descricaoAtividade")}
                  className={textareaClass}
                />
              </FormField>
            </div>
          </div>

          {error && <p className="text-sm text-red-500">{error}</p>}
          {success && <p className="text-sm text-green-600">{success}</p>}
        </form>
      </main>
    </div>
  );
}
