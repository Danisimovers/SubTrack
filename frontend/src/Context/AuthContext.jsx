import React, { createContext, useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { setAuthToken } from "../services/api";

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    const login = (token, userData) => {
        localStorage.setItem("token", token);
        setAuthToken(token);
        setUser(userData);
        navigate("/dashboard");
    };


    const logout = () => {
        localStorage.removeItem("token");
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
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
