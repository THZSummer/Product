# opencode-sdd-plugin

OpenCode Specification-Driven Development 插件源码

## 📦 安装

插件已编译并安装到项目：
```
F:\Product\permission-app\.opencode\plugins\sdd\
```

## 🚀 开发

```bash
# 安装依赖
npm install

# 构建
npm run build

# 监听模式
npm run dev
```

## 📁 目录结构

```
opencode-sdd-plugin/
├── src/
│   └── index.ts          # 插件源码
├── dist/                 # 编译输出（已复制到项目）
├── .gitignore            # Git 忽略文件
├── package.json
├── tsconfig.json
└── README.md
```

## 📚 文档

- [官方插件文档](https://opencode.ai/docs/plugins/)
- [REFACTOR.md](./REFACTOR.md) - 重构说明
- [项目使用说明](../permission-app/SDD-整合说明.md)

## ⚠️ 注意

**不要提交到 Git:**
- `dist/` - 编译输出
- `node_modules/` - 依赖

**已安装位置:**
- `F:\Product\permission-app\.opencode\plugins\sdd\`

修改源码后需要重新构建并复制：
```bash
npm run build
# 然后复制到目标项目
```

---

**版本**: 1.0.0  
**许可证**: MIT
