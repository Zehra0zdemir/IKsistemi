package com.hrms;

import java.util.List;

import com.hrms.dao.AttendanceDAO;
import com.hrms.dao.DepartmentDAO;
import com.hrms.dao.EmployeeDAO;
import com.hrms.model.Attendance;
import com.hrms.model.Department;
import com.hrms.model.Employee;

public class TestKisi1 {
    public static void main(String[] args) {
        System.out.println("=== TEST BAŞLADI ===\n");
        
        DepartmentDAO deptDAO = new DepartmentDAO();
        List<Department> depts = deptDAO.findAll();
        System.out.println("✓ Departman sayısı: " + depts.size());
        
        EmployeeDAO empDAO = new EmployeeDAO();
        List<Employee> emps = empDAO.findAll();
        System.out.println("✓ Çalışan sayısı: " + emps.size());
        
        AttendanceDAO attDAO = new AttendanceDAO();
        List<Attendance> atts = attDAO.findToday();
        System.out.println("✓ Bugünkü devam: " + atts.size());
        
        System.out.println("\n=== TEST TAMAM ===");
    }
}
