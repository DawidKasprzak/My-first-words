package pl.kasprzak.dawid.myfirstwords.repository.dao;

import jakarta.persistence.*;
import lombok.Data;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "parents")
public class ParentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String mail;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<ChildEntity> children = new ArrayList<>();

    private String authority;

}
