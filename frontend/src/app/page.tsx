"use client";

import { useState } from "react";
import Navbar from "@/components/Navbar";
import ProjectCard from "@/components/ProjectCard";
import Footer from "@/components/Footer";

// Dados mockados dos projetos
const mockProjects = [
  {
    id: 1,
    title: "Alfabetização para Crianças",
    description: "Projeto de ensino de leitura e escrita para crianças em comunidades carentes. Buscamos voluntários com experiência em educação.",
  },
  {
    id: 2,
    title: "Apoio a Idosos",
    description: "Programa de companhia e assistência para idosos em casas de repouso. Atividades incluem conversas, jogos e passeios.",
  },
  {
    id: 3,
    title: "Preservação Ambiental",
    description: "Ações de limpeza e preservação de áreas verdes urbanas. Junte-se a nós para tornar nossa cidade mais sustentável.",
  },
  {
    id: 4,
    title: "Distribuição de Alimentos",
    description: "Organização e distribuição de cestas básicas para famílias em situação de vulnerabilidade social.",
  },
  {
    id: 5,
    title: "Aulas de Tecnologia",
    description: "Ensino de informática básica e programação para jovens da periferia. Compartilhe seu conhecimento em tech!",
  },
  {
    id: 6,
    title: "Proteção Animal",
    description: "Cuidado e resgate de animais abandonados. Procuramos voluntários para ajudar no abrigo e em campanhas de adoção.",
  },
];

export default function Home() {
  const [searchTerm, setSearchTerm] = useState("");

  const filteredProjects = mockProjects.filter(
    (project) =>
      project.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      project.description.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="mb-8 flex justify-start">
          <div className="w-full md:w-96">
            <input
              type="text"
              placeholder="Buscar projetos"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:border-[#8F89FB] focus:ring-2 focus:ring-[#8F89FB] focus:ring-opacity-20 transition-all"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProjects.map((project) => (
            <ProjectCard
              key={project.id}
              title={project.title}
              description={project.description}
            />
          ))}
        </div>

        {filteredProjects.length === 0 && (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">
              Nenhum projeto encontrado para "{searchTerm}"
            </p>
          </div>
        )}
      </main>

      <Footer />
    </div>
  );
}
