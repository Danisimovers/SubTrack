import { useState } from 'react';
import api from '../api/axios';

export default function RegisterForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleRegister = async () => {
        try {
            const response = await api.post('/auth/register', {
                email,
                password,
                role: 'USER', // или другой если надо
            });
            console.log('Зарегистрирован:', response.data);
            localStorage.setItem('token', response.data.token); // сохраним токен
        } catch (error) {
            console.error('Ошибка регистрации:', error);
        }
    };

    return (
        <div className="space-y-2">
            <h2 className="font-bold">Регистрация</h2>
            <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Email"
                className="border p-2 rounded w-full"
            />
            <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Пароль"
                className="border p-2 rounded w-full"
            />
            <button onClick={handleRegister} className="bg-green-500 text-white px-4 py-2 rounded">
                Зарегистрироваться
            </button>
        </div>
    );
}
