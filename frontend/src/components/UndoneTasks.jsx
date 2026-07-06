import React, { useEffect, useState } from 'react';
import axios from "axios";
import { Button, Form, Input, Modal, Radio, Space, Table, Tag } from 'antd';
import { deleteTask, getUndoneTasks, markTaskAsDone, updateTask } from '../service/taskService';
import { useFormik } from 'formik';
import * as Yup from "yup";
import { useOutletContext } from 'react-router';

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
export default function UndoneTasks() {
    const [undoneTasks, setUndoneTasks] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
    const [selectedTask, setSelectedTask] = useState({});
    const { reloadTrigger } = useOutletContext();
    const formik = useFormik({
        initialValues: {
            title: selectedTask.title,
            description: selectedTask.description,
            priority: selectedTask.priority
        },
        validationSchema: validationSchema,
        validateOnBlur: true,
        validateOnChange: false,
        onSubmit: async (values, { resetForm }) => {
            await handleUpdateSubmit(values, resetForm);
        }
    })

    const handleDelete = async (id) => {
        setLoading(true);
        try {
            await deleteTask(id);
            const updatedData = await getUndoneTasks();
            setUndoneTasks(updatedData);
            console.log(`Task with ID ${id} deleted successfully`);
        } catch (error) {
            console.error('Error deleting task:', error);
        } finally {

        }
    }

    const handleUpdate = async (id, values) => {
        setLoading(true);
        try {
            await updateTask(id, values);
            const updatedData = await getUndoneTasks();
            setUndoneTasks(updatedData);
            console.log(`Task with ID ${id} udpated successfully`);
        } catch (error) {
            console.error('Error updating task:', error);
        } finally {

        }
    }

    const handleMarkAsDone = async (id) => {
        setLoading(true);
        try {
            await markTaskAsDone(id);
            const updatedData = await getUndoneTasks();
            setUndoneTasks(updatedData);
            console.log(`Task with ID ${id} marked done successfully`);
        } catch (error) {
            console.error('Error marking done task:', error);
        } finally {

        }
    }

    const showModal = () => {
        console.log("showModal() - show Modal")
        setIsModalOpen(true);
    };
    const handleOk = () => {
        console.log("Delete task: " + selectedTask.id);
        handleDelete(selectedTask.id);
        setIsModalOpen(false);
    };
    const handleCancel = () => {
        setIsModalOpen(false);
    };
    const showUpdateModal = () => {
        console.log("showUpdateModal() - show Update Modal");
        console.log("Target task: " + selectedTask.title);
        setIsUpdateModalOpen(true);
    };
    const handleUpdateOk = () => {
        console.log("Delete task: " + selectedTask.id);
        handleDelete(selectedTask.id);
        setIsUpdateModalOpen(false);
    };
    const handleUpdateCancel = () => {
        setIsUpdateModalOpen(false);
    };
    const handleUpdateSubmit = async (values, resetForm) => {
        await handleUpdate(selectedTask.id, values);
        setIsUpdateModalOpen(false);
    }

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
            render: (text) => <strong>{text}</strong>,
        },
        {
            title: 'Description',
            dataIndex: 'description',
            key: 'description',
            ellipsis: true,
        },
        {
            title: 'Status',
            dataIndex: 'done',
            key: 'done',
            render: (done) => (
                <Tag color={done ? 'green' : 'orange'}>
                    {done ? 'Done' : 'Pending'}
                </Tag>
            ),
        },
        {
            title: 'Priority',
            dataIndex: 'priority',
            key: 'priority',
            render: (priority) => {
                if (priority == 1) {
                    return (
                        <Tag color={'error'}>
                            Must do
                        </Tag>
                    )
                } else if (priority == 2) {
                    return (
                        <Tag color={'gold'}>
                            Should do
                        </Tag>
                    )
                } else if (priority == 3) {
                    return (
                        <Tag color={'geekblue'}>
                            Could do
                        </Tag>
                    )
                } else if (priority == 4) {
                    return (
                        <Tag>
                            Won't do
                        </Tag>
                    )
                }
            }
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                <Space size="small">
                    <Button
                        type="primary"
                        size="small"
                        onClick={() => {
                            setSelectedTask(record);
                            formik.setValues({
                                title: record.title,
                                description: record.description,
                                priority: record.priority
                            });
                            showUpdateModal();
                        }}
                    >
                        Edit
                    </Button>
                    <Button
                        danger
                        size="small"
                        onClick={() => {
                            setSelectedTask(record);
                            showModal();
                        }}
                    >
                        Delete
                    </Button>

                    <Button
                        variant='filled'
                        color='green'
                        size="small"
                        onClick={() => {
                            handleMarkAsDone(record.id);
                        }}
                    >
                        Mark as done
                    </Button>
                </Space>
            ),
        },
    ];

    useEffect(() => {
        const fetchApi = async () => {
            try {
                const data = await getUndoneTasks();
                console.log(data);
                setUndoneTasks(data);
            } catch (error) {
                console.log(error);
            } finally {
                setLoading(false);
            }
        };
        fetchApi();
    }, [loading, reloadTrigger]);

    return (
        <>
            <Table
                dataSource={undoneTasks}
                columns={columns}
                loading={loading}
                rowKey="id"
                pagination={{ pageSize: 5 }}
                bordered
            />

            <Modal
                title="Delete Task"
                closable={{ 'aria-label': 'Custom Close Button' }}
                open={isModalOpen}
                onOk={handleOk}
                okText={"Sure"}
                cancelText={"No"}
                onCancel={handleCancel}
                style={{ textAlign: "center" }}
            >
                {`Are you sure to delete task: ${selectedTask.id}`}
            </Modal>

            <Modal
                title="Update Task"
                closable={{ 'aria-label': 'Custom Close Button' }}
                open={isUpdateModalOpen}
                onOk={handleUpdateOk}
                okText={"Sure"}
                cancelText={"No"}
                onCancel={handleUpdateCancel}
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
                        <Form.Item label="Priority">
                            <Radio.Group
                                name="priority"
                                value={formik.values.priority}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur}>
                                <Radio.Button value={1}>Must Do</Radio.Button>
                                <Radio.Button value={2}>Should Do</Radio.Button>
                                <Radio.Button value={3}>Could Do</Radio.Button>
                            </Radio.Group>
                        </Form.Item>

                        <Form.Item label="Title">
                            <Input name="title" value={formik.values.title}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur} />
                        </Form.Item>

                        <Form.Item label="Description">
                            <Input name="description"
                                value={formik.values.description}
                                onChange={formik.handleChange}
                                onBlur={formik.handleBlur} />
                        </Form.Item>

                        <div style={{ textAlign: "center" }}>
                            <Button htmlType='submit' variant='' onSubmit={formik.handleSubmit}>Update</Button>
                        </div>
                    </Form>
                </div>
            </Modal>
        </>


    );
}