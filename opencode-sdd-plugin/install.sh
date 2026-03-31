#!/usr/bin/env bash
# SDD Plugin Installer (Linux/macOS)
# Usage: bash install.sh <TargetDir>
#    or: ./install.sh <TargetDir>
# Note: Must use bash, not sh!

# Detect if running with sh instead of bash
if [ -z "$BASH_VERSION" ]; then
    echo "ERROR: This script requires bash."
    echo ""
    echo "You ran it with 'sh', but it must be run with 'bash':"
    echo ""
    echo "  bash install.sh <TargetDir>"
    echo "  # or"
    echo "  ./install.sh <TargetDir>"
    echo ""
    exit 1
fi

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;90m'
NC='\033[0m'

# Helper function for colored output
print_color() {
    printf "%b\n" "$1"
}

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Parse arguments
if [ -z "$1" ]; then
    print_color "${RED}ERROR: Target directory required${NC}"
    echo ""
    echo "Usage: bash install.sh <TargetDir>"
    echo "   or: ./install.sh <TargetDir>"
    echo ""
    echo "Note: This script requires bash. Do NOT run with 'sh install.sh'"
    echo ""
    exit 1
fi

TARGET_DIR="$1"

echo ""
print_color "${CYAN}=== SDD Plugin Installer ===${NC}"
print_color "Source: ${SCRIPT_DIR}"
print_color "Target: ${TARGET_DIR}"
echo ""

# Step 1: Check source
print_color "${CYAN}[1/6] Checking source...${NC}"
if [ ! -f "${SCRIPT_DIR}/package.json" ]; then
    print_color "${RED}ERROR: package.json not found${NC}"
    exit 1
fi
print_color "${GREEN}[OK] Source validated${NC}"

# Step 2: Build everything to dist/
print_color "${CYAN}[2/6] Building to dist/...${NC}"

# Build agents
print_color "${GRAY}  Building agents...${NC}"
node "${SCRIPT_DIR}/build-agents.cjs"
if [ $? -ne 0 ]; then
    print_color "${RED}Agent build failed${NC}"
    exit 1
fi

# Build TypeScript
if [ -d "${SCRIPT_DIR}/node_modules" ]; then
    print_color "${GRAY}  Building TypeScript...${NC}"
    "${SCRIPT_DIR}/node_modules/.bin/tsc" --project "${SCRIPT_DIR}/tsconfig.json"
    if [ $? -ne 0 ]; then
        print_color "${RED}TS build failed${NC}"
        exit 1
    fi
else
    print_color "${GRAY}  Installing dependencies...${NC}"
    npm install --prefix "${SCRIPT_DIR}"
    if [ $? -ne 0 ]; then
        print_color "${RED}Install failed${NC}"
        exit 1
    fi
    "${SCRIPT_DIR}/node_modules/.bin/tsc" --project "${SCRIPT_DIR}/tsconfig.json"
    if [ $? -ne 0 ]; then
        print_color "${RED}TS build failed${NC}"
        exit 1
    fi
fi

print_color "${GREEN}[OK] Build complete${NC}"

# Step 3: Create directories
print_color "${CYAN}[3/6] Creating directories...${NC}"

# Create directories one by one (POSIX compatible, no arrays)
for dir in "${TARGET_DIR}/.opencode/plugins/sdd" "${TARGET_DIR}/.opencode/agents" "${TARGET_DIR}/.sdd" "${TARGET_DIR}/.sdd/.specs"; do
    if [ -d "$dir" ]; then
        print_color "${YELLOW}[WARN] Exists: $dir${NC}"
    else
        mkdir -p "$dir"
        print_color "${GREEN}[OK] Created: $dir${NC}"
    fi
done

# Step 4: Copy plugin from dist/ (exclude templates/)
print_color "${CYAN}[4/6] Copying plugin from dist/...${NC}"
PLUGIN_DEST="${TARGET_DIR}/.opencode/plugins/sdd"

# Clean destination first
if [ -d "$PLUGIN_DEST" ]; then
    rm -rf "$PLUGIN_DEST"
fi
mkdir -p "$PLUGIN_DEST"

# Copy from dist/ excluding templates/
for item in "${SCRIPT_DIR}/dist"/*; do
    item_name=$(basename "$item")
    if [ "$item_name" != "templates" ]; then
        cp -r "$item" "$PLUGIN_DEST/"
    fi
done

# Copy agents from dist/templates/agents/ to .opencode/agents/
print_color "${GRAY}  Copying agents...${NC}"
cp "${SCRIPT_DIR}/dist/templates/agents/"* "${TARGET_DIR}/.opencode/agents/"
AGENT_COUNT=$(find "${TARGET_DIR}/.opencode/agents" -type f | wc -l)

FILE_COUNT=$(find "${TARGET_DIR}/.opencode/plugins/sdd" -type f | wc -l)
print_color "${GREEN}[OK] Copied $FILE_COUNT plugin files + $AGENT_COUNT agents${NC}"

# Step 5: Copy opencode.json from dist/
print_color "${CYAN}[5/6] Copying opencode.json...${NC}"
cp "${SCRIPT_DIR}/dist/opencode.json" "${TARGET_DIR}/opencode.json"
print_color "${GREEN}[OK] Copied opencode.json${NC}"

# Step 6: Copy installation guide
print_color "${CYAN}[6/7] Copying documentation...${NC}"
cp "${SCRIPT_DIR}/INSTALL.md" "${TARGET_DIR}/SDD_INSTALL_GUIDE.md"
print_color "${GREEN}[OK] Copied SDD_INSTALL_GUIDE.md${NC}"

# Step 7: Initialize .sdd/ directory structure
print_color "${CYAN}[7/7] Initializing .sdd/ directory...${NC}"

# Create .sdd/README.md
cat > "${TARGET_DIR}/.sdd/README.md" << 'EOF'
# SDD Workspace

## 目录结构

```
.sdd/
├── README.md              # 本文件 - SDD 工作空间说明
├── ROADMAP.md             # 版本路线图
├── config.json            # SDD 配置（可选）
└── .specs/                # 规范文件目录
    ├── README.md          # 目录说明
    └── [feature]/         # Feature 目录
```

## 快速开始

1. 使用 `@sdd 开始 [feature 名称]` 开始新 feature
2. 规范文件将自动创建在 `.sdd/.specs/` 目录
3. 文档会自动维护，无需手动创建 README

## Agents

- `@sdd` - 智能入口
- `@sdd-docs` - 目录导航（自动触发）
- `@sdd-roadmap` - Roadmap 规划
EOF

print_color "${GREEN}[OK] Created .sdd/README.md${NC}"

# Create .sdd/.specs/README.md
cat > "${TARGET_DIR}/.sdd/.specs/README.md" << 'EOF'
# SDD 规范目录

## 目录结构

```
.specs/
├── README.md              # 本文件
├── [feature-1]/           # Feature 1
│   ├── spec.md
│   ├── plan.md
│   ├── tasks.md
│   └── state.json
└── [feature-2]/           # Feature 2
    └── ...
```

## 使用说明

- 每个 Feature 有独立的目录
- 文档会自动维护（@sdd-docs）
EOF

print_color "${GREEN}[OK] Created .sdd/.specs/README.md${NC}"

# Done
echo ""
print_color "${GREEN}=== Installation Complete ===${NC}"
echo ""
print_color "Installed to: ${TARGET_DIR}"
echo ""
echo "Files:"
echo "  - .opencode/plugins/sdd/ ($FILE_COUNT files from dist/)"
echo "  - .opencode/agents/ (16 agents from dist/templates/agents/)"
echo "  - opencode.json (plugin configuration)"
echo "  - SDD_INSTALL_GUIDE.md (quick start guide)"
echo "  - .sdd/ (SDD workspace container)"
echo "    └── .specs/ (SDD specification files)"
echo ""
print_color "${CYAN}Agents installed (16 total):${NC}"
echo "  @sdd              - Smart entry point"
echo "  @sdd-help         - Help assistant"
echo "  @sdd-1-spec       - Specification (Phase 1/6)"
echo "  @sdd-2-plan       - Technical planning (Phase 2/6)"
echo "  @sdd-3-tasks      - Task breakdown (Phase 3/6)"
echo "  @sdd-4-build      - Implementation (Phase 4/6)"
echo "  @sdd-5-review     - Code review (Phase 5/6)"
echo "  @sdd-6-validate   - Validation (Phase 6/6)"
echo "  (Short names also available: @sdd-spec, @sdd-plan, etc.)"
echo ""
print_color "${CYAN}Quick Start:${NC}"
echo "  cd '${TARGET_DIR}'"
echo "  opencode"
echo "  @sdd 开始 [feature 名称]"
echo ""
print_color "${GRAY}Documentation:${NC}"
echo "  - README.md     - Full documentation"
echo "  - INSTALL.md    - Detailed installation guide"
echo "  - CHANGELOG.md  - Version history"
echo ""
