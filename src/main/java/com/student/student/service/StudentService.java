package com.student.student.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.student.student.model.Student;
import com.student.student.repository.StudentRepo;

@Service("studentService")
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    public Student addStudent(Student stud){
        return studentRepo.save(stud);
    }

    public List<Student> getAllStudents(){
        return studentRepo.findAll();
    }

    public Student getStudentById(Long id){
        return studentRepo.findById(id).orElse(null);
    }

    public Student updateStudent(Long id,Student stud){
        Student existingStudent = studentRepo.findById(id).orElse(null);
        existingStudent.setName(stud.getName());
        existingStudent.setDepartment(stud.getDepartment());
        existingStudent.setEmail(stud.getEmail());
        existingStudent.setPhoneNumber(stud.getPhoneNumber());
        return studentRepo.save(existingStudent);
    }

    public Student deleteStudent(Long id){
        Student existingStudent = studentRepo.findById(id).orElse(null);
        if(existingStudent != null){
            studentRepo.deleteById(id);
            return existingStudent;
        }
        return null;
    }

}
