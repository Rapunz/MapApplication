

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MyMap extends Application {

	private static final int STANDARD_SPACING = 10;
	private static final int LIST_VIEW_HEIGHT = 100;
	private static final int LIST_VIEW_WIDTH = 200;

	private Stage primaryStage;
	private BorderPane workArea;
	private Pane center;
	private MapDisplay map;

	private MenuItem loadPlacesItem;
	private MenuItem saveItem;
	private RadioButton describedButton;
	private RadioButton namedButton;
	private TextField searchField;
	private ListView<Category> categoriesList;

	private PlaceClickHandler placeClickHandler = new PlaceClickHandler();

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		VBox root = new VBox();
		root.setStyle("-fx-font-weight:bold");

		root.getChildren().add(getMenu());

		workArea = new BorderPane();
		root.getChildren().add(workArea);

		// FlowPane center needed so that mapDisplay doesn't get stretched when window
		// is enlarged which would make clicks outside of map possible
		center = new FlowPane();

		workArea.setCenter(center);
		workArea.setTop(getTop());
		workArea.setRight(getRight());

		// Some actions should be disabled until map is loaded
		setDisableMapActions(true);

		Scene scene = new Scene(root);
		primaryStage.setTitle("MyMap");
		primaryStage.setOnCloseRequest(e -> {
			if (hasUnsavedConflict())
				e.consume();
		});
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private MenuBar getMenu() {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");
		menuBar.getMenus().add(fileMenu);

		MenuItem loadMapItem = new MenuItem("Load Map");
		loadMapItem.setOnAction(new LoadMapHandler());
		loadPlacesItem = new MenuItem("Load Places");
		loadPlacesItem.setOnAction(new LoadPlacesHandler());
		saveItem = new MenuItem("Save");
		saveItem.setOnAction(new SaveHandler());
		saveItem.setDisable(true);
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(
				e -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

		fileMenu.getItems().addAll(loadMapItem, loadPlacesItem, saveItem, exitItem);
		return menuBar;
	}

	class LoadMapHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (hasUnsavedConflict()) {
				return;
			}
			File file = getLoadFile("Map");
			if (file == null)
				return;

			Image mapImage = new Image("file:" + file.getAbsolutePath());

			center.getChildren().clear();

			map = new MapDisplay(mapImage);
			map.setOnPlaceClicked(placeClickHandler);

			center.getChildren().add(map);
			primaryStage.sizeToScene();
			setDisableMapActions(false);
		}
	}

	class PlaceClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			Place place = (Place) event.getSource();
			if (event.getButton() == MouseButton.PRIMARY) {
				map.toggleMarked(place);
			} else if (event.getButton() == MouseButton.SECONDARY) {
				showPopUp(AlertType.INFORMATION, place.toString());
			}
		}
	}

	class LoadPlacesHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (hasUnsavedConflict()) {
				return;
			}

			try {
				File file = getLoadFile("Places");
				if (file == null) {
					return;
				}
				FileReader infil = new FileReader(file);
				BufferedReader in = new BufferedReader(infil);
				String line;
				map.clear();
				while ((line = in.readLine()) != null) {
					map.add(PlaceParser.parse(line));
				}

				in.close();
				infil.close();
				map.save();

			} catch (IOException e) {
				showPopUp(AlertType.ERROR, e.getMessage());
			} catch (IndexOutOfBoundsException e) {
				showPopUp(AlertType.ERROR, "Please choose a file containing places");
			} catch (IllegalArgumentException e) {
				showPopUp(AlertType.ERROR, e.getMessage());
			}
		}
	}

	class SaveHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				File file = getSaveFile("Places");
				if (file == null)
					return;

				FileWriter utfil = new FileWriter(file);
				PrintWriter out = new PrintWriter(utfil);

				out.print(map.getSaveFormat());

				out.close();
				utfil.close();
				map.save();

			} catch (IOException e) {
				showPopUp(AlertType.ERROR, e.getMessage());
			}
		}
	}

	private Pane getTop() {
		HBox top = new HBox();
		top.setAlignment(Pos.CENTER);
		top.setPadding(new Insets(STANDARD_SPACING));
		top.setSpacing(STANDARD_SPACING);

		Button newButton = new Button("New");
		newButton.setOnAction(e -> {
			map.setCursor(Cursor.CROSSHAIR);
			map.setOnMouseClicked(new MapClickHandler());
		});

		searchField = new TextField();
		searchField.setPromptText("Search");

		Button searchButton = new Button("Search");
		searchButton.setOnAction(new SearchHandler());
		Button hideButton = new Button("Hide");
		hideButton.setOnAction(e -> map.hideMarked());
		Button removeButton = new Button("Remove");
		removeButton.setOnAction(e -> map.removeMarked());
		Button coordinatesButton = new Button("Coordinates");
		coordinatesButton.setOnAction(new CoordinatesHandler());

		top.getChildren().addAll(newButton, getRadioBox(), searchField, searchButton, hideButton, removeButton,
				coordinatesButton);
		return top;
	}

	class MapClickHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				Position position = new Position((int) event.getX(), (int) event.getY());
				if (map.get(position) != null) {
					throw new IllegalArgumentException("This location is already occupied");
				}

				Category category = categoriesList.getSelectionModel().getSelectedItem();
				boolean described = describedButton.isSelected();

				AddDialog dialog = described ? new AddDescribedDialog() : new AddNamedDialog();

				Optional<ButtonType> answer = dialog.showAndWait();
				if (answer.isPresent() && answer.get() == ButtonType.OK) {
					String name = dialog.getName();
					Place place;
					if (described) {
						String description = ((AddDescribedDialog) dialog).getDescription();
						place = new DescribedPlace(name, position, category, description);
					} else {
						place = new NamedPlace(name, position, category);
					}

					map.add(place);
				}

				map.setOnMouseClicked(null);
				map.setCursor(Cursor.DEFAULT);
			} catch (IllegalArgumentException e) {
				showPopUp(AlertType.ERROR, e.getMessage());
			}
		}
	}

	class SearchHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			map.unmarkAll();
			map.mark(searchField.getText());
			searchField.clear();
		}
	}

	class CoordinatesHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				CoordinatesDialog dialog = new CoordinatesDialog();
				Optional<ButtonType> answer = dialog.showAndWait();
				if (answer.isPresent() && answer.get() == ButtonType.OK) {
					int x = dialog.getXcoordinate();
					int y = dialog.getYcoordinate();
					Place place = map.get(new Position(x, y));
					if (place == null) {
						showPopUp(AlertType.ERROR, "No places at that location");
						return;
					}
					map.unmarkAll();
					map.mark(place);
				}
			} catch (NumberFormatException e) {
				showPopUp(AlertType.ERROR, "Please use numbers, they are magic");
			}
		}
	}

	private VBox getRadioBox() {
		VBox radioBox = new VBox();
		radioBox.setSpacing(STANDARD_SPACING);

		namedButton = new RadioButton("Named");
		namedButton.setSelected(true);
		describedButton = new RadioButton("Described");
		ToggleGroup placeTypeToggle = new ToggleGroup();
		placeTypeToggle.getToggles().addAll(namedButton, describedButton);

		radioBox.getChildren().addAll(namedButton, describedButton);
		return radioBox;
	}

	private VBox getRight() {
		VBox right = new VBox();
		right.setAlignment(Pos.CENTER);
		right.setPadding(new Insets(STANDARD_SPACING));
		right.setSpacing(STANDARD_SPACING);

		Label categoriesLabel = new Label("Categories");
		categoriesList = new ListView<>();
		categoriesList.setPrefSize(LIST_VIEW_WIDTH, LIST_VIEW_HEIGHT);
		categoriesList.getSelectionModel().selectedItemProperty().addListener((obs, oldC, newC) -> {
			if (newC != null)
				map.show(newC);
		});

		for (Category cat : Category.values()) {
			if (cat != Category.NONE) {
				categoriesList.getItems().add(cat);
			}
		}

		Button hideCategoryButton = new Button("Hide Category");
		hideCategoryButton.setOnAction(new HideCategoryHandler());

		right.getChildren().addAll(categoriesLabel, categoriesList, hideCategoryButton);
		return right;
	}

	class HideCategoryHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Category category = categoriesList.getSelectionModel().getSelectedItem();

			if (category != null) {
				map.hide(category);
				categoriesList.getSelectionModel().clearSelection();
			}

		}
	}

	private void setDisableMapActions(boolean disabled) {
		workArea.setDisable(disabled);
		saveItem.setDisable(disabled);
		loadPlacesItem.setDisable(disabled);
	}

	private File getLoadFile(String type) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open " + type + " File");
		return fileChooser.showOpenDialog(primaryStage);
	}

	private File getSaveFile(String type) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save " + type + " File");
		return fileChooser.showSaveDialog(primaryStage);
	}

	private boolean hasUnsavedConflict() {
		if (map != null && map.hasChanged()) {
			Alert alert = new Alert(AlertType.CONFIRMATION,
					"Your unsaved changes will be lost \n Are you sure you want to continue?");
			alert.setTitle("Unsaved changes");
			alert.setHeaderText(null);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get().equals(ButtonType.CANCEL))
				return true;
		}
		return false;
	}

	private void showPopUp(AlertType alertType, String msg) {
		Alert alert = new Alert(alertType, msg);
		alert.setHeaderText(null);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
