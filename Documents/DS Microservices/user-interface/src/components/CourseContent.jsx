import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Accordion, ProgressBar, Button, Form } from 'react-bootstrap';

const CourseContent = () => {
    // Access location state using useLocation hook
    const { state } = useLocation();
    const enrollmentId = state?.enrollmentId;
    const weekCount = state?.weekCount;
    const [description, setDescription] = useState('Sample Description');
    const role = localStorage.getItem('userRole');

    const [courseDetails, setCourseDetails] = useState(null);
    const [courseContent, setCourseContent] = useState(null);

    const [progress, setProgress] = useState(0);
    const [progressPercentage, setProgressPercentage] = useState(0);

    const [courseStudentProgressDataCourseContent, setCourseStudentProgressDataCourseContent] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const token = localStorage.getItem('jwtToken');

                const response = await fetch(`http://localhost:8082/api/course/${enrollmentId}/content`);
                if (!response.ok) {
                    throw new Error('Failed to fetch course content');
                }
                const data = await response.json();
                console.log('Course details:', data.courseDetails);
                console.log('Course content:', data.courseContent);
                setCourseDetails(data.courseDetails);
                setCourseContent(data.courseContent);

                // Fetch course content details
                const contentResponse = await fetch(`http://localhost:8082/api/course-content/${enrollmentId}`);
                if (!contentResponse.ok) {
                    throw new Error('Failed to fetch course content details');
                }
                const contentData = await contentResponse.json();
                console.log('Total content count:', contentData.totalContent);

                // Fetch progress count
                const progressResponse = await fetch(`http://localhost:8085/enrollment/progress/${enrollmentId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });
                if (!progressResponse.ok) {
                    throw new Error('Failed to fetch progress count');
                }
                const progressData = await progressResponse.json();
                console.log('Progress count:', progressData);

                const calculatedProgressPercentage = (progressData / contentData.totalContent) * 100;
                setProgressPercentage(calculatedProgressPercentage);
                setProgress(calculatedProgressPercentage);


                const courseStudentProgress = await fetch(`http://localhost:8085/enrollment/getSingleStudentEnrollmentCourse/${enrollmentId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });
                if (!courseStudentProgress.ok) {
                    throw new Error('Failed to fetch progress count');
                }
                const courseStudentProgressData = await courseStudentProgress.json();
                console.log('CourseStudentProgressData:', courseStudentProgressData);
                console.log('CourseStudentProgressData CourseContent:', courseStudentProgressData.courseContent);
                setCourseStudentProgressDataCourseContent(courseStudentProgressData.courseContent);
                console.log('Global CourseStudentProgressData CourseContent:', courseStudentProgressData.courseContent);
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

    const handleLinkClick = async (weekId, type) => {
        try {

            if (!courseStudentProgressDataCourseContent) {
                console.error('Course student progress data not available');
                setCourseStudentProgressDataCourseContent({
                    [weekId]: {
                        lectureUrlViewed: false,
                        videoUrlViewed: false
                    }
                });
            }

            let  weekData = courseStudentProgressDataCourseContent[weekId];
            if (!weekData) {
                console.error(`Week ${weekId} data not available`);
                // Set default values for weekData
                weekData = {
                    lectureUrlViewed: false,
                    videoUrlViewed: false
                };
            }

            console.log(`Week ${weekId} data - lectureUrlViewed:`, weekData.lectureUrlViewed);
            console.log(`Week ${weekId} data - videoUrlViewed:`, weekData.videoUrlViewed);

            // Store data in a variable
            const lectureViewed = weekData.lectureUrlViewed || false;
            const videoViewed = weekData.videoUrlViewed || false;

            if (type === 'videoViewed') {
                // Post updated progress data
                const token = localStorage.getItem('jwtToken');
                const response = await fetch('http://localhost:8085/enrollment/progress', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        studentUserName: '', // Replace with actual username
                        courseId: enrollmentId,
                        weekId,
                        videoViewed: true,
                        notesRead: lectureViewed
                    })
                });
                if (!response.ok) {
                    throw new Error('Failed to update progress data');
                }
            } else {
                const token = localStorage.getItem('jwtToken');
                const response = await fetch('http://localhost:8085/enrollment/progress', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        studentUserName: '', // Replace with actual username
                        courseId: enrollmentId,
                        weekId,
                        videoViewed: videoViewed,
                        notesRead: true
                    })
                });
                if (!response.ok) {
                    throw new Error('Failed to update progress data');
                }
            }
            window.location.reload();

        } catch (error) {
            console.error('Error updating progress:', error.message);
        }
    };

    // Display enrollmentId and weekCount if they are available
    // return (
    //     <div>
    //         <div className="row mb-5 ">
    //             <div className="col-md-8 mb-3">
    //                 <h1 className="mb-4">Course Name</h1>
    //                 <p className="mb-2">Description: {description}</p>
    //                 <p className="mb-2">Progress: {`${progressPercentage.toFixed(2)}%`}</p>
    //                 <ProgressBar className="mb-2" now={progress} />
    //             </div>
    //             <div id="add-course-content" className="col-md-4 mb-3 text-center justify-content-center">
    //                 <p className="mb-1">Enrollment Id: SE3030</p>
    //                 <p className="mb-1">Course Fee: 3000</p>
    //                 <p className="mb-1">Week Count: {weekCount}</p>
    //             </div>
    //         </div>

    //         {/* Accordion */}
    //         <Accordion defaultActiveKey="0">
    //             {weeks.map((week, index) => (
    //                 <Accordion.Item key={index} eventKey={index} style={{ marginBottom: '2em' }}>
    //                     <Accordion.Header>
    //                         <span>Week {index + 1}</span>
    //                     </Accordion.Header>
    //                     <Accordion.Body>
    //                         <p>{week.description}</p>
    //                         <p><strong>Video:</strong> {week.videoUrl}</p>
    //                         <p><strong>Slide:</strong> {week.slideUrl}</p>
    //                     </Accordion.Body>
    //                 </Accordion.Item>
    //             ))}
    //         </Accordion>


    //     </div>
    // );
    return (
        <div>
            <div className="row mb-5 ">
                <div className="col-md-8 mb-3">
                    <h1 className="mb-4">{courseDetails ? courseDetails.courseName : 'Course Name'}</h1>
                    <p className="mb-2">Description: {courseDetails ? courseDetails.description : 'Sample Description'}</p>
                    {role === '[ROLE_STUDENT]' && (
                        <>
                            <p className="mb-2">Progress: {`${progressPercentage.toFixed(2)}%`}</p>
                            <ProgressBar className="mb-2" now={progress} />
                        </>
                    )}

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
                                        {/* <p><strong>Video:</strong> <a href={weekContent.videoUrls} target="_blank">{weekContent.videoUrls}</a></p>
                                        <p><strong>Lecture Notes:</strong> <a href={weekContent.lectureNotesUrls} target="_blank">{weekContent.lectureNotesUrls}</a></p> */}

                                        {role === '[ROLE_STUDENT]' ? (
                                            <>
                                                <div style={{ justifyContent: 'space-between', alignItems: 'center' }}>
                                                    <p><strong>Video:</strong> <a href={weekContent.videoUrls} target="_blank" onClick={() => handleLinkClick(weekNumber, 'videoViewed')}>{weekContent.videoUrls}</a></p>
                                                </div>
                                                <div style={{ justifyContent: 'space-between', alignItems: 'center' }}>
                                                    <p><strong>Lecture Notes:</strong> <a href={weekContent.lectureNotesUrls} target="_blank" onClick={() => handleLinkClick(weekNumber, 'notesRead')}>{weekContent.lectureNotesUrls}</a></p>
                                                </div>
                                            </>
                                        ) : (
                                            <>
                                                <p><strong>Video:</strong> <a href={weekContent.videoUrls} target="_blank">{weekContent.videoUrls}</a></p>
                                                <p><strong>Lecture Notes:</strong> <a href={weekContent.lectureNotesUrls} target="_blank">{weekContent.lectureNotesUrls}</a></p>
                                            </>
                                        )}


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

export default CourseContent;
