# translate-to-sql Project

## Overview
The `translate-to-sql` project is designed to translate SQL queries written in one logic into standard SQL. This project leverages the JSqlParser library for parsing SQL queries.


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

### File-Based Translation
for translating a file with queries -
1. Write an input file with schema and queries, in the format -
   ```text
   Table1(col1, col2,...)
   Table2(col1,...)
   …

   SELECT …
   SELECT…
   ```
2. Start the application by running the main class (Main.java) and give the input file path as args, or run from the terminal-
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="input.txt"
   ```
   when “input.txt” is the file path.
3. You can see the translated queries in the project directory in the “output.txt” file.

### Web-Based Query Submission
for sending queries from a local host -
1. Start the application by running the main class (Main.java) or run from the terminal
```bash
mvn spring-boot:run
```
2. Once the application is running, you can access it via your web browser at http://localhost:8080.
3. Enter queries into the input field and submit the form.
4. The application will translate the query to standard SQL and display the result.


## Acknowledgements
Thanks to the JSqlParser library for providing SQL parsing capabilities.


## 2VL logic
Implements the translation algorithm based on the principles described in the article 'Handling SQL Nulls with Two-Valued Logic' by Libkin and Peterfreund (2022).
