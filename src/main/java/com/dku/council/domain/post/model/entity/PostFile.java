package com.dku.council.domain.post.model.entity;

import com.dku.council.global.base.BaseEntity;
import com.dku.council.infra.nhn.model.UploadedFile;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PostFile extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_file_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String fileId;

    private String fileName;


    public PostFile(String fileId, String fileName) {
        this.fileId = fileId;
        this.fileName = fileName;
    }

    public PostFile(UploadedFile file) {
        this(file.getFileId(), file.getOriginalName());
    }

    public void changePost(Post post) {
        if (this.post != null) {
            this.post.getFiles().remove(this);
        }

        this.post = post;
        this.post.getFiles().add(this);
    }
}
