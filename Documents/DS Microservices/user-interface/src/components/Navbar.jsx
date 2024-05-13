// import { useEffect, useState } from 'react';
// import { Navbar, Nav, Container } from 'react-bootstrap';
// import Button from 'react-bootstrap/Button';
// import Offcanvas from 'react-bootstrap/Offcanvas';

// function Navigation({ activeSection,  handleLogout }) {
//   const [showAccount, setShowAccount] = useState(false);
//   const [accountActive, setAccountActive] = useState(false); 
//   const [userData, setUserData] = useState(null); // State to store user data

//   const handleAccountClick = () => {
//     setShowAccount(true);
//     setAccountActive(true); // Set account icon active when clicked
//   };

//   const handleClose = () => {
//     setShowAccount(false);
//     setAccountActive(false); // Reset account icon active when closed
//   };

//     return (
//     <>
//       <Navbar id="head-nav-bar" variant="light" fixed="top" expand="lg" className="bg-body-tertiary">
//         <Container fluid>
//           <Navbar.Brand href="/">
//             <img src="img/logo-white.png" alt="Logo" />
//           </Navbar.Brand>
//           <Navbar.Toggle aria-controls="navbarScroll" />
//           <Navbar.Collapse id="navbarScroll">
//           <Nav className="my-2 my-lg-0 sub-nav" navbarScroll>
//               <Nav.Link href="/" className={activeSection === 'home' ? 'active' : ''}>Home</Nav.Link>
//               <Nav.Link href="/adminHome" className={activeSection === 'adminHome' ? 'active' : ''}>Admin Home</Nav.Link>
//               <Nav.Link href="/payment" className={activeSection === 'payment' ? 'active' : ''}>Payment</Nav.Link>
//               <Nav.Link href="#" onClick={handleAccountClick}>
//                 <img src={accountActive ? "img/user_active.png" : "img/user.png"} alt="Account"/>
//               </Nav.Link>
//             </Nav>
//           </Navbar.Collapse>
//         </Container>
//       </Navbar>

//       <Offcanvas id="account-offcanvas" show={showAccount} onHide={handleClose} placement="end">
//         <Offcanvas.Header closeButton>
//           <Offcanvas.Title className='mb-5'>Account</Offcanvas.Title>
//         </Offcanvas.Header>
//         <Offcanvas.Body>
//           <div className="text-center">
//             <img src="img/user.png" roundedCircle/>
//             {userData && (
//             <>
//             <h5 className="mt-3">{userData.name}</h5>
//             <p>{userData.email}</p>
//             </>
//             )}
//             </div>
//             </Offcanvas.Body>

//             <Button className="btn btn-outline-light btn-lg btn-logout" type="button"  onClick={handleLogout}>
//             Log Out
//             </Button>
//       </Offcanvas>
//     </>
//   );


// }


// export default Navigation;
import { useEffect, useState } from 'react';
import { Navbar, Nav, Container } from 'react-bootstrap';
import { Link, useLocation } from 'react-router-dom'; // Import useLocation
import Button from 'react-bootstrap/Button';
import Offcanvas from 'react-bootstrap/Offcanvas';


function Navigation({ handleLogout }) {
  const [showAccount, setShowAccount] = useState(false);
  const [accountActive, setAccountActive] = useState(false);
  const [userData, setUserData] = useState(null); // State to store user data
  const [activeSection, setActiveSection] = useState(null); // State for active section
  const location = useLocation(); // Get current location
  const [userRole, setUserRole] = useState(null); // State to store user role

  // Update activeSection based on current route
  useEffect(() => {
    const pathname = location.pathname;
    if (pathname === '/') {
      setActiveSection('home');
    } else if (pathname === '/adminHome') {
      setActiveSection('adminHome');
    } else if (pathname === '/paymentHistory') {
      setActiveSection('paymentHistory');
    } else if (pathname === '/adminHome') {
      setActiveSection('adminHome');
    } else if (pathname === '/myCourses') {
      setActiveSection('myCourses');
    } else if (pathname === '/myStudentCourses') {
      setActiveSection('myCourses');
    } else {
      setActiveSection(null);
    }
  }, [location.pathname]);

  useEffect(() => {
    const role = localStorage.getItem('userRole');
    setUserRole(role);
  }, []);

  const handleAccountClick = () => {
    setShowAccount(true);
    setAccountActive(true); // Set account icon active when clicked
  };

  const handleClose = () => {
    setShowAccount(false);
    setAccountActive(false); // Reset account icon active when closed
  };

  return (
    <>
      <Navbar id="head-nav-bar" variant="light" fixed="top" expand="lg" className="bg-body-tertiary">
        <Container fluid>
          <Navbar.Brand as={Link} to="/">
            <img src="img/logo-white.png" alt="Logo" />
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="navbarScroll" />
          <Navbar.Collapse id="navbarScroll">
            <Nav className="my-2 my-lg-0 sub-nav" navbarScroll>
              <Nav.Link as={Link} to="/" className={activeSection === 'home' ? 'active' : ''}>Home</Nav.Link>
              {userRole === "[ROLE_ADMIN]" && (
                <>
                <Nav.Link as={Link} to="/adminHome" className={activeSection === 'adminHome' ? 'active' : ''}>Admin Home</Nav.Link>
                <Nav.Link as={Link} to="/paymentHistory" className={activeSection === 'paymentHistory' ? 'active' : ''}>Transactions</Nav.Link>
                </>
              )}
              {userRole === "[ROLE_FACULTY]" && (
                <Nav.Link as={Link} to="/myCourses" className={activeSection === 'myCourses' ? 'active' : ''}>My Courses</Nav.Link>
              )}
              {userRole === "[ROLE_STUDENT]" && (
                <Nav.Link as={Link} to="/myStudentCourses" className={activeSection === 'myCourses' ? 'active' : ''}>My Courses</Nav.Link>
              )}
              {/* <Nav.Link as={Link} to="/payment" className={activeSection === 'payment' ? 'active' : ''}>Payment</Nav.Link> */}
              <Nav.Link href="#" onClick={handleAccountClick}>
                <img src={accountActive ? "img/user-active-op.png" : "img/user.png"} alt="Account" />
              </Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <Offcanvas id="account-offcanvas" show={showAccount} onHide={handleClose} placement="end">
        <Offcanvas.Header closeButton>
          <Offcanvas.Title className='mb-5'>Account</Offcanvas.Title>
        </Offcanvas.Header>
        <Offcanvas.Body>
          <div className="text-center">
            <img src="img/user.png" roundedCircle />
            {userData && (
              <>
                <h5 className="mt-3">{userData.name}</h5>
                <p>{userData.email}</p>
              </>
            )}
          </div>
        </Offcanvas.Body>
        <Button className="btn btn-outline-light btn-lg btn-logout" type="button" onClick={handleLogout}>
          Log Out
        </Button>
      </Offcanvas>
    </>
  );
}

export default Navigation;
