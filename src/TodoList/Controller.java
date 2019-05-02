package TodoList;

import TodoList.datamodel.TodoData;
import TodoList.datamodel.TodoItems;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    private List<TodoItems> todoItems;

    @FXML
    private ListView<TodoItems> listViewID;

    @FXML
    private TextArea itemDetailTextArea;

    @FXML
    private Label dueDateLabel;

    @FXML
    private BorderPane mainBorderPaneID;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<TodoItems> filteredList;
    private Predicate<TodoItems> wantAll;
    private Predicate<TodoItems> wantToday;

    public void initialize(){
//        TodoItems item1 = new TodoItems("Practice Java", "Practice Java using IntelliJ.",
//                LocalDate.of(2019, Month.MARCH, 1));
//        TodoItems item2 = new TodoItems("Practice Python", "Practice Python using VS Code.",
//                LocalDate.of(2019, Month.APRIL, 30));
//        TodoItems item3 = new TodoItems("Running", "Go out and have some exercises",
//                LocalDate.of(2019, Month.MARCH, 1));
//
//        todoItems = new ArrayList<TodoItems>();
//        todoItems.add(item1);
//        todoItems.add(item2);
//        todoItems.add(item3);
//
//        TodoData.getInstance().setTodoItems(todoItems);

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TodoItems items = listViewID.getSelectionModel().getSelectedItem();
                deleteItem(items);
            }
        });

        listContextMenu.getItems().setAll(deleteMenuItem);

        listViewID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItems>() {
            @Override
            public void changed(ObservableValue<? extends TodoItems> observable, TodoItems oldValue, TodoItems newValue) {
                if(newValue != null) {
                    TodoItems item = listViewID.getSelectionModel().getSelectedItem();
                    itemDetailTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    dueDateLabel.setText(df.format(item.getDeadline()));
                }
            }
        });

        wantAll = new Predicate<TodoItems>() {
            @Override
            public boolean test(TodoItems todoItems) {
                return true;
            }
        };

        wantToday = new Predicate<TodoItems>() {
            @Override
            public boolean test(TodoItems todoItems) {
                return (todoItems.getDeadline().equals(LocalDate.now()));
            }
        };

        filteredList = new FilteredList<TodoItems>(TodoData.getInstance().getTodoItems(), wantAll);

        SortedList<TodoItems> sortedList = new SortedList<TodoItems>(filteredList, new Comparator<TodoItems>() {
            @Override
            public int compare(TodoItems o1, TodoItems o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });

        listViewID.setItems(sortedList);
        listViewID.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listViewID.getSelectionModel().selectFirst();

        listViewID.setCellFactory(new Callback<ListView<TodoItems>, ListCell<TodoItems>>() {
            @Override
            public ListCell<TodoItems> call(ListView<TodoItems> todoItemsListView) {
                ListCell<TodoItems> cell = new ListCell<TodoItems>(){
                    @Override
                    protected void updateItem(TodoItems todoItems, boolean b) {
                        super.updateItem(todoItems, b);
                        if(b){
                            setText(null);
                        }else{
                            setText(todoItems.getShortDescription());
                            if (todoItems.getDeadline().equals(LocalDate.now())){
                                setTextFill(Color.RED);
                            }else if(todoItems.getDeadline().equals((LocalDate.now().plusDays(1)))){
                                setTextFill(Color.BLUE);
                            }
                        }
                    }
                 };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            }else{
                                cell.setContextMenu(listContextMenu);
                            }
                        });
                return cell;

            }
        });
    }

    @FXML
    public void showDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPaneID.getScene().getWindow());
        dialog.setTitle("Add New Todo Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("TodoListDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            DialogController contoller = fxmlLoader.getController();
            TodoItems newItem = contoller.processResult();
            listViewID.getSelectionModel().select(newItem);
            System.out.println("OK Pressed");
        } else{
            System.out.println("Cancel Pressed");
        }
    }

    @FXML
    public void handleKeyPress(KeyEvent keyEvent){
        TodoItems selectItem = listViewID.getSelectionModel().getSelectedItem();
        if (selectItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectItem);
            }
        }}

    @FXML
    public void handleClickListView(){
        TodoItems item = listViewID.getSelectionModel().getSelectedItem();
        itemDetailTextArea.setText(item.getDetails());
        dueDateLabel.setText(item.getDeadline().toString());

    }

    @FXML
    public void deleteItem(TodoItems item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Are you sure you want to delete: " + item.getShortDescription());
//        alert.setContentText("Are you sure you want to delete the item?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)){
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void filterHandle(){
        TodoItems selectItem = listViewID.getSelectionModel().getSelectedItem();

        if(filterToggleButton.isSelected()){
            filteredList.setPredicate(wantToday);
            listViewID.getSelectionModel().select(selectItem);
        }else {
            filteredList.setPredicate(wantAll);
            if(filteredList.isEmpty()){
                itemDetailTextArea.clear();
                dueDateLabel.setText("");
            }else if (filteredList.contains(selectItem)){
                listViewID.getSelectionModel().select(selectItem);
            } else {
                listViewID.getSelectionModel().selectFirst();
            }
        }
    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }
}
