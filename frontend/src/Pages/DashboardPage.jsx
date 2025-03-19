import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";

const DashboardPage = () => {
    const { user } = useAuth();
    const [subscriptions, setSubscriptions] = useState([]);
    const [editingSubscription, setEditingSubscription] = useState(null);
    const [subscriptionData, setSubscriptionData] = useState({
        serviceName: "",
        startDate: "",
        endDate: "",
        price: "",
    });
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

    useEffect(() => {
        fetchSubscriptions();
    }, []);

    const handleSaveSubscription = async (e) => {
        e.preventDefault();
        try {
            if (editingSubscription) {
                await api.put(`/subscriptions/${editingSubscription.id}`, subscriptionData);
            } else {
                await api.post("/subscriptions", {
                    ...subscriptionData,
                    price: parseFloat(subscriptionData.price),
                });
            }
            setSubscriptionData({ serviceName: "", startDate: "", endDate: "", price: "" });
            setEditingSubscription(null);
            fetchSubscriptions();
        } catch (err) {
            console.error(err);
            setError("Ошибка при сохранении подписки.");
        }
    };

    const handleDeleteSubscription = async (id) => {
        try {
            await api.delete(`/subscriptions/${id}`);
            setSubscriptions(subscriptions.filter((sub) => sub.id !== id));
        } catch (err) {
            console.error(err);
            setError("Ошибка при удалении подписки.");
        }
    };

    const handleEditSubscription = (subscription) => {
        setEditingSubscription(subscription);
        setSubscriptionData(subscription);
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 p-8 text-white">
            <h1 className="text-4xl font-bold mb-6">Личный кабинет</h1>

            <div className="bg-white text-black rounded-xl shadow-lg p-6 mb-8">
                <p className="text-xl">Привет, <strong>{user?.email || user?.phone}</strong></p>
            </div>

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
                            <div className="flex justify-end gap-2 mt-4">
                                <button
                                    onClick={() => handleEditSubscription(sub)}
                                    className="bg-yellow-400 text-white px-3 py-1 rounded hover:bg-yellow-500 transition"
                                >
                                    Изменить
                                </button>
                                <button
                                    onClick={() => handleDeleteSubscription(sub.id)}
                                    className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 transition"
                                >
                                    Удалить
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <h2 className="text-xl font-semibold mt-8 mb-4">
                {editingSubscription ? "Изменить подписку" : "Добавить подписку"}
            </h2>
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
                    onChange={(e) => setSubscriptionData({ ...subscriptionData, startDate: e.target.value })}
                    required
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
