import Link from "next/link";

interface ProjectCardProps {
  id: number;
  title: string;
  publicoAlvo: string;
  objetivo: string;
  imageUrl?: string | null;
}

export default function ProjectCard({
  id,
  title,
  publicoAlvo,
  objetivo,
  imageUrl,
}: ProjectCardProps) {
  const coverImage = imageUrl?.trim() || "/logo_volunteer.png";

  return (
    <div className="overflow-hidden rounded-lg border border-gray-100 bg-white shadow-md transition-shadow hover:shadow-xl">
      <div className="h-40 w-full overflow-hidden bg-gray-100">
        <img
          src={coverImage}
          alt={title || "Imagem do projeto"}
          className="h-full w-full object-cover"
          loading="lazy"
        />
      </div>

      <div className="p-6">
        <h3 className="mb-3 text-xl font-bold text-[#2A2599]">{title}</h3>
        <p className="text-gray-800 leading-relaxed">{publicoAlvo}</p>
        <p className="text-gray-600 leading-relaxed">{objetivo}</p>
        <Link
          href={`/projetos/${id}`}
          className="mt-4 inline-flex font-medium text-[#8F89FB] transition-colors hover:text-[#2A2599]"
        >
          Ver mais →
        </Link>
      </div>
    </div>
  );
}
