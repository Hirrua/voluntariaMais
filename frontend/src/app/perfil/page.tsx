"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { volunteerService } from "@/services/volunteerService";
import { InfoProfileResponse } from "@/types/volunteer";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

export default function PerfilVoluntario() {
  const [profile, setProfile] = useState<InfoProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfileData = async () => {
      try {
        setLoading(true);
        setError(null);

        const user = await volunteerService.getCurrentUser();

        const profileData = await volunteerService.getProfile(user.id);
        setProfile(profileData);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Erro ao carregar dados do perfil"
        );
      } finally {
        setLoading(false);
      }
    };

    fetchProfileData();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        <Navbar />
        <main className="flex-grow container mx-auto px-4 py-8">
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-600"></div>
            <p className="text-gray-500 mt-4">Carregando perfil</p>
          </div>
        </main>
        <Footer />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        <Navbar />
        <main className="flex-grow container mx-auto px-4 py-8">
          <div className="text-center py-12">
            <p className="text-red-500 text-lg" role="alert">
              {error}
            </p>
          </div>
        </main>
        <Footer />
      </div>
    );
  }

  const profileImageSrc =
    profile?.fotoPerfilUrl?.trim() || "/logo_volunteer.png";
  const fullName = [profile?.nome, profile?.sobrenome]
    .filter(Boolean)
    .join(" ");
  const availabilityText =
    profile?.disponibilidade?.trim() || "Não informada";

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
        <div className="space-y-8 md:space-y-10">
          <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6 md:p-8">
            <div className="flex flex-col md:flex-row md:items-center md:gap-8">
              <div className="relative w-24 h-24 md:w-32 md:h-32 rounded-full overflow-hidden bg-gray-200 border-4 border-white shadow-md">
                <img
                  src={profileImageSrc}
                  alt={`Foto de perfil de ${profile?.nome || "Voluntário"}`}
                  className="h-full w-full object-cover"
                  width={160}
                  height={160}
                  loading="lazy"
                />
              </div>

              <div className="mt-4 md:mt-0">
                <p className="text-xs font-semibold uppercase tracking-[0.3em] text-gray-500">
                  Perfil voluntário
                </p>
                <h1 className="text-2xl md:text-3xl font-bold text-gray-900 mt-2">
                  {fullName || "Voluntário"}
                </h1>
                <div className="mt-4 flex flex-wrap gap-2 text-sm">
                  <span className="inline-flex items-center rounded-full bg-indigo-50 px-3 py-1 text-indigo-700">
                    Disponibilidade: {availabilityText}
                  </span>
                </div>
              </div>
            </div>
          </section>

          <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,2fr)_minmax(0,1fr)] gap-8">
            <div className="space-y-6">
              <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
                <h2 className="text-lg font-semibold text-gray-900">
                  Informações pessoais
                </h2>
                <dl className="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <InfoField label="Nome" value={profile?.nome} />
                  <InfoField label="Sobrenome" value={profile?.sobrenome} />
                  <InfoField
                    label="Data de nascimento"
                    value={profile?.dataNascimento}
                    type="date"
                  />
                  <InfoField
                    label="Disponibilidade"
                    value={profile?.disponibilidade}
                  />
                </dl>
              </section>

              <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
                <h2 className="text-lg font-semibold text-gray-900">
                  Contato
                </h2>
                <dl className="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <InfoField label="Email" value={profile?.email} type="email" />
                  <InfoField
                    label="Telefone"
                    value={profile?.telefoneContato}
                    type="tel"
                  />
                </dl>
              </section>

              <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
                <h2 className="text-lg font-semibold text-gray-900">
                  Endereço
                </h2>
                <dl className="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <InfoField label="Logradouro" value={profile?.endereco?.logradouro} />
                  <InfoField label="Bairro" value={profile?.endereco?.bairro} />
                  <InfoField label="Complemento" value={profile?.endereco?.complemento} />
                  <InfoField label="Cidade" value={profile?.endereco?.cidade} />
                  <InfoField label="Estado" value={profile?.endereco?.estado} />
                  <InfoField label="CEP" value={profile?.endereco?.cep} />
                </dl>
              </section>
            </div>

            <div className="space-y-6">
              <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
                <h2 className="text-lg font-semibold text-gray-900">
                  ONG
                </h2>
                <p className="mt-4 text-sm text-gray-700 leading-relaxed">
                  Representa uma ONG? Solicite o cadastro (sujeito à aprovação).
                </p>
                <div className="mt-6">
                  <Link
                    href="/ong/criar"
                    className="inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
                  >
                    Solicitar cadastro
                  </Link>
                </div>
              </section>

              <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
                <h2 className="text-lg font-semibold text-gray-900">
                  Bio do voluntário
                </h2>
                <p className="mt-4 text-sm text-gray-700 leading-relaxed">
                  {profile?.bio || "-"}
                </p>
              </section>

              <section className="rounded-2xl bg-white shadow-sm ring-1 ring-gray-200 p-6">
                <h2 className="text-lg font-semibold text-gray-900">
                  Inscrições
                </h2>
                <ul className="mt-4 space-y-3" role="list">
                  <InscricaoItem text="Agallhey of type and scrambled it to make" />
                  <InscricaoItem text="Agallhey of type and scrambled it to make" />
                  <InscricaoItem text="Agallhey of type and scrambled it to make" />
                  <InscricaoItem text="Agallhey of type and scrambled it to make" />
                </ul>
              </section>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}

interface InfoFieldProps {
  label: string;
  value?: string | null;
  type?: "text" | "email" | "tel" | "date";
}

function InfoField({ label, value, type = "text" }: InfoFieldProps) {
  const displayValue = value || "-";

  return (
    <div className="space-y-1">
      <dt className="text-xs font-semibold uppercase tracking-wide text-gray-500">
        {label}
      </dt>
      <dd
        className="text-sm text-gray-900 break-words"
        {...(type === "email" && { itemProp: "email" })}
        {...(type === "tel" && { itemProp: "telephone" })}
      >
        {displayValue}
      </dd>
    </div>
  );
}

interface InscricaoItemProps {
  text: string;
}

function InscricaoItem({ text }: InscricaoItemProps) {
  return (
    <li className="text-gray-900 hover:text-indigo-700 transition-colors">
      <a
        href="#"
        className="block underline decoration-gray-900 hover:decoration-indigo-700"
      >
        {text}
      </a>
    </li>
  );
}
