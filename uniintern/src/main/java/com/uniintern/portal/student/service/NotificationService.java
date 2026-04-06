package com.uniintern.portal.student.service;

import com.uniintern.portal.student.model.Notification;
import com.uniintern.portal.student.repository.NotificationRepository;
import com.uniintern.portal.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StudentRepository studentRepository;

    public void createNotification(Long studentId, String title, String message, String type) {
        Notification notification = new Notification(studentId, title, message, type);
        notificationRepository.save(notification);
    }

    public void broadcastNotification(String title, String message, String type) {
        // Fetch all student IDs from StudentRepository
        studentRepository.findAll().forEach(student -> {
            createNotification(student.getId(), title, message, type);
        });
    }

    public List<Notification> getNotificationsForStudent(Long studentId) {
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public long getUnreadCount(Long studentId) {
        return notificationRepository.findByStudentIdAndIsReadFalse(studentId).size();
    }

    public void markAllAsRead(Long studentId) {
        List<Notification> unread = notificationRepository.findByStudentIdAndIsReadFalse(studentId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
