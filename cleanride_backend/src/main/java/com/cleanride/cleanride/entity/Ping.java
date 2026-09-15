
package com.cleanride.cleanride.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity @Table(name = "ping")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ping { @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id; private String message; }