import { createContext, useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { setAuthToken } from "../services/api";

interface AuthContextProps {
    user: any;
    login: (token: string, userData: any) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextProps | null>(null);

export const useAuth = () => useContext(AuthContext)!;

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<any>(null);
    const navigate = useNavigate();

    const login = (token: string, userData: any) => {
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

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
