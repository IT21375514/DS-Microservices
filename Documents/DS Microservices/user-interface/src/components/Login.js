import React, { useState, useEffect } from 'react';
import { Button, Container, Form, Modal, Spinner } from "react-bootstrap"; // Import Modal component
import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';

// Define the logout function outside of the Login component
export const logout = async (setLoggedIn) => {
  try {
    localStorage.removeItem('loggedIn');
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userRole');
    await logout(setLoggedIn);
    console.log("Removed");
    setLoggedIn(false);
  } catch (error) {
    console.error('Error logging out:', error);
  }
};

function Login({ setLoggedIn }) {
  const [loginUsername, setLoginUsername] = useState("");
  const [signupUsername, setSignupUsername] = useState("");
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [name, setName] = useState("");
  const [role, setRole] = useState("");
  const [signupEmail, setSignupEmail] = useState("");
  const [signupContact, setSignupContact] = useState("");
  const [signupPassword, setSignupPassword] = useState("");
  const [showSuccessModal, setShowSuccessModal] = useState(false); // State for showing success modal
  const [successMessage, setSuccessMessage] = useState(""); // State for success message
  const [showErrorModal, setShowErrorModal] = useState(false); // State for showing failure modal
  const [errorMessage, setErrorMessage] = useState(""); // State for failure message
  const [loading, setLoading] = useState(false); // State for loading spinner
  // const [activeTab, setActiveTab] = useState("home"); 

  const handleLogin = async () => {
    if (!loginEmail || !loginPassword) {
      if (!loginEmail && !loginPassword) {
        setErrorMessage("Please enter username and password.");
      } else if (!loginEmail) {
        setErrorMessage("Please enter your email.");
      } else {
        setErrorMessage("Please enter your password.");
      }
      setShowErrorModal(true); 
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/tms/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          username: loginEmail,
          password: loginPassword
        })
      });

      if (!response.ok) {
        throw new Error('Failed to sign in');
      }

      const responseData = await response.text(); // Get the token directly from the response text
      console.log("Sign in successful. Token:", responseData);

      // Extract JWT token and role from the response
      const tokenStartIndex = responseData.indexOf('Token:') + 'Token:'.length;
      const tokenEndIndex = responseData.indexOf('Role:');
      const token = responseData.substring(tokenStartIndex, tokenEndIndex).trim();

      const roleStartIndex = responseData.indexOf('Role:') + 'Role:'.length;
      const role = responseData.substring(roleStartIndex).trim();

      console.log("JWT Token:", token);
      console.log("Role:", role);
      localStorage.setItem('jwtToken', token);
      localStorage.setItem('userRole', role);

      setLoggedIn(true);
      localStorage.setItem('loggedIn', 'true');
    } catch (error) {
      console.error('Login error:', error.message);
      setErrorMessage('Failed to sign in. Please try again.');
    }


  };



  const handleSignup = async () => {
    if (!name || !signupEmail || !signupPassword || !signupContact || !role) {
      setErrorMessage("Please enter username, email, contact no, password, and role.");
      setShowErrorModal(true);
      return;
    }
  
    try {
      let sendContact = '+940'+signupContact;
      console.log("name:", name);
      console.log("signupEmail:", signupEmail);
      console.log("signupPassword:", signupPassword);
      console.log("signupContact:", signupContact);
      console.log("role:", role);
      console.log("sendContact:", sendContact);
      const response = await fetch('http://localhost:8080/tms/auth/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          username: name,
          email: signupEmail,
          roles: [role],
          password: signupPassword,
          phoneNo: sendContact
        })
      });
  
      if (!response.ok) {
        throw new Error('Failed to sign up');
      }
  
      setShowSuccessModal(true);
      setSuccessMessage("Sign up successful. Please wait..");
      setTimeout(() => {
        window.location.reload();
      }, 3000);
    } catch (error) {
      console.error('Signup error:', error.message);
      setErrorMessage('Failed to sign up. Please try again.');
      setShowErrorModal(true);
    }
  };
  

  const handleChange = (e) => {
    const inputValue = e.target.value;
    const regex = /^[0-9]{0,9}$/; // Regular expression to allow only numeric characters and maximum 9 digits

    // Validate input against the regex pattern
    if (regex.test(inputValue)) {
      // If input is valid, update the state
      setSignupContact(inputValue);
    }
  };

  return (
    <Container id="login" className="d-flex align-items-center justify-content-center">
      <div id="login-div">
        <div className="text-center mb-1">
          <img src='img/logo-black-logo.png' alt="Logo" height="80" />
        </div>
        <div className="ImgContainer mb-5">
          <img src='img/logo-black-text.png' alt="mar-demo-pic" className="bgImage" />
        </div>
        <Tabs
          defaultActiveKey="home"
          id="uncontrolled-tab-example"
          className="mb-3"
        // id="uncontrolled-tab-example"
        // className="mb-3"
        // activeKey={activeTab}
        // onSelect={(key) => setActiveTab(key)}
        >
          <Tab eventKey="home" title="Login">
            <Form>
              <Form.Group className="mb-3" controlId="formBasicUsername">
                <Form.Label>Username</Form.Label>
                <Form.Control type="text" placeholder="Username" value={loginEmail} onChange={(e) => setLoginEmail(e.target.value)} />
              </Form.Group>

              <Form.Group className="mb-3" controlId="formBasicPassword">
                <Form.Label>Password</Form.Label>
                <Form.Control type="password" placeholder="Password" value={loginPassword} onChange={(e) => setLoginPassword(e.target.value)} />
              </Form.Group>
              <div className="mt-5 col-md-12 d-flex justify-content-center">
                <Button className="btn btn-outline-light btn-lg" type="button" onClick={handleLogin}>
                  Login
                </Button>
              </div>
            </Form>
          </Tab>

          <Tab eventKey="profile" title="Signup">
            <Form>
              <Form.Group className="mb-3" controlId="formBasicSigupUsername">
                <Form.Label>Username</Form.Label>
                <Form.Control type="text" placeholder="Enter Username" value={name} onChange={(e) => setName(e.target.value)} />
              </Form.Group>
              <Form.Group className="mb-3" controlId="formBasicSigupEmail">
                <Form.Label>Email</Form.Label>
                <Form.Control type="text" placeholder="Enter Email" value={signupEmail} onChange={(e) => setSignupEmail(e.target.value)} />
              </Form.Group>
              <Form.Group className="mb-3" controlId="formBasicSigupPassword">
                <Form.Label>Password</Form.Label>
                <Form.Control type="password" placeholder="Password (Minimum 6 characters)" value={signupPassword} onChange={(e) => setSignupPassword(e.target.value)} />
              </Form.Group>
              <Form.Group className="mb-4" controlId="formBasicSigupContactNo">
                <Form.Label>Contact No</Form.Label>
                <Form.Control
                  type="tel"
                  pattern="[0-9]{9}"
                  maxLength="9"
                  placeholder="Contact No +94 XX XXX XXXX"
                  value={signupContact}
                  onChange={handleChange}
                  title="Please enter 9 numbers"
                />
              </Form.Group>

              <Form.Group className="mb-3 d-flex align-items-center" controlId="formBasicSignupRole">
                <Form.Label className="me-3 mb-0">Role :</Form.Label>
                <div className="d-flex align-items-center">
                  <Form.Check
                    inline
                    type="radio"
                    id="studentRole"
                    label="Learner"
                    value="ROLE_STUDENT"
                    checked={role === "ROLE_STUDENT"}
                    onChange={() => setRole("ROLE_STUDENT")}
                    className="me-3 mb-0"
                  />
                  <Form.Check
                    inline
                    type="radio"
                    id="teacherRole"
                    label="Instructor"
                    value="ROLE_FACULTY"
                    checked={role === "ROLE_FACULTY"}
                    onChange={() => setRole("ROLE_FACULTY")}
                    className="me-3 mb-0"
                  />
                  <Form.Check
                    inline
                    type="radio"
                    id="adminRole"
                    label="Admin"
                    value="ROLE_ADMIN"
                    checked={role === "ROLE_ADMIN"}
                    onChange={() => setRole("ROLE_ADMIN")}
                    className="mb-0"
                  />
                </div>
              </Form.Group>



              <div className="mt-5 col-md-12 d-flex justify-content-center">
                <Button className="btn btn-outline-light btn-lg" type="button" onClick={handleSignup}>
                  Signup
                </Button>
              </div>
            </Form>
          </Tab>
        </Tabs>
      </div>
      {/* Success Modal */}
      <Modal show={showSuccessModal} onHide={() => setShowSuccessModal(false)}>
        <Modal.Body>
          {loading ? ( // Conditionally render spinner if loading
            <div className="d-flex justify-content-center">
              <Spinner animation="border" role="status">
                <span className="visually-hidden">Loading...</span>
              </Spinner>
            </div>
          ) : (
            <>{successMessage}</>
          )}
        </Modal.Body>
      </Modal>

      {/* Failure Modal */}
      <Modal show={showErrorModal} onHide={() => setShowErrorModal(false)}>
        <Modal.Body>{errorMessage}</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowErrorModal(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}

export default Login;
