const API =
    import.meta.env.VITE_API;
import axios from "axios";

export const getInfo = async () => {
    try {
        const response = await axios.get(`${API}api/me`, {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        const data = response.data;
        console.log(data);
        return data;
    } catch (error) {
        console.log("Error occured");
    }
}