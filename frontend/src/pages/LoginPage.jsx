import React, { useEffect } from 'react';
const { Title, Paragraph, Text, Link } = Typography;
import { Button, Divider, Typography } from 'antd';
import {
    GoogleOutlined
} from '@ant-design/icons';
import { getInfo } from '../service/authService';
import axios from 'axios';
import { useUserStore } from '../store/useUserStore';
import { useNavigate } from "react-router"

const style = {
    position: "absolute",
    top: "40%",
    left: "50%",
    translate: "-50% -50%",
    padding: "30px",
    boxShadow: "rgba(0, 0, 0, 0.24) 0px 3px 8px",
    textAlign: "center",
    borderRadius: "16px"

}

export default function LoginPage() {

    const user = useUserStore((state) => state.user);
    const setUser = useUserStore((state) => state.setUser);
    const navigate = useNavigate();
    if (user != null) {
        navigate("/undone-tasks");
    }

    const handleLogin = async () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    }

    useEffect(() => {
        const checkLoggedIn = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/me", {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                console.log(response.data);
                setUser(response.data);

            } catch (err) {
                console.error("Check login failed:", err);
            } finally {
            }
        };

        checkLoggedIn();
    }, []);
    return (
        <>
            <div style={style}>
                <Title>
                    Welcome to Todolist
                </Title>
                <Button variant='solid' color='danger' icon={<GoogleOutlined />} onClick={handleLogin}>
                    Continue with Google
                </Button>
            </div>
        </>
    )
}
