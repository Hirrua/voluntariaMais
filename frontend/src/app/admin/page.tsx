import Link from "next/link";
import PageHeader from "@/components/admin/PageHeader";

export default function AdminPage() {
  return (
    <div className="space-y-8">
      <PageHeader
        title="Painel Administrativo"
        description="Organize ONGs, projetos, atividades e inscricoes com uma visao central das operacoes."
      />

      <section className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Link
          href="/admin/ongs"
          className="group rounded-lg border border-gray-100 bg-white shadow-md transition-shadow hover:shadow-xl"
        >
          <div className="p-6">
            <h2 className="text-xl font-bold text-[#2A2599]">
            Gerenciar ONGs
            </h2>
            <p className="text-gray-600 mt-3">
              Acompanhe aprovacoes, cadastros e dados publicos das ONGs.
            </p>
            <span className="mt-4 inline-flex font-medium text-[#8F89FB] transition-colors group-hover:text-[#2A2599]">
              Ver mais -&gt;
            </span>
          </div>
        </Link>

        <Link
          href="/admin/projetos"
          className="group rounded-lg border border-gray-100 bg-white shadow-md transition-shadow hover:shadow-xl"
        >
          <div className="p-6">
            <h2 className="text-xl font-bold text-[#2A2599]">
              Gerenciar Projetos
            </h2>
            <p className="text-gray-600 mt-3">
              Navegue por projetos ativos, atividades e inscricoes pendentes.
            </p>
            <span className="mt-4 inline-flex font-medium text-[#8F89FB] transition-colors group-hover:text-[#2A2599]">
              Ver mais -&gt;
            </span>
          </div>
        </Link>
      </section>
    </div>
  );
}
