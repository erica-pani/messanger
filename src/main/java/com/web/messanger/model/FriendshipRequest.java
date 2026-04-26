package com.web.messanger.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne private User sender;

  @ManyToOne private User receiver;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private RequestStatus requestStatus = RequestStatus.PENDING;

  @Builder.Default private LocalTime createdAt = LocalTime.now();
}
