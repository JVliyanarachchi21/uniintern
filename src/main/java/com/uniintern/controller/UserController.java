package com.uniintern.controller;

import com.uniintern.dto.courseDTO;
import com.uniintern.service.courseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "api/v1/")
public class UserController {
    @Autowired
    private courseService courseservice;

    @GetMapping("/getcourse")
    public List<courseDTO> getCourse(){
        return courseservice.getAllCourse();
    }

    @PostMapping("/addcourse")
    public courseDTO saveCourse(@RequestBody courseDTO coursedto){
        return courseservice.saveCourse(coursedto);
    }

    @PutMapping("/updatecourse")
    public courseDTO updatecourse(@RequestBody courseDTO courseDTO){
        return courseservice.updatecourse(courseDTO);
    }

    @DeleteMapping("/deleteecourse")
    public  String deletecourse(@RequestBody courseDTO courseDTO){
        return courseservice.deletecourse(courseDTO);
    }




}
