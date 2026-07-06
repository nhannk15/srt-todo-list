import React, { useEffect, useState } from 'react';
import { Link, Outlet } from "react-router";
import {
    CheckCircleOutlined,
    DashboardOutlined,
    DesktopOutlined,
    FileOutlined,
    Loading3QuartersOutlined,
    LoadingOutlined,
    PieChartOutlined,
    PlusOutlined,
    QuestionCircleOutlined,
    TeamOutlined,
    UnorderedListOutlined,
    UserOutlined,
} from '@ant-design/icons';
import { Form, Breadcrumb, Button, Cascader, FloatButton, Input, InputNumber, Layout, Menu, Modal, Radio, Select, Switch, theme, TreeSelect, Typography } from 'antd';
import { useUserStore } from "../store/useUserStore"
import { useNavigate } from "react-router"
import axios from 'axios';
import { useFormik } from "formik";
import * as Yup from "yup";
import { postTask } from '../service/taskService';
const { Header, Content, Footer, Sider } = Layout;
function getItem(label, key, icon, children) {
    return {
        key,
        icon,
        children,
        label,
    };
}
const items = [
    {
        key: 'sub1',
        icon: <UnorderedListOutlined />,
        label: "Tasks",
        children: [
            {
                key: 'sub1-1',
                icon: <Loading3QuartersOutlined />,
                label: <Link to={"/undone-tasks"}>Undone Tasks</Link>
            },
            {
                key: 'sub1-2',
                icon: <CheckCircleOutlined />,
                label: <Link to={"/done-tasks"}>Done Tasks</Link>
            },
            {
                key: 'sub1-3',
                icon: <DashboardOutlined />,
                label: <Link to={"/all-tasks"}>All Tasks</Link>
            }
        ]
    }
];
const validationSchema = Yup.object({
    title: Yup.string()
        .required("Title is required")
        .min(5, "Min title is 5")
        .max(100, "Max title is 100"),
    description: Yup.string()
        .required("Description is required")
        .min(5, "Min description is 5")
        .max(100, "Max description is 100"),
    priority: Yup.number()
        .min(1, "Min priority is 1")
        .max(3, "Max priority is 3")
        .default(1)
});
const HomePage = () => {
    const [collapsed, setCollapsed] = useState(false);
    const {
        token: { colorBgContainer, borderRadiusLG },
    } = theme.useToken();
    const currentYear = new Date().getFullYear();
    const user = useUserStore((state) => state.user);
    const setUser = useUserStore((state) => state.setUser);
    const logout = useUserStore((state) => state.logout);
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [reloadTrigger, setReloadTrigger] = useState(true);
    const formik = useFormik({
        initialValues: {
            title: "",
            description: "",
            priority: 1
        },
        validationSchema: validationSchema,
        validateOnChange: false,
        validateOnBlur: true,
        onSubmit: async (values, { resetForm }) => {
            await handleSubmit(values, resetForm);
        }
    })
    const handleSubmit = async (values, resetForm) => {
        try {
            const responseData = await postTask(values);
            resetForm();
            handleCancel();
            console.log(values);
            setReloadTrigger(true);
        } catch (error) {
            console.log(error);
        }
    }

    const showModal = () => {
        setIsModalOpen(true);
    };
    const handleOk = () => {
        setIsModalOpen(false);
    };
    const handleCancel = () => {
        setIsModalOpen(false);
    };

    const handleLogout = async () => {
        try {
            const response = await axios.post("http://localhost:8080/logout", {}, {
                withCredentials: true,
                headers: {
                    "Content-Type": "application/json"
                }
            })
        } catch (error) {
            console.log(error);
        }
        logout();
    };

    useEffect(() => {
        if (user == null) {
            navigate("/login");
        }
        setReloadTrigger(false);
    }, [reloadTrigger, user, navigate]);

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider collapsible collapsed={collapsed} onCollapse={value => setCollapsed(value)}>
                <div className="demo-logo-vertical" />
                <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline" items={items} onClick={(event) => console.log(event)} />
            </Sider>
            <Layout>
                <Header style={{ padding: 0, background: colorBgContainer, textAlign: "center", fontSize: "23px" }}>
                    <div style={{display: "flex", alignItems: "center", justifyContent: "space-between"}}>
                        <div style={{marginLeft: "15px"}}>
                            Welcome {user == null ? "" : user.fullname}
                        </div>
                        <Button onClick={handleLogout}>
                            Logout
                        </Button>
                    </div>

                </Header>
                <Content style={{ margin: '0 16px' }}>
                    <Breadcrumb style={{ margin: '16px 0' }} items={[{ title: 'User' }, { title: 'To do list' }]} />
                    <div
                        style={{
                            padding: 24,
                            minHeight: 360,
                            background: colorBgContainer,
                            borderRadius: borderRadiusLG,
                        }}
                    >
                        <div>
                            <Outlet context={{ reloadTrigger }} />
                        </div>
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center' }}>
                    Nguyen Khac Le Nhan ©{currentYear}
                </Footer>
            </Layout>
            <Modal
                title="Add new task"
                closable={{ 'aria-label': 'Custom Close Button' }}
                open={isModalOpen}
                onOk={handleOk}
                onCancel={handleCancel}
                style={{ textAlign: "center" }}
                footer={null}
            >
                <div style={{ textAlign: "left", width: "100%" }}>
                    <Form
                        labelCol={{ span: 4 }}
                        wrapperCol={{ span: 14 }}
                        layout="horizontal"
                        initialValues={{ size: "medium" }}
                        size={"medium"}
                        style={{ maxWidth: 600 }}
                        onFinish={formik.handleSubmit}
                    >
                        <Form.Item label="Priority" validateStatus='error'>
                            <Radio.Group
                                name='priority'
                                value={formik.values.priority}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                            >
                                <Radio.Button value={1}>Must Do</Radio.Button>
                                <Radio.Button value={2}>Should Do</Radio.Button>
                                <Radio.Button value={3}>Could Do</Radio.Button>
                            </Radio.Group>
                        </Form.Item>

                        <Form.Item label="Title"
                            validateStatus={formik.errors.title && "error"}
                            help={formik.errors.title}>
                            <Input
                                name="title"
                                value={formik.values.title}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                            />
                        </Form.Item>

                        <Form.Item label="Description"
                            validateStatus={formik.errors.description && "error"}
                            help={formik.errors.description}>
                            <Input
                                name="description"
                                value={formik.values.description}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}
                            />
                        </Form.Item>

                        <div style={{ textAlign: "center" }}>
                            <Button htmlType='submit' variant='' onSubmit={formik.handleSubmit}>Add</Button>
                        </div>
                    </Form>
                </div>
            </Modal>
            <FloatButton icon={<PlusOutlined />} type="primary" style={{ insetInlineEnd: 24 }} onClick={showModal} />
        </Layout>

    );
};
export default HomePage;