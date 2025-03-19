import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import { FaEnvelope, FaLock } from 'react-icons/fa';

const LoginPage = () => {
    const { login } = useAuth();
    const [credentials, setCredentials] = useState({ email: '', password: '' });
    const [error, setError] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await login(credentials.email, credentials.password);
        } catch {
            setError('Ошибка при входе. Проверьте данные.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-500 to-purple-600 p-6">
            <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md transform hover:scale-105 transition duration-300">
                <h2 className="text-3xl font-extrabold text-center text-blue-700 mb-6">Вход в SubTrack</h2>

                {error && <p className="text-red-500 text-center mb-4">{error}</p>}

                <form onSubmit={handleLogin} className="space-y-4">
                    <div className="relative">
                        <FaEnvelope className="absolute top-3 left-3 text-blue-500" />
                        <input
                            type="email"
                            placeholder="Email"
                            value={credentials.email}
                            onChange={(e) => setCredentials({ ...credentials, email: e.target.value })}
                            required
                            className="w-full pl-10 px-4 py-2 border border-blue-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="relative">
                        <FaLock className="absolute top-3 left-3 text-blue-500" />
                        <input
                            type="password"
                            placeholder="Пароль"
                            value={credentials.password}
                            onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
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
                    Нет аккаунта? {" "}
                    <Link to="/register" className="text-blue-500 hover:underline">
                        Зарегистрироваться
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default LoginPage;
