package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="userregister")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "numele nu trebuie sa fie null")
    @Size(min = 4, max = 20)
    private String nume;
    @NotEmpty(message  = "prenumele nu trebuie sa fie null")
    @Size(min = 4, max = 20)
    private String prenume;
    @NotEmpty(message = "email-ul nu trebuie sa fie null")
    @Email
    private String email;
    @NotEmpty(message = "numarul de telefon nu trebuie sa fie null")
    @Size(min=10, max=10, message = "numarul de telefon trebuie sa aiba 10 cifre")
    private String phone;
    @NotEmpty(message = "facultatea nu trebuie sa fie null")
    private String facultate;
    @NotEmpty(message = "domeniul de studiu nu trebuie sa fie null")
    private String domeniuS;
    @NotEmpty(message = "specializarea nu trebuie sa fie null")
    private String specializare;
    @NotNull
    @Digits(integer=1, fraction=0, message = "anul de studiu trebuie sa contina doar o cifra")
    private int anStudiu;

}