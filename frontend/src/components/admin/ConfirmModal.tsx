type ConfirmModalProps = {
  open: boolean;
  title: string;
  description: string;
  confirmLabel?: string;
  loadingLabel?: string;
  cancelLabel?: string;
  isLoading?: boolean;
  variant?: "danger" | "primary";
  onConfirm: () => void;
  onCancel: () => void;
};

export default function ConfirmModal({
  open,
  title,
  description,
  confirmLabel = "Confirmar",
  loadingLabel = "Processando...",
  cancelLabel = "Cancelar",
  isLoading = false,
  variant = "danger",
  onConfirm,
  onCancel,
}: ConfirmModalProps) {
  if (!open) {
    return null;
  }

  const confirmClasses =
    variant === "danger"
      ? "bg-red-600 hover:bg-red-700"
      : "bg-[#2A2599] hover:bg-[#1f1b7a]";

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
      <div className="w-full max-w-md rounded-lg bg-white shadow-xl">
        <div className="px-6 py-5 space-y-4">
          <h3 className="text-lg font-semibold text-gray-900">{title}</h3>
          <p className="text-sm text-gray-600">{description}</p>
          <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
            <button
              type="button"
              onClick={onCancel}
              className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:border-gray-300"
              disabled={isLoading}
            >
              {cancelLabel}
            </button>
            <button
              type="button"
              onClick={onConfirm}
              className={`rounded-lg px-4 py-2 text-sm font-semibold text-white transition-colors disabled:cursor-not-allowed disabled:opacity-70 ${confirmClasses}`}
              disabled={isLoading}
            >
              {isLoading ? loadingLabel : confirmLabel}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
