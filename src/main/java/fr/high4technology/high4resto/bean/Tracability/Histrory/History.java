package fr.high4technology.high4resto.bean.Tracability.Histrory;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.high4technology.high4resto.bean.Tracability.Delevery.Delevery;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class History {
    @Id
    private String id;
    @Getter
    private String inside;
    @Getter
    private Delevery delevery;

}
