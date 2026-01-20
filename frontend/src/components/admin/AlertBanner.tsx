type AlertBannerProps = {
  variant: "success" | "error";
  message?: string | null;
};

export default function AlertBanner({ variant, message }: AlertBannerProps) {
  if (!message) {
    return null;
  }

  const styles =
    variant === "error"
      ? "border-red-200 bg-red-50 text-red-600"
      : "border-green-200 bg-green-50 text-green-700";

  return (
    <div
      className={`rounded-lg border px-4 py-3 text-sm ${styles}`}
      role={variant === "error" ? "alert" : "status"}
    >
      {message}
    </div>
  );
}
