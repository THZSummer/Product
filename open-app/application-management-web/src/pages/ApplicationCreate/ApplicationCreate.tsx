import React, { useState } from 'react'
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
  Select 
} from 'antd'
import { 
  InboxOutlined, 
  SaveOutlined, 
  ArrowLeftOutlined 
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useApplicationStore } from '@/stores'
import type { CreateAppData } from '@/types'
import { isValidUrl } from '@/utils'
import './ApplicationCreate.less'

const { Title } = Typography
const { Dragger } = Upload

/**
 * 应用创建页组件
 * 提供创建新应用的表单界面
 */
const ApplicationCreate: React.FC = () => {
  const navigate = useNavigate()
  const { createApplication, loading } = useApplicationStore()
  const [form] = Form.useForm()
  
  // 图标上传相关
  const [iconFile, setIconFile] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string>('')

  // 处理表单提交
  const onFinish = async (values: CreateAppData) => {
    try {
      // 创建应用数据
      let processedValues = { ...values };

      // 如果上传了图片，使用上传的图片（Base64），忽略手动输入的 URL
      if (iconFile && previewUrl) {
        processedValues.iconUrl = previewUrl;
      } else if (!iconFile && !previewUrl && processedValues.iconUrl) {
        // 只输入了 URL，保留
        if (!isValidUrl(processedValues.iconUrl)) {
          message.error('图标 URL 格式不正确，请输入有效的 URL 地址');
          return;
        }
      } else {
        // 既没有上传图片也没有输入 URL，移除 iconUrl 字段
        delete processedValues.iconUrl;
      }

      // 如果回调 URL 无效，提示错误
      if (processedValues.callbackUrl && !isValidUrl(processedValues.callbackUrl)) {
        message.error('回调 URL 格式不正确，请输入有效的 URL 地址');
        return;
      }

      const result = await createApplication(processedValues)
      
      if (result) {
        message.success('应用创建成功！')
        // 创建成功后跳转到应用详情页
        navigate(`/apps/${result.id}`)
      } else {
        // 如果创建失败会在 store 中处理错误显示
        message.error('应用创建失败，请重试')
      }
    } catch (error) {
      console.error('Failed to create application:', error)
      // 显示具体错误信息
      const errorMessage = (error as any)?.response?.data?.message || 
                          (error as Error)?.message || 
                          '应用创建失败，请重试';
      message.error(errorMessage)
    }
  }

  // 处理取消操作
  const handleCancel = () => {
    navigate('/apps')
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

  return (
    <div className="application-create">
      <Card className="create-card">
        <div className="create-header">
          <Space size="middle" align="start">
            <Button 
              type="text" 
              icon={<ArrowLeftOutlined />} 
              onClick={handleCancel}
            >
              返回
            </Button>
            <div>
              <Title level={3} style={{ marginBottom: 0 }}>创建新应用</Title>
              <Typography.Text type="secondary">
                填写应用的基本信息以创建一个新的应用
              </Typography.Text>
            </div>
          </Space>
        </div>

        <div className="create-form">
          <Form
            form={form}
            name="createApplication"
            layout="vertical"
            onFinish={onFinish}
            autoComplete="off"
            initialValues={{
              name: '',
              description: '',
              type: 'WEB',
              iconUrl: '',
              callbackUrl: ''
            }}
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
              <Radio.Group>
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
                  pattern: /^(https?|ftp):\/\/(([a-zA-Z0-9\$\-_.+!*'(),;:&=]|%[0-9a-fA-F]{2})+)/,
                  message: '请输入有效的URL地址!'
                }
              ]}
            >
              <Input 
                placeholder="https://your-domain.com/callback" 
              />
            </Form.Item>

            <Form.Item>
              <Space style={{ marginTop: 24 }}>
                <Button 
                  type="primary" 
                  htmlType="submit" 
                  icon={<SaveOutlined />}
                  loading={loading.create}
                >
                  创建应用
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

export default ApplicationCreate