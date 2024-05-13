import React from 'react';

function Footer() {
  return (
    <footer id="my-footer" className="text-light py-4" >
        <div className="row">
          <div id="my-footer-first" className="col-md-6">
            <img src='img/logo-white.png' alt="Logo"/>
            <p>
            EduMatrix is an advanced online learning platform built on a microservices architecture, featuring Spring Boot backend projects and a React frontend. It offers a user-friendly experience for accessing a diverse array of courses.            </p>
          </div>
          <div className="col-md-4">
            {/* Second column content */}
          </div>
          <div id="my-footer-third" className="col-md-2">
            <h6><strong>Technologies</strong></h6>
          {/* First column content */}
            <div className="d-flex align-items-center">
              <img src="img/react.png" alt="Image 1"/>
              <span>React</span>
            </div>
            <div className="d-flex align-items-center">
              <img src="img/bootstrap.png" alt="Image 2"/>
              <span>Bootstrap</span>
            </div>
          </div>

        </div>

    </footer>
  );
}

export default Footer;
