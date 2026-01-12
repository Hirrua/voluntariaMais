"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import FormField from "@/components/FormField";
import SimpleNavbar from "@/components/SimpleNavbar";
import {
  LAST_ONG_ID_KEY,
  PROJECT_DRAFT_KEY,
} from "@/lib/createFlowStorage";

type ProjectDraft = {
  idOng: string;
  nome: string;
  objetivo: string;
  publicoAlvo: string;
  dataInicioPrevista: string;
  dataFimPrevista: string;
  descricaoDetalhada: string;
};

const emptyDraft: ProjectDraft = {
  idOng: "",
  nome: "",
  objetivo: "",
  publicoAlvo: "",
  dataInicioPrevista: "",
  dataFimPrevista: "",
  descricaoDetalhada: "",
};

const inputClass =
  "w-full rounded-lg border border-[#B8B5FF] bg-white px-4 py-2 text-sm text-gray-700 shadow-sm focus:border-[#8F89FB] focus:outline-none focus:ring-2 focus:ring-[#8F89FB]/30";
const textareaClass = `${inputClass} min-h-[160px] resize-none`;

export default function CriarProjetoPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [formData, setFormData] = useState<ProjectDraft>(emptyDraft);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const savedDraft = sessionStorage.getItem(PROJECT_DRAFT_KEY);
    if (savedDraft) {
      try {
        setFormData(JSON.parse(savedDraft));
        return;
      } catch {
        sessionStorage.removeItem(PROJECT_DRAFT_KEY);
      }
    }

    const ongIdParam = searchParams.get("ongId");
    if (ongIdParam) {
      setFormData((prev) => ({ ...prev, idOng: ongIdParam }));
      return;
    }

    const lastOngId = sessionStorage.getItem(LAST_ONG_ID_KEY);
    if (lastOngId) {
      setFormData((prev) => ({ ...prev, idOng: lastOngId }));
    }
  }, [searchParams]);

  const handleChange =
    (field: keyof ProjectDraft) =>
    (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setFormData((prev) => ({ ...prev, [field]: event.target.value }));
    };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (!formData.idOng.trim()) {
      setError("Informe o ID da ONG.");
      return;
    }
    if (!formData.nome.trim()) {
      setError("Informe o nome do projeto.");
      return;
    }

    sessionStorage.setItem(PROJECT_DRAFT_KEY, JSON.stringify(formData));
    router.push("/endereco?context=project");
  };

  return (
    <div className="min-h-screen bg-white">
      <SimpleNavbar />

      <main className="mx-auto max-w-6xl px-6 pb-20 pt-10">
        <h1 className="mb-8 text-xl font-semibold text-[#2A2599]">
          Informações sobre o projeto
        </h1>

        <form onSubmit={handleSubmit} className="space-y-8">
          <div className="grid gap-12 md:grid-cols-2">
            <div className="space-y-6">
              <FormField label="ID da ONG" htmlFor="idOng">
                <input
                  id="idOng"
                  type="number"
                  value={formData.idOng}
                  onChange={handleChange("idOng")}
                  className={inputClass}
                  min={1}
                  required
                />
              </FormField>

              <FormField label="Nome do projeto" htmlFor="nomeProjeto">
                <input
                  id="nomeProjeto"
                  type="text"
                  value={formData.nome}
                  onChange={handleChange("nome")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField label="Objetivo do projeto" htmlFor="objetivo">
                <input
                  id="objetivo"
                  type="text"
                  value={formData.objetivo}
                  onChange={handleChange("objetivo")}
                  className={inputClass}
                />
              </FormField>

              <FormField label="Público alvo" htmlFor="publicoAlvo">
                <input
                  id="publicoAlvo"
                  type="text"
                  value={formData.publicoAlvo}
                  onChange={handleChange("publicoAlvo")}
                  className={inputClass}
                />
              </FormField>

              <FormField
                label="Data do início do projeto"
                htmlFor="dataInicio"
              >
                <input
                  id="dataInicio"
                  type="date"
                  value={formData.dataInicioPrevista}
                  onChange={handleChange("dataInicioPrevista")}
                  className={inputClass}
                />
              </FormField>
            </div>

            <div className="space-y-6 md:border-l md:border-[#B8B5FF] md:pl-12">
              <FormField label="Data do fim do projeto" htmlFor="dataFim">
                <input
                  id="dataFim"
                  type="date"
                  value={formData.dataFimPrevista}
                  onChange={handleChange("dataFimPrevista")}
                  className={inputClass}
                />
              </FormField>

              <FormField label="Descrição detalhada" htmlFor="descricaoDetalhada">
                <textarea
                  id="descricaoDetalhada"
                  value={formData.descricaoDetalhada}
                  onChange={handleChange("descricaoDetalhada")}
                  className={textareaClass}
                />
              </FormField>

              <button
                type="submit"
                className="mt-4 w-full rounded-lg bg-[#2A2599] py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-[#1f1b7a]"
              >
                Prosseguir
              </button>
            </div>
          </div>

          {error && <p className="text-sm text-red-500">{error}</p>}
        </form>
      </main>
    </div>
  );
}
