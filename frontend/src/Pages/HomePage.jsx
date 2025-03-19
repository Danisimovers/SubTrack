import { Link } from 'react-router-dom';

const HomePage = () => {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-r from-blue-500 to-purple-600 p-6">
            <div className="text-center text-white space-y-6 max-w-2xl">
                <h1 className="text-6xl font-extrabold tracking-wide">
                    Добро пожаловать в <span className="bg-clip-text text-transparent bg-gradient-to-r from-yellow-400 to-pink-500">SubTrack</span>
                </h1>

                <p className="text-xl opacity-90">
                    Управляйте своими подписками легко и удобно. Контролируйте расходы и всегда будьте в курсе всех платежей.
                </p>

                <div className="flex justify-center gap-4 mt-6">
                    <Link
                        to="/register"
                        className="bg-yellow-400 text-black px-8 py-3 rounded-full shadow-lg hover:bg-yellow-500 transition transform hover:scale-105"
                    >
                        Начать
                    </Link>
                    <Link
                        to="/login"
                        className="bg-white text-blue-600 px-8 py-3 rounded-full shadow-lg hover:bg-gray-200 transition transform hover:scale-105"
                    >
                        Уже есть аккаунт?
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
