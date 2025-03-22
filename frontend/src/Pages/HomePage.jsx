import { Link } from 'react-router-dom';

const HomePage = () => {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-r from-[#0C0032] to-[#240090] p-6">
            <div className="text-center text-white space-y-6 max-w-2xl">
                <h1 className="text-6xl font-extrabold tracking-wide">
                    Добро пожаловать в <span className="bg-clip-text text-transparent bg-gradient-to-r from-[#3500D3] to-[#282828]">SubTrack</span>
                </h1>

                <p className="text-xl opacity-90">
                    Управляйте своими подписками легко и удобно. Контролируйте расходы и всегда будьте в курсе всех платежей.
                </p>

                <div className="flex justify-center gap-4 mt-6">
                    <Link
                        to="/register"
                        className="bg-[#3500D3] text-white px-8 py-3 rounded-full shadow-lg hover:bg-[#282828] transition transform hover:scale-105"
                    >
                        Начать
                    </Link>
                    <Link
                        to="/login"
                        className="bg-[#282828] text-white px-8 py-3 rounded-full shadow-lg hover:bg-[#3500D3] transition transform hover:scale-105"
                    >
                        Уже есть аккаунт?
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
