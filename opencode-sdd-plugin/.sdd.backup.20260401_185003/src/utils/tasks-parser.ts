// .sdd/src/utils/tasks-parser.ts

/**
 * 并行组的定义
 */
export interface ParallelGroup {
  id: number;
  name: string;
  tasks: ParsedTask[];
  dependencies: number[]; // 依赖的组 ID
}

/**
 * 解析后的任务定义
 */
export interface ParsedTask {
  id: string; // TASK-XXX
  description: string;
  assignee?: string;
  dependencies: string[]; // 依赖的任务 ID
  estimatedTime?: string;
}

/**
 * 解析 tasks.md 中的并行分组
 */
export function parseParallelGroups(markdown: string): ParallelGroup[] {
  const groups: ParallelGroup[] = [];
  const groupRegex = /###\s*组\s*(\d+):\s*([^\n]+)\n([\s\S]*?)(?=### 组|$)/g;
  
  let match;
  let lastGroupEnd = 0;
  while ((match = groupRegex.exec(markdown)) !== null) {
    // 提取组信息    
    const groupId = parseInt(match[1]);
    const groupName = match[2].trim();
    const groupContent = match[3];
    
    // 解析组内的任务
    const tasks = parseTasksInGroup(groupContent);
    
    // 提取组依赖（从组名称或内容中搜索）
    const dependencies = parseGroupDependenciesWithContent(groupName, groupContent);
    
    groups.push({
      id: groupId,
      name: groupName,
      tasks: tasks,
      dependencies: dependencies
    });
    
    lastGroupEnd = match.index + match[0].length;
  }
  
  return groups;
}

/**
 * 解析组内的任务
 */
function parseTasksInGroup(content: string): ParsedTask[] {
  const tasks: ParsedTask[] = [];
  
  // 匹配任务行：- [ ] TASK-XXX: 描述内容
  const taskRegex = /(- \[[ xX]?\] )(.+)$/gm;
  
  let taskMatch;
  while ((taskMatch = taskRegex.exec(content)) !== null) {
    const fullMatch = taskMatch[0];
    const taskPart = taskMatch[2].trim();
    
    // 更鲁棒地解析任务ID和描述
    const idMatch = taskPart.match(/^(TASK-\d+(?:-\d+)?)[\s:-]/);
    let taskId = '';
    let descriptionPart = taskPart;
    
    if (idMatch) {
      taskId = idMatch[1].trim();
      descriptionPart = taskPart.substring(idMatch[0].length).trim();
    } else if (taskPart.includes(':')) {
      // 如果任务格式为 任务描述: 详细内容，则提取第一部分作为ID
      const parts = taskPart.split(':');
      taskId = parts[0].trim();
      descriptionPart = parts.slice(1).join(':').trim(); 
    } else {
      // 如果没有显式的任务ID，则跳过此任务
      continue;
    }
    
    // 解析任务的依赖关系
    const deps = parseTaskDependencies(fullMatch);
    
    tasks.push({
      id: taskId,
      description: descriptionPart,
      dependencies: deps
    });
  }
  
  return tasks;
}

/**
 * 解析任务的依赖关系
 */
function parseTaskDependencies(taskLine: string): string[] {
  const deps: string[] = [];
  
  // 支持多种依赖语法的正则表达式
  const patterns = [
    /\(依赖：([^\)]+)\)/g,      // (依赖：XXX)
    /\(depends:\s*([^\)]+)\)/g, // (depends: XXX)
    /依赖：([^\n]+)/g,          // 依赖：XXX (单独一行)
    /依赖([^\n\(]+)/g,         // 依赖XXX (不带括号)
    /FR-\d+/g,                 // FR-XXX
    /F-\d+/g                   // F-XXX
  ];
  
  for (const pattern of patterns) {
    let match;
    // 由于全局正则的 lastIndex 状态共享问题，我们需要重置它
    pattern.lastIndex = 0;
    while ((match = pattern.exec(taskLine)) !== null) {
      if (match[1]) {
        // 如果有捕获组，分割并添加所有依赖项
        const matchedDeps = match[1].split(/[,&\s]\s*/).map(d => d.trim()).filter(d => d);
        deps.push(...matchedDeps);
      } else {
        // 如果没有捕获组，使用整个匹配项（如FR-XXX或F-XXX）
        deps.push(match[0].trim());
      }
    }
  }
  
  // 去重并过滤空项
  return [...new Set(deps)].filter(d => d);
}

/**
 * 从组名称解析组依赖，同时考虑组内容中的跨子 Feature 依赖
 */
function parseGroupDependenciesWithContent(groupName: string, content: string): number[] {
  const dependencies: number[] = [];
  
  // 从组名称中查找 "等待组 X 完成" 或类似模式
  const nameDependencyRegex = /(?:等待组\s*(\d+)|(?:等待|阻塞|需要)\s*组\s*(\d+)|(?:等待|等到)\s*组\s*(\d+)\s*(?:完成|完毕|后))/gi;
  
  let match;
  while ((match = nameDependencyRegex.exec(groupName)) !== null) {
    const depNumStr = match[1] || match[2] || match[3]; // 三个捕获组之一
    if (depNumStr) {
      const depNum = parseInt(depNumStr, 10);
      if (!isNaN(depNum)) {
        dependencies.push(depNum);
      }
    }
  }
  
  // 内容可能包含子 Feature 依赖（跨子 Feature 依赖），但这些是任务层面的，不是组的依赖
  // 如果未来需要支持更复杂的依赖形式，这里可以扩展
  
  return [...new Set(dependencies)];
}

/**
 * 计算任务执行顺序（考虑组间和组内依赖）
 */
export function computeExecutionOrder(groups: ParallelGroup[]): ParallelGroup[][] {
  const waves: ParallelGroup[][] = [];
  const completed = new Set<number>();
  const remaining = new Set(groups.map(g => g.id));
  
  while (remaining.size > 0) {
    const readyWave: ParallelGroup[] = [];
    
    // 扫描剩余的所有组
    for (const groupId of remaining) {
      const group = groups.find(g => g.id === groupId)!;
      // 检查所有依赖组是否已完成
      const dependenciesMet = group.dependencies.every(depId => completed.has(depId));
      if (dependenciesMet) {
        readyWave.push(group);
      }
    }
    
    // 如果找不到任何组能够加入新波次，说明存在循环依赖
    if (readyWave.length === 0) {
      const unresolvedIds = Array.from(remaining).join(', ');
      throw new Error(`检测到循环依赖：无法满足组 [${unresolvedIds}] 的依赖关系`);
    }
    
    // 添加找到的波次并更新进度
    waves.push(readyWave);
    readyWave.forEach(g => {
      completed.add(g.id);
      remaining.delete(g.id);
    });
  }
  
  return waves;
}