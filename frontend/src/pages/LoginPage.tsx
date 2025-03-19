import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";
import { Link } from "react-router-dom";

const LoginPage = () => {
    const { login } = useAuth();

    const [loginMethod, setLoginMethod] = useState<"email" | "phone">("email");
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");

        try {
            const response = await api.post("/auth/login", {
                [loginMethod]: identifier,
                password,
            });

            const { token, user } = response.data;
            login(token, user); // Сохраняем токен и пользователя
        } catch (err: any) {
            setError(err.response?.data?.message || "Ошибка авторизации");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
                <h2 className="text-2xl font-bold mb-4 text-center">Вход</h2>

                <div className="flex justify-center space-x-4 mb-4">
                    <button
                        className={`px-4 py-2 rounded ${loginMethod === "email" ? "bg-blue-500 text-white" : "bg-gray-200"}`}
                        onClick={() => setLoginMethod("email")}
                    >
                        Email
                    </button>
                    <button
                        className={`px-4 py-2 rounded ${loginMethod === "phone" ? "bg-blue-500 text-white" : "bg-gray-200"}`}
                        onClick={() => setLoginMethod("phone")}
                    >
                        Телефон
                    </button>
                </div>

                <form onSubmit={handleLogin} className="space-y-4">
                    <input
                        type={loginMethod === "email" ? "email" : "tel"}
                        placeholder={loginMethod === "email" ? "Email" : "Телефон"}
                        value={identifier}
                        onChange={(e) => setIdentifier(e.target.value)}
                        required
                        className="w-full px-4 py-2 border rounded"
                    />
                    <input
                        type="password"
                        placeholder="Пароль"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        className="w-full px-4 py-2 border rounded"
                    />
                    {error && <div className="text-red-500">{error}</div>}
                    <button
                        type="submit"
                        className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
                    >
                        Войти
                    </button>
                </form>

                <p className="mt-4 text-center">
                    Нет аккаунта?{" "}
                    <Link to="/register" className="text-blue-500 hover:underline">
                        Регистрация
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default LoginPage;
