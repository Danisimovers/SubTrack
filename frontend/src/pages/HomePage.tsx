import { Link } from "react-router-dom";

const HomePage = () => {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md text-center">
                <h1 className="text-2xl font-bold mb-6">Добро пожаловать в SubTrack</h1>
                <p className="mb-8">Пожалуйста, войдите или зарегистрируйтесь, чтобы продолжить</p>
                <div className="flex justify-around">
                    <Link to="/login">
                        <button className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition">
                            Вход
                        </button>
                    </Link>
                    <Link to="/register">
                        <button className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition">
                            Регистрация
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
