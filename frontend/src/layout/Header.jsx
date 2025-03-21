import { useState, useRef, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { FaUserCircle } from 'react-icons/fa';
import { AuthContext } from '../context/AuthContext'; // Подключаем контекст аутентификации

const Header = () => {
    const { user, logout } = useContext(AuthContext); // Достаем данные пользователя из контекста
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const menuRef = useRef(null);

    // Закрытие меню при клике вне его
    useEffect(() => {
        const handleOutsideClick = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsMenuOpen(false);
            }
        };
        document.addEventListener('mousedown', handleOutsideClick);
        return () => {
            document.removeEventListener('mousedown', handleOutsideClick);
        };
    }, []);

    return (
        <header className="bg-[#0C0032] shadow-lg sticky top-0 z-50">
            <div className="container mx-auto flex justify-between items-center py-4 px-6">
                <Link to="/" className="text-4xl font-extrabold tracking-wide text-white">
                    SubTrack
                </Link>

                <nav className="flex items-center gap-4">
                    {user ? (
                        <>
                            <Link
                                to="/statistics"
                                className="bg-white text-red-700 px-4 py-2 rounded-lg shadow-md hover:bg-blue-100 transition"
                            >
                                Статистика
                            </Link>

                            {/* Кабинет с выпадающим меню */}
                            <div className="relative" ref={menuRef}>
                                <button
                                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                                    className="flex items-center gap-2 bg-white text-purple-600 px-4 py-2 rounded-lg shadow-md hover:bg-purple-100 transition"
                                >
                                    <FaUserCircle size={24} />
                                    <span>Кабинет</span>
                                </button>

                                {isMenuOpen && (
                                    <div className="absolute right-0 mt-2 w-48 bg-white border shadow-lg rounded-lg">
                                        <ul className="p-2 space-y-2">
                                            <li>
                                                <Link to="/dashboard" className="block px-4 py-2 hover:bg-blue-100 rounded">
                                                    Управление подписками
                                                </Link>
                                            </li>
                                            <li>
                                                <Link to="/profile" className="block px-4 py-2 hover:bg-blue-100 rounded">
                                                    Профиль
                                                </Link>
                                            </li>
                                            <li>
                                                <button
                                                    onClick={logout}
                                                    className="block w-full text-left px-4 py-2 hover:bg-red-100 rounded text-red-500"
                                                >
                                                    Выйти
                                                </button>
                                            </li>
                                        </ul>
                                    </div>
                                )}
                            </div>
                        </>
                    ) : (
                        <>
                            <Link
                                to="/register"
                                className="bg-white text-blue-600 px-4 py-2 rounded-lg shadow-md hover:bg-blue-100 transition"
                            >
                                Регистрация
                            </Link>
                            <Link
                                to="/login"
                                className="bg-white text-green-600 px-4 py-2 rounded-lg shadow-md hover:bg-green-100 transition"
                            >
                                Вход
                            </Link>
                        </>
                    )}
                </nav>
            </div>
        </header>
    );
};

export default Header;
