# translate-to-sql Project

## Overview
The `TranslateToSql` project is designed to translate SQL queries written in one logic into standard SQL. This project leverages the JSqlParser library for parsing SQL queries.


## Prerequisites
- **Java**: You will need to have JDK 8 or JDK 11 installed. Please note that JSQLParser-4.9 is the last JDK 8 compatible release and all development after will depend on JDK 11.
- **Maven**: This project uses Maven for dependency management and building. Ensure Maven 3.6.0 or higher is installed.


## Getting Started

### Cloning the Repository
To get started with the `translate-to-sql` project, clone this repository to your local machine using the following command:

```bash
git clone https://github.com/toviberger/translate-to-sql.git
```

### Building the Project
Navigate to the root directory of the project (where the `pom.xml` file is located) and use Maven to compile and package the project:

```bash
mvn clean install
```
This command will download the necessary dependencies, compile the project, and produce an executable JAR file if your project is configured to do so.


## Usage
follow these steps:
1. Start the application by running the main class (Main.java) or run from the terminal
```bash
mvn spring-boot:run
```
2. Once the application is running, you can access it via your web browser at http://localhost:8080.
3. Enter queries into the input field and submit the form.
4. The application will translate the query to standard SQL and display the result.


## Acknowledgements
Thanks to the JSqlParser library for providing SQL parsing capabilities.
This project is based on the algorithm described in the article "Handling SQL Nulls with Two-Valued Logic".



## 2VL logic
Implements the translation algorithm based on the principles described in the article "Handling SQL Nulls with Two-Valued Logic".

### Unsupported grammer

