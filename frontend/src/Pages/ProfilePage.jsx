import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { FaEdit } from 'react-icons/fa'; // Иконка редактирования
import api from "../services/api";

const ProfilePage = () => {
    const { user, updateUser, token } = useAuth();

    const [formData, setFormData] = useState({
        name: user?.name || '',
        email: user?.email || '',
        phoneNumber: user?.phoneNumber || '',
        notifications: user?.notifications || ''
    });

    const [phoneError, setPhoneError] = useState(false);
    const [emailError, setEmailError] = useState(false);
    const [shake, setShake] = useState({ phone: false, email: false });
    const [isEditing, setIsEditing] = useState(false);

    const toggleEdit = () => {
        setIsEditing(!isEditing);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === "phoneNumber") setPhoneError(false);
        if (name === "email") setEmailError(false);

        if (name === "notifications") {
            if (value === "phoneNumber" && !formData.phoneNumber) {
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
        console.log("Форма отправлена, handleSubmit вызван!"); // Проверка вызова функции // Лог перед отправкой
        try {
            const updatedUser = await updateUser(formData);
            console.log("Обновлённые данные пользователя (ответ сервера):", updatedUser); // Лог ответа сервера
            alert('Данные успешно обновлены!');
            setIsEditing(false);
        } catch (err) {
            console.error("Ошибка при обновлении данных:", err);
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
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-[#3500D3]">Профиль</h1>
                    <button
                        onClick={isEditing ? handleSubmit : toggleEdit}
                        className="flex items-center gap-2 bg-[#3500D3] text-white px-4 py-2 rounded-full hover:bg-[#240090] transition"
                    >
                        <FaEdit />
                        <span>{isEditing ? "Сохранить" : "Редактировать"}</span>
                    </button>

                </div>


                <form onSubmit={handleSubmit} className="space-y-4">
                    {['name', 'email', 'phoneNumber'].map((field) => (  // <-- Исправил 'phone' на 'phoneNumber'
                        <div key={field} className="relative">
                            <label className="block text-gray-700 mb-1">
                                {field === 'name' ? 'Имя' : field === 'email' ? 'Email' : 'Телефон'}
                            </label>
                            <input
                                type={field === 'email' ? 'email' : 'text'}
                                name={field}
                                value={formData[field]}
                                onChange={handleChange}
                                className={`w-full p-2 border rounded-lg ${field === 'phoneNumber' && phoneError ? 'border-red-500' : ''} ${field === 'email' && emailError ? 'border-red-500' : ''}`}
                                readOnly={!isEditing}
                            />
                            {field === 'phoneNumber' && phoneError && (
                                <p className="text-red-500 text-sm mt-1">Введите номер телефона!</p>
                            )}
                            {field === 'email' && emailError && (
                                <p className="text-red-500 text-sm mt-1">Введите email!</p>
                            )}
                        </div>
                    ))}

                    <fieldset className="space-y-2">
                        <legend className="text-gray-700 mb-2 font-medium">Получать уведомления через:</legend>
                        {['email', 'phone'].map((type) => (
                            <label
                                key={type}
                                className={`flex items-center gap-2 cursor-pointer ${shake[type] ? 'animate-shake' : ''}`}
                            >
                                <input
                                    type="radio"
                                    name="notifications"
                                    value={type}
                                    checked={formData.notifications === type}
                                    onChange={handleChange}
                                    className="hidden peer"
                                />
                                <div className={`w-5 h-5 border-2 border-gray-400 rounded-full flex items-center justify-center 
                                    peer-checked:border-[#3500D3] peer-checked:bg-[#3500D3] ${shake[type] ? 'border-red-500' : ''}`}>
                                    <div className="w-2.5 h-2.5 bg-white rounded-full hidden peer-checked:block"></div>
                                </div>
                                <span className="text-gray-700">{type === 'email' ? 'Email' : 'Телефон'}</span>
                            </label>
                        ))}
                    </fieldset>
                </form>


            </div>
        </div>
    );
};

export default ProfilePage;