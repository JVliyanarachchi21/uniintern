package com.uniintern.portal.free_course.service;

import com.uniintern.portal.free_course.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizDataService {

    private final Map<String, List<Question>> quizData = new HashMap<>();

    public QuizDataService() {
        initializeQuizData();
    }

    private void initializeQuizData() {
        // SQL Quiz
        quizData.put("sql", Arrays.asList(
            createQuestion(1, "What does SQL stand for?",
                Arrays.asList("Structured Query Language", "Strong Question Language", "Structured Question Language", "Simple Query Language"), 0),
            createQuestion(2, "Which SQL statement is used to extract data from a database?",
                Arrays.asList("GET", "OPEN", "EXTRACT", "SELECT"), 3),
            createQuestion(3, "Which SQL statement is used to update data in a database?",
                Arrays.asList("SAVE", "UPDATE", "MODIFY", "SAVE AS"), 1),
            createQuestion(4, "Which SQL statement is used to delete data from a database?",
                Arrays.asList("REMOVE", "COLLAPSE", "DELETE", "CLEAR"), 2),
            createQuestion(5, "Which SQL statement is used to insert new data in a database?",
                Arrays.asList("ADD NEW", "INSERT INTO", "INSERT NEW", "ADD RECORD"), 1),
            createQuestion(6, "Which SQL keyword is used to sort the result-set?",
                Arrays.asList("SORT BY", "ORDER BY", "GROUP BY", "ARRANGE BY"), 1),
            createQuestion(7, "Which SQL clause is used to filter records?",
                Arrays.asList("FILTER", "HAVING", "WHERE", "CONDITION"), 2),
            createQuestion(8, "Which SQL function is used to count the number of rows?",
                Arrays.asList("NUMBER()", "SUM()", "TOTAL()", "COUNT(*)"), 3),
            createQuestion(9, "What is the correct SQL syntax to return only distinct values?",
                Arrays.asList("SELECT UNIQUE", "SELECT DISTINCT", "SELECT DIFFERENT", "SELECT NODUP"), 1),
            createQuestion(10, "Which JOIN returns rows when there is a match in both tables?",
                Arrays.asList("LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "OUTER JOIN"), 2),
            createQuestion(11, "What does the GROUP BY statement do?",
                Arrays.asList("Sorts the result set", "Groups rows that have the same values", "Filters grouped records", "Joins two tables"), 1),
            createQuestion(12, "Which operator is used to search for a specified pattern in a column?",
                Arrays.asList("FIND", "SEARCH", "LIKE", "MATCH"), 2),
            createQuestion(13, "What is a PRIMARY KEY in SQL?",
                Arrays.asList("A key that encrypts data", "A unique identifier for each record in a table", "The first column in a table", "A foreign reference"), 1),
            createQuestion(14, "Which SQL keyword is used to combine rows from two or more tables?",
                Arrays.asList("MERGE", "COMBINE", "JOIN", "ATTACH"), 2),
            createQuestion(15, "What does NULL represent in SQL?",
                Arrays.asList("Zero", "Empty string", "A missing or undefined value", "False"), 2),
            createQuestion(16, "Which SQL statement is used to create a new table?",
                Arrays.asList("MAKE TABLE", "CREATE TABLE", "NEW TABLE", "BUILD TABLE"), 1),
            createQuestion(17, "What is the purpose of the HAVING clause?",
                Arrays.asList("Filter rows before grouping", "Filter groups after GROUP BY", "Sort the results", "Limit the number of rows"), 1),
            createQuestion(18, "Which aggregate function returns the highest value?",
                Arrays.asList("TOP()", "HIGHEST()", "MAX()", "UPPER()"), 2),
            createQuestion(19, "What does the BETWEEN operator do?",
                Arrays.asList("Compares two columns", "Selects values within a given range", "Joins two tables", "Checks for NULL values"), 1),
            createQuestion(20, "Which statement is used to remove a table from a database?",
                Arrays.asList("DELETE TABLE", "REMOVE TABLE", "DROP TABLE", "DESTROY TABLE"), 2)
        ));

        // Python Quiz
        quizData.put("python", Arrays.asList(
            createQuestion(1, "What is the correct file extension for Python files?",
                Arrays.asList(".pt", ".pyt", ".py", ".pyth"), 2),
            createQuestion(2, "How do you create a variable with the numeric value 5?",
                Arrays.asList("x = 5", "x = int(5)", "Both A and B", "int x = 5"), 2),
            createQuestion(3, "What is the correct syntax to output \"Hello World\" in Python?",
                Arrays.asList("print(\"Hello World\")", "echo(\"Hello World\");", "p(\"Hello World\")", "console.log(\"Hello World\")"), 0),
            createQuestion(4, "How do you insert comments in Python code?",
                Arrays.asList("// This is a comment", "/* This is a comment */", "# This is a comment", "-- This is a comment"), 2),
            createQuestion(5, "Which collection is ordered, changeable, and allows duplicates?",
                Arrays.asList("Set", "Dictionary", "Tuple", "List"), 3),
            createQuestion(6, "What is the output of: print(type(5))?",
                Arrays.asList("<class 'float'>", "<class 'int'>", "<class 'number'>", "<class 'str'>"), 1),
            createQuestion(7, "Which keyword is used to define a function in Python?",
                Arrays.asList("function", "func", "def", "define"), 2),
            createQuestion(8, "How do you start a for loop in Python?",
                Arrays.asList("for (i = 0; i < 5; i++)", "for i in range(5):", "foreach i in range(5)", "loop i from 0 to 5"), 1),
            createQuestion(9, "Which method adds an element to the end of a list?",
                Arrays.asList("add()", "push()", "append()", "insert()"), 2),
            createQuestion(10, "What is the correct way to create a dictionary?",
                Arrays.asList("d = [\"key\": \"value\"]", "d = (\"key\": \"value\")", "d = {\"key\": \"value\"}", "d = <\"key\": \"value\">"), 2),
            createQuestion(11, "Which operator is used for exponentiation in Python?",
                Arrays.asList("^", "**", "exp()", "//"), 1),
            createQuestion(12, "What does the len() function do?",
                Arrays.asList("Returns the largest item", "Returns the length of an object", "Returns the data type", "Returns the last element"), 1),
            createQuestion(13, "How do you handle exceptions in Python?",
                Arrays.asList("try/catch", "try/except", "do/catch", "begin/rescue"), 1),
            createQuestion(14, "What is a tuple in Python?",
                Arrays.asList("A mutable ordered collection", "An immutable ordered collection", "An unordered collection", "A key-value pair collection"), 1),
            createQuestion(15, "Which keyword is used to create a class in Python?",
                Arrays.asList("struct", "object", "class", "define"), 2),
            createQuestion(16, "What does the \"self\" keyword refer to in a Python class?",
                Arrays.asList("The parent class", "The current instance of the class", "A global variable", "The class constructor"), 1),
            createQuestion(17, "Which built-in function converts a string to an integer?",
                Arrays.asList("str()", "float()", "int()", "num()"), 2),
            createQuestion(18, "What is a lambda function in Python?",
                Arrays.asList("A named function", "A small anonymous function", "A recursive function", "A generator function"), 1),
            createQuestion(19, "Which module is used for regular expressions in Python?",
                Arrays.asList("regex", "pyregex", "re", "regexp"), 2),
            createQuestion(20, "What does \"pip\" stand for in Python?",
                Arrays.asList("Python Install Package", "Pip Installs Packages", "Package Installer for Python", "Python Integrated Platform"), 1)
        ));

        // HTML/CSS Quiz
        quizData.put("htmlcss", Arrays.asList(
            createQuestion(1, "What does HTML stand for?",
                Arrays.asList("Hyper Text Markup Language", "Home Tool Markup Language", "Hyperlinks and Text Markup Language", "Hyper Tool Markup Language"), 0),
            createQuestion(2, "Choose the correct HTML element for the largest heading:",
                Arrays.asList("<heading>", "<h6>", "<head>", "<h1>"), 3),
            createQuestion(3, "What does CSS stand for?",
                Arrays.asList("Computer Style Sheets", "Cascading Style Sheets", "Creative Style Sheets", "Colorful Style Sheets"), 1),
            createQuestion(4, "Which HTML tag is used to define an internal style sheet?",
                Arrays.asList("<css>", "<script>", "<style>", "<link>"), 2),
            createQuestion(5, "Which property is used to change the background color?",
                Arrays.asList("color", "bgcolor", "background-color", "bg-color"), 2),
            createQuestion(6, "Which HTML element is used to define important text?",
                Arrays.asList("<b>", "<i>", "<strong>", "<em>"), 2),
            createQuestion(7, "Which CSS property controls the text size?",
                Arrays.asList("text-size", "font-style", "font-size", "text-style"), 2),
            createQuestion(8, "How do you add a comment in CSS?",
                Arrays.asList("// comment", "/* comment */", "<!-- comment -->", "# comment"), 1),
            createQuestion(9, "Which HTML attribute specifies an alternate text for an image?",
                Arrays.asList("title", "src", "alt", "longdesc"), 2),
            createQuestion(10, "Which CSS property is used to change the font of an element?",
                Arrays.asList("font-style", "text-font", "font-family", "font-weight"), 2),
            createQuestion(11, "What is the correct HTML for creating a hyperlink?",
                Arrays.asList("<a href=\"url\">link</a>", "<link url=\"url\">", "<a url=\"url\">link</a>", "<hyperlink>url</hyperlink>"), 0),
            createQuestion(12, "Which CSS property is used to make text bold?",
                Arrays.asList("font-style: bold", "text-decoration: bold", "font-weight: bold", "text-style: bold"), 2),
            createQuestion(13, "What is the correct HTML for an unordered list?",
                Arrays.asList("<ol>", "<list>", "<ul>", "<dl>"), 2),
            createQuestion(14, "Which CSS display value hides an element completely?",
                Arrays.asList("visibility: hidden", "display: none", "opacity: 0", "display: hidden"), 1),
            createQuestion(15, "What does the \"box-sizing: border-box\" property do?",
                Arrays.asList("Adds a border to all elements", "Includes padding and border in the element total width", "Makes the element a box shape", "Removes all margins"), 1),
            createQuestion(16, "Which HTML tag is used to define a table row?",
                Arrays.asList("<td>", "<th>", "<tr>", "<table>"), 2),
            createQuestion(17, "What is Flexbox used for in CSS?",
                Arrays.asList("Adding animations", "Creating responsive layouts", "Changing fonts", "Adding shadows"), 1),
            createQuestion(18, "Which HTML element is used for user input?",
                Arrays.asList("<input>", "<form>", "<textfield>", "<select>"), 0),
            createQuestion(19, "What is the default position value in CSS?",
                Arrays.asList("relative", "absolute", "fixed", "static"), 3),
            createQuestion(20, "Which CSS property adds space inside an element?",
                Arrays.asList("margin", "padding", "spacing", "border"), 1)
        ));

        // Java Quiz
        quizData.put("java", Arrays.asList(
            createQuestion(1, "Which data type is used to create a variable that should store text?",
                Arrays.asList("String", "Txt", "string", "myString"), 0),
            createQuestion(2, "How do you create a variable with the numeric value 5 in Java?",
                Arrays.asList("num x = 5", "float x = 5;", "int x = 5;", "x = 5;"), 2),
            createQuestion(3, "Which method can be used to find the length of a string?",
                Arrays.asList("getSize()", "length()", "len()", "size()"), 1),
            createQuestion(4, "Which operator is used to add together two values?",
                Arrays.asList("*", "+", "&", "/"), 1),
            createQuestion(5, "To declare an array in Java, define the variable type with:",
                Arrays.asList("{}", "()", "[]", "<>"), 2),
            createQuestion(6, "What is the entry point of a Java program?",
                Arrays.asList("start() method", "run() method", "main() method", "init() method"), 2),
            createQuestion(7, "Which keyword is used to create a class in Java?",
                Arrays.asList("struct", "object", "class", "define"), 2),
            createQuestion(8, "What is the purpose of the \"final\" keyword?",
                Arrays.asList("To end a program", "To make a variable constant", "To finalize garbage collection", "To close a file"), 1),
            createQuestion(9, "Which of these is NOT a Java primitive type?",
                Arrays.asList("int", "boolean", "String", "char"), 2),
            createQuestion(10, "What does OOP stand for?",
                Arrays.asList("Object-Oriented Programming", "Out Of Process", "Object-Ordered Protocol", "Open Object Platform"), 0),
            createQuestion(11, "Which keyword is used to inherit a class in Java?",
                Arrays.asList("implements", "inherits", "extends", "super"), 2),
            createQuestion(12, "What is encapsulation in Java?",
                Arrays.asList("Hiding implementation details", "Creating multiple objects", "Inheriting from a class", "Overloading methods"), 0),
            createQuestion(13, "Which collection class allows duplicate elements?",
                Arrays.asList("HashSet", "TreeSet", "ArrayList", "HashMap"), 2),
            createQuestion(14, "What is the default value of a boolean variable in Java?",
                Arrays.asList("true", "false", "null", "0"), 1),
            createQuestion(15, "Which exception is thrown when dividing by zero?",
                Arrays.asList("NullPointerException", "ArithmeticException", "NumberFormatException", "IOException"), 1),
            createQuestion(16, "What does the \"static\" keyword mean?",
                Arrays.asList("The variable cannot change", "The method belongs to the class, not instances", "The class cannot be inherited", "The method runs first"), 1),
            createQuestion(17, "Which interface must be implemented for sorting objects?",
                Arrays.asList("Sortable", "Comparable", "Serializable", "Iterable"), 1),
            createQuestion(18, "What is polymorphism?",
                Arrays.asList("Using multiple constructors", "Objects taking many forms", "Creating abstract classes", "Hiding data members"), 1),
            createQuestion(19, "Which keyword is used to handle exceptions?",
                Arrays.asList("throw", "try", "catch", "All of the above"), 3),
            createQuestion(20, "What is an abstract class?",
                Arrays.asList("A class that cannot be instantiated", "A class with no methods", "A class that is always static", "A class with only private members"), 0)
        ));

        // C++ Quiz
        quizData.put("cpp", Arrays.asList(
            createQuestion(1, "What is a correct syntax to output \"Hello World\" in C++?",
                Arrays.asList("System.out.println(\"Hello World\");", "Console.WriteLine(\"Hello World\");", "cout << \"Hello World\";", "print(\"Hello World\");"), 2),
            createQuestion(2, "How do you insert comments in C++ code?",
                Arrays.asList("# This is a comment", "// This is a comment", "/* This is a comment", "-- This is a comment"), 1),
            createQuestion(3, "Which data type is used to store text in C++?",
                Arrays.asList("string", "String", "txt", "myString"), 0),
            createQuestion(4, "How do you create a variable with the floating number 2.8?",
                Arrays.asList("int x = 2.8;", "byte x = 2.8", "double x = 2.8;", "x = 2.8;"), 2),
            createQuestion(5, "Which operator is used to multiply numbers?",
                Arrays.asList("%", "/", "*", "x"), 2),
            createQuestion(6, "What is a pointer in C++?",
                Arrays.asList("A variable that stores a memory address", "A function that returns void", "A type of loop", "A class method"), 0),
            createQuestion(7, "Which header file is needed for input/output in C++?",
                Arrays.asList("<stdio.h>", "<iostream>", "<input.h>", "<conio.h>"), 1),
            createQuestion(8, "What does the \"&\" operator do when placed before a variable?",
                Arrays.asList("Multiplies the value", "Returns the memory address", "Performs bitwise AND", "Both B and C depending on context"), 3),
            createQuestion(9, "What is the correct way to declare a constant in C++?",
                Arrays.asList("constant int x = 5;", "const int x = 5;", "final int x = 5;", "static int x = 5;"), 1),
            createQuestion(10, "Which keyword is used to define a class in C++?",
                Arrays.asList("struct", "class", "object", "Both A and B"), 3),
            createQuestion(11, "What is the purpose of the \"new\" keyword?",
                Arrays.asList("Creates a new file", "Allocates memory dynamically", "Defines a new class", "Creates a new thread"), 1),
            createQuestion(12, "What is a destructor in C++?",
                Arrays.asList("A function that creates objects", "A function that deletes files", "A function called when an object is destroyed", "A function that handles errors"), 2),
            createQuestion(13, "Which STL container provides key-value pairs?",
                Arrays.asList("vector", "list", "map", "set"), 2),
            createQuestion(14, "What is function overloading?",
                Arrays.asList("Calling a function too many times", "Multiple functions with the same name but different parameters", "A function that calls itself", "Overriding a base class function"), 1),
            createQuestion(15, "What does \"virtual\" keyword do in C++?",
                Arrays.asList("Makes a variable constant", "Enables runtime polymorphism", "Creates a virtual machine", "Hides a function"), 1),
            createQuestion(16, "What is the difference between \"struct\" and \"class\" in C++?",
                Arrays.asList("No difference", "Default access: struct is public, class is private", "Structs cannot have methods", "Classes cannot have public members"), 1),
            createQuestion(17, "Which operator is used to access members through a pointer?",
                Arrays.asList(".", "::", "->", "&"), 2),
            createQuestion(18, "What is a reference in C++?",
                Arrays.asList("A copy of a variable", "An alias for an existing variable", "A pointer to a pointer", "A constant variable"), 1),
            createQuestion(19, "What does RAII stand for?",
                Arrays.asList("Run And Initialize Always", "Resource Acquisition Is Initialization", "Reference And Integer Interaction", "Runtime Application Interface Integration"), 1),
            createQuestion(20, "Which C++ feature allows writing generic code?",
                Arrays.asList("Macros", "Templates", "Virtual functions", "Inline functions"), 1)
        ));

        // Cyber Security Quiz
        quizData.put("cyber", Arrays.asList(
            createQuestion(1, "What does CIA stand for in Cyber Security?",
                Arrays.asList("Confidentiality, Integrity, Availability", "Cyber Information Agency", "Central Intelligence Agency", "Computer Internet Access"), 0),
            createQuestion(2, "Which of the following is a type of malware?",
                Arrays.asList("Firewall", "Ransomware", "Antivirus", "VPN"), 1),
            createQuestion(3, "What is Phishing?",
                Arrays.asList("A type of fishing", "Securing a network", "Fraudulent attempt to obtain sensitive info", "A firewall configuration"), 2),
            createQuestion(4, "What is the purpose of a Firewall?",
                Arrays.asList("To speed up internet", "To block unauthorized access", "To store passwords", "To clean viruses"), 1),
            createQuestion(5, "What does VPN stand for?",
                Arrays.asList("Virtual Public Network", "Visual Private Network", "Virtual Private Network", "Virtual Protected Node"), 2),
            createQuestion(6, "What is a DDoS attack?",
                Arrays.asList("Direct Denial of Service", "Distributed Denial of Service", "Data Destruction of Systems", "Digital Denial of Security"), 1),
            createQuestion(7, "What is encryption?",
                Arrays.asList("Deleting sensitive data", "Converting data into a coded format", "Backing up data", "Compressing files"), 1),
            createQuestion(8, "What is two-factor authentication (2FA)?",
                Arrays.asList("Using two passwords", "Using two different verification methods", "Logging in from two devices", "Having two user accounts"), 1),
            createQuestion(9, "What is a Trojan horse in cybersecurity?",
                Arrays.asList("A type of firewall", "Malware disguised as legitimate software", "An encryption algorithm", "A network protocol"), 1),
            createQuestion(10, "What does HTTPS stand for?",
                Arrays.asList("Hyper Text Transfer Protocol Secure", "High Tech Transfer Protocol System", "Hyper Text Transport Protocol Safe", "Home Transfer Text Protocol Secure"), 0),
            createQuestion(11, "What is Social Engineering in cybersecurity?",
                Arrays.asList("Building social media platforms", "Manipulating people to reveal confidential information", "Engineering social networks", "Creating social media bots"), 1),
            createQuestion(12, "What is a Zero-Day vulnerability?",
                Arrays.asList("A bug that was fixed on day zero", "A vulnerability unknown to the vendor", "A virus that activates at midnight", "A security patch released on day one"), 1),
            createQuestion(13, "What is the purpose of penetration testing?",
                Arrays.asList("Testing internet speed", "Simulating attacks to find vulnerabilities", "Testing software performance", "Checking hardware durability"), 1),
            createQuestion(14, "What is a brute force attack?",
                Arrays.asList("Physically breaking into a server room", "Trying every possible password combination", "Sending millions of emails", "Overloading a network"), 1),
            createQuestion(15, "What is the principle of least privilege?",
                Arrays.asList("Giving everyone admin access", "Users get only the minimum access needed", "Removing all user permissions", "Granting access based on seniority"), 1),
            createQuestion(16, "What is a Man-in-the-Middle (MitM) attack?",
                Arrays.asList("A physical attack on servers", "Intercepting communication between two parties", "A type of social engineering", "Hacking a database directly"), 1),
            createQuestion(17, "What is SQL Injection?",
                Arrays.asList("Installing SQL on a server", "Inserting malicious SQL code through input fields", "A type of database backup", "Updating SQL software"), 1),
            createQuestion(18, "What is the purpose of an IDS (Intrusion Detection System)?",
                Arrays.asList("To block all traffic", "To detect suspicious activity on a network", "To encrypt data", "To manage user passwords"), 1),
            createQuestion(19, "What is a keylogger?",
                Arrays.asList("A tool to manage encryption keys", "Software that records keystrokes", "A type of firewall", "A password manager"), 1),
            createQuestion(20, "What is the difference between symmetric and asymmetric encryption?",
                Arrays.asList("Symmetric is slower", "Symmetric uses one key, asymmetric uses a key pair", "Asymmetric uses one key", "There is no difference"), 1)
        ));
    }

    private Question createQuestion(int id, String text, List<String> options, int correctIndex) {
        Question q = new Question();
        q.setId(id);
        q.setText(text);
        q.setOptions(options);
        q.setCorrectIndex(correctIndex);
        return q;
    }

    public List<Question> getQuizData(String courseId) {
        return quizData.getOrDefault(courseId, new ArrayList<>());
    }

    public boolean hasQuiz(String courseId) {
        return quizData.containsKey(courseId);
    }
}
