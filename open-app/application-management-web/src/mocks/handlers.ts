import { http, HttpResponse } from 'msw'
import { Application, AppStatus, AppType } from '../types/application'
import type { PaginationData } from '../types/api'

/**
 * Mock 数据生成器
 */
const generateMockApps = (): Application[] => {
  const apps: Application[] = [
    {
      id: 'app_mock_001',
      name: '测试应用 A',
      description: '这是一个测试应用',
      type: AppType.WEB,
      status: AppStatus.ACTIVE,
      iconUrl: 'https://example.com/icon-a.png',
      callbackUrl: 'https://example.com/callback-a',
      ownerId: 'user_001',
      ownerType: 'user',
      createdAt: '2026-03-20T10:00:00Z',
      updatedAt: '2026-03-23T15:30:00Z',
    },
    {
      id: 'app_mock_002',
      name: '第三方应用 B',
      description: '第三方集成应用',
      type: AppType.MOBILE,
      status: AppStatus.DRAFT,
      iconUrl: 'https://example.com/icon-b.png',
      callbackUrl: 'https://example.com/callback-b',
      ownerId: 'user_001',
      ownerType: 'user',
      createdAt: '2026-03-19T09:00:00Z',
      updatedAt: '2026-03-22T14:00:00Z',
    },
    {
      id: 'app_mock_003',
      name: '个人应用 C',
      description: '个人开发者应用',
      type: AppType.API,
      status: AppStatus.DISABLED,
      ownerId: 'user_002',
      ownerType: 'user',
      createdAt: '2026-03-18T08:00:00Z',
      updatedAt: '2026-03-21T10:00:00Z',
    },
  ]
  return apps
}

/**
 * 模拟应用列表数据
 */
const mockListData: PaginationData<Application> = {
  items: generateMockApps(),
  total: 3,
  page: 1,
  pageSize: 20,
  totalPages: 1,
}

/**
 * MSW Handlers
 * 定义所有 API 端点的 Mock 处理逻辑
 */
export const handlers = [
  /**
   * GET /api/v1/applications
   * 获取应用列表
   */
  http.get('/api/v1/applications', ({ request }) => {
    const url = new URL(request.url)
    const page = Number(url.searchParams.get('page')) || 1
    const pageSize = Number(url.searchParams.get('pageSize')) || 20
    const keyword = url.searchParams.get('keyword')

    // 简单的关键词过滤
    let items = mockListData.items
    if (keyword) {
      items = items.filter((app) => app.name.includes(keyword))
    }

    return HttpResponse.json({
      code: 0,
      message: 'success',
      data: {
        ...mockListData,
        items,
        page,
        pageSize,
      },
    })
  }),

  /**
   * GET /api/v1/applications/:id
   * 获取应用详情
   */
  http.get('/api/v1/applications/:id', ({ params }) => {
    const { id } = params
    const app = mockListData.items.find((a) => a.id === id)

    if (!app) {
      return HttpResponse.json(
        {
          code: 404,
          message: '应用不存在',
          data: null,
        },
        { status: 404 },
      )
    }

    return HttpResponse.json({
      code: 0,
      message: 'success',
      data: app,
    })
  }),

  /**
   * POST /api/v1/applications
   * 创建应用
   */
  http.post('/api/v1/applications', async ({ request }) => {
    const body = await request.json()
    const newApp: Application = {
      id: `app_mock_${Date.now()}`,
      name: (body as Record<string, unknown>).name as string,
      description: (body as Record<string, unknown>).description as string | undefined,
      type: (body as Record<string, unknown>).type as AppType,
      status: AppStatus.DRAFT,
      iconUrl: (body as Record<string, unknown>).iconUrl as string | undefined,
      callbackUrl: (body as Record<string, unknown>).callbackUrl as string | undefined,
      ownerId: 'user_current',
      ownerType: 'user' as const,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    }

    mockListData.items.push(newApp)
    mockListData.total += 1

    return HttpResponse.json({
      code: 0,
      message: '创建成功',
      data: newApp,
    })
  }),

  /**
   * PUT /api/v1/applications/:id
   * 更新应用
   */
  http.put('/api/v1/applications/:id', async ({ params, request }) => {
    const { id } = params
    const body = await request.json()
    const appIndex = mockListData.items.findIndex((a) => a.id === id)

    if (appIndex === -1) {
      return HttpResponse.json(
        {
          code: 404,
          message: '应用不存在',
          data: null,
        },
        { status: 404 },
      )
    }

    const updatedApp: Application = {
      ...mockListData.items[appIndex],
      ...(body as Record<string, unknown>),
      updatedAt: new Date().toISOString(),
    }

    mockListData.items[appIndex] = updatedApp

    return HttpResponse.json({
      code: 0,
      message: '更新成功',
      data: updatedApp,
    })
  }),

  /**
   * DELETE /api/v1/applications/:id
   * 删除应用（软删除）
   */
  http.delete('/api/v1/applications/:id', ({ params }) => {
    const { id } = params
    const appIndex = mockListData.items.findIndex((a) => a.id === id)

    if (appIndex === -1) {
      return HttpResponse.json(
        {
          code: 404,
          message: '应用不存在',
          data: null,
        },
        { status: 404 },
      )
    }

    // 软删除：更新状态为 deleted
    mockListData.items[appIndex] = {
      ...mockListData.items[appIndex],
      status: AppStatus.DELETED,
      updatedAt: new Date().toISOString(),
    }

    return HttpResponse.json({
      code: 0,
      message: '删除成功',
      data: null,
    })
  }),
]
