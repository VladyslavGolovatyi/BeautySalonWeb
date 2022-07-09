package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private int id;
    private User worker;
    private User client;
    private String status;
    private String timeslot;
    private Service service;
}
