import axios from "axios";

export const getUndoneTasks = async () => {
    try {
        const response = await axios.get("http://localhost:8080/api/undone-tasks", {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    } catch (error) {
        console.log(error);
    }
}

export const getDoneTasks = async () => {
    try {
        const response = await axios.get("http://localhost:8080/api/done-tasks", {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    } catch (error) {
        console.log(error);
    }
}

export const getAllTasks = async () => {
    try {
        const response = await axios.get("http://localhost:8080/api/tasks", {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    } catch (error) {
        console.log(error);
    }
}

export const deleteTask = async (id) => {
    try {
        const response = await axios.delete(`http://localhost:8080/api/tasks/${id}`, {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    } catch (error) {
        console.log(error);
    }
}

export const updateTask = async (id, values) => {
    try {
        const response = await axios.put(`http://localhost:8080/api/tasks/${id}`, values, {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    } catch (error) {
        console.log(error);
    }
}

export const markTaskAsDone = async (id) => {
    try {
        const response = await axios.post(`http://localhost:8080/api/mark-as-done`, {
            taskId: id
        }, {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    } catch (error) {
        console.log(error);
    }
}

export const postTask = async (values) => {
    try {
        const response = await axios.post(`http://localhost:8080/api/tasks`, values, {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        return response.data;
    } catch (error) {
        console.log(error);
    }
}