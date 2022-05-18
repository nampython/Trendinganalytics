package com.sentiment.worker.models;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Tweet {

    String id;
    String timestamp;
    String tweet64;
    String location;
}

