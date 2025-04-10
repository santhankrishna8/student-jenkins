package com.student.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.student.student.model.Student;
import com.student.student.service.StudentService;

@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/student")
    public ResponseEntity<Student> addStudent(@RequestBody Student stud){
        Student student = studentService.addStudent(stud);
        if(student!=null){
            System.out.println(student);
            return new ResponseEntity<>(student,HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @GetMapping("/student")
    public ResponseEntity<List<Student>> getAllStudents(){
        List<Student> list = studentService.getAllStudents();
        if(list!=null){
            System.out.println(list);
            return new ResponseEntity<>(list,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/student/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("id") Long id){
        Student stud = studentService.getStudentById(id);
        if(stud != null){
            System.out.println(stud);
            return new ResponseEntity<>(stud,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/student/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") Long id,@RequestBody Student stud){
        Student student = studentService.updateStudent(id,stud);
        if(student!=null){
            System.out.println(student);
            return new ResponseEntity<>(student,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable("id") Long id){
        Student student = studentService.deleteStudent(id);
        if(student!=null){
            System.out.println(student);
            return new ResponseEntity<>(student,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    

}
