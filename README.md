**University Timetable Management System (UTMS)**

The University Timetable Management System (UTMS) is a Spring Boot-based web application designed to manage university course schedules, faculty assignments, student enrollments, and class resources efficiently. It provides authentication and authorization mechanisms using JSON Web Tokens (JWT) for secure access to its functionalities.

**Features**

•	Course Management: Manage university courses efficiently within the system. Faculty members can create, update, and delete course information as needed.

•	Faculty Assignment: Assign faculty members to specific courses for teaching responsibilities. This feature allows administrators to allocate teaching duties to faculty members effectively.

•	Student Enrollment: Enable students to enroll in courses through the system. Students can view available courses, register for their desired courses, and manage their course schedules.

•	Timetable Management: Faculty members can manage course timetables and related student schedules within the system. They can create and update course schedules, assign class timings, and manage any changes to the timetable. Additionally, the system automatically sends email alerts to relevant students for any changes to the timetable, ensuring students stay informed and updated.

•	Resource Management: Ensure efficient utilization of resources such as labs, halls, and other facilities. The system prevents overlapping of resources by managing their availability and scheduling to avoid conflicts.

**Technologies Used**

•	Java

•	Spring Boot

•	Maven

•	MongoDB

•	JWT

•	Postman (for API endpoint)

•	JUnit and Mockito (for unit testing)

•	Docker (for MongoDB integration testing)

**Setup Instructions**

1.	Clone the repository: git clone [https://github.com/your/repository.git](https://github.com/sliitcsse/assignment-01-IT21375514.git)
2.	Navigate to the project directory
3.	Build the project using Maven
4.	Run the application
   
**API Endpoint Documentation**

The API endpoint documentation is provided in the Postman collection file University_TMS.postman_collection.json. Import this file into Postman to view and test the available endpoints.

**Running Tests**

**Unit Tests**
1.	Run unit tests using Maven

**Integration Tests**
1.	Ensure Docker is installed and running on your system.
2.	Navigate to the project directory.
3.	Start MongoDB Docker container
4.	Run integration tests
   
**Environment Setup for Testing**

•	For integration and performance testing, Docker is required to run MongoDB in a containerized environment.

•	Postman is used for API endpoint testing. Import the provided Postman collection file for testing.

