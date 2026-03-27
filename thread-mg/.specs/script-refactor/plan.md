# 技术规划

## 执行顺序

```
1. 创建 scripts/lib.sh
2. 重构 test-phase2.sh (使用 lib.sh)
3. 重构 run-benchmark.sh (使用 lib.sh)
4. 删除 test-full.sh
5. 删除 test-watch.sh
6. 更新 README.md
7. 创建 SCRIPT-STANDARDS.md
8. 运行验证测试
```

## 详细步骤

### 步骤 1: 创建 lib.sh

**位置**: `scripts/lib.sh`

**内容**:
- 颜色定义（7 个变量）
- 日志函数（4 个函数）
- 工具函数（3 个函数）

**权限**: `chmod +x`

### 步骤 2: 重构 test-phase2.sh

**删除内容**:
- 行 37-45: 颜色定义
- 行 62-127: 辅助函数（supports_color, print_*）

**添加内容**:
- 脚本开头：`source "$(dirname "$0")/lib.sh"`

**替换内容**:
- `print_info` → `log_info`
- `print_success` → `log_success`
- `print_error` → `log_error`
- `print_warning` → `log_warning`
- `print_header` → 使用 log_* 组合
- `print_subheader` → 使用 log_* 组合

**新增功能**:
- 长参数支持：`-f, --full`、`-i, --incremental` 等

### 步骤 3: 重构 run-benchmark.sh

**删除内容**:
- 行 38-43: 颜色定义
- 行 97-113: 日志函数

**添加内容**:
- 脚本开头：`source "$(dirname "$0")/lib.sh"`

**替换内容**:
- 已有 `log_*` 函数与 lib.sh 兼容，无需替换

### 步骤 4-5: 删除冗余脚本

```bash
rm scripts/test-full.sh
rm scripts/test-watch.sh
```

### 步骤 6: 更新 README.md

**查找**: `test-full.sh`, `test-watch.sh` 的引用
**替换**: 更新为 `test-phase2.sh` 的用法说明

### 步骤 7: 创建规范文档

**位置**: `docs/development/SCRIPT-STANDARDS.md`

**内容**:
- 脚本编写规范
- 公共库使用说明
- 颜色和日志函数标准
- 参数解析规范

### 步骤 8: 验证测试

```bash
# 语法检查
bash -n scripts/lib.sh
bash -n scripts/test-phase2.sh
bash -n scripts/run-benchmark.sh

# 功能测试
./scripts/test-phase2.sh -help
./scripts/run-benchmark.sh -h

# 代码检查
grep -n "COLOR_" scripts/*.sh || echo "✅ 无旧颜色定义"
grep -n "print_info\|print_success" scripts/*.sh || echo "✅ 无旧日志函数"
```

## 风险评估

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| lib.sh 路径错误 | 低 | 高 | 使用 `$(dirname "$0")` 动态获取 |
| 函数名冲突 | 低 | 中 | 使用唯一前缀 `log_` |
| 向后兼容性问题 | 中 | 低 | 保留短参数支持 |
