package br.com.sidineisilva.todolist.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  @Column(nullable = false, name = "user_id")
  private UUID userId;

  @Column(nullable = false, name = "description")
  private String description;

  @Column(nullable = false, name = "title")
  private String title;

  @Column(nullable = false, name = "start_date")
  private LocalDateTime startDate;

  @Column(nullable = false, name = "end_date")
  private LocalDateTime endDate;

  @Column(nullable = false, name = "priority")
  private String priority;
  
  public void setTitle(String title) throws Exception{
    if(title.length() > 50){
      throw new Exception("O campo title deve ter no maximo 50 caracteres");
    }
    this.title = title;
  }
}
