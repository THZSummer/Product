import React, { useState } from 'react'
import { Form, Input, Button, Card, message, Typography } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { authApi } from '../../services/authApi'

const { Title } = Typography

const LoginPage: React.FC = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true)
    try {
      const response = await authApi.login(values.username, values.password)
      
      // 存储 token 和用户信息
      localStorage.setItem('access_token', response.data.token)
      localStorage.setItem('user_id', response.data.userId)
      localStorage.setItem('username', response.data.username)
      localStorage.setItem('role', response.data.role)
      
      message.success('登录成功')
      navigate('/apps')
    } catch (error: any) {
      console.error('Login failed:', error)
      if (error.response?.status === 401) {
        message.error('用户名或密码错误')
      } else {
        message.error('登录失败，请稍后重试')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      height: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <Card style={{ width: 400, boxShadow: '0 20px 60px rgba(0,0,0,0.3)' }}>
        <div style={{ textAlign: 'center', marginBottom: 30 }}>
          <Title level={2} style={{ margin: 0 }}>应用管理系统</Title>
          <Typography.Text type="secondary">请使用账号密码登录</Typography.Text>
        </div>
        
        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input 
              prefix={<UserOutlined />} 
              placeholder="用户名" 
              defaultValue="admin"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
              defaultValue="changeme"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              登录
            </Button>
          </Form.Item>
        </Form>
        
        <Typography.Text type="secondary" style={{ fontSize: 12 }}>
          默认账号：admin / changeme
        </Typography.Text>
      </Card>
    </div>
  )
}

export default LoginPage
