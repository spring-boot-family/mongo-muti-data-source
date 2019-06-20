package com.xinyan.mongo.repository.secondary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "secondary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SecondaryObject {
    @Id
    private String id;

    private String name;
}
