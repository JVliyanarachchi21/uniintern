package com.uniintern.service;

import com.uniintern.dto.courseDTO;
import com.uniintern.model.course;
import com.uniintern.repo.coursereepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class courseService {
    @Autowired
    private coursereepo coursereepo;

    @Autowired
    private ModelMapper modelMapper;

    public List<courseDTO> getAllCourse(){
        List<course>courseList =coursereepo.findAll();
        return  modelMapper.map(courseList, new TypeToken<List<courseDTO>>(){}.getType());

    }

    public courseDTO saveCourse(courseDTO courseDTO){
        coursereepo.save(modelMapper.map(courseDTO, course.class));
        return courseDTO;
    }

    public courseDTO updatecourse(courseDTO courseDTO){
        coursereepo.save(modelMapper.map(courseDTO, course.class));
        return courseDTO;
    }

    public String deletecourse (courseDTO courseDTO){
        coursereepo.delete(modelMapper.map(courseDTO, course.class));
        return "Course deleted";
    }




}
