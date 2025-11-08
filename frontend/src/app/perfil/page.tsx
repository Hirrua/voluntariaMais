"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { volunteerService } from "@/services/volunteerService";
import { authService } from "@/services/authService";
import { InfoProfileResponse } from "@/types/volunteer";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import Image from "next/image";

export default function PerfilVoluntario() {
  const router = useRouter();
  const [profile, setProfile] = useState<InfoProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const handleLogout = async () => {
    try {
      await authService.logout();
      router.push("/login");
    } catch (err) {
      router.push("/login");
    }
  };

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

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
        <div className="flex justify-between items-center mb-8 md:mb-12">
          <h1 className="text-3xl md:text-4xl font-bold text-indigo-700">
            Perfil voluntário
          </h1>
          <button
            onClick={handleLogout}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-red-600 bg-red-50 hover:bg-red-100 border border-red-200 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
            aria-label="Sair da conta"
          >
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
              />
            </svg>
            Sair
          </button>
        </div>

        {/* Avatar */}
        <div className="mb-8 md:mb-12">
          <div className="relative w-32 h-32 md:w-40 md:h-40 rounded-full overflow-hidden bg-gray-200 border-4 border-white shadow-lg">
            <Image
              src="/api/placeholder/160/160"
              alt={`Foto de perfil de ${profile?.nome || "Voluntário"}`}
              fill
              className="object-cover"
              priority
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 md:gap-12">
          <div className="space-y-4">
            <InfoField label="Nome voluntário" value={profile?.nome} />
            <InfoField label="Sobrenome voluntário" value={profile?.sobrenome} />
            <InfoField
              label="Email de contato"
              value={profile?.email}
              type="email"
            />
            <InfoField
              label="Telefone de contato"
              value={profile?.telefoneContato}
              type="tel"
            />
            <InfoField label="Logradouro" value={profile?.endereco?.logradouro} />
            <InfoField label="Bairro" value={profile?.endereco?.bairro} />
            <InfoField label="Complemento" value={profile?.endereco?.complemento} />
            <InfoField label="Cidade" value={profile?.endereco?.cidade} />
            <InfoField label="Estado" value={profile?.endereco?.estado} />
            <InfoField label="CEP" value={profile?.endereco?.cep} />
          </div>

          <div className="space-y-4 md:pl-8 lg:pl-12 md:border-l border-gray-200">
            <InfoField label="Bio voluntário" value={profile?.bio} />
            <InfoField
              label="Disponibilidade"
              value={profile?.disponibilidade}
            />
            <InfoField
              label="Data de nascimento"
              value={profile?.dataNascimento}
              type="date"
            />
          </div>

          <div className="md:pl-8 lg:pl-12 md:border-l border-gray-200">
            <h2 className="text-xl font-bold text-indigo-700 mb-6">
              Inscrições
            </h2>
            <ul className="space-y-3" role="list">
              <InscricaoItem text="Agallhey of type and scrambled it to make" />
              <InscricaoItem text="Agallhey of type and scrambled it to make" />
              <InscricaoItem text="Agallhey of type and scrambled it to make" />
              <InscricaoItem text="Agallhey of type and scrambled it to make" />
            </ul>
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
    <div className="group">
      <dt className="text-sm font-medium text-gray-700 mb-1">{label}</dt>
      <dd
        className="text-base text-gray-900"
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