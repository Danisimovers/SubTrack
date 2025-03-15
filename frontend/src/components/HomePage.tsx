// src/components/HomePage.tsx
import { Link, useNavigate } from 'react-router-dom';

const HomePage = () => {
    const token = localStorage.getItem('token');
    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-4xl font-bold mb-8">Добро пожаловать в SubTrack</h1>
            {!token ? (
                <div className="space-x-4">
                    <Link
                        to="/login"
                        className="px-6 py-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition"
                    >
                        Войти
                    </Link>
                    <Link
                        to="/register"
                        className="px-6 py-3 bg-green-500 text-white rounded-md hover:bg-green-600 transition"
                    >
                        Зарегистрироваться
                    </Link>
                </div>
            ) : (
                <button
                    onClick={() => navigate('/dashboard')}
                    className="px-6 py-3 bg-purple-500 text-white rounded-md hover:bg-purple-600 transition"
                >
                    Перейти в личный кабинет
                </button>
            )}
        </div>
    );
};

export default HomePage;
