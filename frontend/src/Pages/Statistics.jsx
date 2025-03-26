import { useEffect, useState } from "react";
import api from "../services/api";
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend } from "recharts";

const Statistics = () => {
    const [monthlyExpenses, setMonthlyExpenses] = useState(0);
    const [yearlyExpenses, setYearlyExpenses] = useState(0);
    const [mostExpensive, setMostExpensive] = useState(null);
    const [cheapest, setCheapest] = useState(null);
    const [error, setError] = useState("");

    const fetchStatistics = async () => {
        try {
            const [monthlyRes, yearlyRes, expensiveRes, cheapRes] = await Promise.all([
                api.get("/subscriptions/analytics/monthly-expenses"),
                api.get("/subscriptions/analytics/yearly-expenses"),
                api.get("/subscriptions/analytics/most-expensive"),
                api.get("/subscriptions/analytics/cheapest")
            ]);

            setMonthlyExpenses(monthlyRes.data);
            setYearlyExpenses(yearlyRes.data);
            setMostExpensive(expensiveRes.data);
            setCheapest(cheapRes.data);
        } catch (err) {
            console.error("Ошибка загрузки статистики", err);
            setError("Не удалось загрузить статистику.");
        }
    };

    useEffect(() => {
        fetchStatistics();
    }, []);

    return (
        <div className="p-6 bg-gray-100 min-h-screen">
            <h1 className="text-2xl font-bold mb-6">Статистика</h1>

            {error && <div className="text-red-500 mb-4">{error}</div>}

            <div className="grid grid-cols-2 gap-4 mb-6">
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <p>Ежемесячные расходы</p>
                    <h2 className="text-3xl font-bold">{monthlyExpenses} ₽</h2>
                </div>
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <p>Годовые расходы</p>
                    <h2 className="text-3xl font-bold">{yearlyExpenses} ₽</h2>
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4 mb-6">
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <h3>Самая дорогая подписка</h3>
                    {mostExpensive ? (
                        <p>{mostExpensive.name}: {mostExpensive.price} ₽</p>
                    ) : (
                        <p>Нет данных</p>
                    )}
                </div>
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <h3>Самая дешёвая подписка</h3>
                    {cheapest ? (
                        <p>{cheapest.name}: {cheapest.price} ₽</p>
                    ) : (
                        <p>Нет данных</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Statistics;
