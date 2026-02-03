package com.tradepulse.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "app_users") // 'user' is a reserved word in SQL, so we use 'app_users'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    // הקשר המיוחד: משתמש אחד -> הרבה מניות
    // Many-to-Many: כי אותה מניה (PLTR) יכולה להופיע אצל הרבה משתמשים
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_portfolio",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_symbol")
    )
    private Set<Asset> portfolio = new HashSet<>();

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}