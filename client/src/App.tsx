import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Home from './pages/Home';
import Navbar from "./components/Navbar";
import Signup from "./pages/SignUp";
import Login from "./pages/Login";
import Footer from "./components/Footer";

function App() {

  return (
    <Router>
      <Navbar />
     <Routes>
      <Route path="/home" element={<Home />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/login" element={<Login />} />
     </Routes>
     <Footer />
    </Router>
  )
}

export default App
