"use client";

import type { ChangeEvent, FormEvent } from "react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import FormField from "@/components/FormField";
import { ONG_DRAFT_KEY, ONG_LOGO_KEY } from "@/lib/createFlowStorage";

type OngDraft = {
  nomeOng: string;
  emailContatoOng: string;
  cnpj: string;
  telefoneOng: string;
  website: string;
  dataFundacao: string;
  descricao: string;
};

type StoredLogo = {
  dataUrl: string;
  name: string;
  type: string;
};

const emptyDraft: OngDraft = {
  nomeOng: "",
  emailContatoOng: "",
  cnpj: "",
  telefoneOng: "",
  website: "",
  dataFundacao: "",
  descricao: "",
};

const inputClass =
  "w-full rounded-lg border border-[#B8B5FF] bg-white px-4 py-2 text-sm text-gray-700 shadow-sm focus:border-[#8F89FB] focus:outline-none focus:ring-2 focus:ring-[#8F89FB]/30";
const textareaClass = `${inputClass} min-h-[140px] resize-none`;

const fileToDataUrl = (file: File) =>
  new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(file);
  });

export default function CriarOngPage() {
  const router = useRouter();
  const [formData, setFormData] = useState<OngDraft>(emptyDraft);
  const [logo, setLogo] = useState<StoredLogo | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const savedDraft = sessionStorage.getItem(ONG_DRAFT_KEY);
    if (savedDraft) {
      try {
        setFormData(JSON.parse(savedDraft));
      } catch {
        sessionStorage.removeItem(ONG_DRAFT_KEY);
      }
    }

    const savedLogo = sessionStorage.getItem(ONG_LOGO_KEY);
    if (savedLogo) {
      try {
        setLogo(JSON.parse(savedLogo));
      } catch {
        sessionStorage.removeItem(ONG_LOGO_KEY);
      }
    }
  }, []);

  const handleChange =
    (field: keyof OngDraft) =>
    (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setFormData((prev) => ({ ...prev, [field]: event.target.value }));
    };

  const handleLogoChange = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) {
      setLogo(null);
      return;
    }

    try {
      const dataUrl = await fileToDataUrl(file);
      setLogo({ dataUrl, name: file.name, type: file.type });
    } catch {
      setLogo(null);
      setError("Não foi possível carregar o arquivo da logo.");
    }
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (!formData.nomeOng.trim()) {
      setError("Informe o nome da ONG.");
      return;
    }
    if (!formData.emailContatoOng.trim()) {
      setError("Informe o e-mail de contato.");
      return;
    }
    if (!formData.cnpj.trim()) {
      setError("Informe o CNPJ.");
      return;
    }

    sessionStorage.setItem(ONG_DRAFT_KEY, JSON.stringify(formData));
    if (logo) {
      sessionStorage.setItem(ONG_LOGO_KEY, JSON.stringify(logo));
    } else {
      sessionStorage.removeItem(ONG_LOGO_KEY);
    }
    router.push("/endereco?context=ong");
  };

  return (
    <div className="min-h-screen bg-white">
      <main className="mx-auto max-w-6xl px-6 pb-20 pt-10">
        <h1 className="mb-8 text-xl font-semibold text-[#2A2599]">
          Informações sobre a ONG
        </h1>

        <form onSubmit={handleSubmit} className="space-y-8">
          <div className="grid gap-12 md:grid-cols-2">
            <div className="space-y-6">
              <FormField label="Nome da ONG" htmlFor="nomeOng">
                <input
                  id="nomeOng"
                  type="text"
                  value={formData.nomeOng}
                  onChange={handleChange("nomeOng")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField label="E-mail de contato" htmlFor="emailContatoOng">
                <input
                  id="emailContatoOng"
                  type="email"
                  value={formData.emailContatoOng}
                  onChange={handleChange("emailContatoOng")}
                  className={inputClass}
                  required
                />
              </FormField>

              <FormField label="CNPJ" htmlFor="cnpj">
                <input
                  id="cnpj"
                  type="text"
                  inputMode="numeric"
                  value={formData.cnpj}
                  onChange={handleChange("cnpj")}
                  className={inputClass}
                  maxLength={14}
                  required
                />
              </FormField>

              <FormField label="Telefone de contato" htmlFor="telefoneOng">
                <input
                  id="telefoneOng"
                  type="tel"
                  value={formData.telefoneOng}
                  onChange={handleChange("telefoneOng")}
                  className={inputClass}
                  maxLength={20}
                />
              </FormField>

              <FormField label="Site da ONG" htmlFor="website">
                <input
                  id="website"
                  type="url"
                  value={formData.website}
                  onChange={handleChange("website")}
                  className={inputClass}
                />
              </FormField>
            </div>

            <div className="space-y-6 md:border-l md:border-[#B8B5FF] md:pl-12">
              <FormField label="Data da fundação" htmlFor="dataFundacao">
                <input
                  id="dataFundacao"
                  type="date"
                  value={formData.dataFundacao}
                  onChange={handleChange("dataFundacao")}
                  className={inputClass}
                />
              </FormField>

              <FormField label="Logo da ONG" htmlFor="logoOng">
                <div className="relative">
                  <input
                    id="logoOng"
                    type="file"
                    accept="image/*"
                    onChange={handleLogoChange}
                    className="absolute inset-0 z-10 cursor-pointer opacity-0"
                  />
                  <div className={`${inputClass} text-gray-500`}>
                    {logo?.name || "Selecione um arquivo"}
                  </div>
                </div>
              </FormField>

              <FormField label="Descrição" htmlFor="descricao">
                <textarea
                  id="descricao"
                  value={formData.descricao}
                  onChange={handleChange("descricao")}
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
