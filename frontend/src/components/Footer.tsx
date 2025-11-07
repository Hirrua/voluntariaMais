export default function Footer() {
  return (
    <footer className="bg-[#2A2599] text-white mt-16">
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Sobre */}
          <div>
            <h3 className="font-bold text-lg mb-4">Voluntariado</h3>
            <p className="text-gray-300 text-sm leading-relaxed">
              Conectando pessoas que querem fazer a diferença com projetos que precisam de ajuda.
            </p>
          </div>

          {/* Links */}
          <div>
            <h3 className="font-bold text-lg mb-4">Links Rápidos</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <a href="/" className="text-gray-300 hover:text-[#8F89FB] transition-colors">
                  Home
                </a>
              </li>
              <li>
                <a href="/sobre" className="text-gray-300 hover:text-[#8F89FB] transition-colors">
                  Sobre
                </a>
              </li>
              <li>
                <a href="/login" className="text-gray-300 hover:text-[#8F89FB] transition-colors">
                  Login
                </a>
              </li>
            </ul>
          </div>

          {/* Contato */}
          <div>
            <h3 className="font-bold text-lg mb-4">Contato</h3>
            <ul className="space-y-2 text-sm text-gray-300">
              <li>contato@voluntariado.com</li>
              <li>(00) 0000-0000</li>
            </ul>
          </div>
        </div>

        {/* Copyright */}
        <div className="border-t border-gray-600 mt-8 pt-6 text-center text-sm text-gray-300">
          <p>&copy; {new Date().getFullYear()} Voluntariado. Todos os direitos reservados.</p>
        </div>
      </div>
    </footer>
  );
}
