import React, { useState, useEffect } from 'react'
import { 
  Form, 
  Input, 
  Button, 
  Card, 
  Space, 
  Radio, 
  Upload, 
  message, 
  Typography,
  Alert,
  Divider
} from 'antd'
import { 
  InboxOutlined, 
  SaveOutlined, 
  ArrowLeftOutlined,
  UndoOutlined
} from '@ant-design/icons'
import { useParams, useNavigate } from 'react-router-dom'
import { useApplicationStore } from '@/stores'
import type { UpdateAppData, Application } from '@/types'
import { isValidUrl } from '@/utils'

const { Title, Text } = Typography
const { Dragger } = Upload

/**
 * 应用编辑页组件
 * 提供编辑应用信息的表单界面
 */
const ApplicationEdit: React.FC = () => {
  const { appId } = useParams<{ appId: string }>()
  const navigate = useNavigate()
  const { 
    currentApplication, 
    loading, 
    fetchApplicationDetail, 
    updateApplication 
  } = useApplicationStore()
  const [form] = Form.useForm()
  
  // 图标上传相关
  const [iconFile, setIconFile] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string>('')

  // 加载应用详情
  useEffect(() => {
    if (appId) {
      fetchApplicationDetail(appId)
    }
  }, [appId])

  // 初始化表单数据
  useEffect(() => {
    if (currentApplication) {
      form.setFieldsValue({
        name: currentApplication.name,
        description: currentApplication.description,
        type: currentApplication.type,
        iconUrl: currentApplication.iconUrl || '',
        callbackUrl: currentApplication.callbackUrl || ''
      })
    }
  }, [currentApplication])

  // 处理表单提交
  const onFinish = async (values: UpdateAppData) => {
    if (!appId || !currentApplication) {
      message.error('应用信息加载失败')
      return
    }

    try {
      // 构造更新数据，处理URL格式
      const updateData = { ...values }

      if (iconFile || previewUrl) {
        updateData.iconUrl = previewUrl // 使用预览URL或上传的文件处理结果
      } else if (currentApplication.iconUrl) {
        updateData.iconUrl = currentApplication.iconUrl // 保持原有的图片URL
      } else if (!values.iconUrl) {
        delete updateData.iconUrl // 如果没有上传也没有提供URL，则删除此项
      }

      // 验证URL合理性
      if (updateData.callbackUrl && !isValidUrl(updateData.callbackUrl)) {
        message.error('请输入有效的回调URL')
        return
      }

      const result = await updateApplication(appId, updateData)

      if (result) {
        message.success('应用更新成功！')
        // 更新成功后跳转到应用详情页
        navigate(`/apps/${result.id}`)
      } else {
        message.error('应用更新失败，请重试')
      }
    } catch (error) {
      console.error('Failed to update application:', error)
      message.error('应用更新失败，请重试')
    }
  }

  // 处理取消操作
  const handleCancel = () => {
    if (currentApplication) {
      navigate(`/apps/${currentApplication.id}`)
    } else {
      navigate('/apps')
    }
  }

  // 重置到原始值
  const handleReset = () => {
    form.resetFields()
  }

  // 图标上传配置
  const iconUploadProps = {
    name: 'file',
    multiple: false,
    accept: 'image/*',
    maxCount: 1,
    beforeUpload: (file: File) => {
      // 验证文件类型
      const isImage = file.type.startsWith('image/')
      if (!isImage) {
        message.error('只能上传图片文件!')
        return false
      }
      
      // 验证文件大小 (例如：限制5MB)
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isLt5M) {
        message.error('图片大小不能超过5MB!')
        return false
      }
      
      setIconFile(file)
      
      // 生成预览URL
      const reader = new FileReader()
      reader.onload = () => {
        setPreviewUrl(reader.result as string)
      }
      reader.readAsDataURL(file)
      
      return false // 返回false阻止自动上传，手动处理上传事件
    },
    onRemove: () => {
      setIconFile(null)
      setPreviewUrl('')
    }
  }

  // 如果当前应用状态是被删除（DELETED）或者被禁用（DISABLED）状态下，
  // 根据业务需求判断是否可以编辑，这里假设仅 DRAFT 状态可以编辑
  const canEdit = currentApplication?.status === 'draft'

  if (!canEdit && currentApplication) {
    return (
      <div className="application-edit">
        <Card className="edit-card">
          <div className="edit-header">
            <Space size="middle" align="start">
              <Button 
                type="text" 
                icon={<ArrowLeftOutlined />} 
                onClick={handleCancel}
              >
                返回
              </Button>
              <div>
                <Title level={3} style={{ marginBottom: 0 }}>编辑应用</Title>
                <Typography.Text type="secondary">
                  {`应用ID: ${currentApplication.id}`}
                </Typography.Text>
              </div>
            </Space>
          </div>

          <Divider />

          <Alert
            message={`应用当前状态为"${currentApplication.status}"，无法编辑`}
            description="应用只能在'草稿'状态下才可以编辑，请检查应用状态或联系系统管理员"
            type="warning"
            showIcon
          />
          
          <div style={{ marginTop: 16 }}>
            <Space>
              <Button 
                icon={<ArrowLeftOutlined />} 
                onClick={handleCancel}
              >
                返回详情页
              </Button>
            </Space>
          </div>
        </Card>
      </div>
    )
  }

  return (
    <div className="application-edit">
      <Card className="edit-card">
        <div className="edit-header">
          <Space size="middle" align="start">
            <Button 
              type="text" 
              icon={<ArrowLeftOutlined />} 
              onClick={handleCancel}
            >
              返回
            </Button>
            <div>
              <Title level={3} style={{ marginBottom: 0 }}>编辑应用</Title>
              <Typography.Text type="secondary">
                {`应用ID: ${currentApplication?.id || '加载中...'}`}
              </Typography.Text>
            </div>
          </Space>
        </div>

        <div className="edit-form">
          <Form
            form={form}
            name="editApplication"
            layout="vertical"
            onFinish={onFinish}
            autoComplete="off"
          >
            <Form.Item
              label="应用名称"
              name="name"
              rules={[
                { 
                  required: true, 
                  message: '请输入应用名称!' 
                },
                { 
                  min: 2, 
                  max: 64, 
                  message: '应用名称长度应在2-64个字符之间!'
                },
                { 
                  pattern: /^[a-zA-Z0-9\u4e00-\u9fa5_-]+$/,
                  message: '应用名称只能包含中文、字母、数字、下划线和连字符!'
                }
              ]}
            >
              <Input 
                placeholder="请输入应用名称" 
                maxLength={64}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="应用类型"
              name="type"
              rules={[{ required: true, message: '请选择应用类型!' }]}
            >
              <Radio.Group disabled={currentApplication?.status !== 'draft'}>
                <Radio value="WEB">Web 应用</Radio>
                <Radio value="MOBILE">移动应用</Radio>
                <Radio value="DESKTOP">桌面应用</Radio>
                <Radio value="API">API 服务</Radio>
                <Radio value="OTHER">其他</Radio>
              </Radio.Group>
            </Form.Item>

            <Form.Item
              label="应用描述"
              name="description"
            >
              <Input.TextArea 
                rows={4} 
                placeholder="请输入应用描述，说明应用的主要功能和特点" 
                maxLength={512}
                showCount
              />
            </Form.Item>

            <Form.Item
              label="应用图标"
              help="建议尺寸 128x128像素，支持 JPG、PNG、SVG 格式，最大 5MB"
            >
              <Space direction="vertical" style={{ width: '100%' }}>
                <Dragger {...iconUploadProps}>
                  <p className="ant-upload-drag-icon">
                    <InboxOutlined />
                  </p>
                  <p className="ant-upload-text">
                    点击或拖拽图片到此处上传应用图标
                  </p>
                </Dragger>
                
                {previewUrl && (
                  <div style={{ textAlign: 'center', marginTop: 16 }}>
                    <img 
                      src={previewUrl} 
                      alt="应用图标预览" 
                      style={{ 
                        width: 64, 
                        height: 64, 
                        borderRadius: 8,
                        border: '1px solid #ddd' 
                      }} 
                    />
                    <p style={{ marginTop: 8, fontSize: '12px', color: '#666' }}>
                      图标预览
                    </p>
                  </div>
                )}
                {!previewUrl && currentApplication?.iconUrl && (
                  <div style={{ textAlign: 'center', marginTop: 16 }}>
                    <img 
                      src={currentApplication.iconUrl} 
                      alt="应用当前图标" 
                      style={{ 
                        width: 64, 
                        height: 64, 
                        borderRadius: 8,
                        border: '1px solid #ddd' 
                      }} 
                    />
                    <p style={{ marginTop: 8, fontSize: '12px', color: '#666' }}>
                      当前图标
                    </p>
                  </div>
                )}
                
                <Form.Item
                  label="或，直接输入图标链接"
                  name="iconUrl"
                  style={{ marginBottom: 0 }}
                >
                  <Input 
                    placeholder="https://example.com/icon.png" 
                  />
                </Form.Item>
              </Space>
            </Form.Item>

            <Form.Item
              label="回调URL"
              name="callbackUrl"
              rules={[
                { 
                  pattern: /^(https?|ftp):\/\/(([a-zA-Z0-9$\-_.+!*'(),;:&=]|%[0-9a-fA-F]{2})+)/,
                  message: '请输入有效的URL地址!'
                }
              ]}
            >
              <Input 
                placeholder="https://your-domain.com/callback" 
              />
            </Form.Item>

            <Divider />

            <Form.Item>
              <Space>
                <Button 
                  type="primary" 
                  htmlType="submit" 
                  icon={<SaveOutlined />}
                  loading={loading.update}
                  disabled={currentApplication?.status !== 'draft'}
                >
                  保存更改
                </Button>
                <Button 
                  icon={<UndoOutlined />} 
                  onClick={handleReset}
                  disabled={loading.update}
                >
                  重置
                </Button>
                <Button onClick={handleCancel}>
                  取消
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </div>
      </Card>
    </div>
  )
}

export default ApplicationEdit