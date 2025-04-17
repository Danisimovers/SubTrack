import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";
import Select from "react-select";  // Импортируем компонент Select

const DashboardPage = () => {
    const { user } = useAuth();
    const [subscriptions, setSubscriptions] = useState([]);
    const [editingSubscription, setEditingSubscription] = useState(null);
    const [subscriptionData, setSubscriptionData] = useState({
        serviceName: "",
        startDate: "",
        endDate: "",
        price: "",
        category: "",
        tags: [],
    });
    const [availableTags, setAvailableTags] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const fetchSubscriptions = async () => {
        try {
            setLoading(true);
            const response = await api.get("/subscriptions");
            setSubscriptions(response.data);
        } catch (err) {
            console.error(err);
            setError("Не удалось загрузить подписки.");
        } finally {
            setLoading(false);
        }
    };

    const fetchTags = async () => {
        try {
            const response = await api.get("/subscriptions/tags");
            setAvailableTags(response.data.map(tag => ({ value: tag, label: tag }))); // Маппируем теги для react-select
        } catch (err) {
            console.error(err);
            setError("Не удалось загрузить теги.");
        }
    };

    useEffect(() => {
        fetchSubscriptions();
        fetchTags();
    }, []);

    const calculateEndDate = (startDate) => {
        const start = new Date(startDate);
        start.setMonth(start.getMonth() + 1); // Добавляем месяц
        return start.toISOString().split("T")[0]; // Возвращаем в формате YYYY-MM-DD
    };

    const handleSaveSubscription = async (e) => {
        e.preventDefault();
        try {
            // Заполняем дату конца, если она не была установлена
            if (!subscriptionData.endDate) {
                subscriptionData.endDate = calculateEndDate(subscriptionData.startDate);
            }

            if (editingSubscription) {
                // Обновляем подписку
                await api.put(`/subscriptions/${editingSubscription.id}`, subscriptionData);
            } else {
                // Создаем новую подписку
                await api.post("/subscriptions", {
                    ...subscriptionData,
                    price: parseFloat(subscriptionData.price),
                });
            }
            setSubscriptionData({ serviceName: "", startDate: "", endDate: "", price: "", category: "", tags: [] });
            setEditingSubscription(null);
            fetchSubscriptions();
        } catch (err) {
            console.error(err);
            setError("Ошибка при сохранении подписки.");
        }
    };

    const handleTagChange = (selectedOptions) => {
        // Обрабатываем изменения в выбранных тегах
        const selectedTags = selectedOptions ? selectedOptions.map(option => option.value) : [];
        setSubscriptionData({ ...subscriptionData, tags: selectedTags });
    };

    const handleExtendSubscription = async (subscriptionId, currentEndDate, existingSubscriptionData) => {
        try {
            if (!existingSubscriptionData) {
                throw new Error("Данные подписки не найдены");
            }

            const today = new Date().toISOString().split("T")[0]; // Получаем сегодняшнюю дату в нужном формате
            const startDate = new Date(existingSubscriptionData.startDate).toISOString().split("T")[0]; // Дата начала подписки

            // Проверка: если дата начала подписки равна сегодняшнему дню, не разрешаем продление
            if (startDate === today) {
                alert("Подписка не может быть продлена, если дата начала - сегодня.");
                return;
            }

            const newEndDate = new Date(currentEndDate);
            newEndDate.setMonth(newEndDate.getMonth() + 1); // Добавляем месяц к текущей дате

            const updatedSubscription = {
                ...existingSubscriptionData, // Сохраняем все данные подписки
                startDate: today, // Обновляем дату начала подписки на сегодняшнюю
                endDate: newEndDate.toISOString().split("T")[0], // Форматируем endDate
            };

            await api.put(`/subscriptions/${subscriptionId}`, updatedSubscription);

            fetchSubscriptions(); // Обновляем список подписок
        } catch (err) {
            console.error(err);
            setError("Не удалось продлить подписку.");
        }
    };


    const handleDeleteSubscription = async (subscriptionId) => {
        try {
            if (window.confirm("Вы уверены, что хотите удалить эту подписку?")) {
                await api.delete(`/subscriptions/${subscriptionId}`);
                fetchSubscriptions(); // Обновляем список подписок после удаления
            }
        } catch (err) {
            console.error(err);
            setError("Не удалось удалить подписку.");
        }
    };

    const handleEditSubscription = (sub) => {
        setEditingSubscription(sub);
        setSubscriptionData({
            serviceName: sub.serviceName,
            startDate: sub.startDate,
            endDate: sub.endDate,
            price: sub.price.toString(),
            category: sub.category || "", // Если категория существует, иначе пустая строка
            tags: sub.tags || [], // Если теги существуют, иначе пустой массив
        });
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 p-8 text-white">
            <h1 className="text-4xl font-bold mb-6">Управление подписками</h1>
            <h2 className="text-2xl font-semibold mb-4">Мои подписки</h2>
            {error && <div className="text-red-500 mb-4">{error}</div>}
            {loading ? (
                <p>Загрузка...</p>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {subscriptions.map((sub) => (
                        <div key={sub.id} className="bg-white text-black p-4 rounded-lg shadow-md">
                            <h3 className="text-lg font-bold">{sub.serviceName}</h3>
                            <p>Начало: {sub.startDate}</p>
                            <p>Конец: {sub.endDate}</p>
                            <p>Цена: {sub.price} ₽</p>
                            <p>Теги: {sub.tags.length > 0 ? sub.tags.join(", ") : "Нет тегов"}</p>

                            <button
                                onClick={() => handleExtendSubscription(sub.id, sub.endDate, sub)} // Передаем всю подписку
                                className="mt-4 bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition"
                            >
                                Продлить на месяц
                            </button>

                            <button
                                onClick={() => handleEditSubscription(sub)} // Устанавливаем подписку для редактирования
                                className="mt-4 bg-yellow-500 text-white py-2 px-4 rounded-lg hover:bg-yellow-600 transition"
                            >
                                Изменить подписку
                            </button>

                            <button
                                onClick={() => handleDeleteSubscription(sub.id)} // Удаляем подписку
                                className="mt-4 bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition"
                            >
                                Удалить подписку
                            </button>
                        </div>
                    ))}
                </div>
            )}

            <h2 className="text-xl font-semibold mt-8 mb-4">{editingSubscription ? "Изменить подписку" : "Добавить подписку"}</h2>
            <form onSubmit={handleSaveSubscription} className="bg-white shadow-lg rounded-xl p-6 space-y-4 text-black">
                <input
                    type="text"
                    placeholder="Название сервиса"
                    value={subscriptionData.serviceName}
                    onChange={(e) => setSubscriptionData({ ...subscriptionData, serviceName: e.target.value })}
                    required
                    className="w-full p-3 border rounded-lg"
                />
                <input
                    type="date"
                    value={subscriptionData.startDate}
                    onChange={(e) => {
                        const startDate = e.target.value;
                        const today = new Date().toISOString().split("T")[0]; // Получаем сегодняшнюю дату в нужном формате

                        // Если выбрана сегодняшняя дата, не разрешаем ее использовать
                        if (startDate > today) {
                            alert("Дата начала подписки не может быть позже сегодняшней.");
                            return;
                        }

                        // Заполняем дату конца автоматически
                        setSubscriptionData({
                            ...subscriptionData,
                            startDate: startDate,
                            endDate: calculateEndDate(startDate),
                        });
                    }}
                    required
                    max={new Date().toISOString().split("T")[0]} // Не даем выбрать дату позже сегодняшней
                    className="w-full p-3 border rounded-lg"
                />
                <input
                    type="date"
                    value={subscriptionData.endDate}
                    onChange={(e) => setSubscriptionData({ ...subscriptionData, endDate: e.target.value })}
                    required
                    className="w-full p-3 border rounded-lg"
                />
                <input
                    type="number"
                    placeholder="Цена (₽)"
                    value={subscriptionData.price}
                    onChange={(e) => setSubscriptionData({ ...subscriptionData, price: e.target.value })}
                    required
                    className="w-full p-3 border rounded-lg"
                />

                <Select
                    isMulti
                    options={availableTags} // Передаем доступные теги
                    value={availableTags.filter(tag => subscriptionData.tags.includes(tag.value))} // Фильтруем выбранные теги
                    onChange={handleTagChange}
                    className="w-full"
                    placeholder="Выберите теги"
                />

                <button
                    type="submit"
                    className="w-full bg-green-500 text-white py-3 rounded-lg shadow-md hover:bg-green-600 transition"
                >
                    {editingSubscription ? "Сохранить" : "Добавить"}
                </button>
            </form>
        </div>
    );
};

export default DashboardPage;
