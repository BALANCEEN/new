package com.example.demo.service;

import com.example.demo.pojo.Student;

import java.util.List;

public interface StudentService {
    public Student getStu(int id);
    public List<Student> getAll();
}
