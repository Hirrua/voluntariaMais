import type { ReactNode } from "react"
import { layoutTokens } from "@/styles/tokens"

type PageContainerProps = {
  children: ReactNode
  className?: string
}

export default function PageContainer({ children, className }: PageContainerProps) {
  return (
    <div className={`${layoutTokens.container}${className ? ` ${className}` : ""}`}>
      {children}
    </div>
  )
}
