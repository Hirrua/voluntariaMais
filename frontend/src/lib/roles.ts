export const isAdminOng = (roles: string[] = []) => roles.includes("ROLE_ADMIN_ONG")

export const isAdmin = (roles: string[] = []) =>
  roles.includes("ROLE_ADMIN") || roles.includes("ROLE_ADMIN_PLATAFORMA")
