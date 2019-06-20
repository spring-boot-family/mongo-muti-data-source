package com.xinyan.mongo.repository.primary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "primary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrimaryObject {
    @Id
    private String id;

    private String name;
}
