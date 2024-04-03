package org.translateToSql;

import org.translateToSql.model.DatabaseMetadata;
import org.translateToSql.core.TranslateFromTwoVL;
import org.junit.jupiter.api.Test;
import org.translateToSql.utils.ExpressionUtils;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TwoVLTests {

    Map<String, List<String>> tables = new HashMap<>(){{
        put("R", new ArrayList<>(Arrays.asList("a", "b")));
        put("D", new ArrayList<>(Arrays.asList("b")));
        put("C", new ArrayList<>(Arrays.asList("a")));
        put("L", new ArrayList<>(Arrays.asList("b")));
        put("Z", new ArrayList<>(Arrays.asList("A")));
        put("Employees", new ArrayList<>(Arrays.asList("Department", "Salary", "FirstName", "LastName")));
        put("Departments", new ArrayList<>(Arrays.asList("Name", "ID", "FirstName", "ManagerID", "DepartmentName", "location")));
        put("Products", new ArrayList<>(Arrays.asList("Price", "ProductId", "FirstName", "CategoryID")));
        put("Orders", new ArrayList<>(Arrays.asList("EmployeeID")));
        put("Table1", new ArrayList<>(Arrays.asList("Column1", "Column2")));
        put("Table2", new ArrayList<>(Arrays.asList("Column1", "Column2")));
        put("r", new ArrayList<>(Arrays.asList("A")));
        put("S", new ArrayList<>(Arrays.asList("A")));
        put("sales", new ArrayList<>(Arrays.asList("Salesperson", "SaleId")));
        put("ProjectAssignments", new ArrayList<>(Arrays.asList("ProjectID")));
        put("events", new ArrayList<>(Arrays.asList("startData", "endDate", "ID")));
        put("s", new ArrayList<>(Arrays.asList("a")));
        put("tableName", new ArrayList<>(Arrays.asList("columnName")));
        put("mainTable", new ArrayList<>(Arrays.asList("a", "b")));

    }};
    DatabaseMetadata db = new DatabaseMetadata(tables);
    TranslateFromTwoVL twoVL = new TranslateFromTwoVL(db);


    // test tr_t, basic query
    @Test
    public void test1() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R as e");
        assertEquals(result, "SELECT * FROM R AS e");
    }

    // test tr_t, AND
    @Test
    void test2() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE Employees.Department = 'HR' AND Employees.Salary > 50000");
        assertEquals(result, "SELECT * FROM Employees WHERE Employees.Department = 'HR' AND Employees.Salary > 50000");
    }

    // test tr_t, SUM
    @Test
    void test3() {
        // Action
        String result = this.twoVL.translate("SELECT SUM(Salary) FROM Employees");
        assertEquals(result, "SELECT SUM(Salary) FROM Employees");
    }

    // test tr_t, JOIN
    @Test
    void test4() {
        // Action
        String result = this.twoVL.translate("SELECT Employees.FirstName, Departments.Name FROM Employees JOIN Departments ON Employees.DepartmentID = Departments.ID");
        assertEquals(result, "SELECT Employees.FirstName, Departments.Name FROM Employees JOIN Departments ON Employees.DepartmentID = Departments.ID");
    }

    // test tr_t, EXISTS
    @Test
    void test5() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees e WHERE EXISTS (SELECT EmployeeID FROM Departments d WHERE d.ID = e.DepartmentID AND d.Budget > 100000)");
        assertEquals(result, "SELECT * FROM Employees e WHERE EXISTS (SELECT EmployeeID FROM Departments d WHERE d.ID = e.DepartmentID AND d.Budget > 100000)");
    }

    // test tr_t, SELECT as select item
    @Test
    void test6() {
        // Action
        String result = this.twoVL.translate("SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE Orders.EmployeeID = Employees.ID) AS OrderCount FROM Employees");
        assertEquals(result, "SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE Orders.EmployeeID = Employees.ID) AS OrderCount FROM Employees");
    }

    // test tr_t, NOT EXISTS
    @Test
    void test7() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees e WHERE NOT EXISTS (SELECT EmployeeID FROM ProjectAssignments pa WHERE pa.EmployeeID = e.ID)");
        assertEquals(result, "SELECT * FROM Employees e WHERE NOT EXISTS (SELECT EmployeeID FROM ProjectAssignments pa WHERE pa.EmployeeID = e.ID)");
    }

    // test tr_t, ANY
    @Test
    void test14() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Products WHERE Price > ANY (SELECT Price FROM Products WHERE CategoryID = 1)");
        assertEquals(result, "SELECT * FROM Products WHERE Price > ANY(SELECT Price FROM Products WHERE CategoryID = 1)");
    }

    // test tr_t, IN
    @Test
    void test8() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE DepartmentID IN (SELECT ID FROM Departments WHERE Name != 'HR')");
        assertEquals(result, "SELECT * FROM Employees WHERE DepartmentID = ANY(SELECT ID FROM Departments WHERE (Name IS NULL OR NOT Name = 'HR'))");
    }

    // test tr_t, IN
    @Test
    void test9() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE ID IN (SELECT EmployeeID FROM Orders WHERE Quantity > 5) AND Salary > (SELECT AVG(Salary) FROM Employees)");
        assertEquals(result, "SELECT * FROM Employees WHERE ID = ANY(SELECT EmployeeID FROM Orders WHERE Quantity > 5) AND Salary > (SELECT AVG(Salary) FROM Employees)");
    }

    // test tr_t, INTERSECT
    @Test
    void test10() {
        // Action
        String result = this.twoVL.translate("SELECT FirstName FROM Employees INTERSECT SELECT Name FROM Departments");
        assertEquals(result, "SELECT FirstName FROM Employees INTERSECT SELECT Name FROM Departments");
    }

    // test tr_t, INTERSECT
    @Test
    void test17() {
        // Action
        String result = this.twoVL.translate("SELECT Column1, Column2 FROM Table1\n" +
                "INTERSECT\n" +
                "SELECT Column1, Column2 FROM Table2;");
        assertEquals(result, "SELECT Column1, Column2 FROM Table1 INTERSECT SELECT Column1, Column2 FROM Table2");
    }

    // test t != t'
    @Test
    void test11() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees e WHERE Salary > (SELECT AVG(Salary) FROM Employees WHERE DepartmentID != e.DepartmentID)");
        assertEquals(result, "SELECT * FROM Employees e WHERE Salary > (SELECT AVG(Salary) FROM Employees WHERE (DepartmentID IS NULL OR e.DepartmentID IS NULL OR NOT DepartmentID = e.DepartmentID))");
    }

    // test t != t'
    @Test
    void test16() {
        // Action
        String result = this.twoVL.translate( "SELECT R.a FROM R WHERE R.a != S.a OR NOT (R.a=1)");
        assertEquals(result, "SELECT R.a FROM R WHERE (R.a IS NULL OR S.a IS NULL OR NOT R.a = S.a) OR (R.a IS NULL OR NOT R.a = 1)");
    }

    // test t != t'
    @Test
    void test12() {
        // Action
        String result = this.twoVL.translate("SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE NOT (EmployeeID > Employees.ID)) AS NumberOfOrders FROM Employees");
        assertEquals(result, "SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE (EmployeeID IS NULL OR Employees.ID IS NULL OR NOT EmployeeID > Employees.ID)) AS NumberOfOrders FROM Employees");
    }

    // test WHERE (E) IS NULL
    @Test
    void test18() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE (SELECT ManagerID FROM Departments WHERE DepartmentID != Employees.DepartmentID) IS NULL;\n");
        assertEquals(result, "SELECT * FROM Employees WHERE (SELECT ManagerID FROM Departments WHERE (DepartmentID IS NULL OR Employees.DepartmentID IS NULL OR NOT DepartmentID = Employees.DepartmentID)) IS NULL");
    }

    // test NOT IN
    @Test
    void test13() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE ID NOT IN (SELECT EmployeeID FROM Orders WHERE OrderDate > '2023-01-01')");
        assertEquals(result, "SELECT * FROM Employees WHERE EXISTS (SELECT * FROM (SELECT EmployeeID FROM Orders WHERE OrderDate > '2023-01-01') AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (Employees.ID IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".EmployeeID IS NULL OR NOT Employees.ID = " + ExpressionUtils.SUB_QUERY_NAME + ".EmployeeID))");
    }

    // test NOT IN
    @Test
    void test15() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Events e1 WHERE EXISTS (SELECT ID FROM Events e2 WHERE e2.ID != e1.ID AND e2.StartDate <= e1.EndDate AND e2.EndDate >= e1.StartDate)");
        assertEquals(result, "SELECT * FROM Events e1 WHERE EXISTS (SELECT ID FROM Events e2 WHERE (e2.ID IS NULL OR e1.ID IS NULL OR NOT e2.ID = e1.ID) AND e2.StartDate <= e1.EndDate AND e2.EndDate >= e1.StartDate)");
    }

    // test tr_t, EXISTS
    @Test
    void test19() {
        // Action
        String result = this.twoVL.translate("SELECT r.A FROM r WHERE EXISTS (SELECT S.A FROM S WHERE S.A = r.A)");
        assertEquals(result, "SELECT r.A FROM r WHERE EXISTS (SELECT S.A FROM S WHERE S.A = r.A)");
    }

    // test NOT .. ANY
    @Test
    void test20() {
        // Action
        String result = this.twoVL.translate("SELECT *\n" +
                "FROM Products WHERE NOT(ProductID > ANY (SELECT r.A FROM r WHERE r.A != S.A OR NOT (r.A=1)));");
        assertEquals(result,  "SELECT * FROM Products WHERE NOT EXISTS (SELECT * FROM (SELECT r.A FROM r WHERE (r.A IS NULL OR S.A IS NULL OR NOT r.A = S.A) OR (r.A IS NULL OR NOT r.A = 1)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (Products.ProductID IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".A IS NULL OR NOT Products.ProductID > " + ExpressionUtils.SUB_QUERY_NAME + ".A))");
    }

    // test NOT ... OR
    @Test
    void test21() {
        // Action
        String result = this.twoVL.translate("SELECT R.a FROM R WHERE NOT(R.a != S.a OR NOT (R.a=1))");
        assertEquals(result, "SELECT R.a FROM R WHERE R.a = S.a AND (R.a = 1)");
    }

    // test JOIN
    @Test
    void test22() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeName, d.DepartmentInfo FROM Employees e JOIN (SELECT DepartmentID, CONCAT(DepartmentName, ' - ', Location) AS DepartmentInfo FROM Departments) d ON e.DepartmentID = d.DepartmentID");
        assertEquals(result, "SELECT e.EmployeeName, d.DepartmentInfo FROM Employees e JOIN (SELECT DepartmentID, CONCAT(DepartmentName, ' - ', Location) AS DepartmentInfo FROM Departments) d ON e.DepartmentID = d.DepartmentID");
    }

    // test ANY != (ARRAY...)
    @Test
    void test23() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM table_name WHERE column_name != ANY(ARRAY[value1, value2, value3])");
        assertEquals(result,  "SELECT * FROM table_name WHERE (column_name IS NULL OR NOT column_name = ANY(ARRAY[value1, value2, value3]))");
    }

    // test NOT IN
    @Test
    void test24() {
        // Action
        String result = this.twoVL.translate("SELECT R.a FROM R WHERE R.a NOT IN\n" +
                "( SELECT S.a FROM S )");
        assertEquals(result, "SELECT R.a FROM R WHERE EXISTS (SELECT * FROM (SELECT S.a FROM S) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (R.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT R.a = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    // test NOT ... =
    @Test
    void test25() {
        // Action
        String result = this.twoVL.translate("SELECT R.a FROM R WHERE NOT R.a = S.a");
        assertEquals(result, "SELECT R.a FROM R WHERE (R.a IS NULL OR S.a IS NULL OR NOT R.a = S.a)");
    }

    // test NOT EXISTS
    @Test
    void test26() {
        // Action
        String result = this.twoVL.translate("SELECT R.a FROM R WHERE NOT EXISTS\n" +
                "( SELECT S.a FROM S WHERE S.a=R.a )");
        assertEquals(result, "SELECT R.a FROM R WHERE NOT EXISTS (SELECT S.a FROM S WHERE S.a = R.a)");
    }

    // test DISTINCT
    @Test
    void test27() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT X.A FROM Z X, Z Y\n" +
                "WHERE X.A=Y.A");
        assertEquals(result, "SELECT DISTINCT X.A FROM Z X, Z Y WHERE X.A = Y.A");
    }

    // test SELECT in where
    @Test
    void test28() {
        // Action
        String result = this.twoVL.translate("SELECT c_nationkey, COUNT(c_custkey) FROM customer WHERE c_acctbal > (SELECT avg(c_acctbal) FROM customer WHERE c_acctbal > 0.0 AND c_custkey NOT IN (SELECT o_custkey FROM orders)) GROUP BY c_nationkey");
        assertEquals(result, "SELECT c_nationkey, COUNT(c_custkey) FROM customer WHERE c_acctbal > (SELECT avg(c_acctbal) FROM customer WHERE c_acctbal > 0.0 AND EXISTS (SELECT * FROM (SELECT o_custkey FROM orders) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (customer.c_custkey IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".o_custkey IS NULL OR NOT customer.c_custkey = " + ExpressionUtils.SUB_QUERY_NAME + ".o_custkey))) GROUP BY c_nationkey");
    }

    // test NOT true
    @Test
    void test29() {
        // Action
        String result = this.twoVL.translate("SELECT a FROM R WHERE a IS NOT true");
        assertEquals(result, "SELECT a FROM R WHERE a IS FALSE");
    }

    // test WHERE (E) IS NOT NULL
    @Test
    void test30() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (SELECT SomeColumn FROM AnotherTable WHERE SomeCondition != 1) IS NOT NULL");
        assertEquals(result, "SELECT * FROM MainTable WHERE (SELECT SomeColumn FROM AnotherTable WHERE (SomeCondition IS NULL OR NOT SomeCondition = 1)) IS NOT NULL");
    }

    // test parenthesed in where
    @Test
    void test31() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != (1, 2)");
        assertEquals(result, "SELECT * FROM MainTable WHERE (a IS NULL OR NOT a = 1) AND (b IS NULL OR NOT b = 2)");
    }

    // test parenthesed != ANY...
    @Test
    void test32() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ANY (SELECT a, b FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a, b FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT ((MainTable.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (MainTable.b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".b)))");
    }

    // test parenthesed != ALL ...
    @Test
    void test33() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ALL (SELECT a, b FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE EXISTS (SELECT * FROM (SELECT a, b FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (MainTable.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (MainTable.b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".b))");
    }

    // test ADDITION != ANY ...
    @Test
    void test34() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE a + b != ANY (SELECT a FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (MainTable.a + MainTable.b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a + MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    // test parenthesis
    @Test
    void test35() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a + b) != ANY (SELECT a FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (MainTable.a + MainTable.b IS NULL OR "+ ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a + MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    // test parenthesis
    @Test
    void test36() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a + b != ANY (SELECT a FROM sub))");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (MainTable.a + MainTable.b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a + MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    // test NOT IN (... INTERSECT ...)
    @Test
    void test37() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Table1 WHERE Column NOT IN (SELECT Column FROM Subquery1 INTERSECT SELECT c FROM Subquery2)");
        assertEquals(result, "SELECT * FROM Table1 WHERE EXISTS (SELECT * FROM (SELECT Column FROM Subquery1 INTERSECT SELECT c FROM Subquery2) AS " + ExpressionUtils.SUB_QUERY_NAME +" WHERE (Table1.Column IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".Column IS NULL OR NOT Table1.Column = " + ExpressionUtils.SUB_QUERY_NAME + ".Column))");

    }

    // test MULTIPLICATION != ...
    @Test
    void test38() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Table1 WHERE (a * b) != 1");
        assertEquals(result, "SELECT * FROM Table1 WHERE ((a * b) IS NULL OR NOT (a * b) = 1)");
    }

    // test parenthesed != ALL
    @Test
    void test39() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ALL (SELECT a, b FROM sub WHERE a != b INTERSECT SELECT a, b FROM sub WHERE a != b)");
        assertEquals(result, "SELECT * FROM MainTable WHERE EXISTS (SELECT * FROM (SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b) INTERSECT SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (MainTable.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (MainTable.b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".b))");
    }

    // test parenthesed != ANY
    @Test
    void test40() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ANY (SELECT a, b FROM sub WHERE a != b INTERSECT SELECT a, b FROM sub WHERE a != b)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b) INTERSECT SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT ((MainTable.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (MainTable.b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".b)))");
    }

    // test ANY with SUM
    @Test
    void test41() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE a != ANY(SELECT sum(a) as a from sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT sum(a) AS a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (MainTable.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    // test ANY (...) != ...
    @Test
    void test42() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE ANY (SELECT a FROM sub) != a + b");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (MainTable.a + MainTable.b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT MainTable.a + MainTable.b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    // test ADDITION != ANY(array)
    @Test
    void test43() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE a + b != ANY [1,2,3]");
        assertEquals(result, "SELECT * FROM MainTable WHERE (a + b IS NULL OR NOT a + b = ANY[1, 2, 3])");
    }

    // test q3 from article
    @Test
    void test44() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.a FROM R");
        assertEquals(result, "SELECT DISTINCT R.a FROM R");
    }

    // test ALL
    @Test
    void test45() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.a FROM R WHERE a = ALL (SELECT b FROM R)");
        assertEquals(result, "SELECT DISTINCT R.a FROM R WHERE a = ALL(SELECT b FROM R)");
    }

    // test ALL != ...
    @Test
    void test46() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.a FROM R WHERE ALL (SELECT b FROM R) != R.a");
        assertEquals(result, "SELECT DISTINCT R.a FROM R WHERE EXISTS (SELECT * FROM (SELECT b FROM R) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (R.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT R.a = " + ExpressionUtils.SUB_QUERY_NAME + ".b))");
    }

    // test NOT(... AND...)
    @Test
    void test47() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.a FROM R WHERE NOT (R.a = 1 AND R.b = 2)");
        assertEquals(result, "SELECT DISTINCT R.a FROM R WHERE (R.a IS NULL OR NOT R.a = 1) OR (R.b IS NULL OR NOT R.b = 2)");
    }

    // test DISTINCT
    @Test
    void test48() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT a FROM R WHERE NOT (NOT R.a = 1)");
        assertEquals(result, "SELECT DISTINCT a FROM R WHERE R.a = 1");
    }

    // test NOT FALSE
    @Test
    void test49() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT a FROM R JOIN C WHERE NOT FALSE");
        assertEquals(result, "SELECT DISTINCT a FROM R JOIN C WHERE TRUE");
    }

    // test E IS NOT NULL
    @Test
    void test50() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT a FROM R WHERE (SELECT b FROM D WHERE a != b) IS NOT NULL");
        assertEquals(result, "SELECT DISTINCT a FROM R WHERE (SELECT b FROM D WHERE (a IS NULL OR b IS NULL OR NOT a = b)) IS NOT NULL");
    }

    // test multiplication as select item
    @Test
    void test51() {
        // Action
        String result = this.twoVL.translate("SELECT a * b FROM R WHERE a IN (SELECT b FROM R WHERE a * b = 1)");
        assertEquals(result, "SELECT a * b FROM R WHERE a = ANY(SELECT b FROM R WHERE a * b = 1)");
    }

    // test select as select item
    @Test
    void test52() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE o.EmployeeID = e.EmployeeID) AS TotalOrders FROM Employees e");
        assertEquals(result, "SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE o.EmployeeID = e.EmployeeID) AS TotalOrders FROM Employees e");
    }

    // test complicated select as select item
    @Test
    void test53() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE o.EmployeeID != e.EmployeeID) AS TotalOrders FROM Employees e");
        assertEquals(result, "SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE (o.EmployeeID IS NULL OR e.EmployeeID IS NULL OR NOT o.EmployeeID = e.EmployeeID)) AS TotalOrders FROM Employees e");
    }

    // test sub queries
    @Test
    void test54() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeID, e.FirstName, e.LastName FROM Employees e WHERE (SELECT AVG(o.TotalAmount) FROM Orders o JOIN Employees emp ON o.EmployeeID = emp.EmployeeID WHERE emp.DepartmentID = e.DepartmentID) < ANY(SELECT o2.TotalAmount FROM Orders o2 WHERE o2.EmployeeID = e.EmployeeID)");
        assertEquals(result, "SELECT e.EmployeeID, e.FirstName, e.LastName FROM Employees e WHERE (SELECT AVG(o.TotalAmount) FROM Orders o JOIN Employees emp ON o.EmployeeID = emp.EmployeeID WHERE emp.DepartmentID = e.DepartmentID) < ANY(SELECT o2.TotalAmount FROM Orders o2 WHERE o2.EmployeeID = e.EmployeeID)");
    }

    // test parenthesed comparison
    @Test
    void test55() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM A WHERE (a,b,c) = (b,d,f)");
        assertEquals(result, "SELECT * FROM A WHERE (a, b, c) = (b, d, f)");
    }

    // test parenthesed comparison
    @Test
    void test56() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM A WHERE (a,b,c) != (b,d,f)");
        assertEquals(result, "SELECT * FROM A WHERE (a IS NULL OR b IS NULL OR NOT a = b) AND (b IS NULL OR d IS NULL OR NOT b = d) AND (c IS NULL OR f IS NULL OR NOT c = f)");
    }

    // test AS
    @Test
    void test57() {
        // Action
        String result = this.twoVL.translate("SELECT a as b FROM A WHERE (a,b,c) = (b,d,f)");
        assertEquals(result, "SELECT a AS b FROM A WHERE (a, b, c) = (b, d, f)");
    }

    // test SUM
    @Test
    void test58() {
        // Action
        String result = this.twoVL.translate("SELECT sum(a) FROM A");
        assertEquals(result, "SELECT sum(a) FROM A");
    }

    // test != ANY
    @Test
    void test59() {
        // Action
        String result = this.twoVL.translate("    SELECT A\n" +
                "    FROM R\n" +
                "    WHERE a != ANY(SELECT a FROM R WHERE a!=b)");
        assertEquals(result, "SELECT A FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM R WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (R.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT R.a = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    // test != ANY
    @Test
    void test60() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY(SELECT a AS b FROM R WHERE a!=c)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT a AS b FROM R WHERE (a IS NULL OR c IS NULL OR NOT a = c)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (R.a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT R.a = " + ExpressionUtils.SUB_QUERY_NAME + ".b))");
    }

    // test IN (array)
    @Test
    void test61() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a IN (ARRAY[1,2,3]);");
        assertEquals(result, "SELECT * FROM R WHERE a IN (ARRAY[1, 2, 3])");
    }

    // test NOT IN (array)
    @Test
    void test62() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a NOT IN (1,2,3);");
        assertEquals(result, "SELECT * FROM R WHERE a NOT IN (1, 2, 3)");
    }

    // test HAVING
    @Test
    void test63() {
        // Action
        String result = this.twoVL.translate("SELECT max(a) as S, b FROM R GROUP BY b HAVING max(a) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(a) AS S, b FROM R GROUP BY b) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.S > 10");
    }

    // test HAVING
    @Test
    void test64() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM (SELECT max(a) as S, b FROM R GROUP BY b) AS S WHERE S.S > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(a) AS S, b FROM R GROUP BY b) AS " + ExpressionUtils.SUB_QUERY_NAME+ " WHERE S.S > 10");
    }

    // test HAVING
    @Test
    void test65() {
        // Action
        String result = this.twoVL.translate("SELECT max(a), col1 FROM R GROUP BY b HAVING max(a) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(a) AS col2, col1 FROM R GROUP BY b) AS " + ExpressionUtils.SUB_QUERY_NAME+ " WHERE S.col2 > 10");
    }

    // test HAVING
    @Test
    void test66() {
        // Action
        String result = this.twoVL.translate("SELECT max(b), a FROM R GROUP BY a HAVING max(b) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(b) AS col1, a FROM R GROUP BY a) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 10");
    }

    // test HAVING
    @Test
    void test67() {
        // Action
        String result = this.twoVL.translate("SELECT max(b), col1, col2, max(c) FROM R GROUP BY a HAVING col1 > 10 AND max(c) > 2");
        assertEquals(result, "SELECT * FROM (SELECT max(b) AS col4, col1, col2, max(c) AS col3 FROM R GROUP BY a) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 10 AND S.col3 > 2");
    }

    // test HAVING
    @Test
    void test68() {
        // Action
        String result = this.twoVL.translate("SELECT productType, SUM(revenue) - SUM(cost) AS profit\n" +
                "FROM financials\n" +
                "GROUP BY productType\n" +
                "HAVING (SUM(revenue) - SUM(cost)) > 10000;");
        assertEquals(result, "SELECT * FROM (SELECT productType, SUM(revenue) - SUM(cost) AS profit FROM financials GROUP BY productType) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.profit > 10000");
    }

    // test HAVING
    @Test
    void test69() {
        // Action
        String result = this.twoVL.translate("SELECT storeLocation, COUNT(DISTINCT employeeId)\n" +
                "FROM sales\n" +
                "GROUP BY storeLocation\n" +
                "HAVING COUNT(DISTINCT employeeId) > 3;\n");
        assertEquals(result, "SELECT * FROM (SELECT storeLocation, COUNT(DISTINCT employeeId) AS col1 FROM sales GROUP BY storeLocation) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 3");
    }

    // test HAVING
    @Test
    void test70() {
        // Action
        String result = this.twoVL.translate("SELECT salesperson, COUNT(saleId)\n" +
                "FROM sales\n" +
                "GROUP BY salesperson\n" +
                "HAVING COUNT(saleId) BETWEEN 5 AND 10;");
        assertEquals(result, "SELECT * FROM (SELECT salesperson, COUNT(saleId) AS col1 FROM sales GROUP BY salesperson) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 BETWEEN 5 AND 10");
    }

    // test HAVING
    @Test
    void test71() {
        String result = this.twoVL.translate("SELECT max(b), a FROM R WHERE a != 1 GROUP BY a HAVING max(b) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(b) AS col1, a FROM R WHERE (a IS NULL OR NOT a = 1) GROUP BY a) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 10");
    }

    // test != ANY
    @Test
    void test72() {
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY (SELECT max(b) FROM C)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT max(b) AS col1 FROM C) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (R.a IS NULL OR S.col1 IS NULL OR NOT R.a = S.col1))");
    }

    // test != ANY
    @Test
    void test73() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY(SELECT * FROM D WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT D.b FROM D WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT (R.a IS NULL OR S.b IS NULL OR NOT R.a = S.b))");
    }

    // test parenthesed != ANY
    @Test
    void test74() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE (a, b) != ANY(SELECT * FROM R WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT R.a, R.b FROM R WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT ((R.a IS NULL OR S.a IS NULL OR NOT R.a = S.a) AND (R.b IS NULL OR S.b IS NULL OR NOT R.b = S.b)))");
    }

    // test parenthesed != ANY
    @Test
    void test75() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE (a, b) != ANY(SELECT * FROM C, D WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT C.a, D.b FROM C, D WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT ((R.a IS NULL OR S.a IS NULL OR NOT R.a = S.a) AND (R.b IS NULL OR S.b IS NULL OR NOT R.b = S.b)))");
    }

    // test parenthesed != ANY
    @Test
    void test76() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE (a, b) != ANY(SELECT * FROM R JOIN C ON R.a = C.a WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT R.a, R.b FROM R JOIN C ON R.a = C.a WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT ((R.a IS NULL OR S.a IS NULL OR NOT R.a = S.a) AND (R.b IS NULL OR S.b IS NULL OR NOT R.b = S.b)))");
    }

    // test != ANY
    @Test
    void test77() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY(SELECT * FROM L JOIN D ON L.b = D.b WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT L.b FROM L JOIN D ON L.b = D.b WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT (R.a IS NULL OR S.b IS NULL OR NOT R.a = S.b))");
    }

    // test != ANY
    @Test
    void test78() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R as D WHERE a != ANY(SELECT * FROM L AS E JOIN D ON L.b = D.b WHERE E.a != E.b)");
        assertEquals(result, "SELECT * FROM R AS D WHERE NOT EXISTS (SELECT * FROM (SELECT L.b FROM L AS E JOIN D ON L.b = D.b WHERE (E.a IS NULL OR E.b IS NULL OR NOT E.a = E.b)) AS S WHERE NOT (D.a IS NULL OR S.b IS NULL OR NOT D.a = S.b))");
    }
}

