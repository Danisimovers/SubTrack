import React, { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { setAuthToken } from "../services/api";
import api from "../services/api"; // Убрал дублирующий импорт updateUser

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export { AuthContext };

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    // ✅ Проверяем наличие токена при загрузке
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setAuthToken(token);

            const storedUser = localStorage.getItem("user");

            try {
                const parsedUser =
                    storedUser && storedUser !== "undefined" ? JSON.parse(storedUser) : null;

                setUser(parsedUser);
            } catch (error) {
                console.error("Ошибка парсинга данных пользователя:", error);
                setUser(null); // Сбрасываем пользователя, если данные некорректны
            }
        }
    }, []);

    const login = (token, userData) => {
        localStorage.setItem("token", token);
        localStorage.setItem("user", JSON.stringify(userData)); // ✅ Сохраняем user в виде JSON
        setAuthToken(token);
        setUser(userData);
        navigate("/dashboard");
    };

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setAuthToken(null);
        setUser(null);
        navigate("/");
    };

    const updateUser = async (updatedData) => {
        console.log("Отправляемые данные:", updatedData);
        if (!user) return; // Проверяем, есть ли пользователь

        try {
            const response = await api.put("/user/update", updatedData);
            const updatedUser = response.data;

            setUser((prevUser) => ({
                ...prevUser,
                ...updatedUser,
            }));

            localStorage.setItem("user", JSON.stringify(updatedUser)); // Обновляем данные в локальном хранилище
        } catch (error) {
            console.error("Ошибка при обновлении данных пользователя:", error);
            if (error.response) {
                console.error("Ответ сервера:", error.response.data);
            }
        }
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, updateUser }}>
            {children}
        </AuthContext.Provider>
    );
};
