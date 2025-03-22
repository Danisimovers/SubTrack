import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './context/AuthContext'; // Добавил импорт
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <Router>
            <AuthProvider> {/* Оборачиваем весь App в AuthProvider */}
                <App />
            </AuthProvider>
        </Router>
    </React.StrictMode>
);
