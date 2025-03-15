import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-3xl font-bold mb-6">Личный кабинет</h1>
            <p className="mb-6">Добро пожаловать! Здесь будет отображение ваших подписок.</p>

            <button
                onClick={handleLogout}
                className="px-6 py-3 bg-red-500 text-white rounded-md hover:bg-red-600 transition"
            >
                Выйти
            </button>
        </div>
    );
};

export default Dashboard;
