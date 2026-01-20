"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { authService } from "@/services/authService";
import { isAdmin, isAdminOng } from "@/lib/roles";

type AdminGuardProps = {
  children: React.ReactNode;
};

export default function AdminGuard({ children }: AdminGuardProps) {
  const router = useRouter();
  const pathname = usePathname();
  const [isAllowed, setIsAllowed] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    const checkAdmin = async () => {
      try {
        const user = await authService.getMe();

        const isPlatformAdmin = isAdmin(user.roles);
        const isOngAdmin = isAdminOng(user.roles);

        if (!isPlatformAdmin && !isOngAdmin) {
          router.replace("/");
          return;
        }

        if (isOngAdmin && !pathname.startsWith("/admin/projetos")) {
          router.replace("/ong/perfil");
          return;
        }

        if (isMounted) {
          setIsAllowed(true);
        }
      } catch {
        const redirectTo = pathname ? `?redirect=${encodeURIComponent(pathname)}` : "";
        router.replace(`/login${redirectTo}`);
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };

    checkAdmin();

    return () => {
      isMounted = false;
    };
  }, [router, pathname]);

  if (isLoading || !isAllowed) {
    return (
      <div className="text-center py-12">
        <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#8F89FB]"></div>
        <p className="text-gray-500 mt-4">Carregando painel admin</p>
      </div>
    );
  }

  return <>{children}</>;
}
