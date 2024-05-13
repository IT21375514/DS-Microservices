import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Button } from 'react-bootstrap';

const AddCourseForm = () => {
    const [courseName, setCourseName] = useState('');
    const [courseId, setCourseId] = useState('');
    const [enrollmentId, setRetreivedCourseId] = useState('');
    const [enrollId, setEnrollmentId] = useState('');
    const [fee, setFee] = useState('');
    const [weekCount, setWeekCount] = useState('');
    const [courseDescription, setCourseDescription] = useState('');
    const navigate = useNavigate();

    const handleSubmit1 = async () => {
        if (!courseName || !enrollId || !fee || !weekCount || !courseDescription) {
            alert('Please fill in all fields');
            return;
        }
    
        const token = localStorage.getItem('jwtToken');
    
        try {
            const response = await fetch('http://localhost:8082/api/course', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` // Set JWT token in the Authorization header
                },
                body: JSON.stringify({
                    courseName: courseName,
                    weekCount: parseInt(weekCount), // Ensure weekCount is parsed as an integer
                    description: courseDescription,
                    courseAmount: parseInt(fee), // Ensure fee is parsed as an integer
                    courseEnrollKey: enrollId // Assuming courseEnrollKey needs to be set manually
                })
            });
    
            if (response.ok) {
                const data = await response.json();
                setRetreivedCourseId(data.courseId); // Access courseId from the response data
                console.log('Course created successfully with ID:', data.courseId);
                console.log('EnrollmentId:', enrollmentId);
                navigate('/addCourseContent', { state: { enrollmentId: data.courseId, weekCount } });
            } else {
                // Handle error response
                console.error('Error creating course:', response.statusText);
                // Show an error message to the user or handle it accordingly
            }
            
        } catch (error) {
            console.error('Error creating course:', error);
            // Handle network errors or other exceptions
        }
        
    };
    


    return (
        <div className="container">
            <h1 className="text-center mb-5">Add Course</h1>
            <Form>
                <div className="row mb-3">
                    <div className="col-md-3">
                        <Form.Label>Course Name</Form.Label>
                    </div>
                    <div className="col-md-9">
                        <Form.Control type="text" placeholder="Enter Course Name" value={courseName} onChange={(e) => setCourseName(e.target.value)} />
                    </div>
                </div>
                <div className="row mb-3">
                    <div className="col-md-3">
                        <Form.Label>Enrollment ID</Form.Label>
                    </div>
                    <div className="col-md-9">
                        <Form.Control type="text" placeholder="Enter Enrollment ID" value={enrollId} onChange={(e) => setEnrollmentId(e.target.value)} />
                    </div>
                </div>
                <div className="row mb-3">
                    <div className="col-md-3">
                        <Form.Label>Course Amount</Form.Label>
                    </div>
                    <div className="col-md-9">
                        <Form.Control type="number" placeholder="Enter Course Amount" value={fee} onChange={(e) => setFee(e.target.value)} />
                    </div>
                </div>
                <div className="row mb-3">
                    <div className="col-md-3">
                        <Form.Label>Week Count</Form.Label>
                    </div>
                    <div className="col-md-9">
                        <Form.Control type="number" placeholder="Enter Week Count" value={weekCount} onChange={(e) => setWeekCount(e.target.value)} />
                    </div>
                </div>
                <div className="row mb-5">
                    <div className="col-md-3">
                        <Form.Label>Course Description</Form.Label>
                    </div>
                    <div className="col-md-9">
                        <Form.Control as="textarea" rows={3} placeholder="Enter Course Description" value={courseDescription} onChange={(e) => setCourseDescription(e.target.value)} />
                    </div>
                </div>
                <div className="text-center"> {/* Wrap the button in a div with text-center class */}
                    <Button variant="primary" onClick={handleSubmit1}>Add</Button>
                </div>

            </Form>

        </div>
    );
};

export default AddCourseForm;
