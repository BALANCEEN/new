package com.example.demo.service;

import com.example.demo.mapper.StudentMapper;
import com.example.demo.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentMapper studentMapper;
    @Override
    public Student getStu(int id) {
        return studentMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Student> getAll() {
        return studentMapper.selectAll();
    }
}
