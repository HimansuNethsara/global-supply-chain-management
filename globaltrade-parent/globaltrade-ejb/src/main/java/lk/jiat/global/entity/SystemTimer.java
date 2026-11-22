package lk.jiat.global.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_timers")
@Getter
@Setter
@NoArgsConstructor
public class SystemTimer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timer_id")
    private Integer timerId;

    @NotBlank(message = "Timer name is required")
    @Size(max = 100, message = "Timer name must not exceed 100 characters")
    @Column(name = "timer_name", nullable = false, unique = true, length = 100)
    private String timerName;

    @NotBlank(message = "Timer type is required")
    @Column(name = "timer_type", nullable = false)
    private String timerType;

    @Size(max = 100, message = "Schedule expression must not exceed 100 characters")
    @Column(name = "schedule_expression", length = 100)
    private String scheduleExpression;

    @Column(name = "last_run")
    private LocalDateTime lastRun;

    @Column(name = "next_run")
    private LocalDateTime nextRun;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    public SystemTimer(String timerName, String timerType, String scheduleExpression) {
        this.timerName = timerName;
        this.timerType = timerType;
        this.scheduleExpression = scheduleExpression;
    }
}