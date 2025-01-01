import '../styles/auth.css'

const Login = () => {
    return (
        <div className='auth-container'>
            <div className='hero-section'>
                <img
                    src='src/assets/signup-bg.png'
                    alt="Login illustration"
                />
            </div>
            <div className='form-section'>
                <form className='form'>
                    <h2 className='form-title'>Login to ShopSphere</h2>
                    <p className="form-subtitle">Enter your details below</p>
                    <div className='input-container'>
                        <input
                            type='email'
                            id='email'
                            className='input-field'
                            placeholder='Email'
                        />
                    </div>
                    <div className='input-container'>
                        <input
                            type='password'
                            id='password'
                            className='input-field'
                            placeholder='Password'
                        />
                    </div>
                    <div className='button-section'>
                        <button type='submit' className='button'>
                            Login
                        </button>
                        <p className="forgot-password"><a href='/forgot-password'>Forgot Password?</a></p>
                    </div>
                    <div className='text-center'>
                    Don't have an account?{' '}
                    <a href="/signup" className="text-blue-500 hover:underline">
                        Sign up
                    </a>
               
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;