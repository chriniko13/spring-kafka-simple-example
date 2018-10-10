package com.chriniko.kafka.example.domain;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Person {
    private String _id;
    private int index;
    private UUID guid;
    private boolean isActive;
    private String balance;
    private String picture;
    private int age;
    private String eyeColor;
    private String name;
    private String gender;
    private String company;
    private String email;
    private String phone;
    private String address;
    private String about;
    private String registered;
    private double latitude;
    private double longitude;
    private List<String> tags;
    private List<Friend> friends;
    private String greeting;
    private String favoriteFruit;
}
