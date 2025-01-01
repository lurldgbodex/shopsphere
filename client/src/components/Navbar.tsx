import { useState } from "react";
import { MdOutlineShoppingCart } from "react-icons/md";
import { GrFavorite } from "react-icons/gr";
import { FiUser } from "react-icons/fi";
import { Link, useLocation } from "react-router-dom";
import { CiSearch } from "react-icons/ci";
import styles from '../styles/navbar.module.css';
import { FaBars } from "react-icons/fa";
import useStore from "../redux/store";
import HeaderBanner from "./HeaderBanner";

const Navbar = () => {
    const isSignedIn = useStore((state) => state.isSignedIn);
    const cartCount = useStore((state) => state.cartCount);
    const toggleSignIn = useStore((state) => state.toggleSignIn)
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);
    const [searchOpen, setSearchOpen] = useState(false);
    const location = useLocation();

    return (
        <>
        <HeaderBanner />
        <nav className={styles.navbar}>
            <div className={styles.navbar_container}>
                <div className={styles.navbar_logo}>ShopSphere</div>
                
                <div className={styles.navbar_links}>
                    {['Home', 'Contact', 'About'].map((item, index) => (
                        <Link
                            key={index}
                            to={`/${item.toLowerCase().replace(' ', '')}`}
                            className={`${styles.navbar_link} ${
                                location.pathname === `/${item.toLowerCase().replace(' ', '')}`
                                  ? styles.navbar_link_active
                                  : ''
                            }`}
                        >
                            {item}
                        </Link>
                    ))}
                    {!isSignedIn && (
                        <Link to="/signup" 
                        className={`${styles.navbar_link} ${
                            location.pathname === '/signup'
                              ? styles.navbar_link_active
                              : ''
                          }`}
                    >
                            Sign Up
                    </Link>
                    )}
                </div>

                <div className="flex items-center space-x-4">
                    <div className="relative">
                        <input 
                            type="text"
                            placeholder="What are you looking for?"
                            className={styles.search_input}
                        />
                        <CiSearch 
                            className={styles.search_icon} 
                            onClick={() => setSearchOpen(!searchOpen)}
                        />
                    </div>

                    {isSignedIn && (
                        <>
                            <GrFavorite className={styles.icon} />
                            <div className="relative">
                                <MdOutlineShoppingCart className={styles.icon} />
                                {cartCount > 0 && (
                                    <span className={styles.cart_badge}>                                        
                                        {cartCount}
                                    </span>
                                )}
                            </div>
                            <div className="relative">
                                <div 
                                    className={styles.account_circle}
                                    onClick={() => setDropdownOpen(!dropdownOpen)}
                                >
                                    <FiUser 
                                        className="text-white text-md" />
                                </div>
                                
                                {dropdownOpen && (
                                    <div className={styles.dropdown_menu}>
                                        <Link
                                            to="/account/manage"
                                            className={styles.dropdown_item}
                                        >
                                            Manage Account
                                        </Link>
                                        <Link
                                            to="/account/orders"
                                            className={styles.dropdown_item}
                                        >
                                            Orders
                                        </Link>
                                        <Link
                                            to="/account/cancellations"
                                            className={styles.dropdown_item}
                                        >
                                            Cancellations
                                        </Link>
                                        <button
                                            onClick={() => toggleSignIn}
                                            className={styles.dropdown_logout}
                                        >
                                            Logout
                                        </button>
                                    </div>
                                )}
                            </div>
                        </>
                    )}

                    {/* Hamburger menu (mobile) */}
                    <div className="md:hidden">
                        <FaBars
                            onClick={() => setMenuOpen(!menuOpen)}
                        />
                    </div>
                </div>
            </div>
            
            {/* Mobile Navigation links */}
            {menuOpen && (
                <div className="md:hidden bg-white shadow-lg">
                    <div className={styles.dropdown_menu}>
                        {['Home', 'Contact', 'About'].map((item, index) => (
                            <Link
                                key={index}
                                to={`/${item.toLowerCase().replace(' ', '')}`}
                                className={styles.dropdown_item}
                            >
                                {item}
                            </Link>
                        ))}
                        {!isSignedIn && (
                            <Link
                                to="/signup"
                                className={styles.dropdown_item}
                            >
                                Sign Up
                            </Link>
                        )}
                        {isSignedIn && (
                            <div className="sm:hidden">
                                <Link
                                    to="/account/manage"
                                    className={styles.dropdown_item}
                                >
                                    Manage Account
                                </Link>
                                <Link
                                    to="/account/orders"
                                    className={styles.dropdown_item}
                                >
                                    Orders
                                </Link>
                                <Link
                                    to="/account/cancellations"
                                    className={styles.dropdown_item}
                                >
                                    Cancellations
                                </Link>
                                <button
                                    onClick={() => toggleSignIn}
                                    className={styles.dropdown_logout}
                                >
                                    Logout
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            )}

        </nav>
        </>
    )
}

export default Navbar;