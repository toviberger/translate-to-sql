package org.translateToSql;

import org.translateToSql.database.Database;
import org.translateToSql.twoVL.TwoVL;
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


    }};
    Database db = new Database(tables);
    TwoVL twoVL = new TwoVL(db);

    @Test
    public void test1() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees as e");
        assertEquals(result, "SELECT * FROM Employees AS e");
    }

    @Test
    void test2() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE Department = 'HR' AND Salary > 50000");
        assertEquals(result, "SELECT * FROM Employees WHERE Department = 'HR' AND Salary > 50000");
    }

    @Test
    void test3() {
        // Action
        String result = this.twoVL.translate("SELECT SUM(Salary) FROM Employees");
        assertEquals(result, "SELECT SUM(Salary) FROM Employees");
    }

    @Test
    void test4() {
        // Action
        String result = this.twoVL.translate("SELECT Employees.FirstName, Departments.Name FROM Employees JOIN Departments ON Employees.DepartmentID = Departments.ID");
        assertEquals(result, "SELECT Employees.FirstName, Departments.Name FROM Employees JOIN Departments ON Employees.DepartmentID = Departments.ID");
    }

    @Test
    void test5() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE DepartmentID IN (SELECT ID FROM Departments WHERE Name != 'HR')");
        assertEquals(result, "SELECT * FROM Employees WHERE DepartmentID = ANY(SELECT ID FROM Departments WHERE (Name IS NULL OR NOT Name = 'HR'))");
    }

    @Test
    void test6() {
        // Action
        String result = this.twoVL.translate("SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE Orders.EmployeeID = Employees.ID) AS OrderCount FROM Employees");
        assertEquals(result, "SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE Orders.EmployeeID = Employees.ID) AS OrderCount FROM Employees");
    }

    @Test
    void test7() {
        // Action
        String result = this.twoVL.translate("SELECT FirstName FROM Employees INTERSECT SELECT Name FROM Departments");
        assertEquals(result, "SELECT FirstName FROM Employees INTERSECT SELECT Name FROM Departments");
    }

    @Test
    void test8() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees e WHERE EXISTS (SELECT 1 FROM Departments d WHERE d.ID = e.DepartmentID AND d.Budget > 100000)");
        assertEquals(result, "SELECT * FROM Employees e WHERE EXISTS (SELECT 1 FROM Departments d WHERE d.ID = e.DepartmentID AND d.Budget > 100000)");
    }

    @Test
    void test9() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees e WHERE NOT EXISTS (SELECT 1 FROM ProjectAssignments pa WHERE pa.EmployeeID = e.ID)");
        assertEquals(result, "SELECT * FROM Employees e WHERE NOT EXISTS (SELECT 1 FROM ProjectAssignments pa WHERE pa.EmployeeID = e.ID)");
    }

    @Test
    void test10() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees e WHERE Salary > (SELECT AVG(Salary) FROM Employees WHERE DepartmentID != e.DepartmentID)");
        assertEquals(result, "SELECT * FROM Employees e WHERE Salary > (SELECT AVG(Salary) FROM Employees WHERE (DepartmentID IS NULL OR e.DepartmentID IS NULL OR NOT DepartmentID = e.DepartmentID))");
    }

    @Test
    void test11() {
        // Action
        String result = this.twoVL.translate("SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE NOT (EmployeeID > Employees.ID)) AS NumberOfOrders FROM Employees");
        assertEquals(result, "SELECT FirstName, (SELECT COUNT(*) FROM Orders WHERE (EmployeeID IS NULL OR Employees.ID IS NULL OR NOT EmployeeID > Employees.ID)) AS NumberOfOrders FROM Employees");
    }

    @Test
    void test12() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE ID IN (SELECT EmployeeID FROM Orders WHERE Quantity > 5) AND Salary > (SELECT AVG(Salary) FROM Employees)");
        assertEquals(result, "SELECT * FROM Employees WHERE ID = ANY(SELECT EmployeeID FROM Orders WHERE Quantity > 5) AND Salary > (SELECT AVG(Salary) FROM Employees)");
    }

    @Test
    void test13() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE ID NOT IN (SELECT EmployeeID FROM Orders WHERE OrderDate > '2023-01-01')");
        assertEquals(result, "SELECT * FROM Employees WHERE NOT EXISTS (SELECT * FROM (SELECT EmployeeID FROM Orders WHERE OrderDate > '2023-01-01') AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (ID IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".EmployeeID IS NULL OR NOT ID = " + ExpressionUtils.SUB_QUERY_NAME + ".EmployeeID))");
    }

    @Test
    void test14() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Products WHERE Price > ANY (SELECT Price FROM Products WHERE CategoryID = 1)");
        assertEquals(result, "SELECT * FROM Products WHERE Price > ANY(SELECT Price FROM Products WHERE CategoryID = 1)");
    }

    @Test
    void test15() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Events e1 WHERE EXISTS (SELECT 1 FROM Events e2 WHERE e2.ID != e1.ID AND e2.StartDate <= e1.EndDate AND e2.EndDate >= e1.StartDate)");

        // Assertion
        // Further assertions depending on the expected outcome of the translation
    }


    @Test
    void test16() {
        // Action
        String result = this.twoVL.translate( "SELECT R.A FROM R WHERE R.A != S.A OR NOT (R.A=1)");
        assertEquals(result, "SELECT R.A FROM R WHERE (R.A IS NULL OR S.A IS NULL OR NOT R.A = S.A) OR (R.A IS NULL OR NOT R.A = 1)");
    }

    @Test
    void test17() {
        // Action
        String result = this.twoVL.translate("SELECT Column1, Column2 FROM Table1\n" +
                "INTERSECT\n" +
                "SELECT Column1, Column2 FROM Table2;");
        assertEquals(result, "SELECT Column1, Column2 FROM Table1 INTERSECT SELECT Column1, Column2 FROM Table2");
    }

    @Test
    void test18() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Employees WHERE (SELECT ManagerID FROM Departments WHERE DepartmentID != Employees.DepartmentID) IS NULL;\n");
        assertEquals(result, "SELECT * FROM Employees WHERE (SELECT ManagerID FROM Departments WHERE (DepartmentID IS NULL OR Employees.DepartmentID IS NULL OR NOT DepartmentID = Employees.DepartmentID)) IS NULL");
    }

    @Test
    void test19() {
        // Action
        String result = this.twoVL.translate("SELECT R.A FROM R WHERE EXISTS (SELECT S.A FROM S WHERE S.A = R.A)");
        assertEquals(result, "SELECT R.A FROM R WHERE EXISTS (SELECT S.A FROM S WHERE S.A = R.A)");
    }

    @Test
    void test20() {
        // Action
        String result = this.twoVL.translate("SELECT *\n" +
                "FROM Products WHERE NOT(ProductID > ANY (SELECT R.A FROM R WHERE R.A != S.A OR NOT (R.A=1)));");
        assertEquals(result,  "SELECT * FROM Products WHERE NOT EXISTS (SELECT * FROM (SELECT R.A FROM R WHERE (R.A IS NULL OR S.A IS NULL OR NOT R.A = S.A) OR (R.A IS NULL OR NOT R.A = 1)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (ProductID IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".A IS NULL OR NOT ProductID > " + ExpressionUtils.SUB_QUERY_NAME + ".A))");
    }

    @Test
    void test21() {
        // Action
        String result = this.twoVL.translate("SELECT R.A FROM R WHERE NOT( R.A != S.A OR NOT (R.A=1))");
        assertEquals(result, "SELECT R.A FROM R WHERE R.A = S.A AND (R.A = 1)");
    }

    @Test
    void test22() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeName, d.DepartmentInfo FROM Employees e JOIN (SELECT DepartmentID, CONCAT(DepartmentName, ' - ', Location) AS DepartmentInfo FROM Departments) d ON e.DepartmentID = d.DepartmentID");
        assertEquals(result, "SELECT e.EmployeeName, d.DepartmentInfo FROM Employees e JOIN (SELECT DepartmentID, CONCAT(DepartmentName, ' - ', Location) AS DepartmentInfo FROM Departments) d ON e.DepartmentID = d.DepartmentID");
    }

    @Test
    void test23() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM table_name WHERE column_name != ANY(ARRAY[value1, value2, value3])");
        assertEquals(result,  "SELECT * FROM table_name WHERE (column_name IS NULL OR NOT column_name = ANY(ARRAY[value1, value2, value3]))");
    }

    @Test
    void test24() {
        // Action
        String result = this.twoVL.translate("SELECT R.A FROM R WHERE R.A NOT IN\n" +
                "( SELECT S.A FROM S )");
        assertEquals(result, "SELECT R.A FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT S.A FROM S) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (R.A IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".A IS NULL OR NOT R.A = " + ExpressionUtils.SUB_QUERY_NAME + ".A))");
    }

    @Test
    void test25() {
        // Action
        String result = this.twoVL.translate("SELECT R.A FROM R WHERE NOT R.A = S.A");
        assertEquals(result, "SELECT R.A FROM R WHERE (R.A IS NULL OR S.A IS NULL OR NOT R.A = S.A)");
    }

    @Test
    void test26() {
        // Action
        String result = this.twoVL.translate("SELECT R.A FROM R WHERE NOT EXISTS\n" +
                "( SELECT S.A FROM S WHERE S.A=R.A )");
        assertEquals(result, "SELECT R.A FROM R WHERE NOT EXISTS (SELECT S.A FROM S WHERE S.A = R.A)");
    }

    @Test
    void test27() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT X.A FROM R X, R Y\n" +
                "WHERE X.A=Y.A");
        assertEquals(result, "SELECT DISTINCT X.A FROM R X, R Y WHERE X.A = Y.A");
    }

    @Test
    void test28() {
        // Action
        String result = this.twoVL.translate("SELECT c_nationkey, COUNT(c_custkey) FROM customer WHERE c_acctbal > (SELECT avg(c_acctbal) FROM customer WHERE c_acctbal > 0.0 AND c_custkey NOT IN (SELECT o_custkey FROM orders)) GROUP BY c_nationkey");
        assertEquals(result, "SELECT c_nationkey, COUNT(c_custkey) FROM customer WHERE c_acctbal > (SELECT avg(c_acctbal) FROM customer WHERE c_acctbal > 0.0 AND NOT EXISTS (SELECT * FROM (SELECT o_custkey FROM orders) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (c_custkey IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".o_custkey IS NULL OR NOT c_custkey = " + ExpressionUtils.SUB_QUERY_NAME + ".o_custkey))) GROUP BY c_nationkey");
    }

    @Test
    void test29() {
        // Action
        String result = this.twoVL.translate("SELECT A FROM R WHERE A IS NOT true");
        assertEquals(result, "SELECT A FROM R WHERE A IS FALSE");
    }

    @Test
    void test30() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (SELECT SomeColumn FROM AnotherTable WHERE SomeCondition != 1) IS NOT NULL");
        assertEquals(result, "SELECT * FROM MainTable WHERE (SELECT SomeColumn FROM AnotherTable WHERE (SomeCondition IS NULL OR NOT SomeCondition = 1)) IS NOT NULL");
    }

    @Test
    void test31() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != (1, 2)");
        assertEquals(result, "SELECT * FROM MainTable WHERE (a IS NULL OR NOT a = 1) AND (b IS NULL OR NOT b = 2)");
    }

    @Test
    void test32() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ANY (SELECT a, b FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a, b FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT ((a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT b = " + ExpressionUtils.SUB_QUERY_NAME + ".b)))");
    }

    @Test
    void test33() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ALL (SELECT a, b FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE EXISTS (SELECT * FROM (SELECT a, b FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT b = " + ExpressionUtils.SUB_QUERY_NAME + ".b))");
    }

    @Test
    void test34() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE a + b != ANY (SELECT a FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a + b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a + b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    @Test
    void test35() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a + b) != ANY (SELECT a FROM sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a + b IS NULL OR "+ ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a + b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    @Test
    void test36() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a + b != ANY (SELECT a FROM sub))");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a + b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a + b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    @Test
    void test37() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Table1 WHERE Column NOT IN (SELECT Column FROM Subquery1 INTERSECT SELECT c FROM Subquery2)");
        assertEquals(result, "SELECT * FROM Table1 WHERE NOT EXISTS (SELECT * FROM (SELECT Column FROM Subquery1 INTERSECT SELECT c FROM Subquery2) AS " + ExpressionUtils.SUB_QUERY_NAME +" WHERE NOT (Column IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".Column IS NULL OR NOT Column = " + ExpressionUtils.SUB_QUERY_NAME + ".Column))");

    }

    @Test
    void test38() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM Table1 WHERE (a + b) != 1");
        assertEquals(result, "SELECT * FROM Table1 WHERE ((a + b) IS NULL OR NOT (a + b) = 1)");
    }

    @Test
    void test39() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ALL (SELECT a, b FROM sub WHERE a != b INTERSECT SELECT a, b FROM sub WHERE a != b)");
        assertEquals(result, "SELECT * FROM MainTable WHERE EXISTS (SELECT * FROM (SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b) INTERSECT SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT b = " + ExpressionUtils.SUB_QUERY_NAME + ".b))");
    }

    @Test
    void test40() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE (a, b) != ANY (SELECT a, b FROM sub WHERE a != b INTERSECT SELECT a, b FROM sub WHERE a != b)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b) INTERSECT SELECT a, b FROM sub WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT ((a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a = " + ExpressionUtils.SUB_QUERY_NAME + ".a) AND (b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT b = " + ExpressionUtils.SUB_QUERY_NAME + ".b)))");
    }

    @Test
    void test41() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE a != ANY(SELECT sum(a) as a from sub)");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT sum(a) AS a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    @Test
    void test42() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE ANY (SELECT a FROM sub) != a + b");
        assertEquals(result, "SELECT * FROM MainTable WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM sub) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a + b IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a + b = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    @Test
    void test43() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM MainTable WHERE a + b != ANY [1,2,3]");
        assertEquals(result, "SELECT * FROM MainTable WHERE (a + b IS NULL OR NOT a + b = ANY[1, 2, 3])");
    }

    @Test
    void test44() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.A FROM R");
        assertEquals(result, "SELECT DISTINCT R.A FROM R");
    }

    @Test
    void test45() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.A FROM R WHERE A = ALL (SELECT B FROM R)");
        assertEquals(result, "SELECT DISTINCT R.A FROM R WHERE A = ALL(SELECT B FROM R)");
    }

    @Test
    void test46() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.A FROM R WHERE ALL (SELECT B FROM R) != R.A");
        assertEquals(result, "SELECT DISTINCT R.A FROM R WHERE EXISTS (SELECT * FROM (SELECT B FROM R) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE (R.A IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".B IS NULL OR NOT R.A = " + ExpressionUtils.SUB_QUERY_NAME + ".B))");
    }

    @Test
    void test47() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT R.A FROM R WHERE NOT (R.A = 1 AND R.B = 2)");
        assertEquals(result, "SELECT DISTINCT R.A FROM R WHERE (R.A IS NULL OR NOT R.A = 1) OR (R.B IS NULL OR NOT R.B = 2)");
    }

    @Test
    void test48() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT A FROM R WHERE NOT (NOT R.A = 1)");
        assertEquals(result, "SELECT DISTINCT A FROM R WHERE R.A = 1");
    }

    @Test
    void test49() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT A FROM R JOIN C WHERE NOT FALSE");
        assertEquals(result, "SELECT DISTINCT A FROM R JOIN C WHERE TRUE");
    }

    @Test
    void test50() {
        // Action
        String result = this.twoVL.translate("SELECT DISTINCT A FROM R WHERE (SELECT B FROM D WHERE A != B) IS NOT NULL");
        assertEquals(result, "SELECT DISTINCT A FROM R WHERE (SELECT B FROM D WHERE (A IS NULL OR B IS NULL OR NOT A = B)) IS NOT NULL");
    }

    @Test
    void test51() {
        // Action
        String result = this.twoVL.translate("SELECT a * b FROM R WHERE a IN (SELECT b FROM R WHERE a * b = 1)");
        assertEquals(result, "SELECT a * b FROM R WHERE a = ANY(SELECT b FROM R WHERE a * b = 1)");
    }

    @Test
    void test52() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE o.EmployeeID = e.EmployeeID) AS TotalOrders FROM Employees e");
        assertEquals(result, "SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE o.EmployeeID = e.EmployeeID) AS TotalOrders FROM Employees e");
    }

    @Test
    void test53() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE o.EmployeeID != e.EmployeeID) AS TotalOrders FROM Employees e");
        assertEquals(result, "SELECT e.EmployeeID, e.FirstName, e.LastName, (SELECT COUNT(*) FROM Orders o WHERE (o.EmployeeID IS NULL OR e.EmployeeID IS NULL OR NOT o.EmployeeID = e.EmployeeID)) AS TotalOrders FROM Employees e");
    }

    @Test
    void test54() {
        // Action
        String result = this.twoVL.translate("SELECT e.EmployeeID, e.FirstName, e.LastName FROM Employees e WHERE (SELECT AVG(o.TotalAmount) FROM Orders o JOIN Employees emp ON o.EmployeeID = emp.EmployeeID WHERE emp.DepartmentID = e.DepartmentID) < ANY(SELECT o2.TotalAmount FROM Orders o2 WHERE o2.EmployeeID = e.EmployeeID)");
        assertEquals(result, "SELECT e.EmployeeID, e.FirstName, e.LastName FROM Employees e WHERE (SELECT AVG(o.TotalAmount) FROM Orders o JOIN Employees emp ON o.EmployeeID = emp.EmployeeID WHERE emp.DepartmentID = e.DepartmentID) < ANY(SELECT o2.TotalAmount FROM Orders o2 WHERE o2.EmployeeID = e.EmployeeID)");
    }

    @Test
    void test55() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM A WHERE (a,b,c) = (b,d,f)");
        assertEquals(result, "SELECT * FROM A WHERE (a, b, c) = (b, d, f)");
    }

    @Test
    void test56() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM A WHERE (a,b,c) != (b,d,f)");
        assertEquals(result, "SELECT * FROM A WHERE (a IS NULL OR b IS NULL OR NOT a = b) AND (b IS NULL OR d IS NULL OR NOT b = d) AND (c IS NULL OR f IS NULL OR NOT c = f)");
    }

    @Test
    void test57() {
        // Action
        String result = this.twoVL.translate("SELECT a as b FROM A WHERE (a,b,c) = (b,d,f)");
        assertEquals(result, "SELECT a AS b FROM A WHERE (a, b, c) = (b, d, f)");
    }

    @Test
    void test58() {
        // Action
        String result = this.twoVL.translate("SELECT sum(a) FROM A");
        assertEquals(result, "SELECT sum(a) FROM A");
    }

    @Test
    void test59() {
        // Action
        String result = this.twoVL.translate("    SELECT A\n" +
                "    FROM R\n" +
                "    WHERE a != ANY(SELECT a FROM R WHERE a!=b)");
        assertEquals(result, "SELECT A FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT a FROM R WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".a IS NULL OR NOT a = " + ExpressionUtils.SUB_QUERY_NAME + ".a))");
    }

    @Test
    void test60() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY(SELECT a AS b FROM R WHERE a!=c)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT a AS b FROM R WHERE (a IS NULL OR c IS NULL OR NOT a = c)) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a IS NULL OR " + ExpressionUtils.SUB_QUERY_NAME + ".b IS NULL OR NOT a = " + ExpressionUtils.SUB_QUERY_NAME + ".b))");
    }

    @Test
    void test61() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a IN (ARRAY[1,2,3]);");
        assertEquals(result, "SELECT * FROM R WHERE a IN (ARRAY[1, 2, 3])");
    }

    @Test
    void test62() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a NOT IN (1,2,3);");
        assertEquals(result, "SELECT * FROM R WHERE a NOT IN (1, 2, 3)");
    }

    @Test
    void test63() {
        // Action
        String result = this.twoVL.translate("SELECT max(a) as S, b FROM R GROUP BY b HAVING max(a) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(a) AS S, b FROM R GROUP BY b) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.S > 10");
    }

    @Test
    void test64() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM (SELECT max(a) as S, b FROM R GROUP BY b) AS S WHERE S.S > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(a) AS S, b FROM R GROUP BY b) AS " + ExpressionUtils.SUB_QUERY_NAME+ " WHERE S.S > 10");
    }

    @Test
    void test65() {
        // Action
        String result = this.twoVL.translate("SELECT max(a), col1 FROM R GROUP BY b HAVING max(a) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(a) AS col2, col1 FROM R GROUP BY b) AS " + ExpressionUtils.SUB_QUERY_NAME+ " WHERE S.col2 > 10");
    }

    @Test
    void test66() {
        // Action
        String result = this.twoVL.translate("SELECT max(b), a FROM R GROUP BY a HAVING max(b) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(b) AS col1, a FROM R GROUP BY a) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 10");
    }

    @Test
    void test67() {
        // Action
        String result = this.twoVL.translate("SELECT max(b), col1, col2, max(c) FROM R GROUP BY a HAVING col1 > 10 AND max(c) > 2");
        assertEquals(result, "SELECT * FROM (SELECT max(b) AS col4, col1, col2, max(c) AS col3 FROM R GROUP BY a) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 10 AND S.col3 > 2");
    }

    @Test
    void test68() {
        // Action
        String result = this.twoVL.translate("SELECT productType, SUM(revenue) - SUM(cost) AS profit\n" +
                "FROM financials\n" +
                "GROUP BY productType\n" +
                "HAVING (SUM(revenue) - SUM(cost)) > 10000;");
        assertEquals(result, "SELECT * FROM (SELECT productType, SUM(revenue) - SUM(cost) AS profit FROM financials GROUP BY productType) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.profit > 10000");
    }

    @Test
    void test69() {
        // Action
        String result = this.twoVL.translate("SELECT storeLocation, COUNT(DISTINCT employeeId)\n" +
                "FROM sales\n" +
                "GROUP BY storeLocation\n" +
                "HAVING COUNT(DISTINCT employeeId) > 3;\n");
        assertEquals(result, "SELECT * FROM (SELECT storeLocation, COUNT(DISTINCT employeeId) AS col1 FROM sales GROUP BY storeLocation) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 3");
    }

    @Test
    void test70() {
        // Action
        String result = this.twoVL.translate("SELECT salesperson, COUNT(saleId)\n" +
                "FROM sales\n" +
                "GROUP BY salesperson\n" +
                "HAVING COUNT(saleId) BETWEEN 5 AND 10;");
        assertEquals(result, "SELECT * FROM (SELECT salesperson, COUNT(saleId) AS col1 FROM sales GROUP BY salesperson) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 BETWEEN 5 AND 10");
    }

    @Test
    void test71() {
        String result = this.twoVL.translate("SELECT max(b), a FROM R WHERE a != 1 GROUP BY a HAVING max(b) > 10");
        assertEquals(result, "SELECT * FROM (SELECT max(b) AS col1, a FROM R WHERE (a IS NULL OR NOT a = 1) GROUP BY a) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE S.col1 > 10");
    }

    @Test
    void test72() {
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY (SELECT max(b) FROM C)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT max(b) AS col1 FROM C) AS " + ExpressionUtils.SUB_QUERY_NAME + " WHERE NOT (a IS NULL OR S.col1 IS NULL OR NOT a = S.col1))");
    }


    @Test
    void test73() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY(SELECT * FROM D WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT D.b FROM D WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT (a IS NULL OR S.b IS NULL OR NOT a = S.b))");
    }

    @Test
    void test74() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE (a, b) != ANY(SELECT * FROM R WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT R.a, R.b FROM R WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT ((a IS NULL OR S.a IS NULL OR NOT a = S.a) AND (b IS NULL OR S.b IS NULL OR NOT b = S.b)))");
    }

    @Test
    void test75() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE (a, b) != ANY(SELECT * FROM C, D WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT C.a, D.b FROM C, D WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT ((a IS NULL OR S.a IS NULL OR NOT a = S.a) AND (b IS NULL OR S.b IS NULL OR NOT b = S.b)))");
    }

    @Test
    void test76() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE (a, b) != ANY(SELECT * FROM R JOIN C ON R.a = C.a WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT R.a, R.b FROM R JOIN C ON R.a = C.a WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT ((a IS NULL OR S.a IS NULL OR NOT a = S.a) AND (b IS NULL OR S.b IS NULL OR NOT b = S.b)))");
    }

    @Test
    void test77() {
        // Action
        String result = this.twoVL.translate("SELECT * FROM R WHERE a != ANY(SELECT * FROM L JOIN D ON L.b = D.b WHERE a != b)");
        assertEquals(result, "SELECT * FROM R WHERE NOT EXISTS (SELECT * FROM (SELECT L.b FROM L JOIN D ON L.b = D.b WHERE (a IS NULL OR b IS NULL OR NOT a = b)) AS S WHERE NOT (a IS NULL OR S.b IS NULL OR NOT a = S.b))");
    }
}

