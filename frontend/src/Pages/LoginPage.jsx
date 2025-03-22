import { useState } from 'react';
import api from "../services/api";
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import { FaEnvelope, FaPhone, FaLock } from 'react-icons/fa';

const LoginPage = () => {
    const { login } = useAuth();

    const [loginMethod, setLoginMethod] = useState("email"); // "email" или "phone"
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await api.post("/auth/login", {
                [loginMethod]: identifier,
                password,
            });

            const { token, name, email, phoneNumber } = response.data;
            const user = { name, email, phoneNumber };
            login(token, user);
        } catch (err) {
            setError(err.response?.data?.message || "Ошибка входа");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-500 to-purple-600 p-6">
            <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md transform hover:scale-105 transition duration-300">
                <h2 className="text-3xl font-extrabold text-center text-blue-700 mb-6">Вход в SubTrack</h2>

                <div className="flex justify-center space-x-4 mb-6">
                    <button
                        type="button"
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-white ${loginMethod === "email" ? "bg-blue-600" : "bg-gray-300 text-gray-700"}`}
                        onClick={() => setLoginMethod("email")}
                    >
                        <FaEnvelope /> Email
                    </button>
                    <button
                        type="button"
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-white ${loginMethod === "phone" ? "bg-blue-600" : "bg-gray-300 text-gray-700"}`}
                        onClick={() => setLoginMethod("phone")}
                    >
                        <FaPhone /> Телефон
                    </button>
                </div>

                {error && <p className="text-red-500 text-center mb-4">{error}</p>}

                <form onSubmit={handleLogin} className="space-y-4">
                    <div className="relative">
                        {loginMethod === "email" ? (
                            <FaEnvelope className="absolute top-3 left-3 text-blue-500" />
                        ) : (
                            <FaPhone className="absolute top-3 left-3 text-blue-500" />
                        )}
                        <input
                            type={loginMethod === "email" ? "email" : "tel"}
                            placeholder={loginMethod === "email" ? "Email" : "Телефон"}
                            value={identifier}
                            onChange={(e) => setIdentifier(e.target.value)}
                            required
                            className="w-full pl-10 px-4 py-2 border border-blue-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="relative">
                        <FaLock className="absolute top-3 left-3 text-blue-500" />
                        <input
                            type="password"
                            placeholder="Пароль"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="w-full pl-10 px-4 py-2 border border-blue-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-green-500 text-white py-3 rounded-lg shadow-md hover:bg-green-600 transition"
                    >
                        Войти
                    </button>
                </form>

                <p className="mt-4 text-center">
                    Нет аккаунта?{" "}
                    <Link to="/register" className="text-blue-500 hover:underline">
                        Зарегистрироваться
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default LoginPage;
