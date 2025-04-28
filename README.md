Matrix Calculator Android App
Overview
This project is an Android application developed for an assignment that includes two main features:

Q1: Matrix Calculator - A tool to perform matrix operations (addition, subtraction, multiplication, division) using a native C++ library.
Q2: WiFi RSS Logger - A feature to log WiFi signal strengths (RSS) for multiple locations and display the minimum and maximum RSS ranges for each access point.

The app uses HomeActivity as the launcher, with buttons to navigate to the Matrix Calculator and WiFi RSS Logger.
Features
Q1: Matrix Calculator

Functionality:
Perform matrix operations: addition, subtraction, multiplication, and division.
Input matrix dimensions (e.g., "2 2" for a 2x2 matrix) and elements (e.g., "1,2;3,4" for a 2x2 matrix).
Operations are implemented in C++ using the Eigen library and interfaced with Java via JNI.


UI:
Input fields for matrix dimensions and elements.
Buttons for each operation (Add, Subtract, Multiply, Divide).
Displays results or error messages (e.g., invalid dimensions, non-invertible matrix for division).


Q2: WiFi RSS Logger

Functionality:
Select a location ("Home", "Office", "Lab") from a Spinner.
Log 100 samples of WiFi signal strengths (RSS) at 500ms intervals.
Display the minimum and maximum RSS range for each access point (AP) per location.


UI:
Spinner to select location.
Button to start/stop logging.
TextView to display logging progress and results.

Technologies Used

Languages: Kotlin (Android app), C++ (matrix operations)
Libraries/Frameworks:
Android SDK
Eigen 3.3.9 (for matrix operations in C++)
JNI (Java Native Interface) for native integration


Tools:
Android Studio (development environment)
CMake (for building the C++ library)
Git/GitHub (version control)



Project Structure

app/src/main/java/com/example/matrixcalculator/
HomeActivity.kt: Launcher Activity with navigation to Q1 and Q2.
MainActivity.kt: Matrix Calculator (Q1) implementation.
WifiRssLogger.kt: WiFi RSS Logger (Q2) implementation.


app/src/main/res/layout/
activity_home.xml: Layout for HomeActivity.
activity_main.xml: Layout for Matrix Calculator.
activity_wifi_rss.xml: Layout for WiFi RSS Logger.


app/src/main/cpp/
native-lib.cpp: C++ implementation of matrix operations.
CMakeLists.txt: CMake configuration for building the native library.
eigen-3.3.9/: Eigen library for matrix operations.


AndroidManifest.xml: Declares Activities and permissions.

Setup Instructions

Clone the Repository:git clone https://github.com/KartavyaChauhan/MatrixCalculator.git
cd MatrixCalculator


Open in Android Studio:
Open Android Studio.
Select Open an existing project and choose the MatrixCalculator directory.


Sync and Build:
Sync the project with Gradle by clicking Sync Project with Gradle Files.
Build the project (Build > Make Project).


Run the App:
Connect an Android device or use an emulator.
For Q2 (WiFi RSS Logger), a physical device with WiFi enabled is required.
Run the app (Run > Run 'app').



Usage and Testing
Q1: Matrix Calculator

From HomeActivity, tap "Matrix Calculator".
Enter inputs:
Matrix 1 Dimensions: "2 2"
Matrix 1 Elements: "1,2;3,4"
Matrix 2 Dimensions: "2 2"
Matrix 2 Elements: "5,6;7,8"


Test operations:
Add: Result should be "6.0 8.0; 10.0 12.0".
Subtract: Result should be "-4.0 -4.0; -4.0 -4.0".
Multiply: Result should be "19.0 22.0; 43.0 50.0".
Divide: Result should be approximately "-4.0 -2.0; -3.5 -2.0".


Tap "Back" to return to HomeActivity.

Q2: WiFi RSS Logger

From HomeActivity, tap "WiFi RSS Logger".
Grant permissions (ACCESS_WIFI_STATE, ACCESS_FINE_LOCATION, CHANGE_WIFI_STATE) when prompted.
Select a location (e.g., "Home") from the Spinner.
Tap "Start Logging" to log 100 samples of WiFi RSS.
View results (e.g., "Home: AP 00:14:22:01:23:45: RSS Range: -85 to -70 dBm").
Tap "Back" to return to HomeActivity.

Notes

Navigation: The app uses HomeActivity as the launcher, with proper back navigation from both features.
Permissions: Q2 requires WiFi and location permissions, handled dynamically.
Cross-Platform: The app has been tested on a physical Android device (foldable, Android 14).
Line Endings: Git line-ending warnings (LF to CRLF) have been addressed by setting core.autocrlf false.

Submission

Repository: https://github.com/KartavyaChauhan/MatrixCalculator
Submitted via Google Classroom with collaborator access for instructor/TA.

