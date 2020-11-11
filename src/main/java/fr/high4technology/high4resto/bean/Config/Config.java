package fr.high4technology.high4resto.bean.Config;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Album.Album;
import fr.high4technology.high4resto.bean.Image.Image;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Config {
    @Id
    private String id;
    @Getter
    private String title;
    @Getter
    private Image logo;
    @Getter
    private String description;
    @Getter
    private Album banniere;
    @Getter
    private String mapApiKey;        
    @Getter
    private String payementApiKey;        
}