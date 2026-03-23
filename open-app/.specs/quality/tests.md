# 测试策略 - Test Strategy

## 测试金字塔

```
        /\
       /  \      E2E 测试 (10%)
      /----\     - 关键用户流程
     /      \    - 集成场景
    /--------\   
   /  集成测试  \  (20%)
  /--------------\ - API 集成
 /    单元测试     \ - 模块交互
/------------------\ - 权限验证
   (70%)
   - 函数级别
   - 组件级别
```

## 测试要求

### 单元测试
- [ ] 所有权限验证函数必须有单元测试
- [ ] 边界条件必须覆盖
- [ ] 错误路径必须测试
- [ ] 目标覆盖率：≥ 80%

### 集成测试
- [ ] 权限检查中间件必须测试
- [ ] 角色 - 权限关系必须测试
- [ ] 数据库交互必须测试

### E2E 测试
- [ ] 用户登录 → 权限验证流程必须测试
- [ ] 管理员分配权限流程必须测试
- [ ] 权限拒绝场景必须测试

## 权限测试特殊要求

### 权限矩阵测试
对每个角色 × 权限组合进行测试：

```typescript
describe('Permission Matrix', () => {
  const roles = ['admin', 'editor', 'viewer']
  const permissions = ['create', 'read', 'update', 'delete']
  
  roles.forEach(role => {
    permissions.forEach(perm => {
      it(`${role} 应该${expected(role, perm)} ${perm}`, () => {
        // 测试逻辑
      })
    })
  })
})
```

### 越权访问测试
```typescript
describe('Authorization Bypass Prevention', () => {
  it('普通用户不能访问管理员接口', async () => {
    // 测试越权访问被拒绝
  })
  
  it('用户只能访问自己的数据', async () => {
    // 测试数据级权限
  })
})
```

## 测试框架

| 类型 | 推荐框架 |
|------|----------|
| 单元测试 | Jest / Vitest |
| 集成测试 | Supertest + Jest |
| E2E 测试 | Playwright / Cypress |

## 测试文件组织

```
src/
├── auth/
│   ├── permission.service.ts
│   └── __tests__/
│       ├── permission.service.test.ts
│       └── permission.matrix.test.ts
```

## 测试命名规范

```typescript
describe('PermissionService', () => {
  describe('hasPermission', () => {
    it('应该返回 true 当用户有所请求的权限', async () => {})
    it('应该返回 false 当用户无所请求的权限', async () => {})
    it('应该拒绝越权访问', async () => {})
  })
})
```

## 验收标准模板

每个需求的验收标准格式：

```markdown
### 验收标准
- [ ] [条件] 下，[操作] 应该返回 [预期结果]
- [ ] 测试文件：`src/__tests__/xxx.test.ts`
- [ ] 测试用例：`it('...', ...)`
```

## 验证命令

```bash
# 运行所有测试
npm test

# 运行权限相关测试
npm test -- --testPathPattern=permission

# 运行覆盖率
npm test -- --coverage

# 运行特定测试
npm test -- --testNamePattern="hasPermission"
```

---

**版本**: 1.0.0  
**最后更新**: 2026-03-20  
**项目**: open-app
