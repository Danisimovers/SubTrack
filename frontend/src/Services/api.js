import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8081/api",  // Замените на свой URL
    headers: {
        "Content-Type": "application/json",
    },
});

export const setAuthToken = (token) => {
    if (token) {
        api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    } else {
        delete api.defaults.headers.common["Authorization"];
    }
};
export const sendTestSms = async (phone, message) => {
    try {
        const response = await api.post('/sms/send', null, {
            params: { phone, message }
        });
        return response.data;
    } catch (error) {
        console.error('Ошибка при отправке SMS:', error);
        throw error;
    }
};

export default api;
