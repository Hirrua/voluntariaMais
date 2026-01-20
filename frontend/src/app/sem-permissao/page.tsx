"use client";

import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import Link from "next/link";

export default function SemPermissaoPage() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <main className="flex-grow container mx-auto px-4 py-12">
        <div className="rounded-lg border border-gray-100 bg-white shadow-md p-8 text-center">
          <h1 className="text-2xl md:text-3xl font-bold text-[#2A2599]">
            Sem permissao
          </h1>
          <p className="text-gray-600 mt-3">
            Voce nao tem acesso a esta area. Se precisar, fale com o suporte.
          </p>
          <div className="mt-6">
            <Link
              href="/"
              className="inline-flex items-center justify-center rounded-lg bg-[#2A2599] px-5 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#1f1b7a]"
            >
              Voltar para a Home
            </Link>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
