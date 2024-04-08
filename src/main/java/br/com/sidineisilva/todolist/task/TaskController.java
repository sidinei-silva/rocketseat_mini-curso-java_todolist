package br.com.sidineisilva.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.sidineisilva.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    if (taskModel == null) {
      return ResponseEntity.badRequest().body("Invalid task model");
    }
    
    var idUser = (UUID) request.getAttribute("idUser");
    taskModel.setUserId(idUser);
    
    var currentDate = LocalDateTime.now();
    if(currentDate.isAfter(taskModel.getStartDate()) || currentDate.isAfter(taskModel.getEndDate())){
      return ResponseEntity.badRequest().body("A data de início / data de termino da tarefa não pode ser menor que a data atual");
    }
    
    if(taskModel.getStartDate().isAfter(taskModel.getEndDate())){
      return ResponseEntity.badRequest().body("A data de início da tarefa não pode ser maior que a data de término");
    }
    
    var taskCreated = this.taskRepository.save(taskModel);
    return ResponseEntity.ok().body(taskCreated);
  }

  @GetMapping
  public ResponseEntity<List<TaskModel>> list(HttpServletRequest request){
    var idUser = (UUID) request.getAttribute("idUser");
    var tasks = this.taskRepository.findByUserId(idUser);
    return ResponseEntity.ok().body(tasks);
  
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskModel,   HttpServletRequest request, @PathVariable UUID id) {
    var idUser = (UUID) request.getAttribute("idUser");
    
    var task = this.taskRepository.findById(id).orElse(null);
    
    if(task == null){
      return ResponseEntity.badRequest().body("Tarefa não encontrada");
    }
    
    if(!task.getUserId().equals(idUser)){
      return ResponseEntity.badRequest().body("Usuario não tem permissão para alterar essa tarefa");
    }

    Utils.copyNoNullProperties(taskModel, task);

    this.taskRepository.save(task);
    return ResponseEntity.ok().body(task);
  }

}
