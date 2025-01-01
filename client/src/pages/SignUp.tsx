import { useState } from "react";
import { useNavigate } from "react-router-dom";
import '../styles/auth.css'

const Signup = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
    });

    const [errors, setErrors] = useState({
        name: '',
        email: '',
        password: '',
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData({...formData, [id]: value})
        validateField(id, value);
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const hasErrors = Object.values(errors).some((error) => error);
        if (!hasErrors && Object.values(formData).every((field) => field)) {
          alert('User registered successfully!');
          navigate('/home');
        } else {
          alert('Please fill all fields correctly.');
        }
      };

    const validateField = (field: string, value: string) => {
        let error = '';
        if (field === 'name' && !value) error = 'Name is required.';
        if (field === 'email' && !/^\S+@\S+\.\S+$/.test(value)) error = 'Enter a valid email';
        if (field === 'password' && value.length < 8) error = 'Password must be at least 8 characters.';
        setErrors({ ...errors, [field]: error });
    }
    return (
        <div className="auth-container">
            <div className="flex max-w-1/2 items-center justify-center">
                <img
                    src="src/assets/signup-bg.png"
                    alt="Signup Illustration"
                    className="bg-cover h-auto"
                />
            </div>

            <div className="form-section">
                <form className="form" onSubmit={handleSubmit}>
                    <h2 className="form-title">Create an account</h2>
                    <p className="form-subtitle">Enter your details below</p> 
                    <div className="input-container">
                        <input
                            type="text"
                            id="name"
                            className={`input-field ${
                                errors.name ? 'border-red-500' : 'focus:outline-none'
                              }`}
                            placeholder="Name"
                            value={formData.name}
                            onChange={handleChange}
                        />
                        {errors.name && <p className="text-red-500 text-sm mt-1">{errors.name}</p>}
                    </div>
                    <div className="input-container">
                      <input
                        type="text"
                        id="email"
                        className={`input-field ${
                          errors.email ? 'border-red-500' : 'focus:outline-none'
                        }`}              
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleChange}
                      />
                      {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email}</p>}
                    </div>
                    {/* Password Field */}
                    <div className="input-container">
                    
                      <input
                        type="password"
                        id="password"
                        className={`input-field ${
                          errors.password ? 'border-red-500' : 'focus:outline-none'
                        }`}
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                      />
                      {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password}</p>}
                    </div>
                    {/* Signup Button */}
                    <div className="button-section">
                      <button
                        type="submit"
                        className="signup-button">
                        Create Account
                      </button>
                    </div>
                    
                </form>
                <div className="google-button-section">
                    <button className="google-button">
                      Signup with Google
                    </button>
                    <p className="text-xs text-center">
                        Already have an account?{' '}
                        <a href="/login" className="nav-link">
                            Login
                        </a>
                    </p>
                </div>
            </div>
        </div>
    );
}

export default Signup;