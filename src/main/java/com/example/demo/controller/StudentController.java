package com.example.demo.controller;

import com.example.demo.mapper.StudentMapper;
import com.example.demo.pojo.StuMap;
import com.example.demo.pojo.Student;
import com.example.demo.service.StudentService;
import com.github.pagehelper.PageHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentMapper studentMapper;

    private static final Logger logger=Logger.getLogger(StudentController.class);
    @RequestMapping("/stu/select/{id}")
    @ResponseBody
    public Student getStu(@PathVariable int id) {
        return studentService.getStu(id);
    }

//    @GetMapping(name="/stu/select1",produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public String getStu1(String callback) {
//        // 如果字符串不为空，需要支持jsonp调用
//
////        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue("1234");
////        mappingJacksonValue.setJsonpFunction(callback);
////        return mappingJacksonValue;
//        logger.info(callback);
//        logger.info("hhhhfhdsakkfjdlskafjlsda");
//        logger.info(callback==null);
//        return  callback+"("+111909+")";
//        /* return "hkj"; */
//    }

        @RequestMapping("/stu/selectAll")
        @ResponseBody
        public List<Student> getStu2() {
        PageHelper.startPage(0,2);
        return studentService.getAll();
    }

    @RequestMapping("/stu/selectA")
    @ResponseBody
    public List<StuMap> getStu1() {
        return studentMapper.selectMap(2
        );
    }
    @RequestMapping("/stu/selectA2")
    @ResponseBody
    public List<StuMap> getStu3() {
        return studentMapper.selectMap2();
    }
    @RequestMapping("/stu/selectA3")
    @ResponseBody
    public StuMap getStu4() {
        return studentMapper.selectMap3();
    }

    @RequestMapping("/login")
    @ResponseBody
    public boolean login(String name, String pwd) {
        if (name.equals("123")&&pwd.equals("123")){
            return true;
        }
        return false;
    }

    @RequestMapping("/index")
    public String aa(){
        return "aaa";
    }

    @RequestMapping("/")
    public String aaa(){
        return "index";
    }
}
