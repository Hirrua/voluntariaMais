type PageHeaderProps = {
  title: string;
  description?: string;
  actions?: React.ReactNode;
};

export default function PageHeader({ title, description, actions }: PageHeaderProps) {
  return (
    <header className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
      <div className="space-y-2">
        <h1 className="text-2xl md:text-3xl font-bold text-[#2A2599]">
          {title}
        </h1>
        {description && <p className="text-gray-600">{description}</p>}
      </div>
      {actions && <div className="flex flex-wrap gap-3">{actions}</div>}
    </header>
  );
}
