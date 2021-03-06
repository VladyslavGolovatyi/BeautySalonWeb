package entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Appointment {
    private int id;
    private User worker;
    private User client;
    private String status;
    private String timeslot;
    private Service service;
    private boolean hasResponse;
}
