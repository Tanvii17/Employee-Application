package com.employeeApplication.controller;

import com.employeeApplication.Entity.Employee;
import com.employeeApplication.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EmployeeController {

    Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private EmployeeService employeeService;
    private Employee employee;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    // to display list of employees
    @GetMapping("/")
    public String viewHomePage(Employee employee, Model model, @Param("keyword") String keyword) {

        return findPaginated(1, "employeeName", "asc", model);
    }


    @RequestMapping(path = {"/","/search"})
    public String home(Employee employee, Model model, String keyword) {
        if(keyword!=null) {
            List<Employee> list = employeeService.getByKeyword(keyword);
            model.addAttribute("listEmployees",list);
        }else {
            List<Employee> list = employeeService.getAllEmployees();
            model.addAttribute("listEmployees", list);}
        return "index";
    }


    // to add new record
    @GetMapping("/newEmployeeForm")
    public String showNewEmployeeForm(Model model) {
        // create Entity attribute to bind form data
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        logger.info("Employee form opened");
        return "new_employee";
    }

    // to save data
    @PostMapping("/employee")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        // save employee to database
        employeeService.saveEmployee(employee);
        logger.info("Employee details saved");
        return "redirect:/";
    }


    // To update details
    @GetMapping("/updateEmployee/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") long id, Model model) {

        // get employee from the service
        Employee employee = employeeService.getEmployeeById(id);

        // set employee as an Entity attribute to pre-populate the form
        model.addAttribute("employee", employee);
        logger.info("Employee details updated");
        return "update_employee";
    }


    // To delete record
    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable(value = "id") long id) {

        // call delete employee method
        this.employeeService.deleteEmployeeById(id);
        logger.info("Employee details deleted");
        return "redirect:/";
    }


    // pagination
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir, Model model) {
        int pageSize = 5;

        Page<Employee> page = employeeService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Employee> listEmployees = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listEmployees", listEmployees);
        return "index";
    }


}


