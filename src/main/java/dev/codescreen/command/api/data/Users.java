package dev.codescreen.command.api.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Users {
    @Id
    private String userId;
    private String balance;
    private String currency;
}