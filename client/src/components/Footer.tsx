import { GoPaperAirplane } from 'react-icons/go';
import '../styles/footer.css';

const Footer: React.FC = () => {
  return (
    <footer className="footer">
        <div className="footer-container">

            <div className="footer-section">
                <h2 className="footer-title">ShopSphere</h2>
                <p className="footer-subtitle">Subscribe</p>
                <p>Get 10% off your first order.</p>
                <div className="subscribe-container">
                    <input
                        type="email"
                        placeholder="Enter your email"
                        className="subscribe-input"
                    />
                    <GoPaperAirplane className='send-icon' />
                </div>
            </div>

            <div className="footer-section">
                <h2 className="footer-title">Support</h2>
                <p>123, Main Street, Abuja Nigeria</p>
                <p>support@example.com</p>
                <p>+123 456 7890</p>
            </div>

            <div className="footer-section">
                <h2 className="footer-title">Account</h2>
                <p className='icon'>Login/Register</p>
                <p className='icon'>Cart</p>
                <p className='icon'>Wishlist</p>
                <p className='icon'>Shop</p>
            </div>

            <div className="footer-section">
                <h3 className="footer-title">Quick Links</h3>
                <p className='icon'>Privacy Policy</p>
                <p className='icon'>Terms of Use</p>
                <p className='icon'>FAQ</p>
                <p className='icon'>Contact</p>
            </div>

            <div className="footer-section">
                <div>
                    <h3 className="footer-title">Download App</h3>
                    <img src="qr-code.png" alt="QR Code" className="qr-code" />
                </div>
                <div className="social-icons">
                    <i className="fab fa-facebook"></i>
                    <i className="fab fa-twitter"></i>
                    <i className="fab fa-instagram"></i>
                    <i className="fab fa-linkedin"></i>
                </div>
            </div>
            </div>

      <div className="footer-bottom">
        <p>&copy; Copyright Shopsphere 2024. All rights reserved.</p>
      </div>
    </footer>
  );
};

export default Footer;
