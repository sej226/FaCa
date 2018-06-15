package com.faca.web.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String providerUserID;

    @Column(nullable = false, unique = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToMany
    private List<Feed> feeds;

    @OneToMany
    private List<Category> categories;

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public void addFeed(Feed feed) {
        this.feeds.add(feed);
    }

    public void deleteCategory(String categoryName) {
        for (int i = 0; i < feeds.size(); i++) {
            if (feeds.get(i).getCategory() != null && feeds.get(i).getCategory().equals(categoryName)) {
                feeds.get(i).setCategory(null);
            }
        }

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equals(categoryName)) {
                categories.remove(i);
                break;
            }
        }
    }
}
