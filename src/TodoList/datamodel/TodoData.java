package TodoList.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "TodoListItem.txt";

    private ObservableList<TodoItems> todoItems;
    private DateTimeFormatter formatter;

    public static TodoData getInstance(){
        return instance;
    }

    private TodoData(){
        formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    }

    public ObservableList<TodoItems> getTodoItems() {
        return todoItems;
    }

    public void addTodoItems(TodoItems item){
        todoItems.add(item);
    }

    public void deleteTodoItem(TodoItems item){
        todoItems.remove(item);
    }

//    public void setTodoItems(List<TodoItems> todoItems) {
//        this.todoItems = todoItems;
//    }

    public void loadTodoItems() throws IOException {
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader bf = Files.newBufferedReader(path);

        String input;
        try{
            while ((input = bf.readLine()) != null){
                String[] itemPieces = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);
                TodoItems todoItem = new TodoItems(shortDescription, details, date);
                todoItems.add(todoItem);
            }
        } finally {
            if (bf != null){
                bf.close();
            }
        }
    }

    public void storeItem() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<TodoItems> i = todoItems.iterator();
            while(i.hasNext()){
                TodoItems item = i.next();
                bw.write(String.format("%s\t%s\t%s", item.getShortDescription(), item.getDetails(),
                        item.getDeadline().format(formatter)));
                bw.newLine();
            }
        } finally {
            if(bw != null){
                bw.close();
            }
        }
    }
}
