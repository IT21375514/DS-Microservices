import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FaPlus } from 'react-icons/fa';
import { Card, Button, Tab, Tabs } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

export default function MyCourses(props) {
  const [courses, setCourses] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem('jwtToken');
  const [enrolledCourses, setEnrolledCourses] = useState([]);
  const [userRole, setUserRole] = useState(null);

  useEffect(() => {
    // Fetch data from the API endpoint
    async function fetchData() {
      const token = localStorage.getItem('jwtToken');
      try {
        let response;
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
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    }

    fetchData(); // Call the fetchData function when the component mounts
  }, []);


  // Define button click functions
  const handleViewClick = async (enrollmentId, weekCount) => {
    // Logic for handling view click
    console.log(`View clicked for course ${enrollmentId}`);
    navigate('/CourseContent', { state: { enrollmentId, weekCount } });
  };

  const filteredEnrolledCourses = courses.filter(course => enrolledCourses.includes(course.courseId));

  return (
    <section id="my-user-courses">
     
     <div className="row">
        {filteredEnrolledCourses.map((course) => {
          return (
            <div key={course.courseId} className="col-md-4 mb-3">
              <Card>
                <Card.Body>
                  <Card.Title>{course.courseName}</Card.Title>
                  <Card.Text>{course.description}</Card.Text>
                  <Card.Text>Week Count: {course.weekCount}</Card.Text>
                </Card.Body>
                <Card.Footer className="d-flex justify-content-between">
                  <small className="text-muted">Fee: {course.courseAmount}</small>
                  <Button variant="primary" onClick={() => handleViewClick(course.courseId, course.weekCount)}>View</Button>
                </Card.Footer>
              </Card>
            </div>
          );
        })}
      </div>
    </section>
  );
}
