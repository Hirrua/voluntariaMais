"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter, usePathname } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { authService } from "@/services/authService";

export default function Navbar() {
  const router = useRouter();
  const pathname = usePathname();
  const menuRef = useRef<HTMLDivElement>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [showUserMenu, setShowUserMenu] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      const authenticated = await authService.checkAuth();
      setIsAuthenticated(authenticated);
      setLoading(false);
    };

    checkAuth();
  }, [pathname]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setShowUserMenu(false);
      }
    };

    if (showUserMenu) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showUserMenu]);

  const handleLogout = async () => {
    try {
      await authService.logout();
      setIsAuthenticated(false);
      router.push("/");
    } catch (error) {
      setIsAuthenticated(false);
      router.push("/");
    }
  };

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
            <span className="text-[#2A2599] font-bold text-xl">
              Voluntaria+
            </span>
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

          {loading ? (
            <div className="w-20 h-10 bg-gray-200 rounded-lg animate-pulse" />
          ) : isAuthenticated ? (
            <div className="relative" ref={menuRef}>
              <button
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex items-center gap-2 bg-[#8F89FB] text-white px-6 py-2 rounded-lg hover:bg-[#2A2599] transition-colors font-medium focus:outline-none focus:ring-2 focus:ring-[#8F89FB] focus:ring-offset-2"
                aria-expanded={showUserMenu}
                aria-haspopup="true"
              >
                <svg
                  className="w-5 h-5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  />
                </svg>
                Perfil
                <svg
                  className={`w-4 h-4 transition-transform ${
                    showUserMenu ? "rotate-180" : ""
                  }`}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M19 9l-7 7-7-7"
                  />
                </svg>
              </button>

              {showUserMenu && (
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-1 z-50 border border-gray-200">
                  <Link
                    href="/perfil"
                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                    onClick={() => setShowUserMenu(false)}
                  >
                    <div className="flex items-center gap-2">
                      <svg
                        className="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                        />
                      </svg>
                      Meu Perfil
                    </div>
                  </Link>
                </div>
              )}
            </div>
          ) : (
            <Link
              href="/login"
              className="bg-[#8F89FB] text-white px-6 py-2 rounded-lg hover:bg-[#2A2599] transition-colors font-medium"
            >
              Login
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
}
