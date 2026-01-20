"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { volunteerService } from "@/services/volunteerService";
import { InfoProfileResponse, StatusInscricaoEnum, VolunteerSubscriptionResponse } from "@/types/volunteer";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

export default function PerfilVoluntario() {
  const [profile, setProfile] = useState<InfoProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [hasOng, setHasOng] = useState(false);
  const [subscriptions, setSubscriptions] = useState<VolunteerSubscriptionResponse[]>([]);
  const [subscriptionsError, setSubscriptionsError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfileData = async () => {
      try {
        setLoading(true);
        setError(null);

        const user = await volunteerService.getCurrentUser();

        setHasOng(Boolean(user.ongId));
        const profileData = await volunteerService.getProfile(user.id);
        setProfile(profileData);

        if (user.roles?.includes("ROLE_VOLUNTARIO")) {
          try {
            const subscriptionData = await volunteerService.getMySubscriptions();
            setSubscriptions(subscriptionData);
            setSubscriptionsError(null);
          } catch (subscriptionError) {
            setSubscriptions([]);
            setSubscriptionsError(
              subscriptionError instanceof Error
                ? subscriptionError.message
                : "Erro ao carregar inscricoes"
            );
          }
        } else {
          setSubscriptions([]);
          setSubscriptionsError(null);
        }
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
                  {hasOng ? (
                    <Link
                      href="/ong/perfil"
                      className="inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
                    >
                      Ir para o painel da ONG
                    </Link>
                  ) : (
                    <Link
                      href="/ong/criar"
                      className="inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
                    >
                      Cadastrar ONG
                    </Link>
                  )}
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
                  {subscriptionsError ? (
                    <li className="text-sm text-red-500">{subscriptionsError}</li>
                  ) : subscriptions.length === 0 ? (
                    <li className="text-sm text-gray-500">
                      Nenhuma inscricao encontrada
                    </li>
                  ) : (
                    subscriptions.map((subscription) => (
                      <InscricaoItem
                        key={subscription.id}
                        nome={subscription.infoActivitySubscription?.nomeAtividade || "-"}
                        status={subscription.statusInscricaoEnum}
                        dataInscricao={subscription.dataInscricao}
                      />
                    ))
                  )}
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

const formatSubscriptionDate = (value?: string | null) => {
  if (!value) {
    return null;
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return null;
  }
  return date.toLocaleDateString("pt-BR");
};

const formatSubscriptionStatus = (status: StatusInscricaoEnum) => {
  switch (status) {
    case "CONFIRMADA":
      return "Confirmada";
    case "CANCELADA_PELO_VOLUNTARIO":
      return "Cancelada";
    case "RECUSADA_PELA_ONG":
      return "Recusada";
    case "CONCLUIDA_PARTICIPACAO":
      return "Concluida";
    default:
      return "Pendente";
  }
};

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
  nome: string;
  status: StatusInscricaoEnum;
  dataInscricao?: string | null;
}

function InscricaoItem({ nome, status, dataInscricao }: InscricaoItemProps) {
  const dateLabel = formatSubscriptionDate(dataInscricao);
  const statusLabel = formatSubscriptionStatus(status);

  return (
    <li className="flex items-center justify-between rounded-lg border border-gray-100 px-3 py-2">
      <div>
        <p className="text-sm font-medium text-gray-900">{nome}</p>
        {dateLabel && (
          <p className="text-xs text-gray-500">Inscrito em {dateLabel}</p>
        )}
      </div>
      <span className="text-xs font-semibold text-gray-600">{statusLabel}</span>
    </li>
  );
}
