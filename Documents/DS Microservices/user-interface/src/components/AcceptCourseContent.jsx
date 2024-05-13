import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Accordion, Button, Form } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

const AcceptCourseContent = () => {
    // Access location state using useLocation hook
    const { state } = useLocation();
    const enrollmentId = state?.enrollmentId;
    const weekCount = state?.weekCount;
    const [description, setDescription] = useState('Sample Description');
    const navigate = useNavigate();

    const [courseDetails, setCourseDetails] = useState(null);
    const [courseContent, setCourseContent] = useState(null);

    const [weeks, setWeeks] = useState(Array.from({ length: weekCount }, (_, i) => i + 1).map(week => ({
        description: `Sample Description for Week ${week}`,
        videoUrl: `Sample Video URL for Week ${week}`,
        slideUrl: `Sample Slide URL for Week ${week}`,
        quizUrl: `Sample Quiz URL for Week ${week}`,
    })));

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

    const handleApprove = async () => {
        try {
            const response = await fetch(`http://localhost:8082/api/course/approve/${enrollmentId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ approve: 'Approved' })
            });
            if (!response.ok) {
                throw new Error('Failed to approve course');
            }
            navigate('/adminHome');

        } catch (error) {
            console.error('Error approving course:', error.message);
        }
    };

    const handleReject = async () => {
        try {
            const response = await fetch(`http://localhost:8082/api/course/approve/${enrollmentId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ approve: 'Rejected' })
            });
            if (!response.ok) {
                throw new Error('Failed to reject course');
            }
            navigate('/adminHome');
        } catch (error) {
            console.error('Error rejecting course:', error.message);
        }
    };

    if (!enrollmentId || !weekCount) {
        // Handle the case where enrollmentId or weekCount is undefined
        return (
            <div>
                <h1>Error: EnrollmentId or WeekCount not provided</h1>
                <p>Please go back and fill in all the required fields</p>
            </div>
        );
    }

    // Display enrollmentId and weekCount if they are available
    return (
        <div>
            <div className="row mb-5 ">
                <div className="col-md-8 mb-3">
                    <h1 className="mb-5">{courseDetails ? courseDetails.courseName : 'Course Name'}</h1>
                    <p className="mb-2">Description: {courseDetails ? courseDetails.description : 'Sample Description'}</p>
                </div>
                <div className="col-md-4">
                    <div className="row justify-content-center">
                        <div className="col-md-6 text-end">
                            <Button className="accept mb-3 w-100" onClick={handleApprove}>Accept</Button>
                        </div>
                        <div className="col-md-6 text-end">
                            <Button className="reject mb-3 w-100" onClick={handleReject}>Reject</Button>
                        </div>
                    </div>
                    <div id="add-course-content" className="row mb-3 text-center justify-content-center">
                        <p className="mb-1">Enrollment Id: {courseDetails ? courseDetails.courseEnrollKey : 'SE3030'}</p>
                        <p className="mb-1">Course Fee: {courseDetails ? `LKR ${courseDetails.courseAmount}` : '$3000'}</p>
                        <p className="mb-1">Week Count: {weekCount}</p>
                    </div>
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
                                        <p>
                                            <strong>Video:</strong>
                                            <iframe
                                                title="Video Player"
                                                width="100%"
                                                height="auto" 
                                                src={weekContent.videoUrls}
                                                frameBorder="0"
                                                allowFullScreen
                                            ></iframe>
                                        </p>

                                        <p><strong>Lecture Notes:</strong> <a href={weekContent.lectureNotesUrls} target="_blank">{weekContent.lectureNotesUrls}</a></p>

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


        </div>
    );
};

export default AcceptCourseContent;
