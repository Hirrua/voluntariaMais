import AdminGuard from "@/components/AdminGuard";
import AdminLayoutShell from "@/components/AdminLayout";

type AdminLayoutProps = {
  children: React.ReactNode;
};

export default function AdminLayout({ children }: AdminLayoutProps) {
  return (
    <AdminLayoutShell>
      <AdminGuard>{children}</AdminGuard>
    </AdminLayoutShell>
  );
}
