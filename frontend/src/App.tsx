import { Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage.tsx";
import RegisterPage from "./pages/RegisterPage.tsx";
import LoginPage from "./pages/LoginPage.tsx";
import DashboardPage from "./pages/DashboardPage.tsx";


function App() {
    return (
        <Routes>
            {/* Главная страница */}
            <Route path="/" element={<HomePage />} />

            {/* Страница регистрации */}
            <Route path="/register" element={<RegisterPage />} />

            {/* Страница входа */}
            <Route path="/login" element={<LoginPage />} />

            {/* Личный кабинет (доступен только авторизованным) */}
            <Route
                path="/dashboard"
                element={

                    <DashboardPage />

                }
            />
        </Routes>
    );
}

export default App;
