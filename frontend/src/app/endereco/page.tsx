"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import FormField from "@/components/FormField";
import {
  LAST_PROJECT_ID_KEY,
  ONG_DRAFT_KEY,
  ONG_LOGO_KEY,
  PROJECT_DRAFT_KEY,
} from "@/lib/createFlowStorage";
import { ongService } from "@/services/ongService";
import { projectService } from "@/services/projectService";
import { volunteerService } from "@/services/volunteerService";
import type { AddressPayload } from "@/types/address";
import type { CreateOngRequest } from "@/types/ong";
import type { CreateProjectRequest } from "@/types/project";

type StoredLogo = {
  dataUrl: string;
  name: string;
  type: string;
};

const emptyAddress: AddressPayload = {
  logradouro: "",
  bairro: "",
  complemento: "",
  cidade: "",
  estado: "",
  cep: "",
};

const inputClass =
  "w-full rounded-lg border border-[#B8B5FF] bg-white px-4 py-2 text-sm text-gray-700 shadow-sm focus:border-[#8F89FB] focus:outline-none focus:ring-2 focus:ring-[#8F89FB]/30";

const dataUrlToFile = async (storedLogo: StoredLogo) => {
  const response = await fetch(storedLogo.dataUrl);
  const blob = await response.blob();
  return new File([blob], storedLogo.name, { type: storedLogo.type });
};

export default function EnderecoPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [address, setAddress] = useState<AddressPayload>(emptyAddress);
  const [context, setContext] = useState<"ong" | "project" | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const contextParam = searchParams.get("context");
    if (contextParam === "ong" || contextParam === "project") {
      setContext(contextParam);
    } else {
      setContext(null);
    }
  }, [searchParams]);

  const handleChange =
    (field: keyof AddressPayload) =>
    (event: ChangeEvent<HTMLInputElement>) => {
      setAddress((prev) => ({ ...prev, [field]: event.target.value }));
    };

  const handleBack = () => {
    if (context === "ong") {
      router.push("/ong/criar");
      return;
    }
    if (context === "project") {
      router.push("/projetos/criar");
      return;
    }
    router.back();
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (!context) {
      setError("Não foi possível identificar o contexto do endereço.");
      return;
    }

    const missingField = Object.entries(address).find(
      ([, value]) => !value.trim()
    );
    if (missingField) {
      setError("Preencha todos os campos de endereço.");
      return;
    }

    try {
      setLoading(true);

      if (context === "ong") {
        const draftRaw = sessionStorage.getItem(ONG_DRAFT_KEY);
        if (!draftRaw) {
          setError("Preencha os dados da ONG antes do endereço.");
          return;
        }

        const draft = JSON.parse(draftRaw) as Omit<
          CreateOngRequest,
          "idUsuarioResponsavel" | "endereco"
        >;

        const user = await volunteerService.getCurrentUser();
        const payload: CreateOngRequest = {
          idUsuarioResponsavel: user.id,
          nomeOng: draft.nomeOng,
          cnpj: draft.cnpj,
          descricao: draft.descricao || "",
          emailContatoOng: draft.emailContatoOng,
          telefoneOng: draft.telefoneOng || "",
          website: draft.website || "",
          dataFundacao: draft.dataFundacao || null,
          endereco: address,
        };

        const response = await ongService.createOng(payload);
        const logoRaw = sessionStorage.getItem(ONG_LOGO_KEY);
        sessionStorage.removeItem(ONG_DRAFT_KEY);
        sessionStorage.removeItem(ONG_LOGO_KEY);

        let successMessage = "ONG criada com sucesso.";
        if (logoRaw && response.id) {
          try {
            const storedLogo = JSON.parse(logoRaw) as StoredLogo;
            const file = await dataUrlToFile(storedLogo);
            await ongService.uploadOngLogo(response.id, file);
          } catch {
            successMessage = "ONG criada, mas a logo nao foi enviada.";
          }
        }

        window.alert(successMessage);
        router.push("/");
        return;
      }

      const draftRaw = sessionStorage.getItem(PROJECT_DRAFT_KEY);
      if (!draftRaw) {
        setError("Preencha os dados do projeto antes do endereço.");
        return;
      }

      const draft = JSON.parse(draftRaw) as Omit<CreateProjectRequest, "endereco">;

      const payload: CreateProjectRequest = {
        nome: draft.nome,
        descricaoDetalhada: draft.descricaoDetalhada || "",
        objetivo: draft.objetivo || "",
        publicoAlvo: draft.publicoAlvo || "",
        dataInicioPrevista: draft.dataInicioPrevista || null,
        dataFimPrevista: draft.dataFimPrevista || null,
        endereco: address,
        urlImagemDestaque: null,
      };

      const response = await projectService.createProject(payload);
      sessionStorage.removeItem(PROJECT_DRAFT_KEY);

      if (response.id) {
        sessionStorage.setItem(LAST_PROJECT_ID_KEY, String(response.id));
      }

      window.alert("Projeto criado com sucesso.");
      router.push("/");
    } catch (err: any) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data ||
        "Não foi possível salvar o endereço.";
      window.alert(message);
      router.push("/");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-white">
      <main className="mx-auto max-w-6xl px-6 pb-20 pt-10">
        <div className="mb-8 flex items-center gap-3">
          <button
            type="button"
            onClick={handleBack}
            className="flex h-7 w-7 items-center justify-center rounded-full border border-[#B8B5FF] text-[#2A2599] transition hover:bg-[#F3F2FF]"
            aria-label="Voltar"
          >
            <svg
              className="h-4 w-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth={2}
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="m15 18-6-6 6-6" />
            </svg>
          </button>
          <h1 className="text-xl font-semibold text-[#2A2599]">Endereço</h1>
        </div>

        <form onSubmit={handleSubmit} className="space-y-8">
          <div className="grid gap-12 md:grid-cols-2">
            <div className="space-y-6">
              <FormField label="Logradouro" htmlFor="logradouro">
                <input
                  id="logradouro"
                  type="text"
                  value={address.logradouro}
                  onChange={handleChange("logradouro")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField label="Bairro" htmlFor="bairro">
                <input
                  id="bairro"
                  type="text"
                  value={address.bairro}
                  onChange={handleChange("bairro")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField label="Complemento" htmlFor="complemento">
                <input
                  id="complemento"
                  type="text"
                  value={address.complemento}
                  onChange={handleChange("complemento")}
                  className={inputClass}
                  required
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
              <FormField label="CEP" htmlFor="cep">
                <input
                  id="cep"
                  type="text"
                  inputMode="numeric"
                  value={address.cep}
                  onChange={handleChange("cep")}
                  className={inputClass}
                  maxLength={8}
                  required
                />
              </FormField>

              <FormField label="Cidade" htmlFor="cidade">
                <input
                  id="cidade"
                  type="text"
                  value={address.cidade}
                  onChange={handleChange("cidade")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField label="Estado" htmlFor="estado">
                <input
                  id="estado"
                  type="text"
                  value={address.estado}
                  onChange={(event) =>
                    setAddress((prev) => ({
                      ...prev,
                      estado: event.target.value.toUpperCase(),
                    }))
                  }
                  className={inputClass}
                  maxLength={2}
                  required
                />
              </FormField>
            </div>
          </div>

          {error && <p className="text-sm text-red-500">{error}</p>}
        </form>
      </main>
    </div>
  );
}
