package com.introstudio.minibank.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_organization")
public class Organization extends AuditModel {

    @Id
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "organization_id", columnDefinition = "uuid")
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @Size(min = 3, max = 255)
    private String address;

    @NotBlank
    @Email
    @Size(max = 40)
    private String email;

    @Size(max = 100)
    private String website;

    @Size(max = 100)
    private String pic;

    private int status;

}
