import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Login | Voluntariado",
  description:
    "Faça login na plataforma de voluntariado para acessar seu perfil, gerenciar suas inscrições e participar de atividades.",
  keywords: [
    "login",
    "entrar",
    "voluntariado",
    "acesso",
    "autenticação",
  ],
  robots: {
    index: true,
    follow: true,
  },
  openGraph: {
    title: "Login - Plataforma de Voluntariado",
    description: "Acesse sua conta de voluntário",
    type: "website",
  },
};

export default function LoginLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}
