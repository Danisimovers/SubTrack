import {memo, useEffect, useState} from "react";
import api from "../services/api";
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer, CartesianGrid, PieChart, Pie, Cell } from "recharts";

const Statistics = memo(() => {
    const [monthlyExpenses, setMonthlyExpenses] = useState(0);
    const [yearlyExpenses, setYearlyExpenses] = useState(0);
    const [mostExpensive, setMostExpensive] = useState(null);
    const [cheapest, setCheapest] = useState(null);
    const [averagePrice, setAveragePrice] = useState(0);
    const [tagDistribution, setTagDistribution] = useState([]);
    const [totalSubscriptions, setTotalSubscriptions] = useState(0);
    const [subscriptionsByStatus, setSubscriptionsByStatus] = useState({});
    const [error, setError] = useState("");
    const fetchStatistics = async () => {
        try {
            const [
                monthlyRes,
                yearlyRes,
                expensiveRes,
                cheapRes,
                avgPriceRes,
                tagDistRes,
                totalSubsRes,
                statusCountRes,
            ] = await Promise.all([
                api.get("/subscriptions/analytics/monthly-expenses"),
                api.get("/subscriptions/analytics/yearly-expenses"),
                api.get("/subscriptions/analytics/most-expensive"),
                api.get("/subscriptions/analytics/cheapest"),
                api.get("/subscriptions/average-price"),
                api.get("/subscriptions/expenses/tags"),
                api.get("/subscriptions/total"),
                api.get("/subscriptions/status/count"),
            ]);

            setMonthlyExpenses(monthlyRes.data);
            setYearlyExpenses(yearlyRes.data);
            setMostExpensive(expensiveRes.data);
            setCheapest(cheapRes.data);
            setAveragePrice(avgPriceRes.data);
            setTagDistribution(Object.entries(tagDistRes.data).map(([tag, value]) => ({ tag, value })));
            setTotalSubscriptions(totalSubsRes.data);
            setSubscriptionsByStatus(statusCountRes.data);
        } catch (err) {
            console.error("Ошибка загрузки статистики", err);
            setError("Не удалось загрузить статистику.");
        }
    };


    useEffect(() => {
        let ignore = false;

        fetchStatistics().then(() => {
            if (ignore) return;
        });

        return () => {
            ignore = true;
        };
    }, []);
    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <h1 className="text-3xl font-semibold text-gray-800 mb-8">Статистика по подпискам</h1>

            {error && <div className="text-red-600 mb-4 text-lg">{error}</div>}

            <div className="grid grid-cols-2 gap-4 mb-8">
                <div className="p-6 bg-white rounded-lg shadow-xl">
                    <p className="text-gray-500">Средняя цена подписки</p>
                    <h2 className="text-3xl font-bold text-indigo-600">{averagePrice} ₽</h2>
                </div>
                <div className="p-6 bg-white rounded-lg shadow-xl">
                    <p className="text-gray-500">Общее количество подписок</p>
                    <h2 className="text-3xl font-bold text-indigo-600">{totalSubscriptions}</h2>
                </div>
            </div>
            <div className="grid grid-cols-2 gap-4 mb-8">
                <div className="p-6 bg-white rounded-lg shadow-xl">
                    <p className="text-gray-500">Самая дорогая подписка</p>
                    <h2 className="text-3xl font-bold text-indigo-600">{mostExpensive?.name} {mostExpensive?.price} ₽</h2>
                </div>
                <div className="p-6 bg-white rounded-lg shadow-xl">
                    <p className="text-gray-500">Самая дешевая подписка</p>
                    <h2 className="text-3xl font-bold text-indigo-600">{cheapest?.name} {cheapest?.price} ₽</h2>
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4 mb-8">
                <div className="p-6 bg-white rounded-lg shadow-xl">
                    <p className="text-gray-500">Ежемесячные расходы</p>
                    <h2 className="text-3xl font-bold text-indigo-600">{monthlyExpenses} ₽</h2>
                </div>
            </div>


            <div className="mb-8">
                <h3 className="text-xl font-semibold text-gray-800 mb-4">Распределение расходов по тегам</h3>
                <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                        <Pie
                            data={tagDistribution}
                            dataKey="value"
                            nameKey="tag"
                            cx="50%"
                            cy="50%"
                            outerRadius={100}
                            labelLine={true}  // Включаем линии меток
                            label={({name, percent}) => `${name} (${(percent * 100).toFixed(1)}%)`}  // Форматируем подпись с процентом
                        >
                            {tagDistribution.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={["#ff7300", "#ff0000", "#00ff00", "#00aaff"][index % 4]} />
                            ))}
                        </Pie>
                    </PieChart>
                </ResponsiveContainer>
            </div>

        </div>
    );
});

export default memo(Statistics);