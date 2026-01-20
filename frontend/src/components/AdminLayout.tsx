"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { cn } from "@/lib/utils";

type AdminLayoutProps = {
  children: React.ReactNode;
};

const navItems = [
  { label: "Dashboard", href: "/admin" },
  { label: "ONGs", href: "/admin/ongs" },
  { label: "Projetos", href: "/admin/projetos" },
];

const labelMap: Record<string, string> = {
  ongs: "ONGs",
  projetos: "Projetos",
  atividades: "Atividades",
  inscricoes: "Inscricoes",
};

const buildBreadcrumb = (pathname: string) => {
  const segments = pathname.split("/").filter(Boolean);
  const adminIndex = segments.indexOf("admin");
  const trail = adminIndex >= 0 ? segments.slice(adminIndex + 1) : [];
  const labels = trail.filter((segment) => !/^\d+$/.test(segment)).map((segment) => {
    return labelMap[segment] ?? segment.charAt(0).toUpperCase() + segment.slice(1);
  });

  if (labels.length === 0) {
    labels.push("Dashboard");
  }

  return ["Admin", ...labels];
};

export default function AdminLayout({ children }: AdminLayoutProps) {
  const pathname = usePathname() ?? "";
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const breadcrumb = useMemo(() => buildBreadcrumb(pathname), [pathname]);

  useEffect(() => {
    setIsSidebarOpen(false);
  }, [pathname]);

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />

      <main className="flex-grow">
        <div className="container mx-auto px-4 py-8">
          <div className="flex items-center justify-between md:hidden mb-4">
            <span className="text-sm font-semibold text-[#2A2599]">Menu</span>
            <button
              type="button"
              onClick={() => setIsSidebarOpen((prev) => !prev)}
              className="rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-medium text-[#2A2599] shadow-sm"
              aria-controls="admin-sidebar"
              aria-expanded={isSidebarOpen}
            >
              {isSidebarOpen ? "Fechar" : "Abrir"}
            </button>
          </div>

          <div className="flex flex-col md:flex-row gap-6">
            <aside
              id="admin-sidebar"
              className={cn(
                "w-full md:w-64",
                isSidebarOpen ? "block" : "hidden",
                "md:block"
              )}
            >
              <div className="rounded-lg border border-gray-100 bg-white shadow-md p-4">
                <nav className="space-y-2">
                  {navItems.map((item) => {
                    const isActive =
                      item.href === "/admin"
                        ? pathname === "/admin"
                        : pathname.startsWith(item.href);

                    return (
                      <Link
                        key={item.href}
                        href={item.href}
                        className={cn(
                          "flex items-center justify-between rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                          isActive
                            ? "bg-[#8F89FB] text-white"
                            : "text-[#2A2599] hover:bg-gray-100"
                        )}
                      >
                        {item.label}
                      </Link>
                    );
                  })}
                </nav>
              </div>
            </aside>

            <section className="flex-1 min-w-0">
              <div className="mb-6">
                <nav aria-label="breadcrumb" className="text-sm text-gray-500">
                  {breadcrumb.map((item, index) => (
                    <span key={`${item}-${index}`}>
                      {index > 0 && <span className="mx-2">/</span>}
                      <span className={index === breadcrumb.length - 1 ? "text-[#2A2599] font-medium" : ""}>
                        {item}
                      </span>
                    </span>
                  ))}
                </nav>
              </div>

              {children}
            </section>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
