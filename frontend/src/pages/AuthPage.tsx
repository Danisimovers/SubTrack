import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';

export default function AuthPage() {
    return (
        <div className="min-h-screen flex flex-col justify-center items-center space-y-8 bg-gray-100 p-4">
            <h1 className="text-2xl font-bold">Добро пожаловать</h1>
            <RegisterForm />
            <LoginForm />
        </div>
    );
}
