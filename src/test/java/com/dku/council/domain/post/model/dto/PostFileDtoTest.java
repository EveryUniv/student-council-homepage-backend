package com.dku.council.domain.post.model.dto;

import com.dku.council.domain.post.model.entity.PostFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostFileDtoTest {
    @Test
    @DisplayName("URL Path가 잘 합쳐지는가?")
    public void buildingPathProperly() {
        // given
        PostFile postFile1 = new PostFile("fileId", "fileName");
        PostFile postFile2 = new PostFile("/fileId", "fileName");
        PostFile postFile3 = new PostFile("fileId/", "fileName");
        PostFile postFile4 = new PostFile("/fileId/", "fileName");

        // when
        PostFileDto dtoCase1 = new PostFileDto("http://base.com", postFile1);
        PostFileDto dtoCase2 = new PostFileDto("http://base.com", postFile2);
        PostFileDto dtoCase3 = new PostFileDto("http://base.com", postFile3);
        PostFileDto dtoCase4 = new PostFileDto("http://base.com", postFile4);
        PostFileDto dtoCase5 = new PostFileDto("http://base.com/", postFile1);
        PostFileDto dtoCase6 = new PostFileDto("http://base.com/", postFile2);
        PostFileDto dtoCase7 = new PostFileDto("http://base.com/", postFile3);
        PostFileDto dtoCase8 = new PostFileDto("http://base.com/", postFile4);

        // then
        assertThat(dtoCase1.getUrl()).isEqualTo("http://base.com/fileId");
        assertThat(dtoCase2.getUrl()).isEqualTo("http://base.com/fileId");
        assertThat(dtoCase3.getUrl()).isEqualTo("http://base.com/fileId/");
        assertThat(dtoCase4.getUrl()).isEqualTo("http://base.com/fileId/");
        assertThat(dtoCase5.getUrl()).isEqualTo("http://base.com/fileId");
        assertThat(dtoCase6.getUrl()).isEqualTo("http://base.com/fileId");
        assertThat(dtoCase7.getUrl()).isEqualTo("http://base.com/fileId/");
        assertThat(dtoCase8.getUrl()).isEqualTo("http://base.com/fileId/");
    }

}