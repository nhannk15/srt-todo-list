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
    TeamOutlined,
    UnorderedListOutlined,
    UserOutlined,
} from '@ant-design/icons';
import { Breadcrumb, Button, Layout, Menu, theme, Typography } from 'antd';
import { useUserStore } from "../store/useUserStore"
import { useNavigate } from "react-router"
import axios from 'axios';
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
    }, [user, navigate]);

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider collapsible collapsed={collapsed} onCollapse={value => setCollapsed(value)}>
                <div className="demo-logo-vertical" />
                <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline" items={items} onClick={(event) => console.log(event)} />
            </Sider>
            <Layout>
                <Header style={{ padding: 0, background: colorBgContainer, textAlign: "center", fontSize: "23px" }}>
                    To-do List
                    <Button onClick={handleLogout}>
                        Logout
                    </Button>
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
                            <Outlet />
                        </div>
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center' }}>
                    Nguyen Khac Le Nhan ©{currentYear}
                </Footer>
            </Layout>
        </Layout>
    );
};
export default HomePage;