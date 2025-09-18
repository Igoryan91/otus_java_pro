package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {

    private long id;

    private String number;

    public PhoneDto(String number) {
        this.number = number;
    }
}
