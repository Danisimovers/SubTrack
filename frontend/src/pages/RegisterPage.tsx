import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";
import { Link } from "react-router-dom";

const RegisterPage = () => {
    const { login } = useAuth();

    const [registerMethod, setRegisterMethod] = useState<"email" | "phone">("email");
    const [identifier, setIdentifier] = useState("");
    const [username, setUsername] = useState(""); // Новое поле для имени пользователя
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");

        try {
            const response = await api.post("/auth/register", {
                username, // всегда отправляем username
                [registerMethod]: identifier, // либо email, либо phone
                password
            });

            const { token, user } = response.data;
            login(token, user); // Сохраняем токен и пользователя
        } catch (err: any) {
            setError(err.response?.data?.message || "Ошибка регистрации");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
                <h2 className="text-2xl font-bold mb-4 text-center">Регистрация</h2>

                {/* Переключение метода регистрации */}
                <div className="flex justify-center space-x-4 mb-4">
                    <button
                        type="button"
                        className={`px-4 py-2 rounded ${registerMethod === "email" ? "bg-blue-500 text-white" : "bg-gray-200"}`}
                        onClick={() => setRegisterMethod("email")}
                    >
                        Email
                    </button>
                    <button
                        type="button"
                        className={`px-4 py-2 rounded ${registerMethod === "phone" ? "bg-blue-500 text-white" : "bg-gray-200"}`}
                        onClick={() => setRegisterMethod("phone")}
                    >
                        Телефон
                    </button>
                </div>

                {/* Форма регистрации */}
                <form onSubmit={handleRegister} className="space-y-4">
                    <input
                        type="text"
                        placeholder="Имя пользователя"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        className="w-full px-4 py-2 border rounded"
                    />
                    <input
                        type={registerMethod === "email" ? "email" : "tel"}
                        placeholder={registerMethod === "email" ? "Email" : "Телефон"}
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
                        className="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600"
                    >
                        Зарегистрироваться
                    </button>
                </form>

                {/* Ссылка на страницу входа */}
                <p className="mt-4 text-center">
                    Уже есть аккаунт?{" "}
                    <Link to="/login" className="text-blue-500 hover:underline">
                        Войти
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;
