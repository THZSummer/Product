/**
 * 子 Feature 元数据接口
 */
export interface SubFeatureMeta {
    id: string;
    name: string;
    status: string;
    assignee?: string;
    dir: string;
}
/**
 * 检测 Feature 模式（单模块 vs 多子 Feature）
 *
 * @param featurePath Feature 的根目录路径
 * @returns 'single' - 单模块模式, 'multi' - 多子 Feature 模式
 */
export declare function detectFeatureMode(featurePath: string): Promise<'single' | 'multi'>;
/**
 * 创建子 Feature 目录结构
 *
 * @param featurePath Feature 的根目录路径
 * @param subFeatureId 子 Feature ID
 * @param name 子 Feature 名称
 * @returns 子 Feature 目录路径
 */
export declare function createSubFeature(featurePath: string, subFeatureId: string, name: string): Promise<string>;
/**
 * 生成子 Feature 索引表
 *
 * @param featurePath Feature 的根目录路径
 * @returns 子 Feature 索引表的 Markdown 内容
 */
export declare function generateSubFeatureIndex(featurePath: string): Promise<string>;
/**
 * 扫描子 Feature 目录，获取所有子 Feature 的元数据
 *
 * @param featurePath Feature 的根目录路径
 * @returns 子 Feature 元数据数组
 */
export declare function scanSubFeatures(featurePath: string): Promise<SubFeatureMeta[]>;
/**
 * 验证子 Feature 文档的完整性
 *
 * @param subFeature 子 Feature 元数据
 * @returns 包含验证结果的对象
 */
export declare function validateSubFeatureCompleteness(subFeature: SubFeatureMeta): {
    valid: boolean;
    missing: string[];
};
