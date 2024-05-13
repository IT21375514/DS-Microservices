import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Accordion, ProgressBar, Button, Form, Modal, Spinner } from 'react-bootstrap';
import { FaPlus } from 'react-icons/fa';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

const CourseContentPreview = () => {
    // Access location state using useLocation hook
    const { state } = useLocation();
    const enrollmentId = state?.enrollmentId;
    const weekCount = state?.weekCount;
    const navigate = useNavigate();
    const [description, setDescription] = useState('Sample Description');
    const role = localStorage.getItem('userRole');

    const [courseDetails, setCourseDetails] = useState(null);
    const [courseContent, setCourseContent] = useState(null);

    const [showSuccessModal, setShowSuccessModal] = useState(false); // State for showing success modal
    const [successMessage, setSuccessMessage] = useState(""); // State for success message
    const [loading, setLoading] = useState(false); // State for loading spinner

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch(`http://localhost:8082/api/course/${enrollmentId}/content`);
                if (!response.ok) {
                    throw new Error('Failed to fetch course content');
                }
                const data = await response.json();
                console.log('Course details:', data.courseDetails);
                console.log('Course content:', data.courseContent);
                setCourseDetails(data.courseDetails);
                setCourseContent(data.courseContent);
            } catch (error) {
                console.error('Error fetching course content:', error.message);
            }
        };

        if (enrollmentId && weekCount) {
            fetchData();
        } else {
            console.error('EnrollmentId or WeekCount not provided');
        }
    }, [enrollmentId, weekCount]);

    if (!enrollmentId || !weekCount) {
        // Handle the case where enrollmentId or weekCount is undefined
        return (
            <div>
                <h1>Error: EnrollmentId or WeekCount not provided</h1>
                <p>Please go back and fill in all the required fields</p>
            </div>
        );
    }

    const studentEnrollHandle = async (enrollmentId) => {
        const token = localStorage.getItem('jwtToken');

        try {
            const response = await fetch('http://localhost:8085/enrollment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: JSON.stringify({
                    id: {
                        studentUserName: ""
                    },
                    courseId: enrollmentId
                })
            });

            if (response.ok) {
                console.log('Course enrolled successfully');
            } else {
                // Handle error response
                console.error('Error enrolled course:', response.statusText);
            }
        } catch (error) {
            console.error('Error creating course:', error);
            // Handle network errors or other exceptions
        }

        // Send the POST request for email notification
        try {
            const notificationResponse = await fetch('http://localhost:9091/notifications/email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `${token}`
                },
                body: JSON.stringify({
                    subject: 'Enrollment',
                    body: `Successfully enrolled ${courseDetails.courseEnrollKey}`
                })
            });

            if (!notificationResponse.ok) {
                throw new Error('Failed to send email notification');
            }
        } catch (error) {
            console.error('Error sending email notification:', error.message);
        }

        setShowSuccessModal(true);
        setLoading(true);

        setTimeout(() => {
            setSuccessMessage("Course enrolled successfully");
            setLoading(false);
            setTimeout(() => {
                setShowSuccessModal(false);
                navigate('/', { state: { enrollmentId } });
            }, 1000); // 60,000 milliseconds = 1 minute
        }, 1000);


    };

    // Display enrollmentId and weekCount if they are available
    return (
        <div>
            <div className="row mb-5 ">
                <div className="col-md-8 mb-3">
                    <h1 className="mb-4">{courseDetails ? courseDetails.courseName : 'Course Name'}</h1>
                    <p className="mb-2">Description: {courseDetails ? courseDetails.description : 'Sample Description'}</p>
                </div>
                <div id="add-course-content" className="col-md-4 mb-3 text-center justify-content-center">
                    <p className="mb-1">Enrollment Id: {courseDetails ? courseDetails.courseEnrollKey : 'SE3030'}</p>
                    <p className="mb-1">Course Fee: {courseDetails ? `LKR ${courseDetails.courseAmount}` : '$3000'}</p>
                    <p className="mb-1">Week Count: {weekCount}</p>
                </div>
            </div>
            {/* Accordion */}
            <Accordion defaultActiveKey="0">
                {Array.from({ length: weekCount }, (_, i) => i + 1).map((weekNumber, index) => (
                    <Accordion.Item key={index} eventKey={index} style={{ marginBottom: '2em' }}>
                        <Accordion.Header>
                            <span>Week {weekNumber}</span>
                        </Accordion.Header>
                        <Accordion.Body>
                            {courseContent && courseContent[weekNumber] ? (
                                courseContent[weekNumber].map((weekContent, contentIndex) => (
                                    <div key={contentIndex}>
                                        <p>{weekContent.weekDescription}</p>
                                    </div>
                                ))
                            ) : (
                                <div>
                                    <p>No data available for this week.</p>
                                </div>
                            )}
                        </Accordion.Body>
                    </Accordion.Item>
                ))}
            </Accordion>
            {role === '[ROLE_STUDENT]' && (
                <div style={{ position: 'fixed', bottom: '20px', right: '20px', zIndex: '999' }}>
                    <Button id="enroll-button" onClick={() => studentEnrollHandle(courseDetails.courseId)} className="btn btn-primary btn-floating rounded-circle p-3">
                        Enroll Now
                    </Button>
                </div>
            )}
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
        </div>

    );
};

export default CourseContentPreview;
