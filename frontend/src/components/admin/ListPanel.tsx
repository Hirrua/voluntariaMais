type ListPanelProps = {
  title: string;
  count?: number;
  countLabel?: string;
  actions?: React.ReactNode;
  children: React.ReactNode;
};

export default function ListPanel({
  title,
  count,
  countLabel = "registros",
  actions,
  children,
}: ListPanelProps) {
  return (
    <section className="rounded-lg border border-gray-100 bg-white shadow-md p-6 space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
          {typeof count === "number" && (
            <p className="text-sm text-gray-500">
              {count} {countLabel}
            </p>
          )}
        </div>
        {actions && <div className="w-full md:w-80">{actions}</div>}
      </div>
      {children}
    </section>
  );
}
