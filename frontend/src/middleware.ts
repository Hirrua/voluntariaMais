import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  const authCookie = request.cookies.get("access_volunteer");
  const isLoginPage = request.nextUrl.pathname === "/login";
  const isPerfilPage = request.nextUrl.pathname.startsWith("/perfil");

  if (isPerfilPage && !authCookie) {
    const loginUrl = new URL("/login", request.url);
    loginUrl.searchParams.set("redirect", request.nextUrl.pathname);
    return NextResponse.redirect(loginUrl);
  }

  if (isLoginPage && authCookie) {
    return NextResponse.redirect(new URL("/perfil", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/perfil/:path*", "/login"],
};
