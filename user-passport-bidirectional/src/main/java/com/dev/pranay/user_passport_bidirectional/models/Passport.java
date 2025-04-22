package com.dev.pranay.user_passport_bidirectional.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "passport")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String passportNumber;

    private String nationality;

    private LocalDateTime issueDate;

    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public void prePersist() {
         if(this.issueDate == null) {
             this.issueDate = LocalDateTime.now();
         }

         if(this.expiryDate == null) {
             this.expiryDate = this.issueDate.plusYears(10);
         }
    }
}
