package TodoList;

import TodoList.datamodel.TodoData;
import TodoList.datamodel.TodoItems;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionID;
    @FXML
    private TextArea detailsID;
    @FXML
    private DatePicker datePickerID;

    public TodoItems processResult(){
        String shortDescription = shortDescriptionID.getText().trim();
        String details = detailsID.getText().trim();
        LocalDate date = datePickerID.getValue();

        TodoItems newItem = new TodoItems(shortDescription, details, date);
        TodoData.getInstance().addTodoItems(newItem);
        return newItem;
    }
}
