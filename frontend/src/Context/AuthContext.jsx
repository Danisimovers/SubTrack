import React, { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { setAuthToken } from "../services/api";

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
                const parsedUser = storedUser && storedUser !== "undefined"
                    ? JSON.parse(storedUser)
                    : null;

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

    const updateUser = (updatedData) => {
        setUser((prevUser) => ({
            ...prevUser,
            ...updatedData
        }));
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, updateUser }}>
            {children}
        </AuthContext.Provider>
    );
};
