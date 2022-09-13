package org.opennms.devjam2022.apiserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRole {
    String id;
    String role;
}
