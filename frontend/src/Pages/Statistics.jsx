import React from "react";
import { PieChart, Pie, Cell, LineChart, Line, XAxis, YAxis, Tooltip, Legend } from "recharts";

const dataGrowth = [
    { name: "Jan", Premium: 10, Basic: 5 },
    { name: "Feb", Premium: 15, Basic: 10 },
    { name: "Mar", Premium: 25, Basic: 15 },
    { name: "Apr", Premium: 40, Basic: 20 },
    { name: "May", Premium: 60, Basic: 30 },
    { name: "Jun", Premium: 90, Basic: 40 },
    { name: "Jul", Premium: 120, Basic: 60 },
    { name: "Aug", Premium: 140, Basic: 80 },
    { name: "Sep", Premium: 160, Basic: 100 },
    { name: "Oct", Premium: 180, Basic: 120 },
    { name: "Nov", Premium: 190, Basic: 130 },
    { name: "Dec", Premium: 200, Basic: 140 },
];

const dataPie = [
    { name: "Premium Annual", value: 3000 },
    { name: "Premium Monthly", value: 2500 },
    { name: "Basic Annual", value: 2000 },
    { name: "Basic Monthly", value: 1555 },
];

const Statistics = () => {
    return (
        <div className="p-6 bg-gray-100 min-h-screen">
            <h1 className="text-2xl font-bold mb-6">Statistics</h1>

            <div className="grid grid-cols-4 gap-4 mb-6">
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <p>Total Subscribers</p>
                    <h2 className="text-3xl font-bold">12,346</h2>
                </div>
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <p>Monthly Revenue</p>
                    <h2 className="text-3xl font-bold">$86,429</h2>
                </div>
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <p>Churn Rate</p>
                    <h2 className="text-3xl font-bold">2.8%</h2>
                </div>
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <p>Avg. Lifetime Value</p>
                    <h2 className="text-3xl font-bold">$1,240</h2>
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4 mb-6">
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <h3>Subscription Growth</h3>
                    <LineChart width={400} height={200} data={dataGrowth}>
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Line type="monotone" dataKey="Premium" stroke="#8884d8" />
                        <Line type="monotone" dataKey="Basic" stroke="#82ca9d" />
                    </LineChart>
                </div>
                <div className="p-4 bg-white rounded-lg shadow-md">
                    <h3>Subscription by Plan</h3>
                    <PieChart width={300} height={200}>
                        <Pie
                            data={dataPie}
                            cx="50%"
                            cy="50%"
                            outerRadius={80}
                            fill="#8884d8"
                            dataKey="value"
                            label
                        >
                            {dataPie.map((_, index) => (
                                <Cell key={`cell-${index}`} fill={["#8884d8", "#82ca9d", "#ffc658", "#ff7300"][index]} />
                            ))}
                        </Pie>
                    </PieChart>
                </div>
            </div>

            <div className="bg-white p-4 rounded-lg shadow-md">
                <h3 className="text-lg font-bold mb-4">Recent Subscribers</h3>
                <table className="w-full">
                    <thead>
                    <tr>
                        <th>Customer</th>
                        <th>Plan</th>
                        <th>Amount</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>John Doe</td>
                        <td>Premium Annual</td>
                        <td>$199.99</td>
                        <td>Active</td>
                    </tr>
                    <tr>
                        <td>Alice Smith</td>
                        <td>Premium Monthly</td>
                        <td>$19.99</td>
                        <td>Active</td>
                    </tr>
                    <tr>
                        <td>Robert Johnson</td>
                        <td>Basic Annual</td>
                        <td>$99.99</td>
                        <td>Pending</td>
                    </tr>
                    <tr>
                        <td>Emily Williams</td>
                        <td>Basic Monthly</td>
                        <td>$9.99</td>
                        <td>Cancelled</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Statistics;