
import { useState } from "react";
import Footer from "./components/Footer";
import Navbar from "./components/Navbar";
import Home from "./components/Home";
import MyCourses from "./components/MyCourses"
import MyStudentCourses from "./components/MyStudentCourses"
import Payment from "./components/Payment";
import AddCourse from "./components/AddCourse";
import AddCourseContent from "./components/AddCourseContent";
import AcceptCourseContent from "./components/AcceptCourseContent";
import CourseContent from "./components/CourseContent";
import CourseContentPreview from "./components/CourseContentPreview";
import PaymentHistory from "./components/PaymentHistory";
import AdminHome from "./components/AdminHome";
import Login, { logout } from "./components/Login";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';



function App() {
  // const [loggedIn, setLoggedIn] = useState(false);
  const [loggedIn, setLoggedIn] = useState(() => {
    const isLoggedIn = localStorage.getItem('jwtToken') !== null;
    console.log("isLoggedIn:", isLoggedIn);
        return isLoggedIn;
  });
  
  const handleLogout = async () => {
    try {
      // Call the logout function directly
      await logout(setLoggedIn);
      setLoggedIn(false); // Update loggedIn state to false
    } catch (error) {
      console.error("Error logging out:", error);
      // Handle any errors that occur during logout
    }
  };

  return (
    <Router>
      <div className="App">
        {loggedIn ? (
          <>
            <Navbar handleLogout={handleLogout}/>
            <div className="in-content">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/adminHome" element={<AdminHome />} />
              <Route path="/myCourses" element={<MyCourses />} />
              <Route path="/myStudentCourses" element={<MyStudentCourses />} />
              <Route path="/payment" element={<Payment />} />
              <Route path="/addCourse" element={<AddCourse  />} />
              <Route path="/addCourseContent" element={<AddCourseContent  />} />
              <Route path="/acceptCourseContent" element={<AcceptCourseContent  />} />
              <Route path="/courseContent" element={<CourseContent  />} />
              <Route path="/courseContentPreview" element={<CourseContentPreview  />} />
              <Route path="/paymentHistory" element={<PaymentHistory  />} />

            </Routes>
            </div>
            <Footer />
          </>
        ) : (
          <Login setLoggedIn={setLoggedIn} handleLogout={handleLogout}/>
        )}
      </div>
    </Router>
  );
}

export default App;
