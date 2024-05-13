import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FaPlus } from 'react-icons/fa';
import { Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

export default function Home(props) {
  const [courses, setCourses] = useState([]);
  const enrolled = true;
  const [enrolledCourses, setEnrolledCourses] = useState([]);
  const role = localStorage.getItem('userRole');
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchCourses() {
      const token = localStorage.getItem('jwtToken');
      try {
        let response;
        if (role === '[ROLE_STUDENT]') {
          response = await fetch('http://localhost:8085/enrollment/myenrollment', {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`
            }
          });
          if (response.ok) {
            const dataArray = await response.json();
            const courseIds = dataArray.map(enrollment => enrollment.courseId);
            setEnrolledCourses(courseIds);
          } else {
            console.error('Failed to fetch enrolled courses');
          }

          response = await fetch('http://localhost:8082/api/course/approve/Approved');
          if (!response.ok) {
            throw new Error('Failed to fetch courses');
          }
          const data = await response.json();
          setCourses(data);
        } else {
          response = await fetch('http://localhost:8082/api/course/approve/Approved');
          if (!response.ok) {
            throw new Error('Failed to fetch courses');
          }
          const data = await response.json();
          setCourses(data);
        }


      } catch (error) {
        console.error('Error fetching courses:', error.message);
      }
    }

    fetchCourses();
  }, []);


  useEffect(() => {
    console.log('Enrolled Courses:', enrolledCourses);
  }, [enrolledCourses]);

  const adminHandle = async (enrollmentId, weekCount) => {
    navigate('/CourseContent', { state: { enrollmentId, weekCount } });
  };
  
  const facultyHandle = async (enrollmentId, weekCount) => {
    navigate('/CourseContentPreview', { state: { enrollmentId, weekCount } });
  };

  const studentHandle = async (enrollmentId, weekCount) => {
    navigate('/CourseContent', { state: { enrollmentId, weekCount } });
  };

  const studentNotEnrolledHandle = async (enrollmentId, weekCount) => {
    navigate('/CourseContentPreview', { state: { enrollmentId, weekCount } });
  };

  return (
    <section id="home-user">
      <div className="row">
        {courses.map((course) => {
          const isEnrolled = enrolledCourses.includes(course.courseId);
          return (
            <div key={course.courseId} className="col-md-4 mb-3">
              <Card>
                <Card.Body>
                  <Card.Title>{course.courseName}</Card.Title>
                  <Card.Text>{course.description}</Card.Text>
                  <Card.Text>Week Count: {course.weekCount}</Card.Text>
                  {/* <Card.Text>Id: {course.courseId}</Card.Text> */}
                </Card.Body>
                <Card.Footer className="d-flex justify-content-between">
                  <small className="text-muted">Fee: ${course.courseAmount}</small>

                  {role === '[ROLE_STUDENT]' && (
                    <div id="enrolled">
                      {isEnrolled ? (
                        <>
                        <span className="text-success">Enrolled</span>
                        <Button variant="primary" onClick={() => studentHandle(course.courseId, course.weekCount)}>View</Button>
                        </>
                      ) : (
                        <>
                        <span className="text-danger">Not Enrolled</span>
                        <Button variant="primary" onClick={() => studentNotEnrolledHandle(course.courseId, course.weekCount)}>View</Button>
                        </>
                      )}
                    </div>
                  )}

                  {role === '[ROLE_ADMIN]' && (
                    <div id="enrolled">
                      <Button variant="primary" onClick={() => adminHandle(course.courseId, course.weekCount)}>View</Button>
                    </div>
                  )}

                  {role === '[ROLE_FACULTY]' && (
                    <div id="enrolled">
                      <Button variant="primary" onClick={() => facultyHandle(course.courseId, course.weekCount)}>View</Button>
                    </div>
                  )}


                </Card.Footer>
              </Card>
            </div>
          );
        })}
      </div>
      {role === '[ROLE_FACULTY]' && (
      <div style={{ position: 'fixed', bottom: '20px', right: '20px', zIndex: '999' }}>
        <Link to="/addCourse" id="plus-button" className="btn btn-primary d-flex align-items-center justify-content-center rounded-circle p-3">
          <FaPlus size={20} />
          <span className="ms-2">Course</span>
        </Link>
      </div>
        )}
    </section>
  );
}

{/* <header>
<div
  className='bg-image align-items-center' 
>
  <div className='mask'> 
    <div className='container'>
      <div className='row align-items-center'>
        <div id="home-left" className='col-md-6'  >
          <div>
            <h6 className='mb-1'>NASA API</h6>
            <h1 className='mb-3'>AstroDiscover</h1>
            <h4 className='mb-3'>Free and open source project, built with REST APIs from api.nasa.gov</h4>
            <Link to="/about" className="btn btn-outline-light btn-lg">
              Go to About
            </Link>

          </div>
        </div>
        <div className='col-md-6 text-center'>
          <img src='https://upload.wikimedia.org/wikipedia/commons/7/7f/Rotating_earth_animated_transparent.gif' alt='Rotating Earth' className='img-fluid' />
        </div>
      </div>
    </div>
  </div>
</div>
<div style={{ position: 'fixed', bottom: '20px', right: '20px', zIndex: '999' }}>
  <Link to="/addCourse" id="plus-button" className="btn btn-primary btn-floating rounded-circle p-3">
    <FaPlus size={30} />
  </Link>
</div>
</header> */}