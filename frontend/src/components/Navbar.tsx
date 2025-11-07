import Link from "next/link";
import Image from "next/image";

export default function Navbar() {
  return (
    <nav className="bg-white shadow-md">
      <div className="container mx-auto px-4 py-4 flex items-center justify-between">
        <div className="flex items-center">
          <Link href="/" className="flex items-center gap-2">
            <Image
              src="/logo_volunteer.png"
              alt="Logo Voluntariado"
              width={40}
              height={40}
              className="object-contain"
            />
            <span className="text-[#2A2599] font-bold text-xl">Voluntaria+</span>
          </Link>
        </div>

        <div className="flex items-center gap-8">
          <Link
            href="/"
            className="text-[#2A2599] hover:text-[#8F89FB] font-medium transition-colors"
          >
            Home
          </Link>
          <Link
            href="/sobre"
            className="text-[#2A2599] hover:text-[#8F89FB] font-medium transition-colors"
          >
            Sobre
          </Link>
          <Link
            href="/login"
            className="bg-[#8F89FB] text-white px-6 py-2 rounded-lg hover:bg-[#2A2599] transition-colors font-medium"
          >
            Login
          </Link>
        </div>
      </div>
    </nav>
  );
}
