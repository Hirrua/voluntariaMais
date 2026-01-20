import { ReactNode } from "react";

interface FormFieldProps {
  label: string;
  htmlFor?: string;
  children: ReactNode;
}

export default function FormField({ label, htmlFor, children }: FormFieldProps) {
  return (
    <div className="space-y-2">
      <label
        htmlFor={htmlFor}
        className="text-sm font-medium text-[#2A2599]"
      >
        {label}
      </label>
      {children}
    </div>
  );
}
