import React from 'react'
import { Modal, Alert } from 'antd'
import type { Application } from '@/types'

export interface DeleteConfirmModalProps {
  /** 应用详情 */
  application?: Application
  /** 是否显示模态框 */
  visible: boolean
  /** 确认删除回调 */
  onConfirm: () => void
  /** 取消删除回调 */
  onCancel: () => void
}

/**
 * 删除应用确认模态框
 * 提供删除操作的二次确认界面
 */
const DeleteConfirmModal: React.FC<DeleteConfirmModalProps> = ({
  application,
  visible,
  onConfirm,
  onCancel,
}) => {
  return (
    <Modal
      title={`删除应用 "${application?.name}"`}
      open={visible}
      onOk={onConfirm}
      onCancel={onCancel}
      okButtonProps={{ danger: true }}
      okText="确认删除"
      cancelText="取消"
    >
      <div style={{ padding: '8px 0' }}>
        <Alert
          message="删除应用注意事项"
          description={
            <>
              <p>确认删除后，应用将：</p>
              <ul style={{ margin: 0, paddingLeft: 20 }}>
                <li>状态变更为"已删除"</li>
                <li>不再提供服务</li>
                <li>将在30天后备份自动清除</li>
                <li>在30天内可联系管理员恢复应用</li>
              </ul>
            </>
          }
          type="warning"
          showIcon
        />
        <div style={{ marginTop: 16, padding: 8, backgroundColor: '#f6ffed', border: '1px solid #b7eb8f', borderRadius: 4 }}>
          <strong>重要提醒：</strong>删除为不可逆操作，请确认是否真的需要删除此应用。
        </div>
      </div>
    </Modal>
  )
}

export default DeleteConfirmModal