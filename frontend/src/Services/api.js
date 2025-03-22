import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8081/api",
    headers: {
        "Content-Type": "application/json",
    },
});

// Устанавливаем заголовок авторизации
export const setAuthToken = (token) => {
    if (token) {
        api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
        console.log("Токен установлен:", token);
    } else {
        delete api.defaults.headers.common["Authorization"];
        console.log("Токен удален");
    }
};

// Функция отправки SMS с логами
export const sendTestSms = async (phone, message) => {
    try {
        console.log("Отправка SMS:", { phone, message });
        const response = await api.post('/sms/send', null, {
            params: { phone, message }
        });
        console.log("Ответ сервера:", response.data);
        return response.data;
    } catch (error) {
        console.error("Ошибка при отправке SMS:", error);
        throw error;
    }
};

// Метод обновления данных пользователя
export const updateUser = async (updatedData) => {
    try {
        const response = await api.put('/user/update', updatedData);
        return response.data;
    } catch (error) {
        console.error("Ошибка обновления пользователя:", error);
        throw error;
    }
};

export default api;
