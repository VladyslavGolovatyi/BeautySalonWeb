package entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

   private String email;
   private String password;
   private String role;
   private String first_name;
   private String last_name;
   private String phone_number;
   private int moneyBalance;
   //only for workers
   private int rating;
   private List<Service> services;
   private List<String> workingDays;
   private List<String> responses;
   Map<LocalDateTime,Boolean> slots = new HashMap<>();

}
