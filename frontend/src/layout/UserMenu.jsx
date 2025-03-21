import { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // Импорт useAuth

const UserMenu = () => {
    const [isOpen, setIsOpen] = useState(false);
    const { logout } = useAuth(); // Добавляем logout из контекста
    const menuRef = useRef(null);

    // Закрытие меню при клике вне его области
    useEffect(() => {
        const handleOutsideClick = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        };
        document.addEventListener('mousedown', handleOutsideClick);
        return () => {
            document.removeEventListener('mousedown', handleOutsideClick);
        };
    }, []);

    const handleLogout = () => {
        logout(); // Корректный вызов logout
        setIsOpen(false); // Закрываем меню после выхода
    };

    return (
        <div className="relative" ref={menuRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center gap-2 text-lg hover:text-blue-700 transition duration-300"
            >
                <span>Кабинет</span>
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-48 bg-white shadow-lg rounded-xl border z-50">
                    <ul className="p-2 space-y-2">
                        <li>
                            <Link
                                to="/dashboard"
                                className="block px-4 py-2 hover:bg-blue-100 rounded"
                            >
                                Управление подписками
                            </Link>
                        </li>
                        <li>
                            <Link
                                to="/profile"
                                className="block px-4 py-2 hover:bg-blue-100 rounded"
                            >
                                Профиль
                            </Link>
                        </li>
                        <li>
                            <button
                                onClick={handleLogout} // Исправленный вызов logout
                                className="block w-full text-left px-4 py-2 hover:bg-red-100 rounded text-red-500"
                            >
                                Выйти
                            </button>
                        </li>
                    </ul>
                </div>
            )}
        </div>
    );
};

export default UserMenu;
``