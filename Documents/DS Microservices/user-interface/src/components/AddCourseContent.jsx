import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Accordion, Button, Form } from 'react-bootstrap';

const AddCourseContent = () => {
    const { state } = useLocation();
    const enrollmentId = state?.enrollmentId;
    const weekCount = state?.weekCount;

    const [isEditable, setIsEditable] = useState(false);
    const [editedIndex, setEditedIndex] = useState(null); // Track the index of the week being edited

    const [courseDetails, setCourseDetails] = useState(null);
    const [courseContent, setCourseContent] = useState(null);

    const [description, setDescription] = useState('');
    const [video, setVideo] = useState('');
    const [slide, setSlide] = useState('');

    // State variables to store original values
    const [originalDescription, setOriginalDescription] = useState('');
    const [originalVideo, setOriginalVideo] = useState('');
    const [originalSlide, setOriginalSlide] = useState('');

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

    const handleUpdate = (index) => {
        setIsEditable(true);
        setEditedIndex(index);

        // Pre-fill form fields with existing values
        const weekContent = courseContent && courseContent[index + 1] && courseContent[index + 1][0]; // Assuming each week has only one content
        if (weekContent) {
            setDescription(weekContent.weekDescription || '');
            setVideo(weekContent.videoUrls || '');
            setSlide(weekContent.lectureNotesUrls || '');

            // Save original values
            setOriginalDescription(weekContent.weekDescription || '');
            setOriginalVideo(weekContent.videoUrls || '');
            setOriginalSlide(weekContent.lectureNotesUrls || '');
        }
    };

    // const handleConfirm = async () => {
    //     // Save the changes
    //     setIsEditable(false);
    //     // Make POST request to update course content
    //     try {
    //         const response = await fetch('http://localhost:8082/api/course-content', {
    //             method: 'POST',
    //             headers: {
    //                 'Content-Type': 'application/json'
    //             },
    //             body: JSON.stringify({
    //                 courseId: courseDetails?.courseId,
    //                 weekId: editedIndex + 1,
    //                 weekTitle: `Week ${editedIndex + 1}`,
    //                 weekDescription: description,
    //                 videoUrls: video,
    //                 lectureNotesUrls: slide
    //             })
    //         });
    //         if (!response.ok) {
    //             throw new Error('Failed to update course content');
    //         }
    //         // Refresh the page after completion
    //         window.location.reload();
    //     } catch (error) {
    //         console.error('Error updating course content:', error.message);
    //     }
    // };
    const handleConfirm = async () => {
        setIsEditable(false);
    
        try {
            // Check if content exists for the week
            const weekContentId = courseContent && courseContent[editedIndex + 1] && courseContent[editedIndex + 1][0]?.courseContentId;
    
            if (weekContentId) {
                // Content exists, make a PUT request to update
                const response = await fetch(`http://localhost:8082/api/course-content/${weekContentId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        lectureNotesUrls: slide,
                        videoUrls: video,
                    })
                });
                if (!response.ok) {
                    throw new Error('Failed to update course content');
                }
            } else {
                // Content doesn't exist, make a POST request to create new content
                const response = await fetch('http://localhost:8082/api/course-content', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        courseId: courseDetails?.courseId,
                        weekId: editedIndex + 1,
                        weekTitle: `Week ${editedIndex + 1}`,
                        weekDescription: description,
                        videoUrls: video,
                        lectureNotesUrls: slide
                    })
                });
                if (!response.ok) {
                    throw new Error('Failed to create course content');
                }
            }
            
            // Refresh the page after completion
            window.location.reload();
        } catch (error) {
            console.error('Error updating course content:', error.message);
        }
    };
    

    const handleCancel = () => {
        // Discard the changes and restore original values
        setIsEditable(false);
        setDescription('');
        setVideo('');
        setSlide('');
        setEditedIndex(null);
    };

    const handleChange = (field, value) => {
        // Update edited values when input fields are changed
        switch (field) {
            case 'description':
                setDescription(value);
                break;
            case 'video':
                setVideo(value);
                break;
            case 'slide':
                setSlide(value);
                break;
            default:
                break;
        }
    };

    return (
        <div>
            <div className="row mb-5 ">
                <div className="col-md-8 mb-3">
                    <h1 className="mb-4">{courseDetails ? courseDetails.courseName : 'Course Name'}</h1>
                    <p className="mb-2">Description: {courseDetails ? courseDetails.description : 'Sample Description'}</p>
                </div>
                <div id="add-course-content" className="col-md-4 mb-3 text-center justify-content-center">
                    <p className="mb-1">Enrollment Id: {courseDetails ? courseDetails.courseEnrollKey : '0000'}</p>
                    <p className="mb-1">Course Fee: {courseDetails ? `LKR ${courseDetails.courseAmount}` : '0000'}</p>
                    <p className="mb-1">Week Count: {weekCount}</p>
                </div>
            </div>

            <Accordion defaultActiveKey="0">
                {Array.from({ length: weekCount }, (_, i) => i + 1).map((weekNumber, index) => (
                    <Accordion.Item key={index} eventKey={index} style={{ marginBottom: '2em' }}>
                        <Accordion.Header>
                            <span>Week {weekNumber}</span>
                        </Accordion.Header>
                        <Accordion.Body>
                            {isEditable && index === editedIndex ? ( // Check if the current row is being edited
                                <Form>
                                    <Form.Group className="mb-3" controlId={`description-${index}`}>
                                        <Form.Label>Description:</Form.Label>
                                        <Form.Control type="text" value={description} onChange={(e) => handleChange('description', e.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-3" controlId={`videoUrl-${index}`}>
                                        <Form.Label>Video URL:</Form.Label>
                                        <Form.Control type="text" value={video} onChange={(e) => handleChange('video', e.target.value)} />
                                    </Form.Group>
                                    <Form.Group className="mb-3" controlId={`slideUrl-${index}`}>
                                        <Form.Label>Slide URL:</Form.Label>
                                        <Form.Control type="text" value={slide} onChange={(e) => handleChange('slide', e.target.value)} />
                                    </Form.Group>
                                </Form>
                            ) : (
                                <>
                                    {courseContent && courseContent[weekNumber] ? (
                                        courseContent[weekNumber].map((weekContent, contentIndex) => (
                                            <div key={contentIndex}>
                                                <p>{weekContent.weekDescription}</p>
                                                <p><strong>Video:</strong> <a href={weekContent.videoUrls} target="_blank">{weekContent.videoUrls}</a></p>
                                                <p><strong>Lecture Notes:</strong> <a href={weekContent.lectureNotesUrls} target="_blank">{weekContent.lectureNotesUrls}</a></p>

                                            </div>
                                        ))
                                    ) : (
                                        <div>
                                            <p>No data available for this week.</p>
                                        </div>
                                    )}
                                </>
                            )}
                            <div className="d-flex justify-content-end">
                                {!isEditable && (
                                    <Button variant="primary" onClick={() => handleUpdate(index)}>Update</Button>
                                )}
                                {isEditable && index === editedIndex && (
                                    <>
                                        <Button className="mx-2" variant="success" onClick={handleConfirm}>Confirm</Button>
                                        <Button variant="danger" onClick={handleCancel}>Cancel</Button>
                                    </>
                                )}
                            </div>
                        </Accordion.Body>
                    </Accordion.Item>
                ))}
            </Accordion>
        </div>
    );
};

export default AddCourseContent;
