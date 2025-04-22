package com.dev.pranay.user_passport_demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String passportNumber;
    private String nationality;

//    @OneToOne(mappedBy = "passport") // 👈 mappedBy = "passport" ties it back to the User entity
//    private User user;


    @CreationTimestamp
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;

    @PrePersist
    public void prePersist() {
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
        if (expiryDate == null) {
            expiryDate = issueDate.plusYears(10); // for example
        }
    }
}
