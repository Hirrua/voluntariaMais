import { AdminApiError } from "@/services/adminApiClient";
import type { AppRouterInstance } from "next/dist/shared/lib/app-router-context.shared-runtime";

export const resolveAdminError = (
  error: unknown,
  router: AppRouterInstance,
  pathname?: string | null
) => {
  if (error instanceof AdminApiError) {
    if (error.code === "UNAUTHORIZED") {
      const redirect = pathname ? `?redirect=${encodeURIComponent(pathname)}` : "";
      router.replace(`/login${redirect}`);
      return null;
    }

    if (error.code === "FORBIDDEN") {
      router.replace("/sem-permissao");
      return null;
    }
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "Erro ao carregar dados";
};
