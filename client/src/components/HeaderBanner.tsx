import { RiArrowDropDownLine } from "react-icons/ri";
import '../styles/headerbanner.css';

const HeaderBanner = () => {
    return (
        <div className='banner-container'>
            <div></div>
            <div className="content" >
                <span>Summer Sale For All Swim Suits And Free Express Delivery - OFF 50%!</span>
                <span className="text-white font-bold p-4 cursor-pointer underline decoration-slate-400">ShopNow</span>
            </div>
            <div className="content language">
                <span>English</span>
                <RiArrowDropDownLine className="text-xl text-white" />
            </div>
        </div>
    )
}

export default HeaderBanner;