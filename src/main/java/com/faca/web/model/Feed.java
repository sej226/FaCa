package com.faca.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String caption;

    @Lob //필드로 집적 매핑되며 객체가 생성될 때 로드되는 것이 아니라 실제 접근될 때 로드됨
    private String message;

    @Lob
    private String description;

    @Lob
    private String imageUrl;

    @Lob
    private String link;

    private Date upDatedTime;

    @OneToOne
    private Category category;

    @Builder  //모델 객체를 생성할 때 Builder를 자동 추가해줌
    public Feed(String name, String caption, String message, String description,
        String imageUrl, String link, Date upDatedTime, Category category) {
        this.name = name;
        this.caption = caption;
        this.message = message;
        this.description = description;
        this.imageUrl = imageUrl;
        this.link = link;
        this.upDatedTime = upDatedTime;
        this.category = category;
    }
}
