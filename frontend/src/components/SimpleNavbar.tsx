import Image from "next/image";
import Link from "next/link";

export default function SimpleNavbar() {
  return (
    <header className="bg-white">
      <div className="mx-auto flex h-16 max-w-4xl items-center justify-between px-6">
        <Link href="/" className="flex items-center">
          <Image
            src="/logo_volunteer.png"
            alt="Logo Voluntaria+"
            width={28}
            height={28}
            className="h-7 w-7 object-contain"
          />
        </Link>

        <nav className="flex items-center gap-6 text-[10px] font-semibold uppercase tracking-[0.22em] text-[#2A2599]">
          <Link href="/" className="transition-colors hover:text-[#8F89FB]">
            Home
          </Link>
          <Link href="/sobre" className="transition-colors hover:text-[#8F89FB]">
            Sobre
          </Link>
          <Link href="/perfil" className="transition-colors hover:text-[#8F89FB]">
            Perfil
          </Link>
        </nav>
      </div>
    </header>
  );
}
