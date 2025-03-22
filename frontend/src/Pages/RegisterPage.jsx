import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";
import { Link } from "react-router-dom";
import { FaEnvelope, FaPhone, FaUser } from "react-icons/fa";

const RegisterPage = () => {
    const { login } = useAuth();

    const [registerMethod, setRegisterMethod] = useState("email");
    const [identifier, setIdentifier] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleRegister = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await api.post("/auth/register", {
                username,
                [registerMethod]: identifier,
                password
            });

            const { token, user } = response.data;
            login(token, user);
        } catch (err) {
            setError(err.response?.data?.message || "Ошибка регистрации");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-500 to-purple-600 p-6">
            <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md transform hover:scale-105 transition duration-300">
                <h2 className="text-3xl font-extrabold text-center text-blue-700 mb-6">Регистрация в SubTrack</h2>

                <div className="flex justify-center space-x-4 mb-6">
                    <button
                        type="button"
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-white ${registerMethod === "email" ? "bg-blue-600" : "bg-gray-300 text-gray-700"}`}
                        onClick={() => setRegisterMethod("email")}
                    >
                        <FaEnvelope /> Email
                    </button>
                    <button
                        type="button"
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-white ${registerMethod === "phone" ? "bg-blue-600" : "bg-gray-300 text-gray-700"}`}
                        onClick={() => setRegisterMethod("phone")}
                    >
                        <FaPhone /> Телефон
                    </button>
                </div>

                <form onSubmit={handleRegister} className="space-y-4">
                    <div className="relative">
                        <FaUser className="absolute top-3 left-3 text-blue-500" />
                        <input
                            type="text"
                            placeholder="Имя пользователя"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            className="w-full pl-10 px-4 py-2 border border-blue-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <input
                        type={registerMethod === "email" ? "email" : "tel"}
                        placeholder={registerMethod === "email" ? "Email" : "Телефон"}
                        value={identifier}
                        onChange={(e) => setIdentifier(e.target.value)}
                        required
                        className="w-full px-4 py-2 border border-blue-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <input
                        type="password"
                        placeholder="Пароль"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        className="w-full px-4 py-2 border border-blue-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    {error && <div className="text-red-500 text-center">{error}</div>}
                    <button
                        type="submit"
                        className="w-full bg-green-500 text-white py-3 rounded-lg shadow-md hover:bg-green-600 transition"
                    >
                        Зарегистрироваться
                    </button>
                </form>

                <p className="mt-4 text-center">
                    Уже есть аккаунт? {" "}
                    <Link to="/login" className="text-blue-500 hover:underline">
                        Войти
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;