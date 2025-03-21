import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from "../services/api";
const ProfilePage = () => {
    const { user, updateUser, token } = useAuth(); // Достаём токен из контекста
    const [formData, setFormData] = useState({
        name: user?.name || '',
        email: user?.email || '',
        phone: user?.phone || '',
        notifications: user?.notifications || 'email'
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleNotificationChange = (e) => {
        setFormData((prev) => ({ ...prev, notifications: e.target.value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await updateUser(formData);
            alert('Данные успешно обновлены!');
        } catch (err) {
            console.error(err);
            alert('Ошибка при обновлении данных');
        }
    };

    // Функция для отправки тестового запроса


    const sendTestRequest = async () => {
        try {
            const response = await api.get('/subscriptions/trigger-check'); // Короткий путь, как у других запросов
            alert(`Сервер ответил: ${response.data}`);
        } catch (err) {
            console.error('Ошибка при отправке тестового запроса:', err);
            alert('Ошибка при отправке тестового зап    роса');
        }
    };


    return (
        <div className="min-h-screen flex items-center justify-center bg-[#0C0032] p-6">
            <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md">
                <h1 className="text-3xl font-bold text-center text-[#3500D3] mb-6">
                    Профиль
                </h1>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-gray-700 mb-1">Имя</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="w-full p-2 border rounded-lg"
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 mb-1">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            className="w-full p-2 border rounded-lg"
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700 mb-1">Телефон</label>
                        <input
                            type="tel"
                            name="phone"
                            value={formData.phone}
                            onChange={handleChange}
                            className="w-full p-2 border rounded-lg"
                        />
                    </div>

                    {/* Чекбоксы для выбора способа уведомления */}
                    <fieldset className="space-y-2">
                        <legend className="text-gray-700 mb-2 font-medium">Получать уведомления через:</legend>
                        <label className="flex items-center gap-2 cursor-pointer">
                            <input
                                type="radio"
                                name="notifications"
                                value="email"
                                checked={formData.notifications === 'email'}
                                onChange={handleNotificationChange}
                                className="hidden peer"
                            />
                            <div className="w-5 h-5 border-2 border-gray-400 rounded-full flex items-center justify-center peer-checked:border-[#3500D3] peer-checked:bg-[#3500D3]">
                                <div className="w-2.5 h-2.5 bg-white rounded-full hidden peer-checked:block"></div>
                            </div>
                            <span className="text-gray-700">Email</span>
                        </label>

                        <label className="flex items-center gap-2 cursor-pointer">
                            <input
                                type="radio"
                                name="notifications"
                                value="phone"
                                checked={formData.notifications === 'phone'}
                                onChange={handleNotificationChange}
                                className="hidden peer"
                            />
                            <div className="w-5 h-5 border-2 border-gray-400 rounded-full flex items-center justify-center peer-checked:border-[#3500D3] peer-checked:bg-[#3500D3]">
                                <div className="w-2.5 h-2.5 bg-white rounded-full hidden peer-checked:block"></div>
                            </div>
                            <span className="text-gray-700">Телефон</span>
                        </label>
                    </fieldset>

                    <button
                        type="submit"
                        className="w-full bg-[#3500D3] text-white py-2 rounded-lg hover:bg-[#240090] transition"
                    >
                        Сохранить изменения
                    </button>
                </form>

                {/* Кнопка для тестового запроса */}
                <button
                    onClick={sendTestRequest}
                    className="mt-4 w-full bg-green-500 text-white py-2 rounded-lg hover:bg-green-700 transition"
                >
                    Отправить тестовый запрос
                </button>
            </div>
        </div>
    );
};

export default ProfilePage;
