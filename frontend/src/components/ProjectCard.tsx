interface ProjectCardProps {
  title: string;
  publicoAlvo: string; 
  objetivo: string;
}

export default function ProjectCard({ title, publicoAlvo, objetivo }: ProjectCardProps) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-xl transition-shadow border border-gray-100">
      <h3 className="text-xl font-bold text-[#2A2599] mb-3">{title}</h3>
      <p className="text-gray-800 leading-relaxed">{publicoAlvo}</p>
      <p className="text-gray-600 leading-relaxed">{objetivo}</p>
      <button className="mt-4 text-[#8F89FB] hover:text-[#2A2599] font-medium transition-colors">
        Ver mais →
      </button>
    </div>
  );
}
