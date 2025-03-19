import { Routes, Route } from 'react-router-dom';
import Layout from './layout/Layout';
import HomePage from './Pages/HomePage';
import RegisterPage from './Pages/RegisterPage';
import DashboardPage from './Pages/DashboardPage';
import LoginPage from './Pages/LoginPage';
import ProfilePage from './Pages/ProfilePage';

const App = () => (
    <Routes>
        <Route path="/" element={<Layout />}>
            <Route index element={<HomePage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
        </Route>
    </Routes>
);

export default App;
