import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Perfil do Voluntário | Voluntariado",
  description:
    "Visualize e gerencie seu perfil de voluntário, incluindo informações pessoais, disponibilidade e inscrições em atividades.",
  keywords: [
    "perfil voluntário",
    "voluntariado",
    "dados pessoais",
    "inscrições",
    "atividades voluntárias",
  ],
  robots: {
    index: false,
    follow: true,
  },
  openGraph: {
    title: "Perfil do Voluntário",
    description:
      "Gerencie seu perfil de voluntário e acompanhe suas inscrições",
    type: "profile",
  },
};

export default function PerfilLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}
