/// <reference types="vite/client" />

// 环境变量类型声明
interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_API_VERSION: string
  readonly VITE_API_BASE_URL: string
  readonly VITE_MOCK_ENABLED: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

// 图片资源类型声明
declare module '*.svg' {
  const content: string
  export default content
}

declare module '*.png' {
  const content: string
  export default content
}

declare module '*.jpg' {
  const content: string
  export default content
}

declare module '*.jpeg' {
  const content: string
  export default content
}

declare module '*.gif' {
  const content: string
  export default content
}

declare module '*.less' {
  const content: Record<string, string>
  export default content
}
