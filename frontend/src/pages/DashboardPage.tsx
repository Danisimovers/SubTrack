import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";

interface Subscription {
    id: number;
    serviceName: string;
    startDate: string;
    endDate: string;
    price: number;
}

const DashboardPage = () => {
    const { user, logout } = useAuth();
    const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
    const [newSubscription, setNewSubscription] = useState({
        serviceName: "",
        startDate: "",
        endDate: "",
        price: "",
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Загрузка подписок
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

    // Добавление подписки
    const handleAddSubscription = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await api.post("/subscriptions", {
                ...newSubscription,
                price: parseFloat(newSubscription.price),
            });
            setNewSubscription({ serviceName: "", startDate: "", endDate: "", price: "" });
            fetchSubscriptions(); // обновляем список
        } catch (err) {
            console.error(err);
            setError("Ошибка при добавлении подписки.");
        }
    };

    // Удаление подписки
    const handleDeleteSubscription = async (id: number) => {
        try {
            await api.delete(`/subscriptions/${id}`);
            setSubscriptions(subscriptions.filter((sub) => sub.id !== id)); // обновляем локально
        } catch (err) {
            console.error(err);
            setError("Ошибка при удалении подписки.");
        }
    };

    return (
        <div className="p-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold">Личный кабинет</h1>
                <button
                    onClick={logout}
                    className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                >
                    Выйти
                </button>
            </div>

            <div className="mb-6">
                <p><strong>Привет,</strong> {user?.email || user?.phone}</p>
            </div>

            <h2 className="text-2xl font-semibold mb-4">Мои подписки</h2>

            {error && <div className="text-red-500 mb-4">{error}</div>}
            {loading ? (
                <p>Загрузка...</p>
            ) : (
                <table className="w-full border">
                    <thead>
                    <tr className="bg-gray-200">
                        <th className="p-2 border">Сервис</th>
                        <th className="p-2 border">Начало</th>
                        <th className="p-2 border">Конец</th>
                        <th className="p-2 border">Цена</th>
                        <th className="p-2 border">Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {subscriptions.map((sub) => (
                        <tr key={sub.id}>
                            <td className="p-2 border">{sub.serviceName}</td>
                            <td className="p-2 border">{sub.startDate}</td>
                            <td className="p-2 border">{sub.endDate}</td>
                            <td className="p-2 border">{sub.price} ₽</td>
                            <td className="p-2 border text-center">
                                <button
                                    onClick={() => handleDeleteSubscription(sub.id)}
                                    className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                                >
                                    Удалить
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}

            <h2 className="text-xl font-semibold mt-8 mb-4">Добавить подписку</h2>
            <form onSubmit={handleAddSubscription} className="space-y-4">
                <input
                    type="text"
                    placeholder="Название сервиса"
                    value={newSubscription.serviceName}
                    onChange={(e) => setNewSubscription({ ...newSubscription, serviceName: e.target.value })}
                    required
                    className="w-full p-2 border rounded"
                />
                <input
                    type="date"
                    placeholder="Дата начала"
                    value={newSubscription.startDate}
                    onChange={(e) => setNewSubscription({ ...newSubscription, startDate: e.target.value })}
                    required
                    className="w-full p-2 border rounded"
                />
                <input
                    type="date"
                    placeholder="Дата окончания"
                    value={newSubscription.endDate}
                    onChange={(e) => setNewSubscription({ ...newSubscription, endDate: e.target.value })}
                    required
                    className="w-full p-2 border rounded"
                />
                <input
                    type="number"
                    placeholder="Цена (₽)"
                    value={newSubscription.price}
                    onChange={(e) => setNewSubscription({ ...newSubscription, price: e.target.value })}
                    required
                    className="w-full p-2 border rounded"
                />
                <button
                    type="submit"
                    className="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600"
                >
                    Добавить
                </button>
            </form>
        </div>
    );
};

export default DashboardPage;
