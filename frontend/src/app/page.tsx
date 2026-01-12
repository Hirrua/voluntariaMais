"use client";

import { useState, useEffect } from "react";
import Navbar from "@/components/Navbar";
import ProjectCard from "@/components/ProjectCard";
import Footer from "@/components/Footer";
import { OngDTO } from "@/types/ong";
import { ongService } from "@/services/ongService";
import { projectService } from "@/services/projectService";
import { ProjectDTO } from "@/types/project";

export default function Home() {
  const [searchTerm, setSearchTerm] = useState("");
  const [projects, setProjects] = useState<ProjectDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await projectService.getProjects({ page: 0, itens: 6 });
        setProjects(data.content);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Erro ao buscar Porjetos");
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, []);

  const filteredProjects = projects.filter(
    (projects) =>
      projects.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
      projects.publicoAlvo.toLowerCase().includes(searchTerm.toLowerCase())
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

        {loading && (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#8F89FB]"></div>
            <p className="text-gray-500 mt-4">Carregando projetos</p>
          </div>
        )}

        {error && (
          <div className="text-center py-12">
            <p className="text-red-500 text-lg">{error}</p>
          </div>
        )}

        {!loading && !error && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredProjects.map((ong) => (
                <ProjectCard
                  key={ong.id}
                  id={ong.id}
                  title={ong.nome}
                  publicoAlvo={ong.publicoAlvo}
                  objetivo={ong.objetivo}
                  imageUrl={ong.urlImagemDestaque}
                />
              ))}
            </div>

            {filteredProjects.length === 0 && (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">
                  Nenhum projeto encontrado
                </p>
              </div>
            )}
          </>
        )}
      </main>

      <Footer />
    </div>
  );
}
