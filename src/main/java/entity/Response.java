package entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Response {
    private int id;
    private String message;
    private int rating;
    private String date;
    private User worker;
    private User client;
    private Service service;
}
