import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from "../services/api";

const ProfilePage = () => {
    const { user, updateUser, token } = useAuth();

    const [formData, setFormData] = useState({
        name: user?.name || '',
        email: user?.email || '',
        phone: user?.phoneNumber || '',
        notifications: user?.notifications || ''
    });

    const [phoneError, setPhoneError] = useState(false);
    const [emailError, setEmailError] = useState(false);
    const [shake, setShake] = useState({ phone: false, email: false });

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === "phone") setPhoneError(false);
        if (name === "email") setEmailError(false);

        if (name === "notifications") {
            if (value === "phone" && !formData.phone) {
                setPhoneError(true);
                setShake({ ...shake, phone: true });
                setTimeout(() => setShake({ ...shake, phone: false }), 300);
                return;
            }
            if (value === "email" && !formData.email) {
                setEmailError(true);
                setShake({ ...shake, email: true });
                setTimeout(() => setShake({ ...shake, email: false }), 300);
                return;
            }
        }

        setFormData((prev) => ({ ...prev, [name]: value }));
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

    const sendTestRequest = async () => {
        try {
            const response = await api.get('/subscriptions/trigger-check');
            alert(`Сервер ответил: ${response.data}`);
        } catch (err) {
            console.error('Ошибка при отправке тестового запроса:', err);
            alert('Ошибка при отправке тестового запроса');
        }
    };

    const sendTestSMS = async () => {
        if (!formData.phone) {
            alert("Введите номер телефона!");
            return;
        }

        try {
            const phone = formData.phone.replace(/\D/g, ""); // Оставляем только цифры
            const message = "Срок действия вашей подписки истекает. Не забудьте продлить ее!)";

            console.log("Отправка SMS:", { phone, message });

            const response = await api.post('sms/send', null, { params: { phone, message } });

            console.log("Ответ сервера:", response.data);
            alert(`СМС отправлено: ${JSON.stringify(response.data)}`);
        } catch (err) {
            console.error("Ошибка при отправке SMS:", err);

            if (err.response) {
                console.error("Статус ошибки:", err.response.status);
                console.error("Ответ сервера:", err.response.data);
                alert(`Ошибка при отправке SMS: ${JSON.stringify(err.response.data)}`);
            } else {
                alert(`Ошибка при отправке SMS: ${err.message}`);
            }
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
                            className={`w-full p-2 border rounded-lg ${emailError ? 'border-red-500' : ''}`}
                        />
                        {emailError && (
                            <p className="text-red-500 text-sm mt-1">Введите email!</p>
                        )}
                    </div>

                    <div>
                        <label className="block text-gray-700 mb-1">Телефон</label>
                        <input
                            type="tel"
                            name="phone"
                            value={formData.phone}
                            onChange={handleChange}
                            className={`w-full p-2 border rounded-lg ${phoneError ? 'border-red-500' : ''}`}
                        />
                        {phoneError && (
                            <p className="text-red-500 text-sm mt-1">Введите номер телефона!</p>
                        )}
                    </div>

                    <fieldset className="space-y-2">
                        <legend className="text-gray-700 mb-2 font-medium">Получать уведомления через:</legend>

                        {/* Email */}
                        <label className={`flex items-center gap-2 cursor-pointer ${shake.email ? 'animate-shake' : ''}`}>
                            <input
                                type="radio"
                                name="notifications"
                                value="email"
                                checked={formData.notifications === 'email'}
                                onChange={handleChange}
                                className="hidden peer"
                            />
                            <div className={`w-5 h-5 border-2 border-gray-400 rounded-full flex items-center justify-center 
                                peer-checked:border-[#3500D3] peer-checked:bg-[#3500D3] ${shake.email ? 'border-red-500' : ''}`}>
                                <div className="w-2.5 h-2.5 bg-white rounded-full hidden peer-checked:block"></div>
                            </div>
                            <span className="text-gray-700">Email</span>
                        </label>

                        {/* Телефон */}
                        <label className={`flex items-center gap-2 cursor-pointer ${shake.phone ? 'animate-shake' : ''}`}>
                            <input
                                type="radio"
                                name="notifications"
                                value="phone"
                                checked={formData.notifications === 'phone'}
                                onChange={handleChange}
                                className="hidden peer"
                            />
                            <div className={`w-5 h-5 border-2 border-gray-400 rounded-full flex items-center justify-center 
                                peer-checked:border-[#3500D3] peer-checked:bg-[#3500D3] ${shake.phone ? 'border-red-500' : ''}`}>
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

                <button
                    onClick={sendTestRequest}
                    className="mt-4 w-full bg-green-500 text-white py-2 rounded-lg hover:bg-green-700 transition"
                >
                    Отправить тестовый запрос
                </button>

                <button
                    onClick={sendTestSMS}
                    className="mt-2 w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-700 transition"
                >
                    Отправить тестовое SMS
                </button>
            </div>

            {/* Стили для вибрации */}
            <style>
                {`
                @keyframes shake {
                    0% { transform: translateX(0); }
                    25% { transform: translateX(-5px); }
                    50% { transform: translateX(5px); }
                    75% { transform: translateX(-5px); }
                    100% { transform: translateX(0); }
                }
                .animate-shake {
                    animation: shake 0.3s ease-in-out;
                }
                `}
            </style>
        </div>
    );
};

export default ProfilePage;
