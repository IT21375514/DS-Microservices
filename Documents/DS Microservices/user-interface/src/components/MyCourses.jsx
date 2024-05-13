import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FaPlus } from 'react-icons/fa';
import { Card, Button, Tab, Tabs } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

export default function MyCourses(props) {
  const [courses, setCourses] = useState([]);
  const [activeTab, setActiveTab] = useState('Approved'); // State to manage active tab
  const navigate = useNavigate();
  const token = localStorage.getItem('jwtToken');
  const [userRole, setUserRole] = useState(null);

  useEffect(() => {
    // Fetch data from the API endpoint
    async function fetchData() {
      try {
        const response = await fetch('http://localhost:8082/api/course/approve/instructorId', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        setCourses(data);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    }

    fetchData(); // Call the fetchData function when the component mounts
  }, []);


  // Filter courses based on active tab
  const filteredCourses = courses.filter(course => course.approve === activeTab);

  // Define button click functions
  const handleViewClick = async (enrollmentId, weekCount) => {
    // Logic for handling view click
    console.log(`View clicked for course ${enrollmentId}`);
    navigate('/addCourseContent', { state: { enrollmentId, weekCount } });
  };

  const handlePendingClick = async (enrollmentId, weekCount) => {
    // Logic for handling pending click
    console.log(`Pending clicked for course ${enrollmentId}`);
    navigate('/addCourseContent', { state: { enrollmentId, weekCount } });
  };

  const handleRejectedClick = async (enrollmentId, weekCount) => {
    // Logic for handling rejected click
    console.log(`Rejected clicked for course ${enrollmentId}`);
    navigate('/courseContent', { state: { enrollmentId, weekCount } });
  };


  return (
    <section id="my-user-courses">
      <Tabs activeKey={activeTab} onSelect={(tab) => setActiveTab(tab)} className="mb-3">
        <Tab eventKey="Approved" title="Approved" />
        <Tab eventKey="Pending" title="Pending" />
        <Tab eventKey="Rejected" title="Rejected" />
      </Tabs>
      <div className="row">
        {filteredCourses.map((course, index) => (
          <div key={index} className="col-md-4 mb-3">
            <Card>
              <Card.Body>
                <Card.Title>{course.courseName}</Card.Title>
                <Card.Text>{course.description}</Card.Text>
                <Card.Text>Week Count: {course.weekCount}</Card.Text>
              </Card.Body>
              <Card.Footer className="d-flex justify-content-between">
                <small className="text-muted">Fee: {course.courseAmount}</small>
                {/* Conditional rendering of buttons */}
                {activeTab === 'Pending' ? (
                  <Button variant="primary" onClick={() => handlePendingClick(course.courseId, course.weekCount)}>View</Button>
                ) : activeTab === 'Rejected' ? (
                  <Button variant="primary" onClick={() => handleRejectedClick(course.courseId, course.weekCount)}>View</Button>
                ) : (
                  <Button variant="primary" onClick={() => handleViewClick(course.courseId, course.weekCount)}>View</Button>
                )}
              </Card.Footer>
            </Card>
          </div>
        ))}
      </div>
      <div style={{ position: 'fixed', bottom: '20px', right: '20px', zIndex: '999' }}>
        <Link to="/addCourse" id="plus-button" className="btn btn-primary d-flex align-items-center justify-content-center rounded-circle p-3">
          <FaPlus size={20} />
          <span className="ms-2">Course</span>
        </Link>
      </div>
    </section>
  );
}
