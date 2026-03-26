#!/usr/bin/env pwsh
# SDD Plugin Installer
# Usage: powershell -ExecutionPolicy Bypass -File install.ps1 <TargetDir>

param(
    [Parameter(Mandatory=$true)]
    [string]$TargetDir
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host ""
Write-Host "=== SDD Plugin Installer ===" -ForegroundColor Cyan
Write-Host "Source: $ScriptDir"
Write-Host "Target: $TargetDir"
Write-Host ""

# Step 1: Check source
Write-Host "[1/6] Checking source..." -ForegroundColor Cyan
if (-not (Test-Path "$ScriptDir\package.json")) {
    Write-Host "ERROR: package.json not found" -ForegroundColor Red
    exit 1
}
Write-Host "[OK] Source validated" -ForegroundColor Green

# Step 2: Build everything to dist/
Write-Host "[2/6] Building to dist/..." -ForegroundColor Cyan

# Build agents
Write-Host "  Building agents..." -ForegroundColor Gray
& node "$ScriptDir\build-agents.cjs"
if ($LASTEXITCODE -ne 0) { Write-Host "Agent build failed" -ForegroundColor Red; exit 1 }

# Build TypeScript
if (Test-Path "$ScriptDir\node_modules") {
    Write-Host "  Building TypeScript..." -ForegroundColor Gray
    & "$ScriptDir\node_modules\.bin\tsc.cmd" --project "$ScriptDir\tsconfig.json"
    if ($LASTEXITCODE -ne 0) { Write-Host "TS build failed" -ForegroundColor Red; exit 1 }
} else {
    Write-Host "  Installing dependencies..." -ForegroundColor Gray
    & npm install --prefix $ScriptDir
    if ($LASTEXITCODE -ne 0) { Write-Host "Install failed" -ForegroundColor Red; exit 1 }
    & "$ScriptDir\node_modules\.bin\tsc.cmd" --project "$ScriptDir\tsconfig.json"
    if ($LASTEXITCODE -ne 0) { Write-Host "TS build failed" -ForegroundColor Red; exit 1 }
}

Write-Host "[OK] Build complete" -ForegroundColor Green

# Step 3: Create directories
Write-Host "[3/6] Creating directories..." -ForegroundColor Cyan
$dirs = @(
    "$TargetDir\.opencode\plugins\sdd",
    "$TargetDir\.opencode\agents",
    "$TargetDir\.specs",
    "$TargetDir\.specs\examples",
    "$TargetDir\.specs\architecture\adr"
)
foreach ($dir in $dirs) {
    if (Test-Path $dir) {
        Write-Host "[WARN] Exists: $dir" -ForegroundColor Yellow
    } else {
        New-Item -ItemType Directory -Force -Path $dir | Out-Null
        Write-Host "[OK] Created: $dir" -ForegroundColor Green
    }
}

# Step 4: Copy plugin from dist/ (exclude templates/)
Write-Host "[4/6] Copying plugin from dist/..." -ForegroundColor Cyan
$pluginDest = "$TargetDir\.opencode\plugins\sdd"
# Clean destination first
if (Test-Path $pluginDest) { Remove-Item $pluginDest -Recurse -Force }
New-Item -ItemType Directory -Force -Path $pluginDest | Out-Null
Get-ChildItem "$ScriptDir\dist" | Where-Object { $_.Name -ne "templates" } | Copy-Item -Destination $pluginDest -Recurse -Force

# Copy agents from dist/templates/agents/ to .opencode/agents/
Write-Host "  Copying agents..." -ForegroundColor Gray
Copy-Item "$ScriptDir\dist\templates\agents\*" -Destination "$TargetDir\.opencode\agents\" -Force
$agentCount = (Get-ChildItem "$TargetDir\.opencode\agents" | Measure-Object).Count

$fileCount = (Get-ChildItem "$TargetDir\.opencode\plugins\sdd" -Recurse | Measure-Object).Count
Write-Host "[OK] Copied $fileCount plugin files + $agentCount agents" -ForegroundColor Green

# Step 5: Copy opencode.json from dist/
Write-Host "[5/6] Copying opencode.json..." -ForegroundColor Cyan
Copy-Item "$ScriptDir\dist\opencode.json" -Destination "$TargetDir\opencode.json" -Force
Write-Host "[OK] Copied opencode.json" -ForegroundColor Green

# Step 6: Copy installation guide
Write-Host "[6/6] Copying documentation..." -ForegroundColor Cyan
Copy-Item "$ScriptDir\INSTALL.md" -Destination "$TargetDir\SDD_INSTALL_GUIDE.md" -Force
Write-Host "[OK] Copied SDD_INSTALL_GUIDE.md" -ForegroundColor Green

# Done
Write-Host ""
Write-Host "=== Installation Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Installed to: $TargetDir"
Write-Host ""
Write-Host "Files:"
Write-Host "  - .opencode/plugins/sdd/ ($fileCount files from dist/)"
Write-Host "  - .opencode/agents/ (14 agents from dist/templates/agents/)"
Write-Host "  - opencode.json (plugin configuration)"
Write-Host "  - SDD_INSTALL_GUIDE.md (quick start guide)"
Write-Host "  - .specs/ (SDD working directories)"
Write-Host ""
Write-Host "Agents installed (14 total):" -ForegroundColor Cyan
Write-Host "  @sdd              - Smart entry point"
Write-Host "  @sdd-help         - Help assistant"
Write-Host "  @sdd-1-spec       - Specification (Phase 1/6)"
Write-Host "  @sdd-2-plan       - Technical planning (Phase 2/6)"
Write-Host "  @sdd-3-tasks      - Task breakdown (Phase 3/6)"
Write-Host "  @sdd-4-build      - Implementation (Phase 4/6)"
Write-Host "  @sdd-5-review     - Code review (Phase 5/6)"
Write-Host "  @sdd-6-validate   - Validation (Phase 6/6)"
Write-Host "  (Short names also available: @sdd-spec, @sdd-plan, etc.)"
Write-Host ""
Write-Host "Quick Start:" -ForegroundColor Cyan
Write-Host "  cd '$TargetDir'"
Write-Host "  opencode"
Write-Host "  @sdd 开始 [feature 名称]"
Write-Host ""
Write-Host "Documentation:" -ForegroundColor Gray
Write-Host "  - README.md     - Full documentation"
Write-Host "  - INSTALL.md    - Detailed installation guide"
Write-Host "  - CHANGELOG.md  - Version history"
Write-Host ""
